package com.example.reflejos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DevicesActivity extends AppCompatActivity {

    // constantes de permisos de bluetooth
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;


    //declaración del módulo Authentification de firebase
    private FirebaseAuth mAuth;

    //declaración del módulo Firestore
    private FirebaseFirestore db;
    private Button searchButton;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> devicesSet = new HashSet<>();
    private List<String> devicesList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;


    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        // configuración de botón y lista en la vista
        configurarVista();
        // verificación de permisos bluetooth e inicialización del adaptador
        solicitarPermisos();
        iniciarAdaptadorBluetooth();
        // actualización de lista con dispositivos vinculados
        actualizarLista();
        // inicialización de cabecera
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
                        if (document.get("isTrainer").toString().equals("true")) {
                            userTypeTextView.setText("Entrenador:");
                        } else {
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

    private void configurarVista() {
        searchButton = findViewById(R.id.searchButton);
        listView = findViewById(R.id.devicesList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devicesList);
        listView.setAdapter(arrayAdapter);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buscarDispositivos();
            }
        });
    }

    /** Método que comprueba si tiene permisos para usar bluetooth en el dispositivo y
     * si no los tiene, los pide al usuario */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        }
    }

    // inicialización y configuración del adaptador de bluetooth
    private void iniciarAdaptadorBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no soportado en el dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Método que descubre dispositivos y los añade a la lista
    private void buscarDispositivos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothAdapter.startDiscovery();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, intentFilter);
        actualizarLista();
    }

    // Recibe los eventos de bluetooth, si encuentra dispositivos los añade al set y actualiza la lista
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Probando", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesSet.add(device);
                actualizarLista();
            }
        }
    };

    // Añade los dispositivos conectados y los encontrados por el receiver a la lista.
    private void actualizarLista() {
        devicesList.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> dispositivosConectados = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : dispositivosConectados) {
            devicesList.add(device.getName() + "\nConectado");
        }
        for (BluetoothDevice device : devicesSet) {
            devicesList.add(device.getName() + "\n" + device.getAddress());
        }
        arrayAdapter.notifyDataSetChanged();
    }

    // Método para liberar recursos quitando el receiver
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Crea error al salir de la activity por no poder escribir en el archivo .log correspondiente.
        //unregisterReceiver(receiver);
    }

}