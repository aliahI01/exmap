package com.example.exmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotiPage extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyTrans")
                .setSmallIcon(R.mipmap.app_logo)
                .setContentTitle("ExMaP")
                .setContentText("Hye user. Have you record your expense or many you get today?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
