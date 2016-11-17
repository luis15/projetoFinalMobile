package com.example.l156435_p107855.projeto;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class ConnectionFragment extends Fragment {

    private MainActivity hostActivity;

    private TextView txtHost;
    private TextView txtPort;
    private TextView txtUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                   Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

        Log.v("oncreate", "oncreate");
        setRetainInstance(true);

        // Iniciando o StartedService
//        Intent intent = new Intent(this, ChatService.class);
//        startService(intent); // Iniciando um startedServer.





        View lview = inflater.inflate(R.layout.fragment_connection, container, false);

        txtHost = (TextView)lview.findViewById(R.id.textViewHost);
        txtPort = (TextView)lview.findViewById(R.id.texViewPort);
        txtUser = (TextView)lview.findViewById(R.id.texViewUser);

        final Switch lswitch = (Switch)lview.findViewById(R.id.buttonswitch);

        lswitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick1(v);
            }
        });

        return lview;
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            hostActivity = (MainActivity) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " Esquisito");
        }
    }

    public void onClick1(View view) {
        boolean serverOn = ((Switch) view).isChecked();
        if (serverOn) {
            String host = txtHost.getText().toString();
            String port = txtPort.getText().toString();
            String user = txtUser.getText().toString();
            hostActivity.startChatService(host, port, user);
        } else {
            hostActivity.finishChatService();
        }
    }



}
