package com.example.iotlampu;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LampControlReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals("lampu1_aktif")) {
                Intent broadcastIntent = new Intent("ACTION_LAMPU_STATUS");
                broadcastIntent.putExtra("status", "Lampu 1 aktif");
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            } else if (action.equals("lampu1_mati")) {
                Intent broadcastIntent = new Intent("ACTION_LAMPU_STATUS");
                broadcastIntent.putExtra("status", "Lampu 1 mati");
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            } else if (action.equals("lampu2_aktif")) {
                Intent broadcastIntent = new Intent("ACTION_LAMPU_STATUS");
                broadcastIntent.putExtra("status", "Lampu 2 aktif");
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            } else if (action.equals("lampu2_mati")) {
                Intent broadcastIntent = new Intent("ACTION_LAMPU_STATUS");
                broadcastIntent.putExtra("status", "Lampu 2 mati");
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            }
            else if (action.equals("lampu3_aktif")) {
                Intent broadcastIntent = new Intent("ACTION_LAMPU_STATUS");
                broadcastIntent.putExtra("status", "Lampu 3 aktif");
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            }
            else if (action.equals("lampu3_mati")) {
                Intent broadcastIntent = new Intent("ACTION_LAMPU_STATUS");
                broadcastIntent.putExtra("status", "Lampu 3 mati");
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            }
        }
    }
}