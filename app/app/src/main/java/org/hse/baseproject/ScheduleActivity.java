package org.hse.baseproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    public static final String SELECTED_SCHEDULE_KEY = "selected_schedule";
    public static  final String DATE_RESPONSE_KEY = "date_key";
    private SelectedSchedule selectedSchedule;
    private TextView title;
    private TextView dateTitle;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private Date  responseDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initData();
    }

    private void onScheduleMenuItemClick(ScheduleItem data){
        Log.d("SCHEDULE_ITEM", "item clicked");
    }

    private void initVars(){
        selectedSchedule = (SelectedSchedule) getIntent().getSerializableExtra(SELECTED_SCHEDULE_KEY);
        responseDate = (Date) getIntent().getSerializableExtra(DATE_RESPONSE_KEY);
        title = findViewById(R.id.schedule_title);
        dateTitle = findViewById(R.id.response_date);
        recyclerView = findViewById(R.id.schedule_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::onScheduleMenuItemClick);
        recyclerView.setAdapter(adapter);
    }

    private void initData(){
        initVars();
        initScheduleItems();
        showSelectedScheduleTitle();
        showDateResponseTitle();
    }

    private void initScheduleItems(){
        List<ScheduleItem> list = new ArrayList<>();

        ScheduleItemHeader header = new ScheduleItemHeader();
        header.setTitle("Понедельник, 28 января");
        list.add(header);

        ScheduleItem item1 = new ScheduleItem();
        item1.setStart("10:00");
        item1.setEnd("11:00");
        item1.setType("Практическое занятие");
        item1.setName("Анализ данных (анг)");
        item1.setPlace("Ауд. 503, Кочновский пр-д, д.3");
        item1.setTeacher("Преп. Гущим Михаил Иванович");
        list.add(item1);

        ScheduleItem item2 = new ScheduleItem();
        item2.setStart("12:00");
        item2.setEnd("13:00");
        item2.setType("Практическое занятие");
        item2.setName("Анализ данных (анг)");
        item2.setPlace("Ауд. 503, Кочновский пр-д, д.3");
        item2.setTeacher("Преп. Гущим Михаил Иванович");
        list.add(item2);

        adapter.setDataList(list);
    }

    private  void showSelectedScheduleTitle(){
        String schedule_title = "";
        String schedule_type = selectedSchedule.ScheduleType == ScheduleType.DAY  ?
                getString(R.string.day)
        : getString(R.string.week);
        switch (selectedSchedule.ScheduleMode){
            case STUDENT:
                var stringToFormat = getString(R.string.schedule_mode_student);
                schedule_title = String.format(stringToFormat, selectedSchedule.Ep, selectedSchedule.Course, selectedSchedule.GroupNumber,schedule_type);
                 break;
            case TEACHER:
                stringToFormat = getString(R.string.schedule_mode_teacher);
                schedule_title = String.format(stringToFormat,selectedSchedule.TeacherName, schedule_type);
                break;
        }

        title.setText(schedule_title);
    }

    private void showDateResponseTitle(){
        SimpleDateFormat dateFormat= new SimpleDateFormat("MMMM d, EEEE" , new Locale("ru"));
        var result = dateFormat.format(responseDate);
        dateTitle.setText(result);
    }




    public static class ViewHolder extends RecyclerView.ViewHolder{
        private Context context;
        private  OnItemClick onItemClick;
        private TextView start;
        private  TextView end;
        private  TextView type;
        private  TextView name;
        private TextView place;
        private  TextView teacher;

        public ViewHolder(@NonNull View itemView , Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.finish);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            place= itemView.findViewById(R.id.place);
            teacher = itemView.findViewById(R.id.teacher);
        }

        public void bind(final ScheduleItem data){
            start.setText(data.getStart());
            end.setText(data.getEnd());
            type.setText(data.getType());
            name.setText(data.getName());
            place.setText(data.getPlace());
            teacher.setText(data.getTeacher());
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder{
        private Context context;
        private  OnItemClick onItemClick;
        private TextView title;


        public ViewHolderHeader(@NonNull View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick  = onItemClick;
            title = itemView.findViewById(R.id.title);
        }

        public void bind(final ScheduleItemHeader data ){
            title.setText(data.getTitle());
        }
    }


    public static final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static  final int TYPE_ITEM =0;
        private static final int TYPE_HEADER = 1;

        private List<ScheduleItem> dataList = new ArrayList<>();
        private  OnItemClick onItemClick;

        public ItemAdapter(OnItemClick onItemClick){
            this.onItemClick = onItemClick;
        }

        public void setDataList(List< ScheduleItem> value){
            dataList = value;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (viewType == TYPE_ITEM) {
                View contactView = inflater.inflate(R.layout.schedule_item, parent, false);
                return new ViewHolder(contactView, context, onItemClick);
            } else if (viewType == TYPE_HEADER) {
                View contactView = inflater.inflate(R.layout.schedule_item_header, parent, false);
                return new ViewHolderHeader(contactView, context, onItemClick); // Исправлено
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ScheduleItem data = dataList.get(position);
            if (holder instanceof ViewHolder ){
                ((ViewHolder) holder).bind(data);
            }else if (holder instanceof  ViewHolderHeader){
                ((ViewHolderHeader) holder).bind((ScheduleItemHeader) data);
            }
        }

        @Override
        public int getItemCount() {
            return (int)dataList.size();
        }

        public int getItemViewType(int position){
            ScheduleItem  data = dataList.get(position);
            if (data instanceof ScheduleItemHeader){
                return TYPE_HEADER;
            }else{
                return TYPE_ITEM;
            }
        }


    }
}