package com.example.l156435_p107855.projeto;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ChatService chatService;
    private boolean bound = false;
    private boolean serverOn;
    // private ConnectionFragment connectionFragment;

    boolean isToReplaceTabletFragment = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            ChatService.ChatBinder chatBinder =
                    (ChatService.ChatBinder) binder;
            chatService = chatBinder.getChatService();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, ChatService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE); // Acoplando para poder enviar mensagem
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //if (chatService != null)
        //    chatService.finishSocket();
    }


    public void startChatService(String host, String port, String username){
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtra("host", host);
        intent.putExtra("username", username);
        int iport = Integer.parseInt(port);
        intent.putExtra("port", iport);
        startService(intent); // Iniciando um startedServer.
    }

    public void finishChatService(){
        chatService.finishSocket();
    }

    public void sendMessage(String message){
        chatService.sendMessage(message);
    }

    public void replaceFragment(Fragment fragment){
        //if (fragment == null)
        //    fragment = this.connectionFragment;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (isToReplaceTabletFragment) {
                fragmentTransaction.replace(R.id.center_container, fragment);
            } else {
                fragmentTransaction.add(R.id.center_container, fragment);
                isToReplaceTabletFragment = true;
            }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }



}
