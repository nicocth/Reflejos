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
    private Button signOutButton, trainingButton, recordButton, createTrainingButton;
    private Button devicesButton, helpButton;

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
        trainingButton = findViewById(R.id.trainingButton);
        recordButton = findViewById(R.id.recordButton);
        createTrainingButton = findViewById(R.id.createTrainingButton);
        devicesButton = findViewById(R.id.devicesButton);
        helpButton = findViewById(R.id.helpButton);
    }


    private void inicializarCabecera() {
        //inicializamos clase de autentificacion firebase
        mAuth = FirebaseAuth.getInstance();

        //inicializamos elementos de la vista
        signOutButton = findViewById(R.id.signOutButton);
        emailTextView = findViewById(R.id.emailTextView);
        userTypeTextView = findViewById(R.id.userTypeTextView);

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

        //Configuración de botón Entrenamientos
        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TrainingActivity.class));
            }
        });

        //Configuración de botón Historial
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
            }
        });

        //Configuración de botón Crear Entrenamiento
        createTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateTrainingActivity.class));
            }
        });

        //Configuración de botón Dispositivos
        devicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DevicesActivity.class));
            }
        });

        //Configuración de botón Ayuda
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }

}