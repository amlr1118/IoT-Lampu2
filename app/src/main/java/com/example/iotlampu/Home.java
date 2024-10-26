package com.example.iotlampu;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.StringJoiner;

public class Home extends AppCompatActivity {

    //private TextView textView;

    private TextView tVLmpAktif1;
    private TextView tVLmpMati1;

    private ImageView imgVSuara;

    private ToggleButton tglLampu1;
    private ToggleButton tglLampu2;
    private ToggleButton tglLampu3;
    private ToggleButton tglLampu4;

    private String id;

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

        id = "1";

        tVLmpAktif1 = findViewById(R.id.tVLmpAktif1);
        tVLmpMati1 = findViewById(R.id.tVLmpMati1);

        imgVSuara = findViewById(R.id.imgVSuara);

        tglLampu1 = findViewById(R.id.tglLampu1);
        tglLampu2 = findViewById(R.id.tglLampu2);
        tglLampu3 = findViewById(R.id.tglLampu3);
        //tglLampu4 = findViewById(R.id.tglLampu4);


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

        tVLmpAktif1.setOnClickListener(view -> {
            showTimePickerDialog();
        });

        tVLmpMati1.setOnClickListener(view -> {
            showTimePickerDialog();
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
                        // Format waktu yang dipilih ke dalam format HH:mm dan tampilkan di TextView
                        tVLmpAktif1.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true); // True untuk format 24 jam, false untuk 12 jam AM/PM
        timePickerDialog.show();
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
            }else {
                Toast.makeText(this, "Perintah tidak dikenali !", Toast.LENGTH_LONG).show();
            }

        }
    }
}