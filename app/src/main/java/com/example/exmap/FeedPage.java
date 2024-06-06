package com.example.exmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exmap.databinding.ActivityFeedPageBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FeedPage extends AppCompatActivity {

    ActivityFeedPageBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DocumentReference documentReference;
    FeedAdapter feedAdapter;
    String problem, explanation, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        problem = getIntent().getStringExtra("problem");
        explanation = getIntent().getStringExtra("explanation");
        docId = getIntent().getStringExtra("docId");

        binding.problem.setText(problem);
        binding.explanation.setText(explanation);

        binding.submitFeed.setOnClickListener((v) -> saveFeed());

        setupRecycleView();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedPage.this, SettingPage.class));
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

    void saveFeed(){
        String feedProblem = binding.problem.getText().toString();
        String feedExplain = binding.explanation.getText().toString();

        if (feedProblem == null || feedProblem.isEmpty()){
            binding.problem.setError("Title is required");
            return;
        }
        Feed feed = new Feed();
        feed.setProblem(feedProblem);
        feed.setExplanation(feedExplain);
        feed.setTimestamp(Timestamp.now());

        saveFeedToDB(feed);
    }

    void  saveFeedToDB(Feed feed){

        documentReference = Utility.cRefFeedback().document();

        documentReference.set(feed).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(FeedPage.this, "Add feedback..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedPage.this, "Try Again..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupRecycleView() {
        Query query = Utility.cRefFeedback().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Feed> options = new FirestoreRecyclerOptions.Builder<Feed>()
                .setQuery(query, Feed.class).build();
        binding.feedRView.setLayoutManager(new LinearLayoutManager(this));

        feedAdapter = new FeedAdapter(options, this);
        binding.feedRView.setAdapter(feedAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        feedAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        feedAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        feedAdapter.notifyDataSetChanged();
    }
}