package com.example.l156435_p107855.projeto;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MessageFragment extends Fragment {
    private MainActivity hostActivity;
    private String usuario;
    private EditText sendMessage = null;
    private TextView chatView = null;
    private String saveText = "";


    public MessageFragment() {
        this.usuario = "[ Público ]";
        // Required empty public constructor
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // Avisando que esta classe está ouvindo uma resposta em brooadcast
        IntentFilter filter = new IntentFilter(ChatService.NEW_MESSAGE_ACTION);
        LocalBroadcastManager.getInstance(hostActivity).registerReceiver(testReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View lview = inflater.inflate(R.layout.fragment_message, container, false);


        sendMessage = (EditText)lview.findViewById(R.id.textSendMessage);
        chatView = (TextView)lview.findViewById(R.id.textMessage);
        final Button button = (Button)lview.findViewById(R.id.buttonSend);
        final TextView top = (TextView)lview.findViewById(R.id.textUsuario);


        top.setText("Mensagem com " + usuario);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = sendMessage.getText().toString();
                chatView.append(">>> " + msg + "\n");
                if (MessageFragment.this.usuario.equals("[ Público ]")) {
                    msg = "c," + msg;
                } else {
                    msg = "d," + MessageFragment.this.usuario + "," + msg;
                }
                sendMessage.setText("");
                hostActivity.sendMessage(msg);
            }
        });


        return lview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            hostActivity = (MainActivity) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " Esquisito");
        }
    }

    @Override
    public void onResume(){
        super.onStart();
        chatView.setText(saveText);
    }

    @Override
    public void onPause(){
        super.onPause();
        saveText = chatView.getText().toString();
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg     = intent.getStringExtra("message");
            String user = intent.getStringExtra("user");
            if (user.equals(MessageFragment.this.usuario)) {
                try {
                    msg = "<<< " + msg;
                    Log.v("MessageBroadcast", "User "+ MessageFragment.this.usuario + " recebeu "+ msg);
                    chatView.append(msg + "\n");
                    saveText = chatView.getText().toString();
                }catch (Exception e){
                    Log.v("MessageBroadcast", "Apanhado pelo catch");
                }
            }
        }
    };

}
