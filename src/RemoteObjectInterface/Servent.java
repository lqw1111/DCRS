package RemoteObjectInterface;

import entity.Course;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Servent extends Remote {
    String addCourse(String courseId, String semester) throws RemoteException;
    String removeCourse(String courseId, String semester) throws RemoteException;
    List<String> listCourseAvailability (String semester) throws RemoteException;
    String enrolCourse (String studentId, String courseId, String semester) throws RemoteException;
    String dropCourse (String studentId, String courseId) throws RemoteException;
    List<Course> getClassSchedule(String studentId) throws RemoteException;
}
