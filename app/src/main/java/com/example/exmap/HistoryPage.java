package com.example.exmap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exmap.databinding.ActivityHistoryPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryPage extends AppCompatActivity{

    ActivityHistoryPageBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String idTrans;
    List<TransactionModel> transactionModelArrayList = new ArrayList<>();
    TransactionAdapter transactionAdapter;
    Calendar calendar;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        idTrans = firebaseUser.getUid();

        transactionAdapter = new TransactionAdapter(getApplicationContext(), transactionModelArrayList);
        binding.expenseHist.setLayoutManager(new LinearLayoutManager(this));
        binding.expenseHist.setAdapter(transactionAdapter);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        transactionAdapter.setDialog(new TransactionAdapter.Dialog(){
            @Override
            public void onClick(int pos) {
                final CharSequence[] dialogItem = {"Show", "Edit", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(HistoryPage.this);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent2 = new Intent(getApplicationContext(), TransactionShow.class);
                                intent2.putExtra("id", transactionModelArrayList.get(pos).getId());
                                intent2.putExtra("amountTrans", transactionModelArrayList.get(pos).getAmountTrans());
                                intent2.putExtra("category", transactionModelArrayList.get(pos).getCategory());
                                intent2.putExtra("note", transactionModelArrayList.get(pos).getNote());
                                intent2.putExtra("type", transactionModelArrayList.get(pos).getType());
                                intent2.putExtra("date", transactionModelArrayList.get(pos).getDate());
                                intent2.putExtra("receipt", transactionModelArrayList.get(pos).getReceipt());
                                startActivity(intent2);
                                break;
                            case 1:
                                Intent intent = new Intent(getApplicationContext(), TransactionEdit.class);
                                intent.putExtra("id", transactionModelArrayList.get(pos).getId());
                                intent.putExtra("amountTrans", transactionModelArrayList.get(pos).getAmountTrans());
                                intent.putExtra("category", transactionModelArrayList.get(pos).getCategory());
                                intent.putExtra("note", transactionModelArrayList.get(pos).getNote());
                                intent.putExtra("type", transactionModelArrayList.get(pos).getType());
                                intent.putExtra("date", transactionModelArrayList.get(pos).getDate());
                                intent.putExtra("receipt", transactionModelArrayList.get(pos).getReceipt());
                                startActivity(intent);
                                break;
                            case 2:
                                deleteData(transactionModelArrayList.get(pos).getId());
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });

        binding.homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(HistoryPage.this, HomePage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        binding.settingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(HistoryPage.this, SettingPage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(HistoryPage.this, "Logout..", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(HistoryPage.this, TransactionPage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.getMessage();
                }
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

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void getData(){
        firestore.collection("record").document(firebaseAuth.getUid())
                .collection("transaction")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        transactionModelArrayList.clear();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                TransactionModel model = new TransactionModel(
                                        document.getString("amountTrans"), document.getString("category"),
                                        document.getString("note"), document.getString("type"),
                                        document.getString("date"), document.getString("receipt"), document.getString("time"));
                                model.setId(document.getId());
                                transactionModelArrayList.add(model);
                            }
                            transactionAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteData(String idTrans){
        firestore.collection("record").document(firebaseAuth.getUid())
                .collection("transaction").document(idTrans)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Data cannot be deleted", Toast.LENGTH_SHORT).show();
                        }
                        getData();
                    }
                });
    }
}