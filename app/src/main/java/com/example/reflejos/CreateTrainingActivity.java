package com.example.reflejos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;

public class CreateTrainingActivity extends AppCompatActivity {

    // Atributos de la clase
    private FirebaseAuth mAuth; // Módulo Authentification de firebase
    private FirebaseFirestore db; // Módulo Firestore
    private EditText etTitulo;
    private EditText etTiempo;
    private RadioButton rbResistencia;
    private RadioButton rbVelocidad;
    private TextView tvSecuencia;
    private Button btDisp1;
    private Button btDisp2;
    private Button btDisp3;
    private Button btDisp4;
    private Button btResetSecuencia;
    private Button btCrearEntrenamiento;
    private boolean secStatus; // Variable conmutador de estado secuencia

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar cabecera
        inicializarCabecera();

        // Inicializar elementos de la interfaz de usuario
        etTitulo = findViewById(R.id.etCampo01);
        etTiempo = findViewById(R.id.etCampo03);
        rbResistencia = findViewById(R.id.radioButton1);
        rbVelocidad = findViewById(R.id.radioButton2);
        tvSecuencia = findViewById(R.id.tvSecuencia);
        btDisp1 = findViewById(R.id.button1);
        btDisp2 = findViewById(R.id.button2);
        btDisp3 = findViewById(R.id.button3);
        btDisp4 = findViewById(R.id.button4);
        btResetSecuencia = findViewById(R.id.buttonResSecuencia);
        btCrearEntrenamiento = findViewById(R.id.buttonCrearEntrenamiento);

        // Inicializar variable conmutador secStatus
        secStatus = false;

        // Asignar OnClickListener a los botones de Dispositivos
        asignarOnClickListener(btDisp1, "1");
        asignarOnClickListener(btDisp2, "2");
        asignarOnClickListener(btDisp3, "3");
        asignarOnClickListener(btDisp4, "4");

        // Asignar OnClickListener al botón btResetSecuencia
        btResetSecuencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSecuencia.setText("[ SECUENCIA ]");
                secStatus = false;
            }
        });

        // Asignar OnClickListener al botón btCrearEntrenamiento
        btCrearEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el título del EditText
                String titulo = etTitulo.getText().toString().trim();

                // Verificar si el título está vacío
                if (titulo.isEmpty()) {
                    // Mostrar un mensaje de error si el título está vacío
                    Toast.makeText(CreateTrainingActivity.this, "Ingrese un título para el entrenamiento", Toast.LENGTH_SHORT).show();
                    return;
                }

                //----------------------------------------------------------------------------------------------------------------------------------------------------

                // Obtener el tiempo del EditText y convertirlo a número
                String tiempoString = etTiempo.getText().toString().trim();
                if (tiempoString.isEmpty()) {
                    // Mostrar un mensaje de error si el tiempo está vacío
                    Toast.makeText(CreateTrainingActivity.this, "Ingrese el tiempo para el entrenamiento", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Convertir el tiempo a un número
                Number tiempo = Integer.parseInt(tiempoString);

                //----------------------------------------------------------------------------------------------------------------------------------------------------

                // Determinar la categoría basada en el RadioButton seleccionado
                String categoria;
                if (rbResistencia.isChecked()) {
                    categoria = "resistencia";
                } else {
                    categoria = "velocidad";
                }

                //----------------------------------------------------------------------------------------------------------------------------------------------------

                // Obtener la secuencia del TextView y convertirla a un array de números
                String secuenciaString = tvSecuencia.getText().toString().trim(); // -> Obtener la secuencia

                // COmprobar que ha introducido una secuencia
                if (secuenciaString.equals("[ SECUENCIA ]")) {
                    // Mostrar un mensaje de error si el tiempo está vacío
                    Toast.makeText(CreateTrainingActivity.this, "Ingrese una secuencia de pulsación de los dispositivos luminosos", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] secuenciaArray = secuenciaString.split("-"); // -> Eliminar los guiones de la secuencia
                List<Number> secuenciaNumeros = new ArrayList<>();
                for (String valor : secuenciaArray) {
                    secuenciaNumeros.add(Integer.parseInt(valor));
                }

                //----------------------------------------------------------------------------------------------------------------------------------------------------

                // Obtener el email del usuario
                String emailUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

                // Obtener la referencia a la colección "entrenamientos" del usuario
                final CollectionReference entrenamientosRef = db.collection("usuarios").document(emailUser).collection("entrenamientos");

                // Crear un nuevo Map para almacenar los datos obtenidos
                Map<String, Object> data = new HashMap<>();
                data.put("tiempo", tiempo);
                data.put("categoria", categoria);
                data.put("secuencia", secuenciaNumeros);

                //----------------------------------------------------------------------------------------------------------------------------------------------------

                // Guardar el documento en la colección del usuario actual
                entrenamientosRef.document(titulo).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Mostrar un mensaje de éxito
                                Toast.makeText(CreateTrainingActivity.this, "Entrenamiento creado exitosamente", Toast.LENGTH_SHORT).show();

                                // Obtener referencia a la colección "usuarios"
                                CollectionReference usuariosRef = db.collection("usuarios");

                                // Consultar todos los usuarios que no son entrenadores
                                usuariosRef.whereEqualTo("isTrainer", "false").get() // Verificar que "isTrainer" es un String con valor "false"
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                    String userEmail = document.getId(); // Obtener el correo electrónico del usuario
                                                    CollectionReference entrenamientosUsuarioRef = db.collection("usuarios").document(userEmail).collection("entrenamientos");
                                                    entrenamientosUsuarioRef.document(titulo).set(data);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Manejar errores de consulta
                                                Log.e("Firestore", "Error al obtener usuarios no entrenadores", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Mostrar un mensaje de error si la creación falla
                                Toast.makeText(CreateTrainingActivity.this, "Error al crear entrenamiento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Firestore", "Error al crear entrenamiento", e);
                            }
                        });
            }
        });
    }

    /**
     * Método inicializarCabecera()
     * Carga los datos de Firebase en la cabecera
     */
    private void inicializarCabecera() {

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
                startActivity(new Intent(CreateTrainingActivity.this, LoginActivity.class));
            }
        });
    }

    /**
     * Método asignarOnClickListener
     * Método para asignar OnClickListener a los botones de Dispositivos
     * @param boton Botón del dispositivo que es pulsado
     * @param numero Número del dispositivo que se inserta en la secunecia
     */
    private void asignarOnClickListener(Button boton, final String numero) {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!secStatus){
                    tvSecuencia.setText(numero);
                    secStatus = true;
                } else {
                    String cadenaProvisional = tvSecuencia.getText().toString() + "-" + numero;
                    tvSecuencia.setText(cadenaProvisional);
                }
            }
        });
    }
}