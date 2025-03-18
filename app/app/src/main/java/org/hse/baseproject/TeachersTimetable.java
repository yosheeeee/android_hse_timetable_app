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

public class TeachersTimetable extends BaseActiviy {
    private static final int spinnerId = R.id.select_teacher_spinner;


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


    @Override
    protected void initData(){
        initSpinner();
        initTextState();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teachers_timetable;
    }
}