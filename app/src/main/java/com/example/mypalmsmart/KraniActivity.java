
package com.example.mypalmsmart;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.provider.MediaStore;



// Langkah 1: Implementasikan MessageListener untuk notifikasi real-time
public class KraniActivity extends AppCompatActivity implements WebSocketManager.MessageListener {

    public static final String EXTRA_NAMA_KRANI = "extra_nama_krani";
    private static final String TAG = "KraniActivity";

    // Variabel yang sudah ada (untuk export CSV)
    private Button btnExport;

    // Variabel baru untuk menampilkan daftar laporan dari server
    private RecyclerView recyclerViewLaporanKrani;
    private LaporanAdapter laporanAdapter; // Kita gunakan adapter yang sama

    // Broadcast receiver untuk mendeteksi jika ada laporan yang di-update (diterima/ditolak)
    private final BroadcastReceiver laporanUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Jika ada sinyal dari DetailPemanenActivity atau EditLaporanActivity
            if (EditLaporanActivity.ACTION_LAPORAN_UPDATED.equals(intent.getAction())) {
                Log.d(TAG, "Menerima sinyal broadcast, memuat ulang data untuk tampilan Krani.");
                loadAndDisplayData(); // Muat ulang data untuk merefleksikan perubahan status
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.krani_activity); // Layout Anda tidak diubah

        // Inisialisasi TextView untuk nama dan tanggal
        String namaKrani = getIntent().getStringExtra(EXTRA_NAMA_KRANI);
        if (namaKrani != null) {
            ((TextView) findViewById(R.id.tvNamaKraniLogin)).setText(namaKrani);
        }
        ((TextView) findViewById(R.id.tvInfoTanggal)).setText(
                new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")).format(new Date())
        );

        // Inisialisasi tombol Export CSV yang sudah ada
        btnExport = findViewById(R.id.btnExportDiterima);
        btnExport.setOnClickListener(v -> {
            // Log.d(TAG, "Tombol Export CSV diklik."); // Ini boleh dihapus
            exportLaporanDiterimaToCsv(); // Langsung panggil fungsi export
        });


        // Langkah 2: Panggil metode-metode baru untuk menampilkan data
        setupRecyclerView();      // Menyiapkan RecyclerView
        loadAndDisplayData();     // Memuat data dari server saat activity dibuat
        registerBroadcastReceiver(); // Mendaftarkan receiver untuk update status

