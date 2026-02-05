package com.example.mypalmsmart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LaporanKraniAdapter extends RecyclerView.Adapter<LaporanKraniAdapter.LaporanViewHolder> {

    private final Context context;
    private List<LaporanModel> laporanList;

    public LaporanKraniAdapter(Context context, List<LaporanModel> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public LaporanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan_mandor, parent, false);
        return new LaporanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanViewHolder holder, int position) {
        LaporanModel laporan = laporanList.get(position);

        holder.tvNama.setText(laporan.getNama());
        holder.tvTanggal.setText(laporan.getTanggal());
        holder.tvDetail.setText(laporan.getJumlahTandan() + " Tandan - " + laporan.getAreaBlok());
        holder.tvStatus.setText(laporan.getStatus());

        String status = laporan.getStatus();
        if ("Diterima".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.status_background_diterima);
        } else if ("Ditolak".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.status_background_ditolak);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.status_background_menunggu);
        }

        // ✅ LOGIKA KLIK HANYA ADA DI SINI, MEMBUKA EditLaporanActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditLaporanActivity.class);
            intent.putExtra("LAPORAN_ID", laporan.getId());
            context.startActivity(intent);
        });
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

    static class LaporanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvTanggal, tvDetail, tvStatus;
        public LaporanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaPekerjaItem);
            tvTanggal = itemView.findViewById(R.id.tvTanggalItem);
            tvDetail = itemView.findViewById(R.id.tvDetailItem);
            tvStatus = itemView.findViewById(R.id.tvStatusItem);
        }
    }
}
