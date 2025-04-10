package org.hse.baseproject;
import android.annotation.SuppressLint;
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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
import androidx.core.content.FileProvider;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Settings extends AppCompatActivity implements SensorEventListener {

    // Константы
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final String USER_NAME_KEY = "user_name";
    private static final String AVATAR_PATH_KEY = "avatar_path";

    private SensorManager sensorManager;
    private Sensor light;
    private TextView sensorsListView;
    private TextView brightnessTextView;
    private TextInputLayout nameInputLayout;
    private ImageView avatarView;
    private Button changeAvatarButton;
    private Button saveChangesButton;
    private String currentPhotoPath;
    private String tempAvatarPath; // Временный путь к фото

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Настройка отступов
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setttings_main_block), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initVars();
        loadSettings();
    }

    private void initVars() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        brightnessTextView = findViewById(R.id.settings_lightning_level);
        sensorsListView = findViewById(R.id.settings_sensor_list);
        List<String> sensorsInfo = getSensorsList(this);
        String allSensors = TextUtils.join("\n\n", sensorsInfo);
        sensorsListView.setText(allSensors);
        sensorsListView.setMovementMethod(new ScrollingMovementMethod());
        nameInputLayout = findViewById(R.id.settings_name_input);
        avatarView = findViewById(R.id.settings_avatar);
        changeAvatarButton = findViewById(R.id.change_avatar_button);
        changeAvatarButton.setOnClickListener(v -> checkPermissionsAndOpenCamera());

        saveChangesButton = findViewById(R.id.settings_save_button);
        saveChangesButton.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
        });

    }

    // Метод проверки разрешений и запуска камеры
    private void checkPermissionsAndOpenCamera() {
        String[] requiredPermissions = new String[]{
                Manifest.permission.CAMERA,
        };

        boolean permissionsGranted = true;
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false;
                break;
            }
        }

        if (permissionsGranted) {
            dispatchTakePictureIntent();
        } else {
            // Запрашиваем разрешения
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                File photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        getPackageManager().getPackageInfo(getPackageName(),0).packageName + ".provider",
                        photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
            } catch (IOException e) {
                Log.e("Settings", "Ошибка при создании файла: " + e.getMessage());
                Toast.makeText(this, "Ошибка при создании файла", Toast.LENGTH_SHORT).show();
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(this, "Package not found", Toast.LENGTH_SHORT).show();
            }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getFilesDir();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            if (currentPhotoPath != null) {
                tempAvatarPath = currentPhotoPath;
                Bitmap bitmap = BitmapFactory.decodeFile(tempAvatarPath);
                avatarView.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                // Разрешения предоставлены, запускаем камеру
                dispatchTakePictureIntent();
            } else {
                // Проверяем, не отказался ли пользователь в настройках
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Разрешения необходимы для работы камеры", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Для включения камеры перейдите в настройки приложения", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this,"123", Toast.LENGTH_SHORT); // Показать объяснение
                }
            }
        }
    }
    public List<String> getSensorsList(Context context) {
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensorInfoList = new ArrayList<>();

        for (Sensor sensor : sensors) {
            String sensorType = parseSensorType(sensor.getStringType());
            String sensorInfo = String.format(
                    "Name: %s\nType: %s\nVendor: %s\nVersion: %d\nResolution: %.2f\nMax Range: %.2f\nPower: %.2f mA",
                    sensor.getName(),
                    sensorType,
                    sensor.getVendor(),
                    sensor.getVersion(),
                    sensor.getResolution(),
                    sensor.getMaximumRange(),
                    sensor.getPower()
            );
            sensorInfoList.add(sensorInfo);
        }

        return sensorInfoList;
    }

    private static String parseSensorType(String fullType) {
        if (fullType == null) return "Unknown";
        String[] parts = fullType.split("\\.");
        return parts.length > 0 ? parts[parts.length - 1] : "Unknown";
    }
    private void loadSettings() {
        String savedName = SettingsManager.getInstance(this).getUserName();
        TextInputEditText nameInput = (TextInputEditText) nameInputLayout.getEditText();
        if (nameInput != null) {
            nameInput.setText(savedName);
        }

        String avatarPath = SettingsManager.getInstance(this).getAvatarPath();
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
        TextInputEditText nameInput = (TextInputEditText) nameInputLayout.getEditText();
        if (nameInput != null) {
            SettingsManager.getInstance(this).setUserName(nameInput.getText().toString());
        }

        // Сохраняем аватар только при нажатии кнопки "Сохранить"
        if (tempAvatarPath != null) {
            SettingsManager.getInstance(this).setAvatarPath(tempAvatarPath);
            // Очищаем временный путь
            tempAvatarPath = null;
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
        brightnessTextView.setText(String.format(getString(R.string.settings_light), lux));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ничего не делаем
    }

    // Обертка для работы с настройками
    private static class SettingsManager {
        private static SettingsManager instance;
        private SharedPreferences prefs;

        private SettingsManager(Context context) {
            prefs = context.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        }

        public static synchronized SettingsManager getInstance(Context context) {
            if (instance == null) {
                instance = new SettingsManager(context);
            }
            return instance;
        }

        public void setUserName(String name) {
            prefs.edit().putString(USER_NAME_KEY, name).apply();
        }

        public String getUserName() {
            return prefs.getString(USER_NAME_KEY, "");
        }

        public void setAvatarPath(String path) {
            prefs.edit().putString(AVATAR_PATH_KEY, path).apply();
        }

        public String getAvatarPath() {
            return prefs.getString(AVATAR_PATH_KEY, null);
        }
    }
}