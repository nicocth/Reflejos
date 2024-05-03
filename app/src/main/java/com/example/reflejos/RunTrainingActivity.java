package com.example.reflejos;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class RunTrainingActivity extends AppCompatActivity {

    // Atributos de la clase
    private FirebaseAuth mAuth; // Módulo Authentification de firebase
    private FirebaseFirestore db; // Módulo Firestore
    private TextView textViewTemporizador;
    private TextView textViewScore;
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonLight;
    private CountDownTimer countDownTimer;
    private boolean temporizadorFuncionando;
    private long tiempoRestante = 10000; // Variable del tiempo en milisegundos (10") -> Tiene que obtener el valor de la base de datos
    private int score = 0; // Variable de puntuación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_run_training);

        /* [ ESTA PARTE DE CODIGO ES NUEVA DESDE EL UPDATE IGUANA. DESCONOZCO SU UTILIDAD ]
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        // Inicializar elementos de la interfaz de usuario
        textViewTemporizador = findViewById(R.id.textViewTimer);
        textViewScore = findViewById(R.id.textViewScore);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonLight = findViewById(R.id.buttonLight);

        // Asignar listener del buttonStart
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        // Asignar listener del buttonStop
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        // Asignar listener del buttonLight
        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsarLuz();
            }
        });

        // Actualizar el texto del temporizador
        actualizarTextoTemporizador();
    }

    /**
     * Método inicializarCabecera()
     * Carga los datos de Firebase
     */
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
                startActivity(new Intent(RunTrainingActivity.this, LoginActivity.class));
            }
        });
    }

    /**
     * Método startTimer()
     * Este método tiene la función de iniciar el temporizador
     */
    private void startTimer() {
        // Solo si el timer no esta en funcionamiento
        if (!temporizadorFuncionando) {
            // Crear un nuevo CountDownTimer con el tiempo restante y un intervalo de 1000 ms (1 segundo)
            countDownTimer = new CountDownTimer(tiempoRestante, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Actualizar el tiempo restante con el tiempo hasta que termine la cuenta regresiva
                    tiempoRestante = millisUntilFinished;
                    // Actualizar el texto del temporizador en la interfaz de usuario
                    actualizarTextoTemporizador();
                }

                @Override
                public void onFinish() {
                    temporizadorFuncionando = false;
                }
            }.start();

            // Cambiar estado del temporizador a "funcionando"
            temporizadorFuncionando = true;
        }
    }

    /**
     * Método stopTimer()
     * Este método tiene la función de detener y reiniciar el teporizador
     */
    private void stopTimer() {
        countDownTimer.cancel();
        // Cambiar estado del temporizador a "no funcionando"
        temporizadorFuncionando = false;
        tiempoRestante = 120000;
        // Actualizar el texto del temporizador en la interfaz de usuario
        actualizarTextoTemporizador();
        // Poner a 0 la puntuación
        textViewScore.setText("0");
        score = 0;
    }

    /**
     * Método actualizarTextoTemporizador()
     * Este método tiene la función de actualizar el texto del temporizador y mostrarlo en el layout
     */
    private void actualizarTextoTemporizador() {
        int minutes = (int) (tiempoRestante / 1000) / 60;
        int seconds = (int) (tiempoRestante / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        textViewTemporizador.setText(timeLeftFormatted);
    }

    /**
     * Método pulsarLuz()
     * Este método sirve paara darle al funcionalidad al botón que simula una luz
     * Al pulsarlo incrementa el valor de la variable score y lo muestra en el layout
     */
    private void pulsarLuz() {
        // Solo si el timer está en funcionamiento
        if (temporizadorFuncionando) {
            score++;
            textViewScore.setText(String.valueOf(score));
        }
    }
}