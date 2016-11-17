package com.example.l156435_p107855.projeto;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatService extends IntentService {

    public static final String NEW_MESSAGE_ACTION = "com.hfad.joke.newMessage";
    public static final String USER_ACTION = "com.hfad.joke.User";


    public static final String EXTRA_MESSAGE = "message";
    public static final int NOTIFICATION_ID = 5453;
    private final IBinder binder = new ChatBinder();

    //private final String HOST = "143.106.243.53"; // Urano de dentro da FT
    //private String HOST = "urano.ft.unicamp.br"; // Urano de qualquer lugar.
    private String HOST = "10.0.2.2"; //Testando

    private int PORT = 8888;
    private String chatAndroidUser = "ChatAndroidUser";

    private Socket socket = null;
    private InputStream input = null;
    private PrintWriter output = null;

    private boolean guiSleeping = false;

    public class ChatBinder extends Binder {
        ChatService getChatService() {
            return ChatService.this;
        }
    }

    public ChatService() {
        super("ChatService");
    }

    public void startNetwork() {
        if (socket != null) {
            return;
        }

        try {
            socket = new Socket(HOST, PORT);
            output = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);
            output.println(chatAndroidUser);
            input = socket.getInputStream();
        } catch (UnknownHostException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }



    // Para que os clientes possam enviar mensagem pela rede. Essa mensagem será enviada na Main Thread.
    public void sendMessage(String message){
        if (output != null)
            output.println(message);
    }

    public void finishSocket() {
        if (null != socket)
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                Log.e("IOException", "IOException");
            }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        HOST = intent.getStringExtra("host");
        PORT = intent.getIntExtra("port",8080);
        chatAndroidUser = intent.getStringExtra("username");

        startNetwork();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                showText(line);
                sendBroadcast(line);

                if (this.guiSleeping){
                    sendNotification(line);
                }
            }
        } catch (IOException e) {
            Log.e("IOException", "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("IOException", "IOException");
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        this.guiSleeping = false;
        return binder;
    }

    @Override
    public void onRebind(Intent intent){
        this.guiSleeping = false;
    }

    @Override
    public boolean onUnbind(Intent intent){
        this.guiSleeping = true;
        return true;
    }

    private String convertMessage(String text){
        boolean save = false;
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < text.length(); i++){
            if (text.charAt(i) == '\''){
                save = !save;
            } else if (save){
                newText.append(text.charAt(i));
            }
        }
        return newText.toString();
    }

    private String convertUser(String text){
        boolean save = false;
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < text.length(); i++){
            if (text.charAt(i) == ','){
                save = !save;
                if (! save)
                    break;
            } else if (save){
                newText.append(text.charAt(i));
            }
        }
        return newText.toString();
    }

    private void sendBroadcast(final String text) {
        String msg = convertMessage(text);

        if (text.charAt(0) == 'c') {
            Intent in = new Intent(NEW_MESSAGE_ACTION);
            in.putExtra("message", msg);
            in.putExtra("user","[ Público ]");
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
        } else if (text.charAt(0) == 'd'){
            Intent in = new Intent(NEW_MESSAGE_ACTION);
            in.putExtra("message", msg);
            in.putExtra("user",convertUser(text));
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
        } else if (text.charAt(0) == 'f') {
            String[] userArray = msg.split(",");
            for (int i = 0; i < userArray.length; i++){
                Intent in = new Intent(USER_ACTION);
                in.putExtra("usuario", userArray[i]);
                in.putExtra("remocao", false);
                LocalBroadcastManager.getInstance(this).sendBroadcast(in);
            }
        } else if (text.charAt(0) == 'a') {
            Intent in = new Intent(USER_ACTION);
            in.putExtra("usuario", msg);
            in.putExtra("remocao", false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
        } else if (text.charAt(0) == 'b') {
            Intent in = new Intent(USER_ACTION);
            in.putExtra("usuario", msg);
            in.putExtra("remocao", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
        }
    }


    private void showText(final String text) {
        Log.v("ChatService", "The message is: " + text);
    }

    private void sendNotification(final String text){
        Context context = this.getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new Notification.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Mensagem Recebida")
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)
            .setContentText(text)
            .build();
        NotificationManager notificationManager =
            ( NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
