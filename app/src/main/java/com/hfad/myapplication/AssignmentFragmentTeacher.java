package com.hfad.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;


public class AssignmentFragmentTeacher extends Fragment{
    private String division,subject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_teacher,container,false);
    }
    private Spinner sub_spin;
    private Spinner div_spin;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button answer;
        super.onViewCreated(view, savedInstanceState);
        sub_spin = Objects.requireNonNull(getView()).findViewById(R.id.spin3);
        div_spin = getView().findViewById(R.id.div_fat);
        answer = getView().findViewById(R.id.answer_button_teacher);
        handleSpinners();
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testDetails())
                fetchAllAnswers();
            }
        });
    }

    private void handleSpinners(){


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.division, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        div_spin.setAdapter(adapter1);
        div_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                division = parent.getItemAtPosition(position).toString();

                if(division.compareTo("G3")==0||division.compareTo("H3")==0||division.compareTo("N3")==0)
                {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.G3, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sub_spin.setAdapter(adapter);
                }
                if(division.compareTo("G2")==0||division.compareTo("H2")==0||division.compareTo("N2")==0)
                {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.G2, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sub_spin.setAdapter(adapter);
                }
                if(division.compareTo("G1")==0||division.compareTo("H1")==0||division.compareTo("N1")==0)
                {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.G1, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sub_spin.setAdapter(adapter);
                }



                sub_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        subject = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void fetchAllAnswers(){
      Intent i = new Intent(getContext(),RetrieveAnswer.class);
        i.putExtra("subject",subject);
        i.putExtra("division",division);
        startActivity(i);
    }

    private boolean testDetails(){
        if(TextUtils.isEmpty(division)){
            Toast.makeText(getContext(), "Please select a division Eg:G3", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(subject)){
            Toast.makeText(getContext(), "Please select a subject Eg:Java", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

