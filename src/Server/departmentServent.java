package Server;

import RemoteObjectInterface.Servent;
import entity.Course;
import entity.Student;
import sun.rmi.runtime.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class departmentServent extends UnicastRemoteObject implements Servent {

    //TODO:need a port table
    //...

    public Logger logger;
    public String department;

    ConcurrentHashMap<String, ConcurrentHashMap<String, Course>> compCourseDatabase = new ConcurrentHashMap<String, ConcurrentHashMap<String, Course>>();

    //studentId -> student
    Map<String, Student> studentEnrollDatabase = new ConcurrentHashMap<String,Student>();

    protected departmentServent(String department, Logger logger) throws RemoteException {
        this.department = department;
        this.logger = logger;

        ConcurrentHashMap<String, Course> fallCourse = new ConcurrentHashMap<String, Course>();
        compCourseDatabase.put("fall", fallCourse);

        ConcurrentHashMap<String, Course> winterCourse = new ConcurrentHashMap<String, Course>();
        compCourseDatabase.put("winter", winterCourse);

        ConcurrentHashMap<String, Course> summerCourse = new ConcurrentHashMap<String, Course>();
        compCourseDatabase.put("summer", summerCourse);

        Student student1 = new Student(department + "s1111",new ArrayList<Course>());
        Student student2 = new Student(department + "s2222",new ArrayList<Course>());
        Student student3 = new Student(department + "s3333",new ArrayList<Course>());
        Student student4 = new Student(department + "s4444",new ArrayList<Course>());
        Student student5 = new Student(department + "s5555",new ArrayList<Course>());
        Student student6 = new Student(department + "s6666",new ArrayList<Course>());
        Student student7 = new Student(department + "s7777",new ArrayList<Course>());
        Student student8 = new Student(department + "s8888",new ArrayList<Course>());
        Student student9 = new Student(department + "s9999",new ArrayList<Course>());
        Student student10 = new Student(department + "s1010",new ArrayList<Course>());

        studentEnrollDatabase.put(department + "s1111",student1);
        studentEnrollDatabase.put(department + "s2222",student2);
        studentEnrollDatabase.put(department + "s3333",student3);
        studentEnrollDatabase.put(department + "s4444",student4);
        studentEnrollDatabase.put(department + "s5555",student5);
        studentEnrollDatabase.put(department + "s6666",student6);
        studentEnrollDatabase.put(department + "s7777",student7);
        studentEnrollDatabase.put(department + "s8888",student8);
        studentEnrollDatabase.put(department + "s9999",student9);
        studentEnrollDatabase.put(department + "s1010",student10);
    }

    protected departmentServent(int port) throws RemoteException {
        super(port);
    }

    protected departmentServent(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }


    @Override
    public String addCourse(String courseId, String semester) throws RemoteException {
        if (compCourseDatabase.get(semester) == null ) return "The Semester Does Not Exist! Please Check The Semester";
        if (courseId.substring(0,4).equals(this.department)){
            String result = "";
            Course newCourse = new Course(courseId , semester);
            ConcurrentHashMap<String, Course> courseIdCourseMap = compCourseDatabase.get(semester);

            if (courseIdCourseMap.containsKey(courseId)){
                result = "The Course Have Already Added!";
            } else {
                synchronized (courseIdCourseMap){
                    courseIdCourseMap.put(courseId, newCourse);
                    compCourseDatabase.put(semester,courseIdCourseMap);
                    result = "Add Successful";
                }
            }
            logger.info("Add Course:" + courseId + " " + semester + ":" + result);

            return result;
        } else {
            logger.info("Add Course:" + courseId + " " + semester + ":" + "Not Authorized");
            return "You Are Not Authorized To Add The Course ";
        }
    }

    @Override
    public String removeCourse(String courseId, String semester) throws RemoteException {
        if (compCourseDatabase.get(semester) == null ) return "The Semester Does Not Exist! Please Check The Semester";

        if (!courseId.substring(0,4).equals(this.department)){
            logger.info("Remove Course:" + courseId + " " + semester + ":" + " Not Authorized");
            return "You Are Not Authorized To Delete The Course ";
        } else{
            ConcurrentHashMap<String, Course> courseList = compCourseDatabase.get(semester);

            //delete the course from course list
            synchronized (courseList){
                if (courseList.containsKey(courseId)){
                    courseList.remove(courseId);

                    //drop the course from all the student who enroll the course
                    String department = courseId.substring(0,4);
                    dropRemovedCourseFromStuCourList(courseId);
                    try {
                        notifyOtherDepartment(courseId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    logger.info("Remove Course:" + courseId + " " + semester + ":" + " Remove Successful");
                    return "Remove Successful";
                } else{
                    logger.info("Remove Course:" + courseId + " " + semester + ":" + "The Course Doesn't Exist");
                    return "The Course Doesn't Exist";
                }
            }
        }
    }

    private void notifyOtherDepartment(String courseId) throws Exception {
        String message = "dropRemovedCourseFromStuCourList " + courseId;
        if (this.department.equals("comp")){

            sendMessage(message , getPort("inse"));
            sendMessage(message , getPort("soen"));
        } else if(this.department.equals("inse")){

            sendMessage(message , getPort("comp"));
            sendMessage(message , getPort("soen"));
        } else if(this.department.equals("soen")){

            sendMessage(message , getPort("comp"));
            sendMessage(message , getPort("inse"));
        }
    }


    public String dropRemovedCourseFromStuCourList(String courseId){
        List<String> studentList = new ArrayList<>();
        synchronized (studentEnrollDatabase){
            for (Map.Entry<String,Student> studentEntry:
                    studentEnrollDatabase.entrySet()) {
                List<Course> courseList = studentEntry.getValue().getStudentEnrollCourseList();

                for (int i = 0; i < courseList.size(); i++) {
                    if(courseList.get(i).getCourseName().equals(courseId)){
                        studentList.add(studentEntry.getKey());
                        studentEntry.getValue().getStudentEnrollCourseList().remove(i);
                    }
                }
            }
        }
        return "Remove Successful";
    }

    @Override
    public List<String> listCourseAvailability(String semester) throws RemoteException {
        logger.info("List Course Availability :" + semester);

        List<String> courseList = getLocalCourseList(semester);

        try {
            String courseAvailibleList = "";
            String message = "listCourseAvailability " + semester;
            if (this.department.equals("comp")){
                courseAvailibleList = getRemoteCourseList(message,2223, message,3334);

            } else if(this.department.equals("inse")){
                courseAvailibleList = getRemoteCourseList(message,2223, message,1112);

            } else if(this.department.equals("soen")){
                courseAvailibleList = getRemoteCourseList(message,1112, message,3334);
            }

            String[] courses = courseAvailibleList.split(" ");
            for (String course :
                    courses) {
                if (!course.equals("")){
                    courseList.add(course);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courseList;
    }

    protected List<String> getLocalCourseList(String semester){
        List<String> courseList = new ArrayList<>();

        //get local data
        ConcurrentHashMap<String, Course> courseMap = compCourseDatabase.get(semester);

        if (courseMap == null) {
            return courseList;
        }

        for (Map.Entry<String, Course> entry: courseMap.entrySet()){
            Course course = entry.getValue();
            if (course.getCapacity() - course.getEnrollNumber() > 0){
                courseList.add(entry.getKey() + "--"+ (entry.getValue().getCapacity() - entry.getValue().getEnrollNumber()));
            }
        }

        return courseList;
    }

    private String getRemoteCourseList(String message1 , int port1, String message2 , int port2) throws Exception {
        String receive1 = sendMessage(message1, port1);
        String receive2 = sendMessage(message2, port2);
        return receive1 + receive2;
    }

    @Override
    public String enrolCourse(String studentId, String courseId, String semester) throws RemoteException {
        String result = "";
        if (studentEnrollDatabase.get(studentId) == null && this.department.equals(studentId.substring(0,4))) {
            return "The Student Does Not Exist! Please Contact With Advisor!";
        }

        String department = courseId.substring(0,4);
        if (department.equals(this.department)){
            if (compCourseDatabase.get(semester).containsKey(courseId)){
                if(allowToEnroll(studentId, courseId, semester) &&
                        compCourseDatabase.get(semester).get(courseId).getEnrollNumber() < compCourseDatabase.get(semester).get(courseId).getCapacity()){
                    Student student = studentEnrollDatabase.get(studentId);
                    Course course = compCourseDatabase.get(semester).get(courseId);

                    synchronized (course) {
                        //if the student belongs to depart,it is a local operate,otherwise it is a remote operate
                        course.getStudentList().add(studentId);
                        course.setEnrollNumber(course.getEnrollNumber() + 1);
                        if (studentId.substring(0, 4).equals(this.department)) {
                            student.getStudentEnrollCourseList().add(course);
                        }
                    }

                    result = (courseId + " Enroll Successfully");
                } else {
                    result = (courseId + " Do not allow to enroll");
                }
            } else {
                result = "The Course does not Exist!";
            }


        } else {
            if(enrollInOtherDepartment(studentId, semester, department)){
                int port = getPort(department);

                try {
                    result = sendMessage("enrolCourse " + studentId + " " + courseId + " " + semester, port);
                    if (result.contains("Successfully")){
                        Course course = new Course(courseId,semester);
                        Student student = studentEnrollDatabase.get(studentId);
                        student.getStudentEnrollCourseList().add(course);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                result = "Do not allow to enroll";
            }

        }
        logger.info("Enroll Course :" + studentId + " " + courseId + " " + semester + ":" + result);
        return result;
    }

    private boolean enrollInOtherDepartment(String studentId, String semester, String department) {
        int courseNum = 0;
        Student student = studentEnrollDatabase.get(studentId);
        if (student == null){
            return false;
        } else{
            List<Course> courses = student.getStudentEnrollCourseList();
            for (Course course : courses){
                if (course.getSemester().equals(semester) && !course.getCourseName().substring(0,4).equals(this.department)){
                    courseNum = courseNum + 1;
                }
            }
            return (courseNum < 2);
        }
    }

    private boolean allowToEnroll(String studentId,String courseId, String semester) {
        if (!studentId.substring(0,4).equals(this.department) && !compCourseDatabase.get(semester).get(courseId).getStudentList().contains(studentId)) return true;
        if (compCourseDatabase.get(semester).get(courseId).getStudentList().contains(studentId)) return false;
        int courseNum = 0;
        Student student = studentEnrollDatabase.get(studentId);
        List<Course> courses = student.getStudentEnrollCourseList();
        for (Course course :
                courses) {
            if (course.getSemester().equals(semester)){
                courseNum = courseNum + 1;
            }
        }
        return (courseNum < 3);
    }

    @Override
    public String dropCourse(String studentId, String courseId) throws RemoteException {

        if (studentEnrollDatabase.get(studentId) == null && this.department.equals(studentId.substring(0,4))) {
            return "The Student Does Not Exist! Please Contact With Advisor!";
        }

        boolean findTargetCourse = false;
        String result = "";
        String department = courseId.substring(0,4);

        Student student = studentEnrollDatabase.get(studentId);
        List<Course> courseList = student.getStudentEnrollCourseList();
        String semester = "";
        List<Course> removeCourses = new ArrayList<Course>();
        for (Course course: courseList){
            if (course.getCourseName().equals(courseId)){
                findTargetCourse = true;
                semester = course.getSemester();
                removeCourses.add(course);
            }
        }

        if (findTargetCourse){
            synchronized (student){
                courseList.removeAll(removeCourses);
                if (department.equals(this.department)){
                    result = dropLocalCourse(studentId, courseId, semester);
                } else{
                    String message = "dropCourse " + studentId + " " + courseId + " " + semester;
                    int port = getPort(department);
                    try {
                        result = sendMessage(message, port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            result = "Course Not Found In The Student Course List";
        }

        logger.info("Drop Course :" + studentId + " " + courseId + ":" + result);

        return result;
    }


    public String dropLocalCourse(String studentId, String courseId, String semester) {
        String result = "";
        ConcurrentHashMap<String, Course> courseMap = compCourseDatabase.get(semester);
        Course course = courseMap.get(courseId);
        List<String> studentIdList = course.getStudentList();

        synchronized (course){
            if(studentIdList.remove(studentId)){
                course.setEnrollNumber(course.getEnrollNumber() - 1);
                result = "Drop Successful!";
            } else{
                result = "Drop Fail";
            }
        }

        return result;
    }

    @Override
    public List<Course> getClassSchedule(String studentId) throws RemoteException {
        if (studentEnrollDatabase.get(studentId) == null) return null;

        logger.info("Get Class Schedule :" + studentId);

        return studentEnrollDatabase.get(studentId).getStudentEnrollCourseList();
    }

    private int getPort(String department){
        int port;
        if (department.equals("comp")){
            port = 1112;
        } else if(department.equals("soen")){
            port = 2223;
        } else {
            port = 3334;
        }
        return port;
    }


    private String sendMessage(String message, int port) throws Exception{
        logger.info("Client Send Request :" + message);
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);

        DatagramSocket socket = new DatagramSocket();

        socket.send(sendPacket);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String info = new String(receiveData, 0, receivePacket.getLength());
        logger.info("Client Recv Response :" + info);
        socket.close();
        return info;
    }

}
