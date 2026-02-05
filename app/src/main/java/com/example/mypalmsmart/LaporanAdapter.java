// =================================================================================
// ===           FILE LaporanAdapter.java - VERSI CERDAS (MEMBEDAKAN PERAN)        ===
// =================================================================================

package com.example.mypalmsmart;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // Kita tambahkan Toast untuk memberi feedback ke Mandor

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder> {

    private final Context context;
    private List<LaporanModel> laporanList;
    private static final String TAG = "LaporanAdapter";

    public LaporanAdapter(Context context, List<LaporanModel> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public LaporanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan, parent, false);
        return new LaporanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanViewHolder holder, int position) {
        LaporanModel currentLaporan = laporanList.get(position);
        holder.bind(currentLaporan);

        // =======================================================================
        // ===            INILAH LOGIKA BARU YANG MEMBEDAKAN PERAN             ===
        // =======================================================================
        holder.itemView.setOnClickListener(v -> {
            // Cek apakah context (activity yang memanggil) adalah instance dari KraniActivity
            if (context instanceof KraniActivity) {
                // JIKA INI KRANI, buka halaman detail untuk verifikasi
                Log.d(TAG, "Item diklik oleh KRANI. Membuka DetailPemanenActivity...");

                Intent intent = new Intent(context, DetailPemanenActivity.class);
                intent.putExtra("LAPORAN_ID", currentLaporan.getId());
                intent.putExtra("LAPORAN_NAMA", currentLaporan.getNama());
                intent.putExtra("LAPORAN_TANGGAL", currentLaporan.getTanggal());
                intent.putExtra("LAPORAN_JUMLAH_TANDAN", currentLaporan.getJumlahTandan());
                intent.putExtra("LAPORAN_AREA_BLOK", currentLaporan.getAreaBlok());
                intent.putExtra("LAPORAN_MANDOR", currentLaporan.getMandor());
                intent.putExtra("LAPORAN_JOBDESK", currentLaporan.getJobdesk());
                intent.putExtra("LAPORAN_STATUS", currentLaporan.getStatus());
                context.startActivity(intent);

            } else if (context instanceof MandorActivity) {
                // JIKA INI MANDOR, jangan lakukan apa-apa (atau tampilkan pesan)
                Log.d(TAG, "Item diklik oleh MANDOR. Aksi diblokir.");
                Toast.makeText(context, "Hanya Krani yang dapat melakukan verifikasi.", Toast.LENGTH_SHORT).show();

            } else {
                // Fallback jika dipanggil dari activity lain (seharusnya tidak terjadi)
                Log.w(TAG, "Item diklik dari context yang tidak dikenal: " + context.getClass().getSimpleName());
            }
        });
        // =======================================================================
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public void updateData(List<LaporanModel> newList) {
        this.laporanList.clear();
        this.laporanList.addAll(newList);
        notifyDataSetChanged();
    }

    // ViewHolder tidak perlu diubah, tetap sama
    static class LaporanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaPemanen, tvAreaBlok, tvJumlahTandan, tvStatus;

        LaporanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaPemanen = itemView.findViewById(R.id.tvItemNamaPemanen);
            tvAreaBlok = itemView.findViewById(R.id.tvItemAreaBlok);
            tvJumlahTandan = itemView.findViewById(R.id.tvItemJumlahTandan);
            tvStatus = itemView.findViewById(R.id.tvItemStatus);
        }

        void bind(LaporanModel laporan) {
            tvNamaPemanen.setText(laporan.getNama());
            tvAreaBlok.setText(laporan.getAreaBlok());
            tvJumlahTandan.setText(String.format("%d Tandan", laporan.getJumlahTandan()));
            tvStatus.setText(laporan.getStatus());

            Context ctx = itemView.getContext();
            if ("Diterima".equalsIgnoreCase(laporan.getStatus())) {
                tvStatus.setBackground(ContextCompat.getDrawable(ctx, R.drawable.status_background_diterima));
            } else if ("Ditolak".equalsIgnoreCase(laporan.getStatus())) {
                tvStatus.setBackground(ContextCompat.getDrawable(ctx, R.drawable.status_background_ditolak));
            } else {
                tvStatus.setBackground(ContextCompat.getDrawable(ctx, R.drawable.status_background_menunggu));
            }
        }
    }

    // Metode baru untuk mendapatkan semua item dari adapter
    public List<LaporanModel> getAllItems() {
        return this.laporanList;
    }

}
