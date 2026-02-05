package com.example.mypalmsmart;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Activity_Krani_Report extends AppCompatActivity {

    private static final String TAG = "KraniReportActivity";
    // Kita tetap menggunakan URL update yang sama
    private static final String UPDATE_URL = "https://masterly-irreplaceable-emerson.ngrok-free.dev/laporan/update";

    private TextView tvCatatanLaporanId;
    private EditText etCatatanPenolakan;
    private Button btnKirimPenolakan;
    private int laporanId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_krani_report);

        // Terima ID laporan yang dikirim dari DetailPemanenActivity
        Intent intent = getIntent();
        laporanId = intent.getIntExtra("LAPORAN_ID", -1);

    }

}
