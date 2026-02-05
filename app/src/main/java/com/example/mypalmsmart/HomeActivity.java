// =========================================================================
// ===        FILE HomeActivity.java - VERSI FINAL YANG BENAR            ===
// =========================================================================
package com.example.mypalmsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    // Variabel untuk menyimpan data pengguna yang sedang login
    private String currentUsername;
    private String currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Memulai koneksi WebSocket saat pemanen login
        WebSocketManager.getInstance().start();

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        Button btnReport = findViewById(R.id.btnReport);

        Button btnProfile = findViewById(R.id.btnProfile);

        // Langkah 1: Terima data dari MainActivity
        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            currentUsername = incomingIntent.getStringExtra("EXTRA_USERNAME");
            currentUserRole = incomingIntent.getStringExtra("EXTRA_ROLE");
            Log.d(TAG, "Menerima data pengguna: Nama=" + currentUsername + ", Peran=" + currentUserRole);
        }

        // Tampilkan pesan selamat datang
        if (currentUsername != null && !currentUsername.isEmpty()) {
            tvWelcome.setText("Selamat datang, " + currentUsername + "!");
        } else {
            tvWelcome.setText("Selamat datang!");
            Log.w(TAG, "Username tidak diterima dari MainActivity.");
        }

        // --- OnClickListener untuk tombol-tombol ---

        // Tombol Buat Laporan
        btnReport.setOnClickListener(v -> {
            Intent reportIntent = new Intent(HomeActivity.this, ReportActivity.class);
            // Kirim juga nama pemanen ke ReportActivity agar otomatis terisi
            reportIntent.putExtra("EXTRA_NAMA_PEMANEN", currentUsername);
            startActivity(reportIntent);
        });

        // Tombol Profil (INI BAGIAN PENTINGNYA)
        btnProfile.setOnClickListener(v -> {
            Log.d(TAG, "Tombol Profil diklik. Mengirim data ke ProfileActivity.");
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            // Langkah 2: Kirim data yang sudah disimpan ke ProfileActivity
            profileIntent.putExtra("EXTRA_USERNAME", currentUsername);
            profileIntent.putExtra("EXTRA_ROLE", currentUserRole);
            startActivity(profileIntent);
        });
    }
}
