package com.example.mypalmsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class EditLaporanActivity extends AppCompatActivity {
    private static final String TAG = "EditLaporanActivity";
    // URL untuk mengirim data yang sudah di-update
    private static final String UPDATE_URL = "https://masterly-irreplaceable-emerson.ngrok-free.dev/laporan/update";
    // Nama unik untuk sinyal broadcast
    public static final String ACTION_LAPORAN_UPDATED = "com.example.mypalmsmart.LAPORAN_UPDATED";

    private int laporanId;
    private TextView tvEditNama, tvEditTanggal;
    private EditText etEditJumlahTandan, etEditAreaBlok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pastikan menggunakan layout yang benar
        setContentView(R.layout.activity_edit_laporan);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Laporan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ================================================================
        // ===  INISIALISASI UI DENGAN ID YANG BENAR DARI activity_edit_laporan.xml  ===
        // ================================================================
        tvEditNama = findViewById(R.id.tvEditNama);
        tvEditTanggal = findViewById(R.id.tvEditTanggal);
        etEditJumlahTandan = findViewById(R.id.etEditJumlahTandan);
        etEditAreaBlok = findViewById(R.id.etEditAreaBlok);
        Button btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan);
        // ================================================================

        // Ambil data yang dikirim dari DetailPemanenActivity
        Intent intent = getIntent();
        laporanId = intent.getIntExtra("LAPORAN_ID", -1);

        if (laporanId == -1) {
            Toast.makeText(this, "Error: ID Laporan tidak ditemukan.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Isi field dengan data lama yang diterima dari intent
        tvEditNama.setText(intent.getStringExtra("LAPORAN_NAMA"));
        tvEditTanggal.setText(intent.getStringExtra("LAPORAN_TANGGAL"));
        etEditJumlahTandan.setText(String.valueOf(intent.getIntExtra("LAPORAN_JUMLAH_TANDAN", 0)));
        etEditAreaBlok.setText(intent.getStringExtra("LAPORAN_AREA_BLOK"));

        // Atur listener untuk tombol "Simpan Perubahan"
        btnSimpanPerubahan.setOnClickListener(v -> simpanPerubahan());
    }

    private void simpanPerubahan() {
        // Ambil nilai baru dari EditText
        String jumlahTandanBaru = etEditJumlahTandan.getText().toString();
        String areaBlokBaru = etEditAreaBlok.getText().toString();
        String statusBaru = "Diupdate"; // Status baru!

        Log.d(TAG, "Menyimpan perubahan untuk ID: " + laporanId);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.PUT, UPDATE_URL,
                response -> {
                    Toast.makeText(this, "Laporan berhasil diupdate!", Toast.LENGTH_LONG).show();
                    // Kirim sinyal broadcast agar KraniActivity dan MandorActivity me-refresh daftarnya
                    sendBroadcast(new Intent(ACTION_LAPORAN_UPDATED));

                    // Kembali ke halaman daftar Krani (membersihkan tumpukan activity di atasnya)
                    Intent intent = new Intent(this, KraniActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish(); // Tutup activity ini dan activity detail sebelumnya
                },
                error -> {
                    Log.e(TAG, "Volley Error: " + error.toString());
                    Toast.makeText(this, "Gagal mengupdate laporan. Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Kirim semua data yang diperlukan server
                params.put("id", String.valueOf(laporanId));
                params.put("jumlah_tandan", jumlahTandanBaru);
                params.put("area_blok", areaBlokBaru);
                params.put("status", statusBaru); // Kirim status baru "Diupdate"
                return params;
            }
        };
        queue.add(request);
    }

    // Fungsi untuk tombol kembali di action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Kembali ke halaman sebelumnya (DetailPemanenActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
