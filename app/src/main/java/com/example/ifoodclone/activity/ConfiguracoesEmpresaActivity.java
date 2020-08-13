package com.example.ifoodclone.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.ConfiguracaoFirebase;
import com.example.ifoodclone.helper.UsuarioFirebase;
import com.example.ifoodclone.model.Empresa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaTempo, editEmpresaTaxa;
    private ImageView imagePerfilEmpresa;

    private String [] categorias = new String[]{
            "Pizzaria", "Hamburgueria", "Lanche", "Comida Chinesa", "Comida japonesa"
    };

    private Spinner campoCategoria;

    private static final int SELECAO_GALERIA = 200;

    private StorageReference storageReference;

    private DatabaseReference firebaseRef;

    private String idUsuarioLogado;

    private String urlImagemSelecionada = "";

    private AlertDialog dialog;

    private CurrencyEditText editEmpresaTaxa2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);


        iniciaComponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Configuração toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {//adiciona evento de clique na imagem
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(i,SELECAO_GALERIA);//encaminha para a tela de midia do celular
                }
            }
        });

        carregarDadosSpinner();


        recuperarDadosEmpresa();

    }

    private void recuperarDadosEmpresa(){

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados").setCancelable(false).build();
        dialog.show();




        DatabaseReference empresaRef = firebaseRef.child("empresas").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);//Um objeto empresa recebe os dados daquela empresa logada
                    editEmpresaNome.setText(empresa.getNome());
                    recuperarDadosSpinner(empresa.getCategoria());
                    if (empresa.getPrecoEntrega()!=null){
                        editEmpresaTaxa2.setText(empresa.getPrecoEntrega().toString());
                    }

                    editEmpresaTempo.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlImagem();
                    if (urlImagemSelecionada!=""){

                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilEmpresa);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dialog.dismiss();
    }


    public void validarDadosEmpresa(View view){

        String nome = editEmpresaNome.getText().toString();

        String taxa = editEmpresaTaxa2.getText().toString();


        String categoria = campoCategoria.getSelectedItem().toString();
        String tempo = editEmpresaTempo.getText().toString();

        if (!nome.isEmpty()){
            if (!categoria.isEmpty()){
                if (!taxa.isEmpty()){

                    taxa = taxa.replace(".","").replace("R$","").replace(",",".");
                    taxa = taxa.substring(1);

                    if (!tempo.isEmpty()){

                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNome(nome);
                        empresa.setPrecoEntrega(Double.parseDouble(taxa));
                        empresa.setCategoria(categoria);
                        empresa.setTempo(tempo);
                        empresa.setUrlImagem(urlImagemSelecionada);

                        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados").setCancelable(false).build();
                        dialog.show();

                        empresa.salvar();

                        dialog.dismiss();

                        exibirMensagem("Dados salvos com sucesso");

                        //exibirMensagem(taxa);
                        finish();



                    }else{
                        exibirMensagem("Digite o tempo de entrega para a empresa");
                    }
                }else{
                    exibirMensagem("Digite a taxa de entrega para a empresa");
                }
            }else{
                exibirMensagem("Digite a categoria para a empresa");
            }
        }else{
            exibirMensagem("Digite um nome para a empresa");
        }

    }

    private void exibirMensagem(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode){
                    case SELECAO_GALERIA: //caso a seleção da imagem seja na galeria
                        Uri localImagem = data.getData(); //recupera a imagem
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem); //configura a imagem recuperada
                        break;
                }

                if (imagem!=null){

                    imagePerfilEmpresa.setImageBitmap(imagem); // seta a visualização da imagem

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70, baos); //compressão da imagem para upload
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference.child("imagens").child("empresas").child(idUsuarioLogado+"jpeg"); //local para salvar a imagem no storage

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {//no caso de erro ao fazer upload
                            Toast.makeText(ConfiguracoesEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { //no caso de sucesso ao fazer o upload
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    urlImagemSelecionada = task.getResult().toString();//recupera a url da imagem upada
                                }
                            });



                            Toast.makeText(ConfiguracoesEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    private void carregarDadosSpinner(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        campoCategoria.setAdapter(adapter);
    }

    private void recuperarDadosSpinner(String categoria){
        int posicaoArray = 0;

        for (int i=0; i<=categorias.length-1; i++){
            if (categorias[i].equals(categoria)){
                posicaoArray = i;
                break;
            }
            else{
                posicaoArray=0;
            }
        }

        campoCategoria.setSelection(posicaoArray);
    }



    private void iniciaComponentes(){
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
        campoCategoria = findViewById(R.id.spinnerCategoria);

        editEmpresaTaxa2 = findViewById(R.id.editEmpresaTaxa2);


        Locale locale = new Locale("pt","BR");
        editEmpresaTaxa2.setLocale(locale);
        editEmpresaTaxa2.setText(null);
    }
}