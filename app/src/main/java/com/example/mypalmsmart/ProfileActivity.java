// =========================================================================
// ===       FILE ProfileActivity.java - VERSI FINAL TANPA IMPORT R      ===
// =========================================================================
package com.example.mypalmsmart;

// PERHATIKAN: TIDAK ADA 'import android.R;' DI SINI
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // Pastikan import MenuItem yang benar
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // 'R' ini sekarang akan menunjuk ke package Anda

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profil Pengguna");
        }

        // Sekarang pencarian ini akan berhasil
        TextView tvProfileUsername = findViewById(R.id.tvProfileUsername);
        TextView tvProfileRole = findViewById(R.id.tvProfileRole);
        Button btnLogout = findViewById(R.id.btnLogout);

        // ... sisa kode Anda sudah benar ...
        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("EXTRA_USERNAME");
            String role = intent.getStringExtra("EXTRA_ROLE");

            Log.d(TAG, "Data diterima: Nama=" + username + ", Peran=" + role);

            tvProfileUsername.setText(username != null ? username : "Tidak Dikenal");
            tvProfileRole.setText(role != null ? role.substring(0, 1).toUpperCase() + role.substring(1) : "Peran Tidak Diketahui");

        } else {
            Log.w(TAG, "Intent kosong, tidak ada data yang diterima.");
        }

        btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "Tombol Logout diklik.");
            WebSocketManager.getInstance().stop();

            Intent logoutIntent = new Intent(ProfileActivity.this, MainActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Khusus untuk ID 'home' (tombol kembali), kita pakai android.R
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
