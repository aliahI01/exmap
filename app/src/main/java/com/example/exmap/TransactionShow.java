package com.example.exmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exmap.databinding.ActivityTransactionShowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class TransactionShow extends AppCompatActivity{

    ActivityTransactionShowBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference collectionReference;
    FirebaseStorage firebaseStorage;
    String idTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance().getReference().getStorage();
        idTrans = firebaseUser.getUid();
        collectionReference = firestore.collection("record").document(firebaseAuth.getUid())
                .collection("transaction");

        String idTrans = getIntent().getStringExtra("id");
        String amount = getIntent().getStringExtra("amountTrans");
        String categ = getIntent().getStringExtra("category");
        String date = getIntent().getStringExtra("date");
        String notes = getIntent().getStringExtra("note");
        String receipt = getIntent().getStringExtra("receipt");
        String type = getIntent().getStringExtra("type");

        binding.amountShow.setText(amount);
        binding.categList.setText(categ);
        binding.date.setText(date);
        binding.notes.setText(notes);
        Picasso.get().load(String.valueOf(receipt)).into(binding.receipt);
        binding.type.setText(type);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionShow.this, HistoryPage.class));
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
}