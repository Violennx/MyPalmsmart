package com.example.mypalmsmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LaporanPemanenAdapter extends RecyclerView.Adapter<LaporanPemanenAdapter.LaporanViewHolder> {

    private List<LaporanModel> daftarLaporan;

    // Konstruktor untuk menerima daftar data
    public LaporanPemanenAdapter(List<LaporanModel> daftarLaporan) {
        this.daftarLaporan = daftarLaporan;
    }

    @NonNull
    @Override
    public LaporanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item untuk setiap baris
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laporan_pemanen, parent, false);
        return new LaporanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanViewHolder holder, int position) {
        // Ambil data pada posisi tertentu
        LaporanModel laporan = daftarLaporan.get(position);

        // Isi view dengan data dari LaporanModel

        holder.tvNamaPemanenItem.setText(laporan.getNama());
        holder.tvJobdeskItem.setText("Jobdesk: " + laporan.getJobdesk());
        holder.tvJumlahTandanItem.setText("Jumlah Tandan: " + laporan.getJumlahTandan());
        holder.tvAreaBlokItem.setText("Area Blok: " + laporan.getAreaBlok());
        holder.tvMandorItem.setText("Mandor: " + laporan.getMandor());
        holder.tvTanggalItem.setText("Tanggal: " + laporan.getTanggal());
        holder.tvStatusItem.setText("Status: " + laporan.getStatus());
    }

    @Override
    public int getItemCount() {
        return daftarLaporan != null ? daftarLaporan.size() : 0;
    }

    // Method update data kalau daftar diganti
    public void updateData(List<LaporanModel> newData) {
        this.daftarLaporan.clear();
        if (newData != null) {
            this.daftarLaporan.addAll(newData);
        }
        notifyDataSetChanged();
    }

    // ViewHolder untuk simpan referensi ke View
    public static class LaporanViewHolder extends RecyclerView.ViewHolder {
        TextView  tvNamaPemanenItem, tvJobdeskItem, tvJumlahTandanItem,
                tvAreaBlokItem, tvMandorItem, tvTanggalItem, tvStatusItem;

        public LaporanViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi view dari layout item_laporan_pemanen.xml

            tvNamaPemanenItem = itemView.findViewById(R.id.tvNamaPemanenItem);
            tvJobdeskItem = itemView.findViewById(R.id.tvJobdeskItem);
            tvJumlahTandanItem = itemView.findViewById(R.id.tvJumlahTandanItem);
            tvAreaBlokItem = itemView.findViewById(R.id.tvAreaBlokItem);
            tvMandorItem = itemView.findViewById(R.id.tvMandorItem);
            tvTanggalItem = itemView.findViewById(R.id.tvTanggalItem);
            tvStatusItem = itemView.findViewById(R.id.tvStatusItem);
        }
    }
}
