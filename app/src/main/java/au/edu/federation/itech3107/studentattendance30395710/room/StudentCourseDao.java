package au.edu.federation.itech3107.studentattendance30395710.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentCourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StudentCourse studentCourse);

    @Update
    int update(StudentCourse studentCourse);

    @Delete
    void delete(StudentCourse studentCourse);

    @Query("SELECT * FROM student_course")
    List<StudentCourse> getAllStudentCourses();

    @Query("SELECT * FROM student_course WHERE student_id = :studentId")
    StudentCourse getStudentCourseById(int studentId);

    @Query("DELETE FROM student_course WHERE student_id = :studentId")
    void deleteByStudentIdAndCourseId(int studentId);

}
