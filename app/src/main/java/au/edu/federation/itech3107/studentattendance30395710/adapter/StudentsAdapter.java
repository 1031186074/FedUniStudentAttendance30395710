package au.edu.federation.itech3107.studentattendance30395710.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import au.edu.federation.itech3107.studentattendance30395710.R;
import au.edu.federation.itech3107.studentattendance30395710.room.AppDatabase;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourse;
import au.edu.federation.itech3107.studentattendance30395710.room.StudentCourseDao;


public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {
    private final Executor databaseExecutor = Executors.newSingleThreadExecutor();

    private List<StudentCourse> studentList;
    private Activity context;
    private StudentCourseDao studentDao;  // Add a StudentDao variable

    public StudentsAdapter(Activity context, List<StudentCourse> studentList) {
        this.context = context;
        this.studentList = studentList;
        
        AppDatabase database = AppDatabase.getDatabase(context);
        studentDao = database.studentCourseDao();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentCourse student = studentList.get(position);
        holder.tvStudentName.setText(student.getStudentName());
        holder.btnDelete.setOnClickListener(view -> {
            databaseExecutor.execute(() -> {
                deleteStudent(student.getStudentId());
                studentList.remove(position);

                // 回到主线程来更新UI
               context. runOnUiThread(() -> {
                    notifyItemRemoved(position);
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void deleteStudent(int studentId) {
        studentDao.deleteByStudentIdAndCourseId(studentId);
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        Button btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
