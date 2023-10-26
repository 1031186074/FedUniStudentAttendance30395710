package au.edu.federation.itech3107.studentattendance30395710.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.edu.federation.itech3107.studentattendance30395710.R;
import au.edu.federation.itech3107.studentattendance30395710.adapter.StudentAttendanceAdapter;
import au.edu.federation.itech3107.studentattendance30395710.room.AppDatabase;
import au.edu.federation.itech3107.studentattendance30395710.room.Attendance;
import au.edu.federation.itech3107.studentattendance30395710.room.Course;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourse;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourseDao;

public class AttendanceActivity extends AppCompatActivity {

    private Spinner dateSpinner;
    private RecyclerView studentsRecyclerView;
    private StudentAttendanceAdapter adapter;
    private AppDatabase appDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        int courseId = getIntent().getIntExtra("COURSE_ID", -1);


        appDatabase = AppDatabase.getDatabase(this);

        dateSpinner = findViewById(R.id.dateSpinner);
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        Button saveAttendanceButton = findViewById(R.id.saveAttendanceButton);
        new Thread(new Runnable() {
            @Override
            public void run() {


                List<StudentCourse> students = getStudentsFromDatabase();

                StudentCourseDao studentCourseDao = appDatabase.studentCourseDao();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new StudentAttendanceAdapter(AttendanceActivity.this, students,studentCourseDao);
                        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(AttendanceActivity.this));
                        studentsRecyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();

        saveAttendanceButton.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveAttendance();
                }
            }).start();


        });




        new Thread(new Runnable() {
            @Override
            public void run() {
                Course courseByBean = appDatabase.courseDao().getCourseById(courseId);
                List<String> listData = courseByBean.getListData();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(AttendanceActivity.this, android.R.layout.simple_spinner_item, listData);
                        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dateSpinner.setAdapter(dateAdapter);
                    }
                });
            }
        }).start();
    }

    private List<StudentCourse> getStudentsFromDatabase() {

        return appDatabase.studentCourseDao().getAllStudentCourses();
    }

    private void saveAttendance() {
        String selectedDate = dateSpinner.getSelectedItem().toString();
        SparseBooleanArray attendance = adapter.getAttendanceState();

        for (int i = 0; i < attendance.size(); i++) {
            int studentId = attendance.keyAt(i);
            boolean isPresent = attendance.valueAt(i);

            // 在尝试保存考勤信息之前，检查该studentId是否存在于StudentCourse表中
            if (appDatabase.attendanceDao().studentCourseExists(studentId) > 0) {

                Attendance newAttendance = new Attendance();
                newAttendance.setAttendanceDate(selectedDate);
                newAttendance.setStudentId(studentId);
                newAttendance.setPresent(isPresent);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.attendanceDao().insert(newAttendance);
                    }
                }).start();
                runOnUiThread(() -> Toast.makeText(this, "Attendance Saved", Toast.LENGTH_SHORT).show());

            } else {

                runOnUiThread(() -> Toast.makeText(this, "Invalid student ID: " + studentId, Toast.LENGTH_SHORT).show());
            }
        }
    }

}
