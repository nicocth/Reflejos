package com.example.reflejos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //declaración de los elementos de la vista
    private Button trainingButton, recordButton, createTrainingButton;
    private Button devicesButton, helpButton;

    //declaración del módulo Authentification de firebase
    private FirebaseAuth mAuth;

    //declaración del módulo Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializamos cabecera
        inicializarCabecera();

        //inicializamos elementos del cuerpo de la vista
        trainingButton = findViewById(R.id.trainingButton);
        recordButton = findViewById(R.id.recordButton);
        createTrainingButton = findViewById(R.id.createTrainingButton);
        devicesButton = findViewById(R.id.devicesButton);
        helpButton = findViewById(R.id.helpButton);

        //Configuramos los botones
        configurarBotones();
    }


    private void inicializarCabecera() {
        //inicializamos clase de autentificacion firebase
        mAuth = FirebaseAuth.getInstance();

        //inicializamos clase de base de datos firestore
        db = FirebaseFirestore.getInstance();

        //inicializamos elementos de la vista
        Button signOutButton = findViewById(R.id.signOutButton);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView userTypeTextView = findViewById(R.id.userTypeTextView);

        //Obtenemos email del usuario
        String emailUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        //Configuramos informacion de usuario
        emailTextView.setText(emailUser);

        DocumentReference docRef = db.collection("usuarios").document(emailUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FirestoreResult", "DocumentSnapshot data: " + document.getData());
                        if (document.get("isTrainer").toString().equals("true")){
                            userTypeTextView.setText("Entrenador:");
                        }else{
                            userTypeTextView.setText("Cliente:");
                        }

                    } else {
                        Log.d("FirestoreResult", "No such document");
                    }
                } else {
                    Log.d("FirestoreResult", "get failed with ", task.getException());
                }
            }
        });

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

    private void configurarBotones() {
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