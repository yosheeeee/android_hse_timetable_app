package org.hse.baseproject;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TeachersTimetable extends AppCompatActivity {
    private static final int spinnerId = R.id.select_teacher_spinner;
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
        setContentView(R.layout.activity_teachers_timetable);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initData();

    }

    private List<Group> getGroupList(){
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "Преподаватель 1"));
        groups.add(new Group(2, "Преподаватель 2"));
        return groups;
    }

    private void initSpinner(){
        var groups = getGroupList();
        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groups);
        Spinner spinner = findViewById(spinnerId);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = adapter.getItem(position);
                Log.d("SelectedItem", "selectedItem: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initTime(){
        TextView timeView = findViewById(timeId);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, EEEE", new Locale("ru", "RU"));
        timeView.setText(String.format(getString(R.string.current_time), dateFormat.format(date)));
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

    private void initTextState(){
        initTime();
        initParaState();
    }

    private void initData(){
        initSpinner();
        initTextState();
    }
}