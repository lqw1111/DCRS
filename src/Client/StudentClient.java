package Client;

import LogTest.LoggerFormatter;
import RemoteObjectInterface.Servent;
import entity.Course;
import entity.Student;
import entity.certificateIdentity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentClient extends Client{

    public String studentId;

    public Servent Login(){

        System.out.println("Input Your Id:");
        Scanner sc = new Scanner(System.in);
        String cmd = sc.nextLine();

        this.studentId = cmd;

        certificateIdentity certificateIdentity = parseStatus(cmd);
        String port = getRmiPort(certificateIdentity.getDepartment());

        if (certificateIdentity.getStatus().equals("s")){
            Servent servent = connect(port, certificateIdentity.getDepartment());
            return servent;
        } else{
            System.out.println("It's Not An Student Account");
            return null;
        }


    }

    public void startStudentClient(Servent servent , String studentId) throws RemoteException {
        Logger logger = Logger.getLogger("client.log");
        logger.setLevel(Level.ALL);

        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(studentId + ".log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(fileHandler);
        logger.info(studentId + "  Login Successful");

        Scanner sc = new Scanner(System.in);
        String cmd = "";

        while(true){
            System.out.println("Input your operate: \n           " +
                    "1.Enroll Course\n           " +
                    "2.Drop Course\n           " +
                    "3.Get Class Schedule");
            cmd = sc.nextLine();

            String result = "";
            String courseId = "";
            String semester = "";

            switch(cmd) {
                //enroll Course
                case "1" :
                    System.out.println("Input The Course Id : ");
                    courseId = sc.nextLine();
                    System.out.println("Input The Semester : ");
                    semester = sc.nextLine();
                    result = servent.enrolCourse(studentId,courseId,semester);
                    logger.info("Operation: " + result);
                    break;
                //dropCourse
                case "2" :
                    System.out.println("Input The Course Id : ");
                    courseId = sc.nextLine();
                    result = servent.dropCourse(studentId ,courseId);
                    logger.info("Operation: " + result);
                    break;
                //getClassSchedule
                case "3" :
                    List<Course> classSchedule = servent.getClassSchedule(studentId);
                    if (classSchedule == null){
                        logger.info("The Student Does Not Exist! Please Contact With Advisor!");
                    }else {
                        System.out.println("Student " + studentId + ":");
                        for (Course course :
                                classSchedule) {
                            System.out.println("                       " + course.getCourseName() + " " + course.getSemester());
                        }
                        logger.info("Operation: get class schedule");
                    }
                    break;
                default :
                    System.out.println("Invalid Command!");
            }
        }
    }

    public static void main(String[] args) throws RemoteException{
        StudentClient studentClient = new StudentClient();
        Servent servent = studentClient.Login();
        if (servent != null) {
            studentClient.startStudentClient(servent, studentClient.studentId);
        }
    }
}
