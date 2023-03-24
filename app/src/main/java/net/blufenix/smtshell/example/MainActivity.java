package net.blufenix.smtshell.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import net.blufenix.smtshell.api.SMTShellAPI;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;

    private final BroadcastReceiver mApiReadyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "API ready!", Toast.LENGTH_SHORT).show();
            btn.setEnabled(true);
        }
    };

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            SMTShellAPI.executeCommand(this, "id", (stdout, stderr, exitCode) -> {
                Toast.makeText(this, stdout, Toast.LENGTH_LONG).show();
            });
        });

        if (ContextCompat.checkSelfPermission(this, SMTShellAPI.PERMISSION_SYSTEM_COMMAND)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Ask the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{SMTShellAPI.PERMISSION_SYSTEM_COMMAND},
                    REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn.setEnabled(false);
        registerReceiver(mApiReadyReceiver, new IntentFilter(SMTShellAPI.ACTION_API_READY));
        SMTShellAPI.ping(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mApiReadyReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            // If request is cancelled, the grantResults arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                // Do the task that requires the permission
            } else {
                // Permission is denied
                // Disable the functionality that depends on this permission.
            }
        }
    }
}