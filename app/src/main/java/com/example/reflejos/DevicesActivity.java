package com.example.reflejos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DevicesActivity extends AppCompatActivity {

    //declaración del módulo Authentification de firebase
    private FirebaseAuth mAuth;

    //declaración del módulo Firestore
    private FirebaseFirestore db;

    //declaramos array para inicializar la lista
    private ArrayList<ModeloLista> datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        //inicializamos cabecera
        inicializarCabecera();

        //inicializar list view
        inicializarLista();
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
                startActivity(new Intent(DevicesActivity.this, LoginActivity.class));
            }
        });
    }

    private void inicializarLista() {
        //inicializamos lista
        ListView list = findViewById(R.id.list);

        //Creacion de almacen datos
        //ArrayList<ModeloLista> datos;
        datos = new ArrayList<ModeloLista>();

        //Obtenemos email del usuario
        String emailUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        //obtenemos datos de la Base de datos
        db.collection("usuarios")
                .document(emailUser)
                .collection("entrenamientos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase Result", document.getId() + " => " + document.getData());
                                List<Number> secuencia = (List<Number>) document.get("secuencia");
                                datos.add(new ModeloLista(R.drawable.bluetooth, document.getId(),
                                        "Numero de pasos " + secuencia.size(),
                                        "Tiempo: " + Objects.requireNonNull(document.getData().get("tiempo")).toString()));



                            }

                            //Añadimos adaptador a la lista
                            list.setAdapter(new AdaptadorListView(getBaseContext(), R.layout.list_layout, datos){
                                @Override
                                public void onEntrada (Object entrada, View view) {
                                    TextView titulo = (TextView) view.findViewById(R.id.texto_titulo);
                                    TextView primerTexto = (TextView) view.findViewById(R.id.texto1);
                                    TextView segundoTexto = (TextView) view.findViewById(R.id.texto2);
                                    ImageView imagen_entrada = (ImageView) view.findViewById(R.id.imagen);

                                    //configuramos elementos con lo que ofrezca el POJO
                                    titulo.setText(((ModeloLista)entrada).get_textoTitulo());
                                    primerTexto.setText(((ModeloLista)entrada).get_texto1());
                                    segundoTexto.setText(((ModeloLista)entrada).get_texto2());
                                    //almaceno el id de la imagen en una variable para pasarla a traves del intent
                                    int idImagen = (((ModeloLista) entrada).get_idImagen());
                                    imagen_entrada.setImageResource(idImagen);
                                }
                            });

                            //añadimos escuchador al adaptador de la lista
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                                    ModeloLista elegido=(ModeloLista)pariente.getItemAtPosition(posicion);
                                    //extrae el texto de ese elemento
                                    CharSequence textoelegido = "Seleccionado: " + elegido.get_texto1();
                                }
                            });

                        } else {
                            Log.d("Firebase Result", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}