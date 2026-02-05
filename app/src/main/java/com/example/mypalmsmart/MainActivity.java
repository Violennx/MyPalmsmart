package com.example.mypalmsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Untuk memeriksa string kosong
import android.util.Log;
import android.view.View; // Untuk setEnabled
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar; // Contoh untuk loading

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextId;
    private Button btnLogin;
    private ProgressBar progressBarLogin; // Contoh ProgressBar
    private final String TAG = "MainActivity_DEBUG";

    // PASTIKAN URL INI BENAR DAN SERVER BERJALAN
    // Gunakan http://10.0.2.2:PORT jika server di laptop dan app di emulator
    // Gunakan IP lokal laptop jika app di perangkat fisik & jaringan sama
    private static final String LOGIN_URL = "https://masterly-irreplaceable-emerson.ngrok-free.dev/login"; // Ganti PORT jika perlu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ACTIVITY STARTED");        setContentView(R.layout.activity_main); // Pastikan activity_main.xml ada
        Log.d(TAG, "onCreate: setContentView(R.layout.activity_main) - OK");

        editTextId = findViewById(R.id.editTextId);
        btnLogin = findViewById(R.id.btnLogin);




        // progressBarLogin = findViewById(R.id.progressBarLogin); // Jika Anda menambahkan ProgressBar di XML

        if (editTextId == null || btnLogin == null) {
            Log.e(TAG, "onCreate: editTextId atau btnLogin IS NULL! Periksa ID di activity_main.xml.");
            Toast.makeText(this, "Error UI: Komponen login tidak ditemukan.", Toast.LENGTH_LONG).show();
            if (btnLogin != null) btnLogin.setEnabled(false); // Nonaktifkan tombol jika ada yang hilang
            return;
        }
        Log.d(TAG, "onCreate: UI elements initialized - OK");

        btnLogin.setOnClickListener(v -> {
            String idInput = editTextId.getText().toString().trim();
            Log.d(TAG, "btnLogin onClick: ID Input = '" + idInput + "'");
            if (!TextUtils.isEmpty(idInput)) { // Gunakan TextUtils untuk cek empty
                loginUser(idInput);
            } else {
                Log.w(TAG, "btnLogin onClick: ID Input is empty.");
                Toast.makeText(MainActivity.this, "Masukkan ID terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
        Log.i(TAG, "onCreate: ACTIVITY CREATION COMPLETE.");
    }

    public void loginUser(final String idInput) {
        Log.i(TAG, "loginUser: Memulai proses login dengan ID: " + idInput);
        setLoadingState(true);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    setLoadingState(false);
                    Log.i(TAG, "loginUser: Respons dari server diterima: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        String nama = obj.optString("nama", "");
                        // PERHATIKAN DI SINI: kita ambil apa adanya dulu, lalu bandingkan tanpa case-sensitive
                        String peranFromServer = obj.optString("peran", "");

                        Log.d(TAG, "loginUser: Parsing JSON -> nama='" + nama + "', peranFromServer='" + peranFromServer + "'");

                        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(peranFromServer)) {
                            Log.e(TAG, "loginUser: Data 'nama' atau 'peran' kosong dari server.");
                            Toast.makeText(MainActivity.this, "Data dari server tidak lengkap.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        WebSocketManager.getInstance().start();
                        Log.d(TAG, "Login sukses, memulai koneksi WebSocket...");


                        Toast.makeText(MainActivity.this, "Login berhasil, selamat datang " + nama, Toast.LENGTH_SHORT).show();

                        Intent intent;

                        // --- INI BAGIAN PALING PENTING ---
                        // Gunakan .equalsIgnoreCase() untuk membandingkan tanpa peduli huruf besar/kecil
                        if ("mandor".equalsIgnoreCase(peranFromServer)) {
                            Log.i(TAG, "loginUser: Peran dikenali sebagai 'mandor'.");
                            intent = new Intent(MainActivity.this, MandorActivity.class);
                            intent.putExtra(MandorActivity.EXTRA_NAMA_MANDOR, nama);
                            startActivity(intent);
                            finishAffinity();

                        } else if ("kerani".equalsIgnoreCase(peranFromServer)) { // INI UNTUK KRANI
                            Log.i(TAG, "loginUser: Peran dikenali sebagai 'kerani'.");
                            intent = new Intent(MainActivity.this, KraniActivity.class);
                            intent.putExtra(KraniActivity.EXTRA_NAMA_KRANI, nama);
                            startActivity(intent);
                            finishAffinity();

                            // KODE BARU YANG SUDAH BENAR
                        } else if ("pemanen".equalsIgnoreCase(peranFromServer)) {
                            Log.i(TAG, "loginUser: Peran dikenali sebagai 'pemanen'. Mengirim nama dan peran ke HomeActivity.");
                            intent = new Intent(MainActivity.this, HomeActivity.class);
                            // Kirim NAMA dan PERAN ke HomeActivity
                            intent.putExtra("EXTRA_USERNAME", nama);
                            intent.putExtra("EXTRA_ROLE", peranFromServer);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else {
                            // Jika tidak ada yang cocok, blok ini akan dieksekusi
                            Log.w(TAG, "loginUser: Peran pengguna TIDAK DIKENALI: '" + peranFromServer + "'");
                            Toast.makeText(MainActivity.this, "Peran pengguna ('" + peranFromServer + "') tidak dikenali.", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "loginUser: Gagal parsing JSON. Respons: " + response, e);
                        Toast.makeText(MainActivity.this, "Format respons dari server salah.", Toast.LENGTH_LONG).show();
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "loginUser: ACTIVITY TIDAK DITEMUKAN! Periksa AndroidManifest.xml.", e);
                        Toast.makeText(MainActivity.this, "Error: Halaman tujuan tidak ditemukan di aplikasi.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "loginUser: Terjadi error tidak terduga: " + e.getMessage(), e);
                        Toast.makeText(MainActivity.this, "Terjadi error tidak terduga.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    setLoadingState(false);
                    Log.e(TAG, "loginUser: Volley Error: " + error.toString());
                    String errorMsg = "Login gagal. Periksa koneksi atau URL server.";
                    if (error.networkResponse != null) {
                        errorMsg += " (Kode: " + error.networkResponse.statusCode + ")";
                    } else if (error.getMessage() != null && error.getMessage().contains("java.net.ConnectException")) {
                        errorMsg = "Koneksi ke server ditolak. Pastikan server berjalan dan IP/URL benar.";
                    }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", idInput);
                return params;
            }
        };
        queue.add(request);

        Log.d(TAG, "loginUser: Volley request added to queue.");
    }

    private void setLoadingState(boolean isLoading) {
        if (progressBarLogin != null) {
            progressBarLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) {
            btnLogin.setEnabled(!isLoading);
        }
        if (editTextId != null) {
            editTextId.setEnabled(!isLoading);
        }
    }
}
