package com.example.exmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.exmap.databinding.ActivityHomePageBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomePage extends AppCompatActivity {

    ActivityHomePageBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    HomeAdapter homeAdapter;
    String idTrans;
    String monthNow;
    String calendarNow;
    String currentDate;
    DateFormat dateFormat;
    Date date;
    double earn = 0.0;
    double exp = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        idTrans = firebaseUser.getUid();

        Calendar calendar = Calendar.getInstance();
        Date d = new Date();
        calendarNow = String.valueOf(calendar.get(Calendar.MONTH+1));
        monthNow = String.valueOf(d.getMonth()+1);
        currentDate = String.valueOf(Timestamp.now());

        dateFormat = new SimpleDateFormat("M");
        date = new Date();

//        maxExpense();
        minExpense();
//        maxEarn();
        minEarn();

        binding.addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(HomePage.this, TransactionPage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

        binding.historyMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(HomePage.this, HistoryPage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                    Toast.makeText(HomePage.this, "No data..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.settingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(HomePage.this, SettingPage.class));
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
                Toast.makeText(HomePage.this, "Logout..", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        setupRecycleView();
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

//    private void maxExpense() {
//
//        Utility.trans()
//                .whereEqualTo("type", "Expense")
//                .orderBy("amountTrans")
//                .limit(1)
//                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

//                    String amountTrans = "";
//                    String category = "";
//
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
//                        String data = "";
//                        for (DocumentSnapshot documentSnapshot : snapshots){
//                            TransactionModel model = documentSnapshot.toObject(TransactionModel.class);
//                            model.setId(documentSnapshot.getId());
//                            String amountTrans = model.getAmountTrans();
//                            String category = model.getCategory();
//                            amountTrans = documentSnapshot.getString("amountTrans");
//                            category = documentSnapshot.getString("category");
//                            data += "Maximum Expense : " + amountTrans + "\n" + category + "\n";
//                        }
//                        binding.graph.setText("Month: " + data);
//                        binding.maxExpense.setText(data);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });
//    }

    private void minExpense() {
        Utility.trans()
                .whereEqualTo("type", "Expense")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    String amountTrans = "";
                    String category = "";

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : snapshots){
                            amountTrans = documentSnapshot.getString("amountTrans");
                            category = documentSnapshot.getString("category");
                        }
                        binding.minExpense.setText("Latest Expense : " + amountTrans + "\n" + category);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
//
//    private void maxEarn() {
//
//        Utility.trans()
//                .whereEqualTo("type", "Earning")
//                .orderBy("amountTrans", Query.Direction.DESCENDING)
//                .limit(1)
//                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//
//                    String amountTrans = "";
//                    String category = "";
//
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
//                        for (DocumentSnapshot documentSnapshot : snapshots){
//                            amountTrans = documentSnapshot.getString("amountTrans");
//                            category = documentSnapshot.getString("category");
//                        }
//                        binding.graph.setText(amountTrans);
//                        binding.maxIncome.setText("Maximum Earning : " + amountTrans + "\n" + category);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });
//    }

    private void minEarn() {
        Utility.trans()
                .whereEqualTo("type", "Earning")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    String amountTrans = "";
                    String category = "";

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            amountTrans = documentSnapshot.getString("amountTrans");
                            category = documentSnapshot.getString("category");
                        }
                        binding.minIncome.setText("Latest Earning: " + amountTrans + "\n" + category);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void setupRecycleView() {
        Query query = Utility.trans().orderBy("category");
        FirestoreRecyclerOptions<TransactionModel> options = new FirestoreRecyclerOptions.Builder<TransactionModel>()
                .setQuery(query, TransactionModel.class).build();
        GridLayoutManager gridLayoutManager = new GridLayoutManager( this, 2);
        binding.categView.setLayoutManager(gridLayoutManager);

        homeAdapter = new HomeAdapter(options, this);
        binding.categView.setAdapter(homeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        homeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        homeAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        earn = 0.0;
        exp = 0.0;
        graphData();
        homeAdapter.notifyDataSetChanged();
    }
    private void graphData() {

        firestore.collection("record").document(firebaseAuth.getUid())
                .collection("transaction")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            TransactionModel transactionModel = documentSnapshot.toObject(TransactionModel.class);

                            if (transactionModel.getType().equals("Earning")){
                                earn += Double.parseDouble(transactionModel.getAmountTrans());
                            } else {
                                exp += Double.parseDouble(transactionModel.getAmountTrans());
                            }
                        }
                        graphExpense();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void graphExpense() {

        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();

        if (earn != 0){
            pieEntryList.add(new PieEntry(Float.parseFloat(String.valueOf(earn)), "Earning"));
            colorList.add(getResources().getColor(R.color.hijaupastel));
        }
        if (exp != 0){
            pieEntryList.add(new PieEntry(Float.parseFloat(String.valueOf(exp)), "Expense"));
            colorList.add(getResources().getColor(R.color.scholar));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, String.valueOf(earn = exp));
        pieDataSet.setColors(colorList);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.hijausea));
        PieData pieData = new PieData(pieDataSet);

        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();
    }

//    private void categAmount() {
//        firestore.collection("record").document(firebaseAuth.getUid())
//                .collection("transaction")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    double sumCateg = 0.0;
//
//                    String pattern = "##,###.##";
//                    DecimalFormat decimalFormat = new DecimalFormat(pattern);
//                    String format;
//
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for (DocumentSnapshot documentSnapshot : task.getResult()){
//                            String amountTrans = documentSnapshot.getString("amountTrans");
//                            String category = documentSnapshot.getString("category");
//                            sumCateg += Double.parseDouble(String.valueOf(amountTrans));
//                        }
//                        binding.amountShow.setText(String.valueOf(decimalFormat.format(sumTrans)));
//                    }
//                });
//    }

}