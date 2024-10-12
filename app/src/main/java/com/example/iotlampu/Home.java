package com.example.iotlampu;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        tVLmpAktif1 = findViewById(R.id.tVLmpAktif1);
        tVLmpMati1 = findViewById(R.id.tVLmpMati1);

        imgVSuara = findViewById(R.id.imgVSuara);

        tglLampu1 = findViewById(R.id.tglLampu1);
        tglLampu2 = findViewById(R.id.tglLampu2);
        tglLampu3 = findViewById(R.id.tglLampu3);
        tglLampu4 = findViewById(R.id.tglLampu4);


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
            } else if (text.equals("lampu 2 aktif")){
                tglLampu2.setChecked(true);
            } else if (text.equals("lampu 3 aktif")){
                tglLampu3.setChecked(true);
            } else if (text.equals("lampu 4 aktif")){
                tglLampu4.setChecked(true);
            }else {
                Toast.makeText(this, "Perintah tidak dikenali !", Toast.LENGTH_LONG).show();
            }

            if (text.equals("lampu 1 mati")){
                tglLampu1.setChecked(false);
            } else if (text.equals("lampu 2 mati")){
                tglLampu2.setChecked(false);
            } else if (text.equals("lampu 3 mati")){
                tglLampu3.setChecked(false);
            } else if (text.equals("lampu 4 mati")){
                tglLampu4.setChecked(false);
            }
        }
    }
}