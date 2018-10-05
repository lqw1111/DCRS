package entity;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {
    public String studentId;
    public List<Course> studentEnrollCourseList;

    public Student(String studentId , List<Course> studentEnrollCourseList) {
        this.studentId = studentId;
        this.studentEnrollCourseList = studentEnrollCourseList;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<Course> getStudentEnrollCourseList() {
        return studentEnrollCourseList;
    }

    public void setStudentEnrollCourseList(List<Course> studentEnrollCourseList) {
        this.studentEnrollCourseList = studentEnrollCourseList;
    }
}
