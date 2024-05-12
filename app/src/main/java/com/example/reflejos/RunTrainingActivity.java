package com.example.reflejos;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RunTrainingActivity extends AppCompatActivity {

    // Atributos de la clase
    private FirebaseAuth mAuth; // Módulo Authentification de firebase
    private FirebaseFirestore db; // Módulo Firestore
    private TextView textTitleTraining; // Título del entrenamiento -> Se obtiene de la BD
    private TextView textViewTemporizador;
    private TextView textViewScore;
    private TextView textSecuencia; // TextView con la secuencia de pulsación de luces -> Se obtiene de la BD
    private int[] arraySecuencia; // Array para almacenar la secuencia de pulsación de luces -> Se obtiene de la BD
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonLight;
    private CountDownTimer countDownTimer;
    private String idEntrenamiento; // Atributo que obtiene el idEntrenamiento del Intent y corresponde con el id del documento de la BD
    private boolean temporizadorFuncionando; // Variable conmutador para determinar el estado del temporizador
    private long tiempoEstablecido; // Variable del tiempo en milisegundos (10") -> Debe obtener su valor de la BD.Es el tiempo establecido por el entrenamiento
    private long tiempoRestante; // Variable del tiempo utilizado por los métodos de la clase
    private int score = 0; // Variable de puntuación
    private Date fechaHoraRegistro; // Variable para registrar la fecha y hora al terminar el entrenamiento

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_training);

        // Inicializar cabecera
        inicializarCabecera();

        // Inicializar elementos de la interfaz de usuario
        textTitleTraining = findViewById(R.id.textTitleTraining);
        textViewTemporizador = findViewById(R.id.textViewTimer);
        textViewScore = findViewById(R.id.textViewScore);
        textSecuencia = findViewById(R.id.textSecuence);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonLight = findViewById(R.id.buttonLight);

        // Obtener los datos recibidos desde la actividad anterior
        Bundle bundle = getIntent().getExtras();

        // Obtener el id del entrenamiento del intent
        idEntrenamiento = bundle.getString("idEntrenamiento");

        // Obtener datos del entrenamiento desde la BD
        obtenerDatosEntrenamiento();

        tiempoRestante = tiempoEstablecido;

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
     * Carga los datos de Firebase en la cabecera
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
     * Método obtenerDatosEntrenamiento()
     * Obtiene los datos del entrenamiento desde la BD, del documento referente al id obtenido mediante el Intent
     */
    private void obtenerDatosEntrenamiento() {
        // Obtener email del usuario
        String emailUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        // Obtener referencia al documento específico
        DocumentReference docRef = db.collection("usuarios")
                .document(emailUser)
                .collection("entrenamientos")
                .document(idEntrenamiento);
        // Obtener los datos del documento específico
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtener el id del documento de la BD y mostrarlo en la interfaz de usuario como título del entrenamiento
                        textTitleTraining.setText(document.getId());
                        // Obtener el valor del campo "tiempo" del documento
                        tiempoEstablecido = document.getLong("tiempo") * 1000;
                        // Actualizar el tiempo restante con el valor obtenido
                        tiempoRestante = tiempoEstablecido;
                        // Actualizar el texto del temporizador en la interfaz de usuario
                        actualizarTextoTemporizador();

                        // Obtener el campo "secuencia" del documento
                        List<Long> listaSecuenia = (List<Long>) document.get("secuencia");
                        // Mientras haya valores el la lista de secuencia
                        if (listaSecuenia != null) {
                            // Inicializar atributo arraySecuencia
                            arraySecuencia = new int[listaSecuenia.size()];
                            // Crear un StringBuilder para mostrar la secuencia en el testView
                            StringBuilder secuenciaSB = new StringBuilder();
                            // Recorrer la lista de valores obtenidos del campo "secuencia"
                            for (int i = 0; i < listaSecuenia.size(); i++) {
                                // Almacenar valor en el atributo arraySecuencia
                                arraySecuencia[i] = listaSecuenia.get(i).intValue();
                                // Agregar el valor al StringBuilder
                                secuenciaSB.append(arraySecuencia[i]);
                                // Agregar un guión si no es el último valor
                                if (i < listaSecuenia.size() - 1) {
                                    secuenciaSB.append("-");
                                }
                            }
                            // Establecer el texto en el textView textSecuencia
                            textSecuencia.setText(secuenciaSB.toString());
                        }

                    } else {
                        Log.d("Firebase Result", "No such document");
                    }
                } else {
                    Log.d("Firebase Result", "get failed with ", task.getException());
                }
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
                    // Cambiar el estado del temporizador a "no funcionando"
                    temporizadorFuncionando = false;
                    // Obtener la fecha y hora actual
                    fechaHoraRegistro = new Date();

                    //Obtenemos email del usuario
                    String emailUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                    Map<String, Object> record = new HashMap<>();
                    record.put("fecha", fechaHoraRegistro);
                    record.put("puntuacion", score);

                    db.collection("usuarios").document(emailUser).collection("historial").document(idEntrenamiento+ " " + fechaHoraRegistro)
                            .set(record)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firestore", "Error writing document", e);
                                }
                            });

                    // Mostrar la fecha y hora en Logcat
                    Log.d("[ FechaHoraReg ]", " -> La fecha y hora del registro es: " + fechaHoraRegistro.toString());
                    // Mostrar la puntuación obtenida en Logcat
                    Log.d("[ ScoreReg ]", " -> La puntuación del registro es: " + String.valueOf(score));
                    // Restablecer el valor del tiempoRestante al tiempoEstablecido
                    tiempoRestante = tiempoEstablecido;
                    // Actualizar el texto del temporizador en la interfaz de usuario
                    actualizarTextoTemporizador();
                    // Poner a 0 la puntuación
                    textViewScore.setText("0");
                    score = 0;

                    Toast.makeText(RunTrainingActivity.this, "Entrenamiento finalizado.", Toast.LENGTH_SHORT).show();
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
        // Solo si el temporizador se encuentra en funcionamiento
        if (temporizadorFuncionando) {
            countDownTimer.cancel();
            // Cambiar estado del temporizador a "no funcionando"
            temporizadorFuncionando = false;
            // Restablecer el valor del tiempoRestante al tiempoEstablecido
            tiempoRestante = tiempoEstablecido;
            // Actualizar el texto del temporizador en la interfaz de usuario
            actualizarTextoTemporizador();
            // Poner a 0 la puntuación
            textViewScore.setText("0");
            score = 0;
        }
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