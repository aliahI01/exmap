package com.example.exmap;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.exmap.databinding.ActivitySettingPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class SettingPage extends AppCompatActivity {

    ActivitySettingPageBinding binding;
    Switch night_switch, noti_switch;
    boolean nightMode;
    String idTrans;
    Uri imgUser = null;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        idTrans = firebaseUser.getUid();

        binding.email.findViewById(R.id.email);
        binding.website.findViewById(R.id.website);

        binding.homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingPage.this, HomePage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        binding.historyMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingPage.this, HistoryPage.class));
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
                finish();
            }
        });

        /* Note */
        binding.addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingPage.this, NotePage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        binding.viewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingPage.this, NoteShow.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                    Toast.makeText(SettingPage.this, "Add profile first than can view the data.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), SettingPage.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }
        });

        /* Feedback */
        binding.addFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingPage.this, FeedPage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        /* Night Mode */
        night_switch = findViewById(R.id.night_switch);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            night_switch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        night_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();
            }
        });

        /* Notification Switch */
        noti_switch = findViewById(R.id.noti_switch);

        noti_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingPage.this);
                if (!prefs.getBoolean("firstTime", false)) {
                    Toast.makeText(SettingPage.this, "Notification On", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingPage.this, NotiPage.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingPage.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 19);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 1);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,
                            pendingIntent);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstTime", true);
                    editor.apply();

                    if (alarmManager != null){
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }
        });

        /* About App */
        binding.aboutDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPage.this, AboutPage.class));
                overridePendingTransition(0, 0);
            }
        });

        binding.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("mailto:aliahizzati2701@gmail.com");
            }
        });

        binding.website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://tinyurl.com/yc4u7pex");
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder";
            String description = "Record your expenses and money you got";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void gotoUrl(String s){
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}