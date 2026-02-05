package com.example.mypalmsmart;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Map;
import java.util.HashMap;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private TextView tvCurrentDate;
    private EditText etNama, etJumlahTandan, etAreaBlok;
    private Spinner spinnerMandor, spinnerJobdesk;
    private Button btnSubmitLaporan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Inisialisasi komponen UI
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        etNama = findViewById(R.id.etNama);
        etJumlahTandan = findViewById(R.id.etJumlahTandan);
        etAreaBlok = findViewById(R.id.etAreaBlok);
        spinnerMandor = findViewById(R.id.spinnerMandor);
        spinnerJobdesk = findViewById(R.id.spinnerJobdesk);
        btnSubmitLaporan = findViewById(R.id.btnSubmitLaporan);

        // Mengatur tanggal hari ini
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText(currentDate);

        btnSubmitLaporan.setOnClickListener(v -> simpanLaporan());
    }

    private void simpanLaporan() {
        // Bagian ini sudah benar dan tidak perlu diubah
        String tanggal = tvCurrentDate.getText().toString();
        String nama = etNama.getText().toString().trim();
        String jumlahTandanStr = etJumlahTandan.getText().toString().trim();
        String areaBlok = etAreaBlok.getText().toString().trim();
        String mandor = spinnerMandor.getSelectedItem().toString();
        String jobdesk = spinnerJobdesk.getSelectedItem().toString();
        String status = "Menunggu";

        // Validasi juga sudah benar
        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(jumlahTandanStr) || TextUtils.isEmpty(areaBlok)) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // =========================================================================
        // === GANTI TOTAL BLOK LOGIKA LAMA ANDA DENGAN BLOK VOLLEY BARU DI BAWAH ===
        // =========================================================================

        // 1. Definisikan URL untuk API pengiriman laporan di server Anda
        String url = "https://masterly-irreplaceable-emerson.ngrok-free.dev/laporan"; // <-- INI BENAR
        // URL ini dari server.js

        // 2. Siapkan antrian Volley
        // 2. Siapkan antrian Volley menggunakan Application Context untuk stabilitas
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


        // 3. Buat request POST baru
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Blok ini dieksekusi jika server merespons 'sukses'
                    Toast.makeText(ReportActivity.this, "Laporan berhasil dikirim ke server!", Toast.LENGTH_LONG).show();
                    // Selesai, kembali ke halaman sebelumnya
                    finish();
                },
                error -> {
                    // Blok ini dieksekusi jika terjadi error
                    android.util.Log.e("ReportActivity", "Volley Error: " + error.toString());
                    Toast.makeText(ReportActivity.this, "Gagal mengirim laporan. Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tanggal", tanggal);
                params.put("nama", nama);
                params.put("jumlah_tandan", jumlahTandanStr); // <-- BENAR (pakai underscore)
                params.put("area_blok", areaBlok);           // <-- BENAR (pakai underscore)
                params.put("mandor", mandor);
                params.put("jobdesk", jobdesk);
                params.put("status", status);
                return params;
            }

        };

        // 4. Jalankan request
        queue.add(stringRequest);

        // PASTIKAN SEMUA LOGIKA LAMA (dbHelper.addLaporan, WebSocketManager.sendMessage, dll.)
        // SUDAH TIDAK ADA LAGI DI DALAM METODE simpanLaporan() INI.
    }
}
