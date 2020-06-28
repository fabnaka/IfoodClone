package com.example.ifoodclone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.UsuarioFirebase;
import com.example.ifoodclone.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        iniciaComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void validarDadosProdutos(View view){

        String nome = editProdutoNome.getText().toString();
        String descrcao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();


        if (!nome.isEmpty()){
            if (!descrcao.isEmpty()){
                if (!preco.isEmpty()){

                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescrcao(descrcao);
                    produto.setPreco(Double.parseDouble(preco));

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
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }
}