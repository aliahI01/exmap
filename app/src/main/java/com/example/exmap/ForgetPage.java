package com.example.exmap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exmap.databinding.ActivityForgetPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPage extends AppCompatActivity {

    ActivityForgetPageBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        binding.loginAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPage.this, MainActivity.class));
            }
        });
    }

    private void resetPassword() {
        String emailUser = binding.email.getText().toString().trim();

        if (emailUser.isEmpty()) {
            binding.email.setError("Email is required");
            binding.email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()){
            binding.email.setError("Invalid email");
            binding.email.requestFocus();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(emailUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPage.this, "Check email to reset your password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgetPage.this, "Try again. Something when wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}