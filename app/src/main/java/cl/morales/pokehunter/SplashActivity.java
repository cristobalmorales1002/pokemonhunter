package cl.morales.pokehunter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkInternetAndProceed, 2000);
    }

    private void checkInternetAndProceed() {
        if (NetworkUtils.isInternetAvailable(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            showNoInternetDialog();
        }
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sin conexión")
                .setMessage("No se detectó conexión a internet. Por favor revisa tu conexión y reintenta.")
                .setPositiveButton("Reintentar", (dialog, which) -> checkInternetAndProceed())
                .setNegativeButton("Cerrar", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
