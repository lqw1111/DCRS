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

public class InseServent extends UnicastRemoteObject implements Servent {
    HashMap<String, HashMap<String, Course>> InseCourseDatabase = new HashMap<String, HashMap<String,Course>>();

    //studentId -> Course
    HashMap<String,LinkedList<Course>> InseStudentEnrollDatabase = new HashMap<String,LinkedList<Course>>();

    protected InseServent() throws RemoteException {
    }

    protected InseServent(int port) throws RemoteException {
        super(port);
    }

    protected InseServent(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
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
