package com.hfad.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class Create_Notes extends Fragment {

    private final int REQUEST_CODE_SPEECH_INPUT = 1;
    private String str = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_voice_notes_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button startNotes,finishNotes;
        super.onViewCreated(view, savedInstanceState);
        startNotes = Objects.requireNonNull(getView()).findViewById(R.id.create_note_button);
        startNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        finishNotes = getView().findViewById(R.id.finish_note_button);
        finishNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEditNoteActivity();
            }
        });
    }
    private void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now!");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }
    private void callEditNoteActivity(){
        Intent intent = new Intent(getContext(),EditNoteActivity.class);
        intent.putExtra("rawNote",str);
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ArrayList result;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                assert result != null;
                str = str + result.get(0).toString();
                Toast.makeText(getContext(), result.get(0).toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Sorry", Toast.LENGTH_SHORT).show();
        }
    }

}
