package com.example.reflejos;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //declaración de elementos de la vista
    private EditText emailEditText, passwordEditText;
    private Button signUpButton, logInButton;
    //declaración del módulo Authentification de firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inicializamos elementos de la vista
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        logInButton = findViewById(R.id.logInButton);

        //inicializamos clase de autentificacion firebase
        mAuth = FirebaseAuth.getInstance();

        //Configuracion de la Autentificacion firebase
        setup();
    }

    /**
     * Método que comprueba si se han introducido valores en los campos email y password,
     * si han sido introducidos capta su valor y llama al método loginUser.
     * De lo contrario muestra error.
     */
    private void setup() {
        setTitle("Autentificación");
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = emailEditText.getText().toString().trim();
                String passwordUser = passwordEditText.getText().toString().trim();
                //Comprobamos si ha sido introducido texto en los campos email y password
                if(emailUser.isEmpty() || passwordUser.isEmpty()){
                    //Si no se ha introducido texto en alguno de los dos campos se muestra error
                    Toast.makeText(LoginActivity.this,
                            "El usuario o la contraseña no han sido introducidos.", Toast.LENGTH_SHORT).show();
                }else{
                    //si se ha introducido texto se intenta hacer login en firebase
                    loginUser(emailUser, passwordUser);
                }
            }
        });
    }

    /**
     * Método que hace login cotejando los usuarios registrados en firebase, si lo logra nos lleva a la actividad principal
     * si no lo logra muestra error.
     * @param emailUser
     * @param passwordUser
     */
    private void loginUser(String emailUser, String passwordUser) {
        mAuth.signInWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Toast.makeText(LoginActivity.this, "Bienvenido.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "El usuario o la contraseña son incorrectos.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sobreescribimos el método OnStart para comprobar si hay una sesion iniciada cuando se abre la app,
     * en caso de haberla nos lleva directamente a MainActivity, no nos pedira logearnos hasta que no cerremos sesion en alguna
     * actividad de la app.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}