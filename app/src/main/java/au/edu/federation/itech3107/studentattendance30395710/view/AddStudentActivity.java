package au.edu.federation.itech3107.studentattendance30395710.view;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.studentattendance30395710.R;
import au.edu.federation.itech3107.studentattendance30395710.adapter.StudentsAdapter;
import au.edu.federation.itech3107.studentattendance30395710.room.AppDatabase;
import au.edu.federation.itech3107.studentattendance30395710.room.Course;
import au.edu.federation.itech3107.studentattendance30395710.room.CourseDao;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourse;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourseDao;


public class AddStudentActivity extends AppCompatActivity {
    private EditText edtStudentName,edtStudentId;
    private Spinner spinnerCourses;
    private Button btnAddStudentToCourse;
    private AppDatabase database;
    private StudentCourseDao studentDao;
    private CourseDao courseDao;
    private List<String> courseNames = new ArrayList<>();
    private List<Integer> courseIds = new ArrayList<>();  // 注意这里修改为Integer

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        
        database = AppDatabase.getDatabase(this);
        studentDao = database.studentCourseDao();
        courseDao = database.courseDao();

        edtStudentId = findViewById(R.id.edtStudentId);
        edtStudentName = findViewById(R.id.edtStudentName);
        spinnerCourses = findViewById(R.id.spinnerCourses);
        btnAddStudentToCourse = findViewById(R.id.btnAddStudentToCourse);

        new AsyncTask<Void, Void, List<Course>>() {
            @Override
            protected List<Course> doInBackground(Void... voids) {
                return courseDao.getAllCourses();  // Execute database operation in background thread
            }

            @Override
            protected void onPostExecute(List<Course> courses) {
                // Now back on main thread
                for (Course course : courses) {
                    courseIds.add(course.getCourseId());
                    courseNames.add(course.getCourseName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStudentActivity.this, android.R.layout.simple_spinner_item, courseNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCourses.setAdapter(adapter);
            }
        }.execute();


        btnAddStudentToCourse.setOnClickListener(v -> {
            String studentIdStr = edtStudentId.getText().toString().trim();
            String studentName = edtStudentName.getText().toString().trim();
            int selectedCoursePosition = spinnerCourses.getSelectedItemPosition();
            int courseId = courseIds.get(selectedCoursePosition);

            StudentCourse student = new StudentCourse();
            if (studentIdStr.length() > 9) {
                 Toast.makeText(this,"a_maximum_of_9_bits_is_required",Toast.LENGTH_SHORT).show();
            } else {
                student.setStudentId(Integer.parseInt(studentIdStr));
            }
            student.setStudentName(studentName);
            student.setCourseId(courseId);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long insertedId = studentDao.insert(student);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (insertedId != -1) {
                                Toast.makeText(AddStudentActivity.this, "Student Added Successfully", Toast.LENGTH_SHORT).show();
                                edtStudentName.setText("");
                                finish();
                            } else {
                                Toast.makeText(AddStudentActivity.this, "Error Adding Student", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).start();

        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new Thread(() -> {
            List<StudentCourse> studentList = studentDao.getAllStudentCourses();
            StudentsAdapter adapters = new StudentsAdapter(this, studentList);
            recyclerView.setAdapter(adapters);
        }).start();

    }
}
