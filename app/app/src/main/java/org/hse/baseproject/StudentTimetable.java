package org.hse.baseproject;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentTimetable extends BaseActiviy {
    private static final int courseSpinnerId = R.id.select_course_spinner;
    private static final int epSpinnerId = R.id.select_ep_spinner;
    private static final int groupSpinnerId = R.id.select_group_spinner;


    @Override
    protected void initData(){
        initTextState();
        initGroupSpinners();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_timetable;
    }

    @Override
    protected void showSchedule(ScheduleType scheduleType) {
        Spinner epSpinner = findViewById(epSpinnerId);
        if (epSpinner == null) {
            return;
        }
        EpInfo ep = (EpInfo) epSpinner.getSelectedItem();
        if (ep == null) {
            return;
        }
        Spinner courseSpinner = findViewById(courseSpinnerId);
        if (courseSpinner == null) {
            return;
        }
        int course = (int)courseSpinner.getSelectedItem();

        Spinner groupSpinner = findViewById(groupSpinnerId);
        if (groupSpinner == null) {
            return;
        }
        int group = (int)groupSpinner.getSelectedItem();
        var selectedSchedule=  new SelectedSchedule();
        selectedSchedule.ScheduleMode = ScheduleMode.STUDENT;
        selectedSchedule.ScheduleType = scheduleType;
        selectedSchedule.Ep = ep.getName();
        selectedSchedule.GroupNumber = group;
        selectedSchedule.Course = course;
        showScheduleActivity(selectedSchedule);
    }

    private List<EpInfo> getEpList(){
        List<EpInfo> list = new ArrayList<>();

        // 1. РИС
        Map<Integer, Integer> courses1 = new LinkedHashMap<>();
        courses1.put(1, 5);
        courses1.put(2, 4);
        courses1.put(3, 4);
        list.add(new EpInfo("РИС", courses1, 1));

        // 2. МБ
        Map<Integer, Integer> courses2 = new LinkedHashMap<>();
        courses2.put(1, 5);
        courses2.put(2, 6);
        courses2.put(3, 5);
        list.add(new EpInfo("МБ", courses2, 2));

        // 3. Ю
        Map<Integer, Integer> courses3 = new LinkedHashMap<>();
        courses3.put(1, 2);
        courses3.put(2, 2);
        courses3.put(3, 2);
        courses3.put(4, 1);
        courses3.put(5, 1);
        list.add(new EpInfo("Ю", courses3, 3));

        // 4. ИЯ
        Map<Integer, Integer> courses4 = new LinkedHashMap<>();
        courses4.put(1, 2);
        courses4.put(2, 2);
        courses4.put(3, 2);
        courses4.put(4, 3);
        list.add(new EpInfo("ИЯ", courses4, 4));

        // 5. И
        Map<Integer, Integer> courses5 = new LinkedHashMap<>();
        courses5.put(2, 1);
        courses5.put(3, 1);
        courses5.put(4, 1);
        courses5.put(5, 1);
        list.add(new EpInfo("И", courses5, 5));

        // 6. БИ
        Map<Integer, Integer> courses6 = new LinkedHashMap<>();
        courses6.put(4, 2);
        list.add(new EpInfo("БИ", courses6, 6));

        // 7. ПИ
        Map<Integer, Integer> courses7 = new LinkedHashMap<>();
        courses7.put(4, 3);
        list.add(new EpInfo("ПИ", courses7, 7));

        // 8. Э
        Map<Integer, Integer> courses8 = new LinkedHashMap<>();
        courses8.put(4, 2);
        list.add(new EpInfo("Э", courses8, 8));

        // 9. УБ
        Map<Integer, Integer> courses9 = new LinkedHashMap<>();
        courses9.put(4, 3);
        list.add(new EpInfo("УБ", courses9, 9));

        return list;
    }


    private void initGroupSpinners() {
        // Получаем ссылки на спиннеры
        Spinner epSpinner = findViewById(epSpinnerId);
        Spinner courseSpinner = findViewById(courseSpinnerId);
        Spinner groupSpinner = findViewById(groupSpinnerId);

        // Инициализируем адаптеры
        ArrayAdapter<EpInfo> epAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );

        ArrayAdapter<Integer> courseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );

        ArrayAdapter<Integer> groupAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );

        // Настраиваем отображение выпадающих списков
        epAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Устанавливаем адаптеры
        epSpinner.setAdapter(epAdapter);
        courseSpinner.setAdapter(courseAdapter);
        groupSpinner.setAdapter(groupAdapter);

        // Заполняем начальные данные для EP
        List<EpInfo> epList = getEpList();
        epAdapter.addAll(epList);
        epAdapter.notifyDataSetChanged();

        // Обработчик выбора образовательной программы
        epSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EpInfo selectedEp = (EpInfo) parent.getItemAtPosition(position);

                // Обновляем список курсов
                List<Integer> courses = selectedEp.getCoursesCount();
                courseAdapter.clear();
                courseAdapter.addAll(courses);
                courseAdapter.notifyDataSetChanged();

                // Автоматически выбираем первый курс
                if (!courses.isEmpty()) {
                    courseSpinner.setSelection(0, true);
                } else {
                    groupAdapter.clear();
                    groupAdapter.notifyDataSetChanged();
                }

                int selectedCourse = (Integer) courseSpinner.getSelectedItem();

                // Генерируем список групп
                int groupsCount = selectedEp.getGroupsCount(selectedCourse);
                List<Integer> groups = new ArrayList<>();
                for (int i = 1; i <= groupsCount; i++) {
                    groups.add(i);
                }

                // Обновляем список групп
                groupAdapter.clear();
                groupAdapter.addAll(groups);
                groupAdapter.notifyDataSetChanged();

                // Автоматически выбираем первую группу
                if (!groups.isEmpty()) {
                    groupSpinner.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                courseAdapter.clear();
                groupAdapter.clear();
            }
        });

        // Обработчик выбора курса
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EpInfo selectedEp = (EpInfo) epSpinner.getSelectedItem();
                int selectedCourse = (Integer) parent.getItemAtPosition(position);

                // Генерируем список групп
                int groupsCount = selectedEp.getGroupsCount(selectedCourse);
                List<Integer> groups = new ArrayList<>();
                for (int i = 1; i <= groupsCount; i++) {
                    groups.add(i);
                }

                // Обновляем список групп
                groupAdapter.clear();
                groupAdapter.addAll(groups);
                groupAdapter.notifyDataSetChanged();

                // Автоматически выбираем первую группу
                if (!groups.isEmpty()) {
                    groupSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                groupAdapter.clear();
            }
        });

        // Инициализируем первый выбор
        if (!epList.isEmpty()) {
            epSpinner.setSelection(0, true);
        }
    }


}