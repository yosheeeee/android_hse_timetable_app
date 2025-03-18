package org.hse.baseproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BaseActiviy extends AppCompatActivity {
    protected abstract void initData();
    private static final int timeId = R.id.current_time;
    private static final int paraStatusId = R.id.para_status;
    private static final int paraNameId = R.id.para_name;
    private static final int paraCabinetId = R.id.para_cabinet;
    private static final int paraCorpusId = R.id.para_corpus;
    private static final int paraPrepodId = R.id.para_prepod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(getLayoutId());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initData();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void initTime(){
        TextView timeView = findViewById(timeId);
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, EEEE", new Locale("ru", "RU"));

        String formattedDate = dateFormat.format(date);

        String[] parts = formattedDate.split(", ");
        if (parts.length == 2) {
            String dayOfWeek = parts[1].substring(0, 1).toUpperCase(Locale.getDefault()) + parts[1].substring(1);
            formattedDate = parts[0] + ", " + dayOfWeek;
        }

        timeView.setText(String.format(getString(R.string.current_time), formattedDate));
    }

    private void initParaState(){
        TextView paraStatusView = findViewById(paraStatusId);
        paraStatusView.setText(getString(R.string.para_no));
        TextView paraNameView = findViewById(paraNameId);
        paraNameView.setText(String.format(getString(R.string.para_name), ""));
        TextView paraCabinetView = findViewById(paraCabinetId);
        paraCabinetView.setText(String.format(getString(R.string.para_cabinet), ""));
        TextView paraCorpusView = findViewById(paraCorpusId);
        paraCorpusView.setText(String.format(getString(R.string.para_corpus),""));
        TextView paraPrepodView = findViewById(paraPrepodId);
        paraPrepodView.setText(String.format(getString(R.string.para_prepod),""));
    }

    protected void initTextState(){
        initTime();
        initParaState();
    }

    protected abstract int getLayoutId();
}
