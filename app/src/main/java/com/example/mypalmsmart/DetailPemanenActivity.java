package com.example.mypalmsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DetailPemanenActivity extends AppCompatActivity {

    private static final String TAG = "DetailPemanenActivity";
    private static final String UPDATE_URL = "https://masterly-irreplaceable-emerson.ngrok-free.dev/laporan/update"; // URL API untuk update status

    private TextView tvIdLaporan, tvNamaPemanenDetail, tvTanggalDetail, tvJumlahTandanDetail, tvAreaBlokDetail, tvStatusDetail;
    private Button btnTerima, btnTolak;

    private int laporanId; // Untuk menyimpan ID laporan yang sedang dilihat

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pemanen);

        // Inisialisasi semua komponen UI
        tvIdLaporan = findViewById(R.id.tvIdLaporan);
        tvNamaPemanenDetail = findViewById(R.id.tvNamaPemanenDetail);
        tvTanggalDetail = findViewById(R.id.tvTanggalDetail);
        tvJumlahTandanDetail = findViewById(R.id.tvJumlahTandanDetail);
        tvAreaBlokDetail = findViewById(R.id.tvAreaBlokDetail);
        tvStatusDetail = findViewById(R.id.tvStatusDetail);
        btnTerima = findViewById(R.id.btnTerima);
        btnTolak = findViewById(R.id.btnTolak);

        // Terima data yang dikirim dari LaporanAdapter
        Intent intent = getIntent();
        if (intent != null) {
            laporanId = intent.getIntExtra("LAPORAN_ID", -1);
            String nama = intent.getStringExtra("LAPORAN_NAMA");
            String tanggal = intent.getStringExtra("LAPORAN_TANGGAL");
            int jumlahTandan = intent.getIntExtra("LAPORAN_JUMLAH_TANDAN", 0);
            String areaBlok = intent.getStringExtra("LAPORAN_AREA_BLOK");
            String status = intent.getStringExtra("LAPORAN_STATUS");

            // Tampilkan data ke UI
            tvIdLaporan.setText(String.valueOf(laporanId));
            tvNamaPemanenDetail.setText(nama);
            tvTanggalDetail.setText(tanggal);
            tvJumlahTandanDetail.setText(String.format("%d Tandan", jumlahTandan));
            tvAreaBlokDetail.setText(areaBlok);
            tvStatusDetail.setText(status);

            // Atur warna status
            updateStatusView(status);
        }

        // Pasang listener untuk tombol
        btnTerima.setOnClickListener(v -> {
            if (laporanId != -1) {
                updateStatusLaporan(laporanId, "Diterima");
            }
        });

        // === KODE BARU: Tombol Tolak sekarang menjadi Tombol Edit ===
        btnTolak.setText("Edit Laporan"); // Ubah teks tombol secara dinamis
        // =========================================================================
// ===     BLOK onCLickListener UNTUK TOMBOL EDIT YANG SUDAH BENAR      ===
// =========================================================================
        btnTolak.setText("Edit Laporan"); // Mengubah teks tombol secara dinamis
        btnTolak.setOnClickListener(v -> {
            if (laporanId != -1) {
                Log.d(TAG, "Tombol Edit diklik. Mempersiapkan data untuk EditLaporanActivity untuk ID: " + laporanId);

                // Siapkan Intent untuk membuka halaman Edit
                Intent editIntent = new Intent(DetailPemanenActivity.this, EditLaporanActivity.class);

                // Ambil Intent asli yang memulai Activity ini
                Intent originalIntent = getIntent();

                // Kirim SEMUA data laporan dari Intent asli, BUKAN dari TextView.
                // Ini adalah cara yang paling aman dan konsisten.
                editIntent.putExtra("LAPORAN_ID", originalIntent.getIntExtra("LAPORAN_ID", -1));
                editIntent.putExtra("LAPORAN_NAMA", originalIntent.getStringExtra("LAPORAN_NAMA"));
                editIntent.putExtra("LAPORAN_TANGGAL", originalIntent.getStringExtra("LAPORAN_TANGGAL"));
                editIntent.putExtra("LAPORAN_JUMLAH_TANDAN", originalIntent.getIntExtra("LAPORAN_JUMLAH_TANDAN", 0));
                editIntent.putExtra("LAPORAN_AREA_BLOK", originalIntent.getStringExtra("LAPORAN_AREA_BLOK"));
                editIntent.putExtra("LAPORAN_MANDOR", originalIntent.getStringExtra("LAPORAN_MANDOR"));
                editIntent.putExtra("LAPORAN_JOBDESK", originalIntent.getStringExtra("LAPORAN_JOBDESK"));

                // Sekarang, kirim Intent dengan data yang lengkap dan benar
                startActivity(editIntent);
            } else {
                Toast.makeText(this, "Error: Tidak bisa mengedit laporan tanpa ID.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateStatusLaporan(int id, String newStatus) {
        Log.d(TAG, "Mengirim update untuk ID: " + id + ", Status baru: " + newStatus);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, UPDATE_URL,
                response -> {
                    // Blok ini dieksekusi jika server berhasil meng-update
                    Log.d(TAG, "Respons server: " + response);
                    Toast.makeText(DetailPemanenActivity.this, "Status berhasil diubah menjadi " + newStatus, Toast.LENGTH_SHORT).show();

                    // Kirim sinyal broadcast bahwa ada update
                    Intent broadcastIntent = new Intent(EditLaporanActivity.ACTION_LAPORAN_UPDATED);
                    sendBroadcast(broadcastIntent);

                    // Tutup halaman detail dan kembali ke daftar laporan
                    finish();
                },
                error -> {
                    // Blok ini dieksekusi jika ada error
                    Log.e(TAG, "Volley Error: " + error.toString());
                    Toast.makeText(DetailPemanenActivity.this, "Gagal meng-update status. Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Parameter yang dikirim ke body request
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));

                params.put("status", newStatus);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void updateStatusView(String status) {
        tvStatusDetail.setText(status);
        if ("Diterima".equalsIgnoreCase(status)) {
            tvStatusDetail.setBackground(ContextCompat.getDrawable(this, R.drawable.status_background_diterima));
        } else if ("Ditolak".equalsIgnoreCase(status)) {
            tvStatusDetail.setBackground(ContextCompat.getDrawable(this, R.drawable.status_background_ditolak));
        } else { // "Menunggu" atau lainnya
            tvStatusDetail.setBackground(ContextCompat.getDrawable(this, R.drawable.status_background_menunggu));
        }
    }
}
