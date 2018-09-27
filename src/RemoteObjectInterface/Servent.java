package RemoteObjectInterface;

import entity.Course;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Servent extends Remote {
    void addCourse(String courseId, String semester) throws RemoteException;
    void removeCourse(String courseId, String semester) throws RemoteException;
    List<Course> listCourseAvailability (String semester) throws RemoteException;
    void enrolCourse (String studentId, String courseId, String semester) throws RemoteException;
    void dropCourse (String studentId, String courseId) throws RemoteException;
    List<Course> getClassSchedule(String studentId) throws RemoteException;
}
