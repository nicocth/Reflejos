package com.example.reflejos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //declaración de los elementos de la vista
    private TextView emailTextView, userTypeTextView;
    private Button signOutButton;

    //declaración del módulo Authentification de firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializamos elementos de la vista
        emailTextView = findViewById(R.id.emailTextView);
        userTypeTextView = findViewById(R.id.userTypeTextView);
        signOutButton = findViewById(R.id.signOutButton);

        //inicializamos clase de autentificacion firebase
        mAuth = FirebaseAuth.getInstance();

        //Configuramos informacion de usuario
        emailTextView.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());


        //Configuramos boton Cerrar sesión
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}