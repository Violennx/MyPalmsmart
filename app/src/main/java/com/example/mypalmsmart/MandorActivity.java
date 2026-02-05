// >> GANTI SELURUH ISI FILE MandorActivity.java DENGAN KODE INI

package com.example.mypalmsmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Pastikan Anda mengimplementasikan interface yang benar
public class MandorActivity extends AppCompatActivity implements WebSocketManager.MessageListener {

    public static final String EXTRA_NAMA_MANDOR = "extra_nama_mandor";
    private static final String TAG = "MandorActivity";

    private TextView tvNamaPekerja, tvTanggalLaporan, tvJobdesk, tvJumlahTandan, tvAreaBlok, tvMandor, tvStatus;
    private LaporanAdapter laporanAdapter;


    private final BroadcastReceiver laporanUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (EditLaporanActivity.ACTION_LAPORAN_UPDATED.equals(intent.getAction())) {
                Log.d(TAG, "Menerima broadcast, memuat ulang data untuk tampilan Mandor.");
                loadAndDisplayData();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mandor_activity);


        initViews();

        String namaMandor = getIntent().getStringExtra(EXTRA_NAMA_MANDOR);
        if (namaMandor != null) {
            ((TextView) findViewById(R.id.tvNamaMandorLogin)).setText(namaMandor);
        }
        ((TextView) findViewById(R.id.tvInfoTanggal)).setText(
                new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")).format(new Date())
        );

        setupRecyclerView();
        loadAndDisplayData();
        registerBroadcastReceiver();

        // Mendaftarkan listener WebSocket
        WebSocketManager.getInstance().setMessageListener(this);
    }

    // ================== BAGIAN PENTING 3 ==================
    // Metode ini HARUS ada dan namanya HARUS SAMA PERSIS dengan yang ada di interface
    @Override
    public void onNewMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MandorActivity.this, "Laporan baru masuk! Memuat ulang...", Toast.LENGTH_LONG).show();
                loadAndDisplayData();
            }
        });
    }
    // ========================================================

    // >> SALIN & TEMPEL KODE INI UNTUK MENGGANTIKAN METODE LAMA <<

    private void loadAndDisplayData() {
        Log.d(TAG, "Memulai pengambilan data laporan dari SERVER...");

        // 1. Definisikan URL untuk API pengambilan laporan di server

        String url = "https://masterly-irreplaceable-emerson.ngrok-free.dev/laporan";


        // 2. Siapkan antrian Volley
        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);

        // 3. Buat request GET untuk mengambil array JSON
        com.android.volley.toolbox.JsonArrayRequest jsonArrayRequest = new com.android.volley.toolbox.JsonArrayRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    // Blok ini dieksekusi jika server berhasil mengembalikan data
                    Log.d(TAG, "Server merespons dengan " + response.length() + " laporan.");

                    List<LaporanModel> laporanFromServer = new ArrayList<>();
                    try {
                        // 4. Ubah setiap objek JSON di dalam array menjadi objek LaporanModel
                        for (int i = 0; i < response.length(); i++) {
                            org.json.JSONObject jsonObject = response.getJSONObject(i);

                            LaporanModel laporan = new LaporanModel(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("tanggal"),
                                    jsonObject.getString("nama"),
                                    jsonObject.getInt("jumlah_tandan"), // Nama kolom harus cocok dengan database MySQL
                                    jsonObject.getString("area_blok"),
                                    jsonObject.getString("mandor"),
                                    jsonObject.getString("jobdesk"),
                                    jsonObject.getString("status")
                            );
                            laporanFromServer.add(laporan);
                        }

                        // 5. Update UI dengan data yang baru diambil dari server
                        if (laporanAdapter != null) {
                            laporanAdapter.updateData(laporanFromServer);
                        }

                        if (!laporanFromServer.isEmpty()) {
                            displayLaporanTerakhir(laporanFromServer.get(0)); // Tampilkan yang terbaru
                        } else {
                            Toast.makeText(MandorActivity.this, "Server tidak memiliki laporan.", Toast.LENGTH_SHORT).show();
                            displayLaporanTerakhir(null); // Kosongkan tampilan
                        }

                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "Gagal parsing JSON dari server.", e);
                        Toast.makeText(MandorActivity.this, "Format data dari server salah.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Blok ini dieksekusi jika terjadi error
                    Log.e(TAG, "Volley Error saat GET laporan: " + error.toString());
                    Toast.makeText(MandorActivity.this, "Gagal memuat data dari server. Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        // 6. Jalankan request
        queue.add(jsonArrayRequest);
    }


    private void initViews() {
        tvNamaPekerja = findViewById(R.id.tvNamaPekerja);
        tvTanggalLaporan = findViewById(R.id.tvTanggalLaporan);
        tvJobdesk = findViewById(R.id.tvJobdesk);
        tvJumlahTandan = findViewById(R.id.tvJumlahTandan);
        tvAreaBlok = findViewById(R.id.tvAreaBlok);
        tvMandor = findViewById(R.id.tvMandor);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerViewLaporan = findViewById(R.id.recyclerViewLaporan);
        recyclerViewLaporan.setLayoutManager(new LinearLayoutManager(this));
        laporanAdapter = new LaporanAdapter(this, new ArrayList<>());
        recyclerViewLaporan.setAdapter(laporanAdapter);
    }

    private void displayLaporanTerakhir(LaporanModel laporanTerakhir) {
        if (laporanTerakhir != null) {
            tvNamaPekerja.setText(laporanTerakhir.getNama());
            tvTanggalLaporan.setText(laporanTerakhir.getTanggal());
            tvJobdesk.setText(laporanTerakhir.getJobdesk());
            tvJumlahTandan.setText(laporanTerakhir.getJumlahTandan() + " Tandan");
            tvAreaBlok.setText(laporanTerakhir.getAreaBlok());
            tvMandor.setText("Mandor: " + laporanTerakhir.getMandor());
            tvStatus.setText(laporanTerakhir.getStatus());

            if ("Diterima".equalsIgnoreCase(laporanTerakhir.getStatus())) {
                tvStatus.setBackgroundResource(R.drawable.status_background_diterima);
            } else if ("Ditolak".equalsIgnoreCase(laporanTerakhir.getStatus())) {
                tvStatus.setBackgroundResource(R.drawable.status_background_ditolak);
            } else {
                tvStatus.setBackgroundResource(R.drawable.status_background_menunggu);
            }
        } else {
            tvNamaPekerja.setText("Belum ada laporan");
            tvTanggalLaporan.setText("-");
            tvJobdesk.setText("-");
            tvJumlahTandan.setText("-");
            tvAreaBlok.setText("-");
            tvMandor.setText("-");
            tvStatus.setText("N/A");
            tvStatus.setBackgroundResource(R.drawable.status_background_menunggu);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(EditLaporanActivity.ACTION_LAPORAN_UPDATED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(laporanUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(laporanUpdateReceiver, filter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(laporanUpdateReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Receiver tidak terdaftar.", e);
        }

        // Membersihkan listener
        WebSocketManager.getInstance().setMessageListener(null);
    }
}
