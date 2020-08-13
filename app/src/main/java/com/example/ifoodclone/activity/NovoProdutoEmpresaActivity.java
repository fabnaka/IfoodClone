package com.example.ifoodclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.ConfiguracaoFirebase;
import com.example.ifoodclone.helper.UsuarioFirebase;
import com.example.ifoodclone.model.Produto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private String idUsuarioLogado;
    private CurrencyEditText editProdutoPreco2;

    private ImageView imagemProduto;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String urlImagemSelecionada = "";

    Produto produto = new Produto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        iniciaComponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagemProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagem);
                        break;
                }

                if (imagem!=null){

                    imagemProduto.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[]dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference.child("imagens").child("produtos").child(idUsuarioLogado).child(produto.getIdProduto()+"jpeg"); //local para salvar a imagem no storage

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    urlImagemSelecionada = task.getResult().toString();
                                }
                            });

                            Toast.makeText(NovoProdutoEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void validarDadosProdutos(View view){ //método chamado ao clicar no botao

        String nome = editProdutoNome.getText().toString();
        String descrcao = editProdutoDescricao.getText().toString();

        String preco = editProdutoPreco2.getText().toString();

        if (!nome.isEmpty()){
            if (!descrcao.isEmpty()){
                if ((!preco.isEmpty()) && preco!="0"){
                    preco = preco.replace(".","").replace("R$","").replace(",",".");
                    preco = preco.substring(1);

                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescrcao(descrcao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.setUrlImagem(urlImagemSelecionada);


                    produto.salvar();

                    finish();

                    exibirMensagem("Produto salvo com sucesso!");

                }else{
                    exibirMensagem("Digite o preço para o produto");
                }
            }else{
                exibirMensagem("Digite a descrição para o produto");
            }
        }else{
            exibirMensagem("Digite o nome para o produto");
        }

    }

    private void exibirMensagem(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }


    private void iniciaComponentes(){
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        imagemProduto = findViewById(R.id.imagemProduto);

        editProdutoPreco2=findViewById(R.id.editProdutoPreco2);
        Locale locale = new Locale("pt", "BR");
        editProdutoPreco2.setLocale(locale);
        editProdutoPreco2.setText(null);

    }
}