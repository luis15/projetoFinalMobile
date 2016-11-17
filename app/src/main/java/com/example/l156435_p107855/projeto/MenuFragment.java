package com.example.l156435_p107855.projeto;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.content.Intent;
import java.util.ArrayList;

public class MenuFragment extends ListFragment {

    private MainActivity hostActivity;
    private static ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
    private static Fragment lastFragmentPlaced;
    private ArrayAdapter<Usuario> listAdapter;


    private  BroadcastReceiver userReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Está faltando adicionar o publico.
            if (usuarios.size() == 1){
                MessageFragment fragment = new MessageFragment();
                fragment.setUsuario("[ Público ]");
                usuarios.add(new Usuario("[ Público ]", fragment));
                lastFragmentPlaced = fragment;
                //hostActivity.replaceFragment(fragment);
            }

            String  msg     = intent.getStringExtra("usuario");
            boolean remocao = intent.getBooleanExtra("remocao", false);
            if (remocao) {
                for (int i = 0; i < usuarios.size(); i++) {
                    if (usuarios.get(i).getName().equals(msg)) {
                        usuarios.remove(i);
                        break;
                    }

                }
            }else {
                MessageFragment fragment = new MessageFragment();
                fragment.setUsuario(msg);
                Usuario lusuario = new Usuario(msg, fragment);
                usuarios.add(lusuario);
            }
            listAdapter.notifyDataSetChanged();

        }
    };

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);


        if (usuarios.size() == 0) {
            lastFragmentPlaced = new ConnectionFragment();
            usuarios.add(new Usuario("Connection", lastFragmentPlaced));
        }

        IntentFilter filter = new IntentFilter(ChatService.USER_ACTION);
        LocalBroadcastManager.getInstance(hostActivity).registerReceiver(userReceiver, filter);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listAdapter = new ArrayAdapter<Usuario>(inflater.getContext(),
                android.R.layout.simple_list_item_1,
                usuarios);
        setListAdapter(listAdapter);


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setDivider(new ColorDrawable(Color.WHITE));
        listView.setDividerHeight(3); // 3 pixels height
    }



    @Override
    public void onListItemClick(ListView listView,
                                View itemView,
                                int position,
                                long id) {
        lastFragmentPlaced = usuarios.get(position).getFragment();
        hostActivity.replaceFragment(lastFragmentPlaced);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            hostActivity = (MainActivity) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " deve implementar FragmentSwapper");
        }
    }

    private class Usuario {
        private Fragment fragment;
        private String name;

        public Usuario(String name, Fragment fragment){
            this.fragment = fragment;
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public String toString(){
            return this.getName();
        }

        public Fragment getFragment(){
            return fragment;
        }

        @Override
        public boolean equals(Object otherUser){
            if (otherUser instanceof Usuario) {
                return (this.name == ((Usuario) otherUser).getName());
            } else {
                return false;
            }
        }
    }

}
