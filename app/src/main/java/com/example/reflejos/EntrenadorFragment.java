package com.example.reflejos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class EntrenadorFragment extends Fragment {

    private Button createTrainingButton, trainingButton, clientsButton, devicesButton, helpButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrenador_fragment, container, false);

        // Inicializar los botones
        trainingButton = view.findViewById(R.id.trainingButton);
        createTrainingButton = view.findViewById(R.id.createTrainingButton);
        clientsButton = view.findViewById(R.id.clientesButton);
        devicesButton = view.findViewById(R.id.devicesButton);
        helpButton = view.findViewById(R.id.helpButton);

        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TrainingActivity.class));
            }
        });
        createTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateTrainingActivity.class));
            }
        });

        clientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           
                startActivity(new Intent(getActivity(), ClientActivity.class));
            }
        });

        devicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DevicesActivity.class));
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });

        return view;
    }
}
