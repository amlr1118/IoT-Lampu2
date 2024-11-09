package com.example.iotlampu;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.StringJoiner;

public class Home extends AppCompatActivity {

    //private TextView textView;

    private TextView tVLmpAktif1;
    private TextView tVLmpMati1;

    private TextView tVLmpAktif2;
    private TextView tVLmpMati2;

    private TextView tVLmpAktif3;
    private TextView tVLmpMati3;

    private ImageView imgVSuara;

    private ToggleButton tglLampu1;
    private ToggleButton tglLampu2;
    private ToggleButton tglLampu3;
    private ToggleButton tglLampu4;

    private String id;
    private String onof;

    private BroadcastReceiver lampuStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            if (status != null) {
                if (status.equals("Lampu 1 aktif")){
                    setRelay1(1);
                }else if (status.equals("Lampu 1 mati")){
                    setRelay1(0);
                }
                else if (status.equals("Lampu 2 aktif")){
                    setRelay2(1);
                }

                else if (status.equals("Lampu 2 mati")){
                    setRelay2(0);
                }

                else if (status.equals("Lampu 3 aktif")){
                    setRelay3(1);
                }else if (status.equals("Lampu 3 mati")){
                    setRelay3(0);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(lampuStatusReceiver,
                new IntentFilter("ACTION_LAMPU_STATUS"));


        id = "1";

        tVLmpAktif1 = findViewById(R.id.tVLmpAktif1);
        tVLmpMati1 = findViewById(R.id.tVLmpMati1);

        tVLmpAktif2 = findViewById(R.id.tVLmpAktif2);
        tVLmpMati2 = findViewById(R.id.tVLmpMati2);

        tVLmpAktif3 = findViewById(R.id.tVLmpAktif3);
        tVLmpMati3 = findViewById(R.id.tvLmpMati3);

        imgVSuara = findViewById(R.id.imgVSuara);

        tglLampu1 = findViewById(R.id.tglLampu1);
        tglLampu2 = findViewById(R.id.tglLampu2);
        tglLampu3 = findViewById(R.id.tglLampu3);



        tVLmpAktif1.setOnClickListener(v -> {
            onof = "aktif1";
            showTimePickerDialog();

        });

        tVLmpMati1.setOnClickListener(v -> {
            onof = "mati1";
            showTimePickerDialog();

        });

        tVLmpAktif2.setOnClickListener(v -> {
            onof = "aktif2";
            showTimePickerDialog();

        });

        tVLmpMati2.setOnClickListener(v -> {
            onof = "mati2";
            showTimePickerDialog();

        });

        tVLmpAktif3.setOnClickListener(v -> {
            onof = "aktif3";
            showTimePickerDialog();

        });

        tVLmpMati3.setOnClickListener(v -> {
            onof = "mati3";
            showTimePickerDialog();

        });

        loadSavedTime();

        tglLampu1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tglLampu1.isChecked()){
                    setRelay1(1);
                }else {
                    setRelay1(0);
                }
            }
        });

        tglLampu2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tglLampu2.isChecked()){
                    setRelay2(1);
                }else {
                    setRelay2(0);
                }
            }
        });

        tglLampu3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tglLampu3.isChecked()){
                    setRelay3(1);
                }else {
                    setRelay3(0);
                }
            }
        });



        imgVSuara.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Mulai berbicara..."); // Ubah prompt ke Bahasa Indonesia
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID"); // Atur bahasa ke Bahasa Indonesia
            startActivityForResult(intent, 100);
        });

        cekStatusRelay1();
        cekStatusRelay2();
        cekStatusRelay3();
    }

    private void loadSavedTime() {
        SharedPreferences sharedPref = getSharedPreferences("LampPreferences", MODE_PRIVATE);

        // Memuat waktu yang disimpan
        String aktifTime1 = sharedPref.getString("lampu1_aktif", "Waktu Belum On diatur");
        String matiTime1 = sharedPref.getString("lampu1_mati", "Waktu Belum Off diatur");

        String aktifTime2 = sharedPref.getString("lampu2_aktif", "Waktu Belum On diatur");
        String matiTime2 = sharedPref.getString("lampu2_mati", "Waktu Belum Off diatur");

        String aktifTime3 = sharedPref.getString("lampu3_aktif", "Waktu Belum On diatur");
        String matiTime3 = sharedPref.getString("lampu3_mati", "Waktu Belum Off diatur");

        tVLmpAktif1.setText(aktifTime1);
        tVLmpMati1.setText(matiTime1);

        tVLmpAktif2.setText(aktifTime2);
        tVLmpMati2.setText(matiTime2);

        tVLmpAktif3.setText(aktifTime3);
        tVLmpMati3.setText(matiTime3);

        // Jika waktu telah diatur, maka jadwalkan ulang alarm
        if (!aktifTime1.equals("Waktu Belum On diatur")) {
            String[] timeParts = aktifTime1.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            scheduleAlarm("lampu1_aktif", hour, minute);
        }
        if (!matiTime1.equals("Waktu Belum Off diatur")) {
            String[] timeParts = matiTime1.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            scheduleAlarm("lampu1_mati", hour, minute);
        }

        // Jika waktu telah diatur, maka jadwalkan ulang alarm
        if (!aktifTime2.equals("Waktu Belum On diatur")) {
            String[] timeParts = aktifTime2.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            scheduleAlarm("lampu2_aktif", hour, minute);
        }
        if (!matiTime2.equals("Waktu Belum Off diatur")) {
            String[] timeParts = matiTime2.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            scheduleAlarm("lampu2_mati", hour, minute);
        }

        // Jika waktu telah diatur, maka jadwalkan ulang alarm
        if (!aktifTime3.equals("Waktu Belum On diatur")) {
            String[] timeParts = aktifTime3.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            scheduleAlarm("lampu3_aktif", hour, minute);
        }
        if (!matiTime3.equals("Waktu Belum Off diatur")) {
            String[] timeParts = matiTime3.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            scheduleAlarm("lampu3_mati", hour, minute);
        }
    }

    private void scheduleAlarm(String action, int hour, int minute) {
        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        Intent intent = new Intent(this, LampControlReceiver.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);


        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void showTimePickerDialog() {
        // Ambil waktu saat ini
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Tampilkan TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(Home.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String key = "";
                        SharedPreferences sharedPref = getSharedPreferences("LampPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        // Format waktu yang dipilih ke dalam format HH:mm dan tampilkan di TextView
                        if (onof.equals("aktif1")){
                            tVLmpAktif1.setText(time);
                            key = "lampu1_aktif";
                            editor.putString("lampu1_aktif", time);
                            editor.apply();
                            //log.d("La Idu", "Waktu aktif disimpan: " + time);
                        }else if (onof.equals("mati1")){
                            tVLmpMati1.setText(time);
                            key = "lampu1_mati";
                            editor.putString("lampu1_mati", time);
                            editor.apply();
                        }else if (onof.equals("aktif2")){
                            tVLmpAktif2.setText(time);
                            key = "lampu2_aktif";
                            editor.putString("lampu2_aktif", time);
                            editor.apply();

                        }else if (onof.equals("mati2")){
                            tVLmpMati2.setText(time);
                            key = "lampu2_mati";
                            editor.putString("lampu2_mati", time);
                            editor.apply();
                        }

                        else if (onof.equals("aktif3")){
                            tVLmpAktif3.setText(time);
                            key = "lampu2_aktif";
                            editor.putString("lampu3_aktif", time);
                            editor.apply();

                        }else if (onof.equals("mati3")){
                            tVLmpMati3.setText(time);
                            key = "lampu2_mati";
                            editor.putString("lampu3_mati", time);
                            editor.apply();
                        }

                        scheduleAlarm(key, hourOfDay, minute);
                    }
                }, hour, minute, true); // True untuk format 24 jam, false untuk 12 jam AM/PM
        timePickerDialog.show();
    }

    private void cekStatusRelay1(){
        String url = getString(R.string.api_server)+"/baca-relay1";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            try {
                                // Mengambil response sebagai string dan mengonversinya menjadi integer
                                String responseString = http.getResponse();
                                int relay = Integer.parseInt(responseString.trim());

                                if (relay > 0 ){
                                    tglLampu1.setChecked(true);
                                }else{
                                    tglLampu1.setChecked(false);
                                }

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(Home.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void cekStatusRelay2(){
        String url = getString(R.string.api_server)+"/baca-relay2";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            try {
                                // Mengambil response sebagai string dan mengonversinya menjadi integer
                                String responseString = http.getResponse();
                                int relay = Integer.parseInt(responseString.trim());

                                if (relay > 0 ){
                                    tglLampu2.setChecked(true);
                                }else{
                                    tglLampu2.setChecked(false);
                                }

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(Home.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void cekStatusRelay3(){
        String url = getString(R.string.api_server)+"/baca-relay3";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            try {
                                // Mengambil response sebagai string dan mengonversinya menjadi integer
                                String responseString = http.getResponse();
                                int relay = Integer.parseInt(responseString.trim());

                                if (relay > 0 ){
                                    tglLampu3.setChecked(true);
                                }else{
                                    tglLampu3.setChecked(false);
                                }

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(Home.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void setRelay1(int relay){
        JSONObject params = new JSONObject();
        try {
            params.put("relay1", relay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String data = params.toString();
        String url = getString(R.string.api_server)+"/update-relay1/"+id;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.setMethod("put");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            if (relay > 0 ){
                                tglLampu1.setChecked(true);

                            }else{
                                tglLampu1.setChecked(false);
                            }
                            try {
                                JSONObject response = new JSONObject(http.getResponse());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (code == 422) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                                //Toast.makeText(Register.this, ""+msg, Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else if (code == 401) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();


    }

    private void setRelay2(int relay){
        JSONObject params = new JSONObject();
        try {
            params.put("relay2", relay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String data = params.toString();
        String url = getString(R.string.api_server)+"/update-relay2/"+id;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.setMethod("put");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            if (relay > 0 ){
                                tglLampu2.setChecked(true);

                            }else{
                                tglLampu2.setChecked(false);
                            }
                            try {
                                JSONObject response = new JSONObject(http.getResponse());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (code == 422) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                                //Toast.makeText(Register.this, ""+msg, Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else if (code == 401) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();


    }

    private void setRelay3(int relay){
        JSONObject params = new JSONObject();
        try {
            params.put("relay3", relay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String data = params.toString();
        String url = getString(R.string.api_server)+"/update-relay3/"+id;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.setMethod("put");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            if (relay > 0 ){
                                tglLampu3.setChecked(true);

                            }else{
                                tglLampu3.setChecked(false);
                            }
                            try {
                                JSONObject response = new JSONObject(http.getResponse());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (code == 422) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                                //Toast.makeText(Register.this, ""+msg, Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else if (code == 401) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();


    }

    private void alertFail(String s) {
        new AlertDialog.Builder(this)
                .setTitle("Failed")
                .setIcon(R.drawable.baseline_error_24)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            String text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);

            // Tampilkan hasil teks di TextView


            // Ganti kata numerik dengan angka untuk penanganan yang lebih konsisten
            text = text.toLowerCase(); // Pastikan teks semua lowercase
            text = text.replace("satu", "1")
                    .replace("dua", "2")
                    .replace("tiga", "3")
                    .replace("empat", "4");

            // Periksa teks dan aktifkan lampu yang sesuai
            if (text.equals("lampu 1 aktif")){
                tglLampu1.setChecked(true);
            }else if (text.equals("lampu 1 mati")){
                tglLampu1.setChecked(false);
            }else if (text.equals("lampu 2 aktif")){
                tglLampu2.setChecked(true);
            } else if (text.equals("lampu 2 mati")){
                tglLampu2.setChecked(false);
            } else if (text.equals("lampu 3 aktif")){
                tglLampu3.setChecked(true);
            } else if (text.equals("lampu 3 mati")){
                tglLampu3.setChecked(false);
            }else if (text.equals("lampu 1 dan 2 aktif")){
                tglLampu1.setChecked(true);
                tglLampu2.setChecked(true);
            }else if (text.equals("lampu 1 dan 2 mati")){
                tglLampu1.setChecked(false);
                tglLampu2.setChecked(false);
            }else if (text.equals("lampu 1 dan 3 aktif")){
                tglLampu1.setChecked(true);
                tglLampu3.setChecked(true);
            }else if (text.equals("lampu 1 dan 3 mati")){
                tglLampu1.setChecked(false);
                tglLampu3.setChecked(false);
            }else if (text.equals("lampu 2 dan 3 aktif")){
                tglLampu2.setChecked(true);
                tglLampu3.setChecked(true);
            }else if (text.equals("lampu 2 dan 3 mati")){
                tglLampu2.setChecked(false);
                tglLampu3.setChecked(false);
            }else if (text.equals("semua lampu aktif")){
                tglLampu1.setChecked(true);
                tglLampu2.setChecked(true);
                tglLampu3.setChecked(true);
            }else if (text.equals("semua lampu mati")){
                tglLampu1.setChecked(false);
                tglLampu2.setChecked(false);
                tglLampu3.setChecked(false);
            }
            else {
                Toast.makeText(this, "Perintah tidak dikenali !", Toast.LENGTH_LONG).show();
            }



        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Batalkan pendaftaran receiver saat activity dihancurkan
        LocalBroadcastManager.getInstance(this).unregisterReceiver(lampuStatusReceiver);
    }
}