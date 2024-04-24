package com.example.reflejos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FormSignInActivity extends AppCompatActivity {

    //declaración de elementos de la vista
    private TextView errorTextView;
    private EditText emailEditText, passwordEditText, passwordConfirmationEditText;
    private Button signUpButton, comebackButton;

    //declaración del módulo Authentification de firebase
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_sign_in);

        //inicializamos elementos de la vista
        errorTextView = findViewById(R.id.errorTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmationEditText = findViewById(R.id.passwordConfirmationEditText);
        signUpButton = findViewById(R.id.signUpButton);
        comebackButton = findViewById(R.id.comebackButton);

        //inicializamos clase de autentificacion firebase
        mAuth = FirebaseAuth.getInstance();

        //Configuracion de la Autentificacion firebase
        setup();
    }

    private void setup() {
        //cambiamos el titulo de la actividad
        setTitle(getString(R.string.form_sign_up_title));
        //configuramos boton volver atrás
        comebackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FormSignInActivity.this, LoginActivity.class));
                finish();
            }
        });
        //configuramos boton Registrase
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se limpia mensaje de error
                errorTextView.setText("");

                //captamos el texto de los tres campos
                String emailUser = emailEditText.getText().toString().trim();
                String passwordUser = passwordEditText.getText().toString().trim();
                String passwordComfirmationUser = passwordConfirmationEditText.getText().toString().trim();
                //Comprobamos si ha sido introducido texto en todos los campos
                if(emailUser.isEmpty() || passwordUser.isEmpty() || passwordComfirmationUser.isEmpty()){
                    //Si no se ha introducido texto en alguno de los dos campos se muestra error
                    Toast.makeText(FormSignInActivity.this,
                            "Hay campos sin rellenar.", Toast.LENGTH_SHORT).show();
                }else{
                    //si se ha introducido texto en todos los campos confirmamos que la contraseña y su confirmacion sean iguales
                    if(passwordUser.equals(passwordComfirmationUser)){
                        //si es correcto llamamos al metodo de registrar usuario
                        signInUser(emailUser, passwordUser);
                    }else{
                        //Si no coincide la contraseña con su confirmacion se muestra error
                        Toast.makeText(FormSignInActivity.this,
                                "La contraseña y la confirmacion de la contraseña no coinciden.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Método que hace añade en firebase, si lo logra nos lleva a la actividad principal con esa sesion iniciada.
     * si no lo logra muestra error.
     * @param emailUser
     * @param passwordUser
     */
    private void signInUser(String emailUser, String passwordUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passwordUser)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //si no hay errores volvemos al login
                        startActivity(new Intent(FormSignInActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        if (task.getException().getMessage() != null){
                            errorTextView.setText(task.getException().getMessage());
                        }
                        //si el registro falla mostramos el error
                        Toast.makeText(FormSignInActivity.this, "Fallo al crear usuario.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}