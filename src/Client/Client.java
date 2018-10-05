package Client;

import LogTest.LoggerFormatter;
import RemoteObjectInterface.Servent;
import entity.Course;
import entity.certificateIdentity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {


    public static Servent connect(String port, String department){
        Servent servent = null;
        try {

            Registry registry = LocateRegistry.getRegistry();

            String registryURL = "rmi://localhost:" + port + "/" + department;

            servent = (Servent) Naming.lookup(registryURL);;

        }catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return servent;
    }

    private static certificateIdentity parseStatus(String cmd) {
        String department = cmd.substring(0,4);
        String status = cmd.substring(4,5);
        String id = cmd.substring(5,cmd.length());

        certificateIdentity certificateIdentity = new certificateIdentity(department,status,id);

        return certificateIdentity;
    }

    private static String getRmiPort(String department){
        String port = "";
        if (department.equals("comp")){
            port = "1111";
        } else if (department.equals("inse")) {
            port = "3333";
        } else if (department.equals("soen")) {
            port = "2222";
        } else {
            System.out.println("Not Found");
            return "";
        }
        return port;
    }

    public void start() throws RemoteException{
        Logger logger = Logger.getLogger("client.log");
        logger.setLevel(Level.ALL);


        System.out.println("Input Your Id:");
        Scanner sc = new Scanner(System.in);
        String cmd = sc.nextLine();
        System.out.println(cmd);

        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(cmd + ".log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(fileHandler);
        logger.info(cmd + "  Login Successful");


        certificateIdentity certificateIdentity = parseStatus(cmd);
        String port = getRmiPort(certificateIdentity.getDepartment());

        Servent servent = connect(port, certificateIdentity.getDepartment());


        //advisor
        if (certificateIdentity.getStatus().equals("a")){
            while(true){
                System.out.println("Input your operate: ");
                cmd = sc.nextLine();

                String[] command = cmd.split(" ");
                String result = "";

                switch(command[0]) {
                    case "add" :
                        result = servent.addCourse(command[1],command[2]);
                        logger.info("Operation: " + result);
                        break;
                    case "remove" :
                        System.out.println("enroll Course");
                        result = servent.enrolCourse(command[1],command[2],command[3]);
                        logger.info("Operation: " + result);
                        break;

                    case "listCourseAvailability" :
                        System.out.println("Well done");
                        List<String> courseAvailability = servent.listCourseAvailability(command[1]);
                        System.out.println("Print Course List: ");
                        for (String course : courseAvailability){
                            System.out.println("              " + course);
                        }
                        logger.info("list Course Availability");
                        break;
                    case "enrolCourse" :
                        result = servent.enrolCourse(command[1],command[2],command[3]);
                        logger.info("Operation: " + result);
                        break;

                    case "dropCourse" :
                        result = servent.dropCourse(command[1],command[2]);
                        logger.info("Operation: " + result);
                        break;
                    case "getClassSchedule" :
                        List<Course> classSchedule = servent.getClassSchedule(command[1]);
                        System.out.println("Student " + command[1] + ":");
                        for (Course course :
                                classSchedule) {
                            System.out.println("         " + course.getCourseName() + " " + course.getSemester());
                        }
                        logger.info("Operation: get class schedule");
                        break;
                    default :
                        System.out.println("Invalid Command!");
                }

            }
        }
        //student
        if (certificateIdentity.getStatus().equals("s")){
            while(true){
                System.out.println("Input your operate: ");
                cmd = sc.nextLine();

                String[] command = cmd.split(" ");
                String result = "";

                switch(command[0]) {
                    case "enrolCourse" :
                        result = servent.enrolCourse(command[1],command[2],command[3]);
                        logger.info("Operation: " + result);
                        break;
                    case "dropCourse" :
                        result = servent.dropCourse(command[1],command[2]);
                        logger.info("Operation: " + result);
                        break;
                    case "getClassSchedule" :
                        List<Course> classSchedule = servent.getClassSchedule(command[1]);
                        System.out.println("Student " + command[1] + ":");
                        for (Course course :
                                classSchedule) {
                            System.out.println("                         " + course.getCourseName() + " " + course.getSemester());
                        }
                        logger.info("Operation: get class schedule");
                        break;
                    default :
                        System.out.println("Invalid Command!");
                }
            }
            
        }

    }

    public static void main(String[] args) throws RemoteException {
        Client client = new Client();
        client.start();
    }

}
