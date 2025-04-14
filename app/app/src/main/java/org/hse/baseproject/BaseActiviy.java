package org.hse.baseproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseActiviy extends AppCompatActivity {
    protected abstract void initData();
    private static final int buttonDayId = R.id.button_day;
    private static final int buttonWeekId = R.id.button_week;
    private static final int timeId = R.id.current_time;
    private static final int paraStatusId = R.id.para_status;
    private static final int paraNameId = R.id.para_name;
    private static final int paraCabinetId = R.id.para_cabinet;
    private static final int paraCorpusId = R.id.para_corpus;
    private static final int paraPrepodId = R.id.para_prepod;
    private static final String TAG = "BaseActiviy";
    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";
    private OkHttpClient client = new OkHttpClient();
    private Date responseDate;


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
        addButtonsListeners();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void addButtonsListeners(){
        Button buttonDay = findViewById(buttonDayId);
        buttonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSchedule(ScheduleType.DAY);
            }
        });
        Button buttonWeek = findViewById(buttonWeekId);
        buttonWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSchedule(ScheduleType.WEEK);
            }
        });
    }

    private void initTime(){
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                parseTimeResponce(response);
          }


            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "getTime",e);
            }
        });
    }


    private void parseTimeResponce(Response response){
        var gson = new Gson();
        var body = response.body();
        try{
            if (body == null){
                return;
            }
            var bodyString=  body.string();
            Log.d(TAG, bodyString);
            var timeResponse = gson.fromJson(bodyString,TimeResponse.class);
            var timeString = timeResponse.getTimeZone().getCurrentTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            var date = simpleDateFormat.parse(timeString);
            responseDate = date;
            runOnUiThread(() -> showTime(date));

        }catch (Exception e){
            Log.e(TAG, "",e);
        }
    }

    private void showTime(Date dateTime){
        if (dateTime == null) {
            return;
        }

        TextView timeView = findViewById(timeId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, MMMM d, EEEE", new Locale("ru", "RU"));

        String formattedDate = dateFormat.format(dateTime);

        String[] parts = formattedDate.split(", ");
        if (parts.length == 3) {
            String dayOfWeek = parts[2].substring(0, 1).toUpperCase(Locale.getDefault()) + parts[2].substring(1);
            formattedDate = parts[0] + ", " + parts[1] + ", " + dayOfWeek;
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

    protected abstract void showSchedule(ScheduleType scheduleType);

    protected void showScheduleActivity(SelectedSchedule selectedSchedule){
        var intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.SELECTED_SCHEDULE_KEY, selectedSchedule);
        intent.putExtra(ScheduleActivity.DATE_RESPONSE_KEY, responseDate);
        startActivity(intent);
    }



    private class TimeResponse{
        @SerializedName("time_zone")
        private TimeZone timeZone;
        public TimeZone getTimeZone(){return timeZone;}
        public void setTimeZone(TimeZone value){this.timeZone = value; }
    }
    private class TimeZone{

        @SerializedName("current_time")
        private String currentTime;
        public String getCurrentTime(){return currentTime;}
        public void setCurrentTime(String value){this.currentTime = value;}
    }
}
