package au.edu.federation.itech3107.studentattendance30395710.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ListView;

import java.util.List;

import au.edu.federation.itech3107.studentattendance30395710.R;
import au.edu.federation.itech3107.studentattendance30395710.adapter.AttendanceAdapter;
import au.edu.federation.itech3107.studentattendance30395710.room.AppDatabase;
import au.edu.federation.itech3107.studentattendance30395710.room.Attendance;
import au.edu.federation.itech3107.studentattendance30395710.room.AttendanceDao;

public class Attendance2 extends AppCompatActivity {

    private ListView attendanceListView;
    private AttendanceDao attendanceDao;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance2);

        DatePicker datePicker = findViewById(R.id.datePicker);
        attendanceListView = findViewById(R.id.attendanceListView);

        AppDatabase database = AppDatabase.getDatabase(this);
        attendanceDao = database.attendanceDao();

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        displayAttendance(selectedDate);
                    }
                });
    }

    private void displayAttendance(String date) {
        Log.e("获取选中日期的 ：",date.toString());
        new Thread(() -> {
            List<Attendance> attendanceList = attendanceDao.getAttendanceByDate(date.trim());

                new Handler(Looper.getMainLooper()).post(() -> {

                    AttendanceAdapter adapter = new AttendanceAdapter(this, attendanceList);
                    attendanceListView.setAdapter(adapter);
                });

        }).start();

    }

}
