package com.example.exmap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exmap.databinding.ActivityTransactionEditBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.text.TextRecognizer;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionEdit extends AppCompatActivity {

    ActivityTransactionEditBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    private static final int CAMERA_REQUEST_CODE = 100;
    StorageReference storageReference;
    String idTrans = "";
    String newCb;
    Calendar calendar;
    Uri imgUri;
    String img = "";
    int year, month, day;
    String date;
    String type = "";
    String newType;
    String receiptUrl;
    DatePickerDialog datePickerDialog;
    TextRecognizer textRecognizer;
    String[] cameraPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionEdit.this, HistoryPage.class));
            }
        });

        Intent intent = getIntent();
        if (intent!=null){
            idTrans = intent.getStringExtra("id");
            binding.amount.setText(intent.getStringExtra("amountTrans"));
            binding.categList.setText(intent.getStringExtra("category"));
            binding.date.setText(intent.getStringExtra("date"));
            binding.notes.setText(intent.getStringExtra("note"));
            String receipt = getIntent().getStringExtra("receipt");
            Picasso.get().load(String.valueOf(receipt)).into(binding.receipt);
//            Picasso.get().load(intent.getStringExtra("receipt")).into(binding.receipt);
            binding.type.setText(intent.getStringExtra("type"));
//            Picasso.get().load(String.valueOf("receipt")).into(binding.receipt);
        }

        receiptUrl = intent.getStringExtra("receipt");
        newType = intent.getStringExtra("type");

        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(TransactionEdit.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.date.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
                            }
                        }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
                datePickerDialog.show();
            }
        });

        binding.btnAdd.setOnClickListener(v -> {
            String amountTrans = binding.amount.getText().toString().trim();

            if (amountTrans.length() <= 0) {
                return;
            }

            saveData(binding.amount.getText().toString(),
                    binding.categList.getText().toString(),
                    binding.date.getText().toString(),
                    binding.notes.getText().toString());
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

    private void saveData(String amount, String category, String date, String note){

        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = timestamp.format(time);

        Map<String, Object> trans = new HashMap<>();
        trans.put("amountTrans", amount);
        trans.put("category", category);
        trans.put("date", date);
        trans.put("note", note);
        trans.put("type", newType);
        trans.put("receipt", receiptUrl);
        trans.put("time", currentTime);

        if (idTrans!=null){
            firestore.collection("record").document(firebaseAuth.getUid())
                    .collection("transaction").document(idTrans)
                    .set(trans)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Add data...", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Try again...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}