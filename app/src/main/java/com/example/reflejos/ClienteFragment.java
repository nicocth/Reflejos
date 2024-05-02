package com.example.reflejos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ClienteFragment extends Fragment {

    private Button trainingButton, recordButton, devicesButton, helpButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cliente_fragment, container, false);

        // Inicializar los botones
        trainingButton = view.findViewById(R.id.trainingButton);
        recordButton = view.findViewById(R.id.recordButton);
        devicesButton = view.findViewById(R.id.devicesButton);
        helpButton = view.findViewById(R.id.helpButton);

        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TrainingActivity.class));
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RecordActivity.class));
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
