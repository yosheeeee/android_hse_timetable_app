package org.hse.baseproject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class SelectedSchedule implements Serializable {
    public int GroupNumber;
    public String Ep;
    public int Course;

    public int TeacherId;
    public String TeacherName;
    public ScheduleMode ScheduleMode;
    public ScheduleType ScheduleType;
}
