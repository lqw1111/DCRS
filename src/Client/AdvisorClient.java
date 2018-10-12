package Client;

import LogTest.LoggerFormatter;
import RemoteObjectInterface.Servent;
import entity.Course;
import entity.certificateIdentity;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvisorClient extends Client{

    public String advisortId;

    public Servent Login(){

        System.out.println("Input Your Id:");
        Scanner sc = new Scanner(System.in);
        String cmd = sc.nextLine();

        advisortId = cmd;

        certificateIdentity certificateIdentity = parseStatus(cmd);
        if (certificateIdentity.getStatus().equals("a")){
            String port = getRmiPort(certificateIdentity.getDepartment());
            Servent servent = connect(port, certificateIdentity.getDepartment());
            return servent;
        } else {
            System.out.println("It's Not An Advisor Account");
            return null;
        }
    }

    public void startAdvisorClient(Servent servent , String AdvisorId) throws RemoteException {
        Logger logger = Logger.getLogger("client.log");
        logger.setLevel(Level.ALL);

        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(AdvisorId + ".log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(fileHandler);
        logger.info(AdvisorId + "  Login Successful");

        Scanner sc = new Scanner(System.in);
        String cmd = "";

        while(true){
            System.out.println("Input your operate: \n           " +
                    "1.Add Course\n           " +
                    "2.Remove Course\n           " +
                    "3.List Course Availability\n           " +
                    "4.Enroll Course\n           " +
                    "5.Drop Course\n           " +
                    "6.Get Class Schedule");
            cmd = sc.nextLine();

            String result = "";
            String courseId = "";
            String studendId = "";

            switch(cmd) {
                //add
                case "1" :
                    System.out.println("Input The Course Id: ");
                    courseId = sc.nextLine();
                    System.out.println("Input Semester: ");
                    String semester = sc.nextLine();
                    result = servent.addCourse(courseId,semester);
                    logger.info("Operation: " + result);
                    break;
                //remove
                case "2" :
                    System.out.println("Input The Course Id: ");
                    courseId = sc.nextLine();
                    System.out.println("Input The Semester : ");
                    semester = sc.nextLine();
                    result = servent.removeCourse(courseId,semester);
                    logger.info("Operation: " + result);
                    break;
                //listCourseAvailability
                case "3" :
                    System.out.println("Input the semester");
                    semester = sc.nextLine();
                    List<String> courseAvailability = servent.listCourseAvailability(semester);
                    System.out.println("Print Course List: ");
                    for (String course : courseAvailability){
                        System.out.println("              " + course);
                    }
                    logger.info("list Course Availability");
                    break;
                //enrolCourse
                case "4" :
                    System.out.println("Input The Student Id: ");
                    studendId = sc.nextLine();
                    System.out.println("Input The Course Id : ");
                    courseId = sc.nextLine();
                    System.out.println("Input The Semester : ");
                    semester = sc.nextLine();
                    result = servent.enrolCourse(studendId,courseId,semester);
                    logger.info("Operation: " + result);
                    break;
                //dropCourse
                case "5" :
                    System.out.println("Input The Student Id: ");
                    studendId = sc.nextLine();
                    System.out.println("Input The Course Id : ");
                    courseId = sc.nextLine();
                    result = servent.dropCourse(studendId,courseId);
                    logger.info("Operation: " + result);
                    break;
                //getClassSchedule
                case "6" :
                    System.out.println("Input The Student Id: ");
                    studendId = sc.nextLine();
                    List<Course> classSchedule = servent.getClassSchedule(studendId);
                    if (classSchedule == null){
                        logger.info("The Student Does Not Exist! Please Contact With Advisor!");
                    }else {
                        System.out.println("Student " + studendId + ":");
                        for (Course course :
                                classSchedule) {
                            System.out.println("                      " + course.getCourseName() + " " + course.getSemester());
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
        AdvisorClient advisorClient = new AdvisorClient();
        Servent servent = advisorClient.Login();
        if (servent != null){
            advisorClient.startAdvisorClient(servent, advisorClient.advisortId);
        }
    }
}
