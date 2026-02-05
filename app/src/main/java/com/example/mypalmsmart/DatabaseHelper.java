package com.example.mypalmsmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PalmSmartDB";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_LAPORAN = "laporan";
    public static final String KEY_ID = "id";
    public static final String KEY_TANGGAL = "tanggal";
    public static final String KEY_NAMA = "nama";
    public static final String KEY_JUMLAH_TANDAN = "jumlah_tandan";
    public static final String KEY_AREA_BLOK = "area_blok";
    public static final String KEY_MANDOR = "mandor";
    public static final String KEY_JOBDESK = "jobdesk";
    public static final String KEY_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LAPORAN_TABLE = "CREATE TABLE " + TABLE_LAPORAN + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TANGGAL + " TEXT,"
                + KEY_NAMA + " TEXT,"
                + KEY_JUMLAH_TANDAN + " INTEGER,"
                + KEY_AREA_BLOK + " TEXT,"
                + KEY_MANDOR + " TEXT,"
                + KEY_JOBDESK + " TEXT,"
                + KEY_STATUS + " TEXT"
                + ")";
        db.execSQL(CREATE_LAPORAN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_LAPORAN + " ADD COLUMN " + KEY_STATUS + " TEXT DEFAULT 'Menunggu'");
        }
    }

    public void addLaporan(LaporanModel laporan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TANGGAL, laporan.getTanggal());
        values.put(KEY_NAMA, laporan.getNama());
        values.put(KEY_JUMLAH_TANDAN, laporan.getJumlahTandan());
        values.put(KEY_AREA_BLOK, laporan.getAreaBlok());
        values.put(KEY_MANDOR, laporan.getMandor());
        values.put(KEY_JOBDESK, laporan.getJobdesk());
        values.put(KEY_STATUS, laporan.getStatus() != null ? laporan.getStatus() : "Menunggu");
        db.insert(TABLE_LAPORAN, null, values);
    }

    // ✅ PERBAIKAN: Jangan tutup database (db.close()) di metode baca
    private LaporanModel cursorToLaporan(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
        String tanggal = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TANGGAL));
        String nama = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA));
        int jumlahTandan = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_JUMLAH_TANDAN));
        String areaBlok = cursor.getString(cursor.getColumnIndexOrThrow(KEY_AREA_BLOK));
        String mandor = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MANDOR));
        String jobdesk = cursor.getString(cursor.getColumnIndexOrThrow(KEY_JOBDESK));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS));
        return new LaporanModel(id, tanggal, nama, jumlahTandan, areaBlok, mandor, jobdesk, status);
    }

    public List<LaporanModel> getAllLaporan() {
        List<LaporanModel> laporanList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LAPORAN + " ORDER BY " + KEY_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                laporanList.add(cursorToLaporan(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return laporanList;
    }

    public LaporanModel getLaporanTerbaru() {
        SQLiteDatabase db = this.getReadableDatabase();
        LaporanModel laporan = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LAPORAN + " ORDER BY " + KEY_ID + " DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            laporan = cursorToLaporan(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        return laporan;
    }

    public LaporanModel getLaporanById(int laporanId) {
        SQLiteDatabase db = this.getReadableDatabase();
        LaporanModel laporan = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LAPORAN + " WHERE " + KEY_ID + " = ?", new String[]{String.valueOf(laporanId)});
        if (cursor != null && cursor.moveToFirst()) {
            laporan = cursorToLaporan(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        return laporan;
    }

    public List<LaporanModel> getLaporanDiterima() {
        List<LaporanModel> laporanList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LAPORAN + " WHERE " + KEY_STATUS + " = 'Diterima'", null);
        if (cursor.moveToFirst()) {
            do {
                laporanList.add(cursorToLaporan(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return laporanList;
    }

    public int updateLaporanStatus(int laporanId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);
        return db.update(TABLE_LAPORAN, values, KEY_ID + " = ?", new String[]{String.valueOf(laporanId)});
    }

    // Metode ekspor biarkan apa adanya karena dia berdiri sendiri
    public void exportToCSV(Context context, List<LaporanModel> laporanList, String namaFile) {
        if (laporanList.isEmpty()) {
            Toast.makeText(context, "Tidak ada data untuk diekspor.", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(context.getExternalFilesDir(null), namaFile);
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("ID Laporan,Tanggal,Nama Pemanen,Jumlah Tandan,Area Blok,Mandor,Status\n");
            for (LaporanModel laporan : laporanList) {
                writer.append(String.valueOf(laporan.getId())).append(",");
                writer.append(laporan.getTanggal()).append(",");
                writer.append(laporan.getNama()).append(",");
                writer.append(String.valueOf(laporan.getJumlahTandan())).append(",");
                writer.append(laporan.getAreaBlok()).append(",");
                writer.append(laporan.getMandor()).append(",");
                writer.append(laporan.getStatus()).append("\n");
            }
            writer.flush();
            Toast.makeText(context, "CSV berhasil disimpan di: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Gagal mengekspor CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
