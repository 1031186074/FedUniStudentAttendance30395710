package au.edu.federation.itech3107.studentattendance30395710.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import au.edu.federation.itech3107.studentattendance30395710.R;
import au.edu.federation.itech3107.studentattendance30395710.room.AppDatabase;
import au.edu.federation.itech3107.studentattendance30395710.room.Attendance;
import au.edu.federation.itech3107.studentattendance30395710.room.Course;
import au.edu.federation.itech3107.studentattendance30395710.room.CourseDao;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourse;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourseDao;


public class AttendanceAdapter extends BaseAdapter {
    private Context context;
    private List<Attendance> attendances;
    private StudentCourseDao studentDao;
    private CourseDao courseDao;

    public AttendanceAdapter(Context context, List<Attendance> attendances) {
        this.context = context;
        this.attendances = attendances;

        AppDatabase database = AppDatabase.getDatabase(context);
        studentDao = database.studentCourseDao();
        courseDao =database.courseDao();
    }

    @Override
    public int getCount() {
        return attendances.size();
    }

    @Override
    public Object getItem(int position) {
        return attendances.get(position);
    }

    @Override
    public long getItemId(int position) {
        return attendances.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        if (convertView == null) {
            view = inflater.inflate(R.layout.attendance_item, parent, false);
        } else {
            view = convertView;
        }

        TextView studentNameTextView = view.findViewById(R.id.studentNameTextView);
        TextView attendanceStatusTextView = view.findViewById(R.id.attendanceStatusTextView);
        TextView  tvClassName =  view.findViewById(R.id.tvClassName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Attendance currentAttendance = attendances.get(position);
                int studentId = currentAttendance.getStudentId();
                Log.e("获取学生信息studentId：",""+studentId );
                StudentCourse studentCourse = studentDao.getStudentCourseById(studentId);
                int courseId = studentCourse.getCourseId();
                Course courseById = courseDao.getCourseById(courseId);
                Log.e("获取学生信息：",""+studentCourse.toString());
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (studentCourse != null) {
                        String studentName = studentCourse.getStudentName();
                        studentNameTextView.setText(studentName != null ? studentName : "Unknown Student");
                        attendanceStatusTextView.setText(currentAttendance.isPresent() ? "Attendance" : "No Attendance");
                         tvClassName.setText(courseById.getCourseName());
                    }
                });
            }
        }).start();

        return view;
    }
}
