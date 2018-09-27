package Server;

import RemoteObjectInterface.Servent;
import entity.Course;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CompServent extends UnicastRemoteObject implements Servent{

    HashMap<String, HashMap<String, Course>> compCourseDatabase = new HashMap<String, HashMap<String,Course>>();

    //studentId -> Course
    HashMap<String,LinkedList<Course>> compStudentEnrollDatabase = new HashMap<String,LinkedList<Course>>();

    protected CompServent() throws RemoteException {
    }

    protected CompServent(int port) throws RemoteException {
        super(port);
    }

    protected CompServent(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void addCourse(String courseId, String semester) throws RemoteException {
        System.out.println("add course");
    }

    @Override
    public void removeCourse(String courseId, String semester) throws RemoteException {
        System.out.println(" remove course");
    }

    @Override
    public List<Course> listCourseAvailability(String semester) throws RemoteException {
        System.out.println("list course availability");
        return null;
    }

    @Override
    public void enrolCourse(String studentId, String courseId, String semester) throws RemoteException {
        System.out.println("enroll course");
    }

    @Override
    public void dropCourse(String studentId, String courseId) throws RemoteException {
        System.out.println("drop course");
    }

    @Override
    public List<Course> getClassSchedule(String studentId) throws RemoteException {
        System.out.println("get class schedule");
        return null;
    }
}
