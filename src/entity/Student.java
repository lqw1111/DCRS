package entity;

import java.util.List;

public class Student {
    public String studentId;
    public List<Course> studentEnrollCourseList;

    public Student() {
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
