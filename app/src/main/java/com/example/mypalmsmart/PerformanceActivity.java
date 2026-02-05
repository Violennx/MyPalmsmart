// ====================================================================================
// ===   FILE PerformanceActivity.java - VERSI LENGKAP DENGAN VOLLEY & SERVER DATA===
// ====================================================================================

package com.example.mypalmsmart;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PerformanceActivity extends AppCompatActivity {

    private TextView tvPerfNamaPekerja, tvPerfTanggalLaporan,
            tvPerfJobdeskDetail, tvPerfJumlahTandanDetail, tvPerfAreaBlokDetail, tvPerfMandorDetail;
    private Button btnKembaliKeHome;
    private LineChart chartPerforma;
    private Spinner spinnerFilterWaktu;

    // List untuk menyimpan semua laporan yang di-download dari server
    private List<LaporanModel> semuaLaporanFromServer = new ArrayList<>();

    private static final String TAG = "PerformanceActivity";
    // PASTIKAN URL ini sama dengan yang ada di activity lain
    private static final String LAPORAN_URL = "https://masterly-irreplaceable-emerson.ngrok-free.dev/laporan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Performa Panen");
        }

        // Inisialisasi UI
        chartPerforma = findViewById(R.id.chartPerforma);
        spinnerFilterWaktu = findViewById(R.id.spinnerFilterWaktu);
        tvPerfNamaPekerja = findViewById(R.id.tvPerfNamaPekerja);
        tvPerfTanggalLaporan = findViewById(R.id.tvPerfTanggalLaporan);
        tvPerfJobdeskDetail = findViewById(R.id.tvPerfJobdeskDetail);
        tvPerfJumlahTandanDetail = findViewById(R.id.tvPerfJumlahTandanDetail);
        tvPerfAreaBlokDetail = findViewById(R.id.tvPerfAreaBlokDetail);
        tvPerfMandorDetail = findViewById(R.id.tvPerfMandorDetail);
        btnKembaliKeHome = findViewById(R.id.btnKembaliKeHome);

        // Hapus atau sembunyikan tombol ekspor CSV jika masih ada di layout
        Button btnExportCSV = findViewById(R.id.btnExportCSV);
        if(btnExportCSV != null) {
            btnExportCSV.setVisibility(View.GONE);
        }

        // Panggil metode untuk mengambil data dari server
        fetchLaporanFromServer();

        spinnerFilterWaktu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Jangan langsung memfilter di sini, tunggu data dari server selesai di-download
                if (!semuaLaporanFromServer.isEmpty()) {
                    String pilihan = parent.getItemAtPosition(position).toString();
                    filterDanTampilkanGrafik(pilihan);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (btnKembaliKeHome != null) {
            btnKembaliKeHome.setOnClickListener(v -> finish());
        }
    }

    // METODE BARU: Mengambil semua data laporan dari server menggunakan Volley
    private void fetchLaporanFromServer() {
        Log.d(TAG, "Memulai pengambilan data dari server: " + LAPORAN_URL);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, LAPORAN_URL, null,
                response -> {
                    Log.d(TAG, "Server merespons dengan " + response.length() + " laporan.");
                    semuaLaporanFromServer.clear(); // Bersihkan list lama
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
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
                            semuaLaporanFromServer.add(laporan);
                        }

                        // Setelah semua data di-download, baru kita proses untuk ditampilkan
                        // Urutkan berdasarkan ID terbaru (asumsi ID terbaru = laporan terbaru)
                        // KODE YANG SUDAH DIPERBAIKI (Kompatibel dengan API 21+)
                        Collections.sort(semuaLaporanFromServer, (l1, l2) -> Integer.compare(l2.getId(), l1.getId()));


                        loadDetailLaporanTerakhir(); // Tampilkan detail laporan paling atas
                        // Tampilkan grafik awal dengan filter default dari spinner
                        filterDanTampilkanGrafik(spinnerFilterWaktu.getSelectedItem().toString());

                    } catch (JSONException e) {
                        Log.e(TAG, "Gagal parsing JSON", e);
                        Toast.makeText(this, "Format data dari server salah.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley Error: " + error.toString());
                    Toast.makeText(this, "Gagal memuat data dari server.", Toast.LENGTH_LONG).show();
                });

        queue.add(jsonArrayRequest);
    }

    // Metode ini sekarang mengambil data dari list 'semuaLaporanFromServer'
    private void loadDetailLaporanTerakhir() {
        if (semuaLaporanFromServer.isEmpty()) {
            tvPerfNamaPekerja.setText("-");
            tvPerfTanggalLaporan.setText("-");
            tvPerfJobdeskDetail.setText("-");
            tvPerfJumlahTandanDetail.setText("-");
            tvPerfAreaBlokDetail.setText("-");
            tvPerfMandorDetail.setText("-");
            return;
        }
        // Ambil laporan pertama dari list (yang sudah diurutkan dari terbaru)
        LaporanModel laporanTerakhir = semuaLaporanFromServer.get(0);
        tvPerfNamaPekerja.setText(laporanTerakhir.getNama());
        tvPerfTanggalLaporan.setText(laporanTerakhir.getTanggal());
        tvPerfJobdeskDetail.setText(laporanTerakhir.getJobdesk());
        tvPerfJumlahTandanDetail.setText(String.valueOf(laporanTerakhir.getJumlahTandan()));
        tvPerfAreaBlokDetail.setText(laporanTerakhir.getAreaBlok());
        tvPerfMandorDetail.setText(laporanTerakhir.getMandor());
    }

    // Metode ini sekarang mengambil data dari list 'semuaLaporanFromServer'
    private void filterDanTampilkanGrafik(String pilihan) {
        if (semuaLaporanFromServer.isEmpty()) {
            chartPerforma.clear();
            chartPerforma.invalidate(); // Refresh chart agar menampilkan pesan "no data"
            Log.d(TAG, "Tidak ada data dari server untuk ditampilkan di grafik.");
            return;
        }

        List<LaporanModel> laporanTerfilter = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar batasWaktu = Calendar.getInstance();
        batasWaktu.set(Calendar.HOUR_OF_DAY, 0);
        batasWaktu.set(Calendar.MINUTE, 0);
        batasWaktu.set(Calendar.SECOND, 0);

        if ("Semua".equals(pilihan)) {
            laporanTerfilter.addAll(semuaLaporanFromServer);
        } else {
            switch (pilihan) {
                case "Hari Ini":
                    // Batas waktu sudah diatur ke awal hari ini, tidak perlu diubah
                    break;
                case "Minggu Terakhir":
                    batasWaktu.add(Calendar.DAY_OF_YEAR, -6);
                    break;
                case "Bulan Terakhir":
                    batasWaktu.add(Calendar.MONTH, -1);
                    break;
            }

            for (LaporanModel laporan : semuaLaporanFromServer) {
                try {
                    Date tanggalLaporan = sdf.parse(laporan.getTanggal());
                    if (tanggalLaporan != null && !tanggalLaporan.before(batasWaktu.getTime())) {
                        laporanTerfilter.add(laporan);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Gagal parsing tanggal: " + laporan.getTanggal(), e);
                }
            }
        }
        tampilkanGrafikDariLaporan(laporanTerfilter);
    }

    // Metode ini tidak perlu diubah sama sekali
    private void tampilkanGrafikDariLaporan(List<LaporanModel> laporanList) {
        if (laporanList == null || laporanList.isEmpty()) {
            chartPerforma.clear();
            chartPerforma.setNoDataText("Tidak ada data untuk periode ini.");
            chartPerforma.invalidate();
            return;
        }

        Map<String, Integer> tandanPerTanggal = new LinkedHashMap<>();
        for (LaporanModel laporan : laporanList) {
            String tanggal = laporan.getTanggal();
            // KODE YANG SUDAH DIPERBAIKI (Kompatibel dengan API 21+)
            Integer tandan = tandanPerTanggal.get(tanggal); // Coba ambil nilai yang sudah ada
            if (tandan == null) {
                tandan = 0; // Jika belum ada (null), mulai dari 0
            }
            tandanPerTanggal.put(tanggal, tandan + laporan.getJumlahTandan());

        }

        List<Entry> entries = new ArrayList<>();
        List<String> tanggalLabels = new ArrayList<>(tandanPerTanggal.keySet());

        // Urutkan tanggal dari yang terlama ke terbaru untuk sumbu X grafik yang benar
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tanggalLabels.sort((d1, d2) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    return sdf.parse(d1).compareTo(sdf.parse(d2));
                } catch (ParseException e) {
                    return 0;
                }
            });
        }

        for (int i = 0; i < tanggalLabels.size(); i++) {
            entries.add(new Entry(i, tandanPerTanggal.get(tanggalLabels.get(i))));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Jumlah Tandan");
        dataSet.setColor(Color.rgb(21, 147, 27));
        dataSet.setCircleColor(Color.rgb(21, 147, 27));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        chartPerforma.setData(lineData);

        XAxis xAxis = chartPerforma.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(tanggalLabels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);

        chartPerforma.getAxisRight().setEnabled(false);
        chartPerforma.getDescription().setEnabled(false);
        chartPerforma.animateY(1000);
        chartPerforma.invalidate(); // Refresh grafik
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
