package com.example.reflejos;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecemos orientación exclusiva de retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Ocultar la barra de titulo
        getSupportActionBar().hide();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        // Usamos un Handler para esperar un cierto tiempo y luego iniciar la nueva actividad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Arrancamos la siguiente actividad
                Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                // Cerramos esta actividad para que el usuario no pueda volver a ella mediante botón de volver atrás
                finish();
            }
        }, 3000); // Tiempo de espera en milisegundos

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}