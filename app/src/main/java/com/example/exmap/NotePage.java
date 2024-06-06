package com.example.exmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exmap.databinding.ActivityNotePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotePage extends AppCompatActivity {
    ActivityNotePageBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DocumentReference documentReference;
    String title, content, docId;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()){
            isEdit = true;
        }

        binding.title.setText(title);
        binding.content.setText(content);

        if (isEdit) {
            binding.header.setText("EDIT NOTE");
            binding.submitNote.setText("EDIT");
            binding.delete.setVisibility(View.VISIBLE);
        }

        binding.submitNote.setOnClickListener((v) -> saveNote());

        binding.delete.setOnClickListener((v) -> deleteNoteDB());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotePage.this, NoteShow.class));
            }
        });
    }

    /* Hide Status Bar & Navigation Bar */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    void saveNote(){
        String noteTitle = binding.title.getText().toString();
        String noteContent = binding.content.getText().toString();

        if (noteTitle == null || noteTitle.isEmpty()){
            binding.title.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToDB(note);
    }

    void  saveNoteToDB(Note note){

        if (isEdit) {
            documentReference = Utility.collectionReference().document(docId);
        } else {
            documentReference = Utility.collectionReference().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(NotePage.this, "Add note..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NotePage.this, NoteShow.class));
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    Toast.makeText(NotePage.this, "Try Again..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void  deleteNoteDB(){

        documentReference = Utility.collectionReference().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(NotePage.this, "Delete note..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NotePage.this, NoteShow.class));
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    Toast.makeText(NotePage.this, "Try Again..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}