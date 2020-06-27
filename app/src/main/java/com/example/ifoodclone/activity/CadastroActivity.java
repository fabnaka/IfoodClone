package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.ConfiguracaoFirebase;
import com.example.ifoodclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private Button botaoCadastrar;
    private EditText campoNome, campoEmail, campoSenha;
    private Switch switchTipo;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


    iniciaComponentes();

        autenticacao= ConfiguracaoFirebase.getFirebaseAutenticacao();

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = campoNome.getText().toString();
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();


                if (!nome.isEmpty()){
                    if (!email.isEmpty()){
                        if (!senha.isEmpty()){
                            usuario= new Usuario();
                            usuario.setNome(nome);
                            usuario.setEmail(email);
                            usuario.setSenha(senha);


                            if (!switchTipo.isChecked()){ //caso o switch estiver em Usuario
                                usuario.setTipo("U");
                            }
                            else { //caso o switch estiver em Empresa
                                usuario.setTipo("E");
                            }

                            cadastrarUsuario();
                        }
                        else {
                            Toast.makeText(CadastroActivity.this, "Favor preencher a senha", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(CadastroActivity.this, "Favor preencher o email", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(CadastroActivity.this, "Favor preencher o nome", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void cadastrarUsuario(){

        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) { //caso o cadastro for sucedido


                    String idUsuario = task.getResult().getUser().getUid();
                    usuario.setId(idUsuario);
                    usuario.salvar();

                    if (usuario.getTipo().equals("U")){ //caso o cadastro for usuario
                        Toast.makeText(CadastroActivity.this, "Cadastro de usuario realizado com sucesso", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    } else { //caso o cadastro for empresa
                        Toast.makeText(CadastroActivity.this, "Cadastro de empresa realizado com sucesso", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
                    }
                }
                else{ //caso ocorra um erro no cadastro
                    String erroExcecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "Favor digitar um email válido";
                    } catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "O email já foi cadastroado";
                    } catch (Exception e){
                        erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, "Erro: "+erroExcecao, Toast.LENGTH_SHORT).show();

                }

            }

        });
    }


    private void iniciaComponentes(){
        botaoCadastrar=findViewById(R.id.buttonCadastrar);
        campoNome=findViewById(R.id.editTextNome);
        campoEmail=findViewById(R.id.editTextEmail);
        campoSenha=findViewById(R.id.editTextSenha);
        switchTipo=findViewById(R.id.switchTipo);
    }
}