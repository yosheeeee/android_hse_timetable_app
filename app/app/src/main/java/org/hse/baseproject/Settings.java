package org.hse.baseproject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Settings extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor light;
    private TextView brightnessTextView;
    private  TextInputLayout nameInputLayout ;
    private ImageView avatarView;
    private  Button changeAvatarButton ;
    private Button saveChangesButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setttings_main_block), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initVars();
        initBrightnessLevel();
        loadSettings();
    }

    private void initVars(){
        // В методе initVars() перед setOnClickListener:
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        brightnessTextView = findViewById(R.id.settings_lightning_level);
        if (brightnessTextView == null) {
            Log.e("Settings", "TextView 'settings_lightning_level' not found!");
        }
        nameInputLayout = findViewById(R.id.settings_name_input);
        avatarView = findViewById(R.id.settings_avatar);
        changeAvatarButton = findViewById(R.id.change_avatar_button);
        changeAvatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });
        saveChangesButton = findViewById(R.id.settings_save_button);
        saveChangesButton.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
        });
    }

    private void initBrightnessLevel() {
        if (light == null) {
            Log.e("Sensor", "Light sensor is not available!");
        } else {
            Log.d("Sensor", "Light sensor is available!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (light != null) {
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        Log.d("Sensor", "Lux: " + lux);

        brightnessTextView.setText(String.format(getString(R.string.settings_light), lux));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);

        String savedName = sharedPreferences.getString("user_name", "");
        TextInputEditText nameInput = (TextInputEditText) nameInputLayout.getEditText();
        if (nameInput != null) {
            nameInput.setText(savedName);
        }

// В loadSettings():
        String avatarPath = sharedPreferences.getString("avatar_path", null);
        if (avatarPath != null) {
            try {
                File imgFile = new File(avatarPath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
                    avatarView.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                Log.e("AvatarLoad", "Error: " + e.getMessage());
            }
        }
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        TextInputEditText nameInput = (TextInputEditText) nameInputLayout.getEditText();
        if (nameInput != null) {
            editor.putString("user_name", nameInput.getText().toString());
        }

        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                avatarView.setImageURI(selectedImageUri);

                SharedPreferences sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String internalPath = copyAvatar(selectedImageUri);
                editor.putString("avatar_path", internalPath);
                editor.apply();
            }
        }
    }
    private String copyAvatar(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "avatar.jpg");
            FileOutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("Avatar", "Copy error: " + e);
            return null;
        }
    }


}
