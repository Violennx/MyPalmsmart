package com.example.mypalmsmart;

public class LaporanModel {
    private int id;
    private String nama; // Ini akan dianggap sebagai nama pemanen yang membuat laporan
    private String jobdesk;
    private int jumlahTandan;
    private String areaBlok;
    private String mandor;
    private String tanggal;

    private String status; // pending, approved, rejected

    public LaporanModel(int id, String nama, String jobdesk, int jumlahTandan,
                        String areaBlok, String mandor, String tanggal, String status) {
        this.id = id;
        this.nama = nama;
        this.jobdesk = jobdesk;
        this.jumlahTandan = jumlahTandan;
        this.areaBlok = areaBlok;
        this.mandor = mandor;
        this.tanggal = tanggal;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // Konstruktor
    public LaporanModel(int id, String nama, String jobdesk, int jumlahTandan, String areaBlok, String mandor, String tanggal) {
        this.id = id;
        this.nama = nama; // Nama pemanen
        this.jobdesk = jobdesk;
        this.jumlahTandan = jumlahTandan;
        this.areaBlok = areaBlok;
        this.mandor = mandor;
        this.tanggal = tanggal;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }    public void setId(int id) {
        this.id = id;
    }

    public String getNama() { // Ini akan digunakan sebagai nama pemanen
        return nama;
    }

    public void setNama(String nama) { // Setter untuk nama pemanen
        this.nama = nama;
    }

    public String getJobdesk() {
        return jobdesk;
    }

    public void setJobdesk(String jobdesk) {
        this.jobdesk = jobdesk;
    }

    public int getJumlahTandan() {
        return jumlahTandan;
    }

    public void setJumlahTandan(int jumlahTandan) {
        this.jumlahTandan = jumlahTandan;
    }

    public String getAreaBlok() {
        return areaBlok;
    }

    public void setAreaBlok(String areaBlok) {
        this.areaBlok = areaBlok;
    }

    public String getMandor() {
        return mandor;
    }

    public void setMandor(String mandor) {
        this.mandor = mandor;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    // toString() bisa berguna untuk debugging
    @Override
    public String toString() {
        return "LaporanModel{" +
                "id=" + id +
                ", namaPemanen='" + nama + '\'' + // Menggunakan 'nama' sebagai namaPemanen di toString
                ", jobdesk='" + jobdesk + '\'' +
                ", jumlahTandan=" + jumlahTandan +
                ", areaBlok='" + areaBlok + '\'' +
                ", mandor='" + mandor + '\'' +
                ", tanggal='" + tanggal + '\'' +
                '}';
    }
}
