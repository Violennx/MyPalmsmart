// >> GANTI TOTAL ISI FILE WebSocketManager.java DENGAN KODE BENAR DI BAWAH INI <<

package com.example.mypalmsmart;

import android.util.Log;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    // !! PENTING: GANTI DENGAN URL DARI RENDER.COM NANTI !!
    // Contoh URL: "https://mypalmsmart-backend.onrender.com"
    private static final String SERVER_URL = "https://masterly-irreplaceable-emerson.ngrok-free.dev";

    private static WebSocketManager instance;
    private Socket mSocket;
    private MessageListener messageListener;

    // Interface ini sudah benar, digunakan oleh MandorActivity
    public interface MessageListener {
        void onNewMessage(String message);
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    // Constructor ini menggunakan pustaka IO.socket dari socket.io-client
    private WebSocketManager() {
        try {
            // URL untuk Socket.IO menggunakan https://, bukan wss://
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e(TAG, "URISyntaxException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    // Metode start() yang baru
    public void start() {
        if (mSocket != null && !mSocket.connected()) {
            mSocket.off(); // Membersihkan semua listener lama untuk mencegah duplikasi

            // Memasang listener baru untuk event dari server Socket.IO
            mSocket.on(Socket.EVENT_CONNECT, args -> {
                Log.d(TAG, "Koneksi Socket.IO Terbuka!"); // Pesan sukses yang kita tunggu
            });
            mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                Log.e(TAG, "Koneksi Socket.IO Gagal: " + args[0]);
            });
            mSocket.on("notifikasiLaporan", args -> { // Mendengarkan event 'notifikasiLaporan' dari server
                if (messageListener != null && args.length > 0) {
                    // Panggil interface yang ada di MandorActivity
                    messageListener.onNewMessage(args[0].toString());
                }
            });

            Log.d(TAG, "Memulai koneksi ke " + SERVER_URL);
            mSocket.connect();
        }
    }

    // Metode sendMessage() yang baru
    public void sendMessage(String message) {
        if (mSocket != null && mSocket.connected()) {
            // Mengirim data dengan event 'laporanBaru' ke server
            mSocket.emit("laporanBaru", message);
        } else {
            Log.e(TAG, "Gagal mengirim: koneksi Socket.IO belum siap.");
        }
    }

    // Metode stop() yang baru
    public void stop() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }
}
