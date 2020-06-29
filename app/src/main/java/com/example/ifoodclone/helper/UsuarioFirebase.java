package com.example.ifoodclone.helper;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.ifoodclone.activity.EmpresaActivity;
import com.example.ifoodclone.activity.HomeActivity;
import com.example.ifoodclone.model.Cadastro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void redirecionaTipoCadastroLogado(final Activity activity){
        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("cadastros")
                .child(getIdentificadorUsuario());

        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Cadastro cadastro = dataSnapshot.getValue(Cadastro.class);//recupera o usuario

                String tipoUsuario = cadastro.getTipo();//recupera o tipo do usuario

                if (tipoUsuario.equals("U")){
                    Intent i = new Intent(activity, HomeActivity.class);
                    activity.startActivity(i);

                }else{
                    Intent i = new Intent(activity, EmpresaActivity.class);
                    activity.startActivity(i);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
}
