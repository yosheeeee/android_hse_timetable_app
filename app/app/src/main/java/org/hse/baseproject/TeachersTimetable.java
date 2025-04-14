package org.hse.baseproject;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void showSchedule(ScheduleType scheduleType) {
        Spinner spinner = findViewById(R.id.select_teacher_spinner);
        var selectedItem = (Group) spinner.getSelectedItem();
        if (selectedItem == null){
            return;
        }
        Log.d("Selected_teacher", selectedItem.getId().toString());
        var selectedSchedule = new SelectedSchedule();
        selectedSchedule.ScheduleMode = ScheduleMode.TEACHER;
        selectedSchedule.ScheduleType = scheduleType;
        selectedSchedule.TeacherId = selectedItem.getId();
        selectedSchedule.TeacherName = selectedItem.getName();
        showScheduleActivity(selectedSchedule);
    }
}