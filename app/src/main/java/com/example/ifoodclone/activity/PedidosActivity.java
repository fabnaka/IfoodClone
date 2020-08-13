package com.example.ifoodclone.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.AdapterPedido;
import com.example.ifoodclone.helper.ConfiguracaoFirebase;
import com.example.ifoodclone.helper.UsuarioFirebase;
import com.example.ifoodclone.listener.RecyclerItemClickListener;
import com.example.ifoodclone.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        iniciarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdentificadorUsuario();

        //Config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Config recycler
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        //Adicionar evento de clique no recycler
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {//finaliza os pedidos
                Pedido pedido = pedidos.get(position);
                pedido.setStatus("finalizado");
                pedido.atualizarStatus();

                Toast.makeText(PedidosActivity.this, "Pedido finalizado!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados").setCancelable(false).build();
        dialog.show();

        DatabaseReference pedidosRef = firebaseRef.child("pedidos").child(idEmpresa); //acessa o nó pedidos e depois o nó id emrpesa
        Query pedidosPesquisa = pedidosRef.orderByChild("status").equalTo("confirmado"); // recupera os dados com status confirmado

        pedidosPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos.clear();//limpa a lista
                if (dataSnapshot.getValue()!=null){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){ //percorre os pedidos da empresa
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido); //adiciona o pedido na lista
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else{
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void iniciarComponentes(){
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }
}