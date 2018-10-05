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

    ConcurrentHashMap<String, HashMap<String, Course>> compCourseDatabase = new ConcurrentHashMap<String, HashMap<String, Course>>();

    //studentId -> student
    Map<String, Student> studentEnrollDatabase = new HashMap<String,Student>();

    protected departmentServent(String department, Logger logger) throws RemoteException {
        this.department = department;
        this.logger = logger;

        HashMap<String, Course> fallCourse = new HashMap<>();
        compCourseDatabase.put("fall", fallCourse);

        HashMap<String, Course> winterCourse = new HashMap<>();
        compCourseDatabase.put("winter", winterCourse);

        HashMap<String, Course> summerCourse = new HashMap<>();
        compCourseDatabase.put("summer", summerCourse);

        Student student1 = new Student(department + "s1111",new ArrayList<Course>());
        Student student2 = new Student(department + "s2222",new ArrayList<Course>());
        Student student3 = new Student(department + "s3333",new ArrayList<Course>());

        studentEnrollDatabase.put(department + "s1111",student1);
        studentEnrollDatabase.put(department + "s2222",student2);
        studentEnrollDatabase.put(department + "s3333",student3);
    }

    protected departmentServent(int port) throws RemoteException {
        super(port);
    }

    protected departmentServent(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }


    @Override
    public String addCourse(String courseId, String semester) throws RemoteException {
        String result = "";
        Course newCourse = new Course(courseId , semester);
        HashMap<String,Course> courseIdCourseMap = compCourseDatabase.get(semester);

        if (courseIdCourseMap.containsKey(courseId)){
            System.out.println("The course have already added!");
            result = "The course have already added!";
        } else {
            courseIdCourseMap.put(courseId, newCourse);
            compCourseDatabase.put(semester,courseIdCourseMap);
        }
        result = "add Successful";
        System.out.println(compCourseDatabase.get("fall").size());
        logger.info("add course:" + courseId + " " + semester + " : " + result);

        return result;

    }

    @Override
    public String removeCourse(String courseId, String semester) throws RemoteException {
        HashMap<String,Course> courseList = compCourseDatabase.get(semester);
        courseList.remove(courseId);
        logger.info("remove course:" + courseId + " " + semester + " : " + "remove Successful");
        return "remove Successful";
    }

    @Override
    public List<String> listCourseAvailability(String semester) throws RemoteException {
        logger.info("list course availability : " + semester);

        List<String> courseList = getLocalCourseList(semester);

        try {
            String courseAvailibleList = "";
            String message = "listCourseAvailability " + semester;
            if (this.department.equals("comp")){
                courseAvailibleList = getRemoteCourseList(message,2223,message,3334);

            } else if(this.department.equals("inse")){
                courseAvailibleList = getRemoteCourseList(message,2223,message,1112);

            } else if(this.department.equals("soen")){
                courseAvailibleList = getRemoteCourseList(message,1112,message,3334);
            }

            String[] courses = courseAvailibleList.split(" ");
            for (String course :
                    courses) {
                courseList.add(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courseList;
    }

    protected List<String> getLocalCourseList(String semester){
        List<String> courseList = new ArrayList<>();

        //get local data
        HashMap<String,Course> courseMap = compCourseDatabase.get(semester);

        for (Map.Entry<String, Course> entry: courseMap.entrySet()){
            Course course = entry.getValue();
            if (course.getCapacity() - course.getEnrollNumber() > 0){
                courseList.add(entry.getKey());
            }
        }

        return courseList;
    }

    private String getRemoteCourseList(String message1 , int port1, String message2 , int port2) throws Exception {
        String receive1 = sendMessage(message1, port1);
        System.out.println("comp课表：" + receive1);
        String receive2 = sendMessage(message2, port2);
        System.out.println("inse课表：" + receive2);

        return receive1 + receive2;
    }

    @Override
    public String enrolCourse(String studentId, String courseId, String semester) throws RemoteException {
        String result = "";

        String department = courseId.substring(0,4);
        if (department.equals(this.department)){
            if (compCourseDatabase.get(semester).containsKey(courseId)){
                if(allowToEnroll(studentId, courseId, semester) &&
                        compCourseDatabase.get(semester).get(courseId).getEnrollNumber() < compCourseDatabase.get(semester).get(courseId).getCapacity()){
                    Student student = studentEnrollDatabase.get(studentId);
                    Course course = compCourseDatabase.get(semester).get(courseId);

                    //if the student belongs to depart,it is a local operate,otherwise it is a remote operate
                    if (studentId.substring(0,4).equals(this.department)){
                        student.getStudentEnrollCourseList().add(course);
                    }
                    course.getStudentList().add(studentId);
                    course.setEnrollNumber(course.getEnrollNumber() + 1);

                    result = (courseId + " enroll Successfully");
                } else {
                    result = (courseId + "Do not allow to enroll");
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
        logger.info("enroll course: " + studentId + " " + courseId + " " + semester + " : " + result);
        return result;
    }

    private boolean enrollInOtherDepartment(String studentId, String semester, String department) {
        int courseNum = 0;
        Student student = studentEnrollDatabase.get(studentId);
        List<Course> courses = student.getStudentEnrollCourseList();
        for (Course course : courses){
            if (course.getSemester().equals(semester) && course.getCourseName().substring(0,4).equals(department)){
                courseNum = courseNum + 1;
            }
        }
        return (courseNum < 2);
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
        } else {
            result = "Course Not Found In The Student Course List";
        }

        logger.info("drop course : " + studentId + " " + courseId + " : " + result);

        return result;
    }


    public String dropLocalCourse(String studentId, String courseId, String semester) {
        String result = "";
        HashMap<String, Course> courseMap = compCourseDatabase.get(semester);
        Course course = courseMap.get(courseId);
        List<String> studentIdList = course.getStudentList();

        if(studentIdList.remove(studentId)){
            result = "drop Successful!";
        } else{
            result = "drop fail";
        }
        return result;
    }

    @Override
    public List<Course> getClassSchedule(String studentId) throws RemoteException {
        logger.info("get class schedule : " + studentId);

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


    private static String sendMessage(String message, int port) throws Exception{
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);

        DatagramSocket socket = new DatagramSocket();

        socket.send(sendPacket);


        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String info = new String(receiveData, 0, receivePacket.getLength());
//        System.out.println("我是客户端，服务器说："+info);
        socket.close();
        return info;
    }

}