        // Mendaftarkan activity ini sebagai pendengar notifikasi real-time dari WebSocket
        WebSocketManager.getInstance().setMessageListener(this);
    }

    // Metode ini menyiapkan RecyclerView untuk menampilkan daftar laporan
    private void setupRecyclerView() {
        // Pastikan di layout krani_activity.xml Anda ada RecyclerView dengan ID ini
        recyclerViewLaporanKrani = findViewById(R.id.recyclerViewLaporanKrani);
        recyclerViewLaporanKrani.setLayoutManager(new LinearLayoutManager(this));

        // Menggunakan LaporanAdapter yang sama.
        // Adapter ini yang akan menangani klik pada item, yang akan membuka halaman
        // DetailPemanenActivity untuk verifikasi. Fungsinya tidak berubah.
        laporanAdapter = new LaporanAdapter(this, new ArrayList<>());
        recyclerViewLaporanKrani.setAdapter(laporanAdapter);
    }

    // Metode ini mengambil data dari server, sama persis seperti di MandorActivity
    private void loadAndDisplayData() {
        Log.d(TAG, "Memulai pengambilan data laporan dari SERVER...");
        String url = "https://masterly-irreplaceable-emerson.ngrok-free.dev/laporan"; // URL server kita

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);

        com.android.volley.toolbox.JsonArrayRequest jsonArrayRequest = new com.android.volley.toolbox.JsonArrayRequest(
                com.android.volley.Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Server merespons dengan " + response.length() + " laporan.");
                    List<LaporanModel> laporanFromServer = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            org.json.JSONObject jsonObject = response.getJSONObject(i);
                            LaporanModel laporan = new LaporanModel(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("tanggal"),
                                    jsonObject.getString("nama"),
                                    jsonObject.getInt("jumlah_tandan"),
                                    jsonObject.getString("area_blok"),
                                    jsonObject.getString("mandor"),
                                    jsonObject.getString("jobdesk"),
                                    jsonObject.getString("status")
                            );
                            laporanFromServer.add(laporan);
                        }

                        // Update data di adapter RecyclerView, UI akan otomatis diperbarui
                        if (laporanAdapter != null) {
                            laporanAdapter.updateData(laporanFromServer);
                        }

                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "Gagal parsing JSON dari server.", e);
                        Toast.makeText(KraniActivity.this, "Format data dari server salah.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley Error saat GET laporan: " + error.toString());
                    Toast.makeText(KraniActivity.this, "Gagal memuat data dari server.", Toast.LENGTH_LONG).show();
                });

        queue.add(jsonArrayRequest);
    }

    // Langkah 3: Implementasikan metode dari interface MessageListener
    // Metode ini akan dipanggil secara otomatis saat ada laporan baru dari pemanen
    @Override
    public void onNewMessage(String message) {
        runOnUiThread(() -> {
            Toast.makeText(KraniActivity.this, "Laporan baru masuk! Memuat ulang...", Toast.LENGTH_LONG).show();
            loadAndDisplayData(); // Langsung muat ulang data untuk menampilkan laporan terbaru
        });
    }

    // Memuat ulang data setiap kali pengguna kembali ke halaman ini
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() dipanggil, memuat ulang data.");
        loadAndDisplayData();
    }

    // Mendaftarkan broadcast receiver
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(EditLaporanActivity.ACTION_LAPORAN_UPDATED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(laporanUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(laporanUpdateReceiver, filter);
        }
    }

    // Membersihkan semua listener saat activity dihancurkan untuk mencegah error
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Membersihkan listener WebSocket
        WebSocketManager.getInstance().setMessageListener(null);
        // Membersihkan broadcast receiver
        try {
            unregisterReceiver(laporanUpdateReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Receiver tidak terdaftar, tidak perlu di-unregister.", e);
        }
    }

    // =========================================================================
    // ===          METODE BARU UNTUK FUNGSI EXPORT KE FILE CSV              ===
    // =========================================================================
    private void exportLaporanDiterimaToCsv() {
        Log.d(TAG, "Memulai proses export CSV...");

        // 1. Cek apakah adapter punya data untuk diekspor
        if (laporanAdapter == null || laporanAdapter.getItemCount() == 0) {
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Export dibatalkan: tidak ada data di adapter.");
            return;
        }

        // 2. Siapkan file di folder Downloads
        // Nama file akan berisi tanggal untuk membuatnya unik
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "laporan_diterima_" + timeStamp + ".csv";

        // Menggunakan ContentResolver untuk menyimpan file, ini cara modern & aman
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        // KODE YANG BENAR
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

        if (uri == null) {
            Toast.makeText(this, "Gagal membuat file CSV", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Gagal mendapatkan URI untuk file CSV.");
            return;
        }

        // 3. Tulis data ke file menggunakan try-with-resources untuk keamanan
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri);
             PrintWriter writer = new PrintWriter(outputStream)) {

            // Tulis baris header (judul kolom)
            writer.println("ID Laporan,Tanggal,Nama Pemanen,Jumlah Tandan,Area/Blok,Mandor,Status");

            int exportedCount = 0;
            // 4. Ambil semua laporan dari adapter
            List<LaporanModel> allLaporan = laporanAdapter.getAllItems();

            // Loop melalui setiap laporan
            for (LaporanModel laporan : allLaporan) {
                // HANYA proses laporan yang statusnya "Diterima"
                if ("Diterima".equalsIgnoreCase(laporan.getStatus())) {
                    // Buat satu baris data CSV
                    String csvLine = String.format(Locale.US, "%d,%s,%s,%d,%s,%s,%s",
                            laporan.getId(),
                            laporan.getTanggal(),
                            laporan.getNama(),
                            laporan.getJumlahTandan(),
                            laporan.getAreaBlok(),
                            laporan.getMandor(),
                            laporan.getStatus()
                    );
                    // Tulis baris tersebut ke file
                    writer.println(csvLine);
                    exportedCount++;
                }
            }

            Log.d(TAG, "Proses penulisan CSV selesai. " + exportedCount + " baris diekspor.");
            Toast.makeText(this, exportedCount + " laporan diterima berhasil diekspor ke folder Downloads", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e(TAG, "Error saat menulis file CSV", e);
            Toast.makeText(this, "Error: Gagal menulis data ke file.", Toast.LENGTH_LONG).show();
        }
    }
    // =========================================================================

}
