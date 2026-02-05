# MyPalmSmart 🌴

![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Platform: Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Backend: Node.js](https://img.shields.io/badge/Backend-Node.js-blue.svg)

Aplikasi pelaporan panen kelapa sawit berbasis Android.  
Tujuan utama: mempermudah alur pelaporan dari **pemanen → mandor → krani**, dengan fitur notifikasi real-time, role-based access, geo-tagging, dan export laporan ke PDF.

---

## ✨ Fitur Utama
- **Role-based access**: halaman berbeda sesuai jabatan (pemanen, mandor, krani).
- **Pelaporan panen harian**: pemanen input jumlah tandan buah segar (TBS).
- **Notifikasi real-time**: mandor langsung menerima laporan baru.
- **Validasi & verifikasi**: mandor memeriksa laporan sebelum diteruskan ke krani.
- **Export laporan ke PDF**: krani dapat menghasilkan laporan resmi.
- **Geo-tagging panen**: lokasi blok kebun tercatat otomatis.
- **Dashboard monitoring**: visualisasi data panen dengan Chart.js.

---

## 🛠️ Teknologi
- **Android**: Java (Android Studio project).
- **Backend**: Node.js (`server.js`).
- **Database**: MySQL.
- **Tunnel**: ngrok (untuk akses publik).
- **UI/UX**: prototyping sederhana, fokus pada kemudahan pemanen & mandor.

---

## 🚀 Cara Menjalankan
1. Clone repository:
   ```bash
   git clone https://github.com/username/mypalmsmart.git
