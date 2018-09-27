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

public class SoenServent extends UnicastRemoteObject implements Servent {
    HashMap<String, HashMap<String, Course>> SoenCourseDatabase = new HashMap<String, HashMap<String,Course>>();

    //studentId -> Course
    HashMap<String,LinkedList<Course>> SoenStudentEnrollDatabase = new HashMap<String,LinkedList<Course>>();

    protected SoenServent() throws RemoteException {
    }

    protected SoenServent(int port) throws RemoteException {
        super(port);
    }

    protected SoenServent(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void addCourse(String courseId, String semester) throws RemoteException {

    }

    @Override
    public void removeCourse(String courseId, String semester) throws RemoteException {

    }

    @Override
    public List<Course> listCourseAvailability(String semester) throws RemoteException {
        return null;
    }

    @Override
    public void enrolCourse(String studentId, String courseId, String semester) throws RemoteException {

    }

    @Override
    public void dropCourse(String studentId, String courseId) throws RemoteException {

    }

    @Override
    public List<Course> getClassSchedule(String studentId) throws RemoteException {
        return null;
    }
}
