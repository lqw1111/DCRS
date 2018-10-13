
import Client.Client;
import RemoteObjectInterface.Servent;
import entity.Course;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ServerTest {

    @Test
    public void testAddCourse() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        String result = compServent.addCourse("comp1","fall");
        Assert.assertEquals("Add Successful",result);

        result = compServent.addCourse("comp1","fall");
        Assert.assertEquals("The Course Have Already Added!",result);
    }

    @Test
    public void testRemoveCourse() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");

        String add = compServent.addCourse("comp1","fall");
        Assert.assertEquals("Add Successful",add);

        String remove = compServent.removeCourse("comp1","fall");
        Assert.assertEquals("Remove Successful",remove);
    }

    @Test
    public void testListCourseAvailability() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");

        String add = compServent.addCourse("comp1","fall");
        Assert.assertEquals("Add Successful",add);

        List<String> res = compServent.listCourseAvailability("fall");
        Assert.assertEquals(1,res.size());

    }

    @Test
    public void testEnroll() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");

        String add = compServent.addCourse("comp1","fall");
        Assert.assertEquals("Add Successful",add);

        String res = compServent.enrolCourse("comps","comp1","fall");
        Assert.assertEquals("The Student Does Not Exist! Please Contact With Advisor!",res);

        res = compServent.enrolCourse("comps1111","comp2","fall");
        Assert.assertEquals("The Course does not Exist!",res);

        res = compServent.enrolCourse("comps1111","comp1","winter");
        Assert.assertEquals("The Course does not Exist!",res);

        res = compServent.enrolCourse("comps1111","comp1","fall");
        Assert.assertEquals("comp1 Enroll Successfully",res);
    }

    @Test
    public void testStudentEnrollclosedCourseAndRemoteEnroll() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        Servent inseServent = client.connect(Client.getRmiPort("inse"),"inse");

        String res = compServent.addCourse("comp1","fall");
        Assert.assertEquals("Add Successful",res);

        inseServent.enrolCourse("inses1111","comp1","fall");
        inseServent.enrolCourse("inses2222","comp1","fall");
        inseServent.enrolCourse("inses3333","comp1","fall");
        inseServent.enrolCourse("inses4444","comp1","fall");
        inseServent.enrolCourse("inses5555","comp1","fall");
        compServent.enrolCourse("comps1111","comp1","fall");

        res = compServent.enrolCourse("comps2222","comp1","fall");
        Assert.assertEquals("comp1 Do not allow to enroll",res);

    }

    @Test
    public void testStudentExceedTheNumOfEnrolLimit() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        Servent inseServent = client.connect(Client.getRmiPort("inse"),"inse");

        compServent.addCourse("comp1","fall");
        compServent.addCourse("comp2","fall");
        compServent.addCourse("comp3","fall");
        compServent.addCourse("comp4","fall");

        String res = compServent.enrolCourse("comps1111","comp1","fall");
        Assert.assertEquals("comp1 Enroll Successfully",res);

        res = compServent.enrolCourse("comps1111","comp2","fall");
        Assert.assertEquals("comp2 Enroll Successfully",res);

        res = compServent.enrolCourse("comps1111","comp3","fall");
        Assert.assertEquals("comp3 Enroll Successfully",res);

        res = compServent.enrolCourse("comps1111","comp4","fall");
        Assert.assertEquals("comp4 Do not allow to enroll",res);

    }

    @Test
    public void testStudentEnrollOtherDepExceedLimt() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        Servent inseServent = client.connect(Client.getRmiPort("inse"),"inse");

        inseServent.addCourse("inse1","fall");
        inseServent.addCourse("inse2","fall");
        inseServent.addCourse("inse3","fall");
        inseServent.addCourse("inse4","fall");

        String res = compServent.enrolCourse("comps1111","inse1","fall");
        Assert.assertEquals("inse1 Enroll Successfully",res);

        res = compServent.enrolCourse("comps1111","inse2","fall");
        Assert.assertEquals("inse2 Enroll Successfully",res);

        res = compServent.enrolCourse("comps1111","inse3","fall");
        Assert.assertEquals("Do not allow to enroll",res);
    }

    @Test
    public void testDropCourse() throws RemoteException {
        Client client = new Client();
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");

        String add = compServent.addCourse("comp1","fall");
        Assert.assertEquals("Add Successful",add);

        String res = compServent.enrolCourse("comps1111","comp1","fall");
        Assert.assertEquals("comp1 Enroll Successfully",res);

        res = compServent.dropCourse("comps11","comp1");
        Assert.assertEquals("The Student Does Not Exist! Please Contact With Advisor!",res);

        res = compServent.dropCourse("comps1111","comp2");
        Assert.assertEquals("Course Not Found In The Student Course List",res);

        res = compServent.dropCourse("comps1111","comp1");
        Assert.assertEquals("Drop Successful!",res);

    }

    @Test
    public void testConcurrent() throws RemoteException {
        Client client = new Client();
        //connect comp
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        compServent.addCourse("comp1","fall");


        Runnable t1 = () -> {
            try {
                compServent.enrolCourse("comps1111","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t2 = () -> {
            try {
                compServent.enrolCourse("comps2222","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t3 = () -> {
            try {
                compServent.enrolCourse("comps3333","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t4 = () -> {
            try {
                compServent.enrolCourse("comps4444","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t5 = () -> {
            try {
                compServent.enrolCourse("comps5555","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t6 = () -> {
            try {
                compServent.enrolCourse("comps6666","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t7 = () -> {
            try {
                compServent.enrolCourse("comps7777","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t8 = () -> {
            try {
                compServent.enrolCourse("comps8888","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t9 = () -> {
            try {
                compServent.enrolCourse("comps9999","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t10 = () -> {
            try {
                compServent.enrolCourse("comps1010","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread compThread = new Thread(t1);
        Thread compThread2 = new Thread(t2);
        Thread compThread3 = new Thread(t3);
        Thread compThread4 = new Thread(t4);
        Thread compThread5 = new Thread(t5);
        Thread compThread6 = new Thread(t6);
        Thread compThread7 = new Thread(t7);
        Thread compThread8 = new Thread(t8);
        Thread compThread9 = new Thread(t9);
        Thread compThread10 = new Thread(t10);

        compThread.start();
        compThread2.start();
        compThread3.start();
        compThread4.start();
        compThread5.start();
        compThread6.start();
        compThread7.start();
        compThread8.start();
        compThread9.start();
        compThread10.start();

        List<String> courselist = compServent.listCourseAvailability("fall");
        List<String> res = new ArrayList<>();
        for(int i = 0 ; i < courselist.size() ; i++){
            if (courselist.get(i).contains("comp1")){
                res.add(courselist.get(i));
            }
        }
        Assert.assertEquals(0,res.size());
    }

    @Test
    public void testEnrollAndDropConcurrent() throws RemoteException {
        Client client = new Client();
        //connect comp
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        compServent.addCourse("comp1","fall");

        compServent.enrolCourse("comps1111","comp1","fall");
        compServent.enrolCourse("comps2222","comp1","fall");
        compServent.enrolCourse("comps3333","comp1","fall");

        Runnable t1 = () -> {
            try {
                compServent.dropCourse("comps1111","comp1");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t2 = () -> {
            try {
                compServent.dropCourse("comps2222","comp1");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t3 = () -> {
            try {
                compServent.dropCourse("comps3333","comp1");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t4 = () -> {
            try {
                compServent.enrolCourse("comps1111","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t5 = () -> {
            try {
                compServent.enrolCourse("comps2222","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t6 = () -> {
            try {
                compServent.enrolCourse("comps3333","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t7 = () -> {
            try {
                compServent.enrolCourse("comps4444","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t8 = () -> {
            try {
                compServent.enrolCourse("comps5555","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t9 = () -> {
            try {
                compServent.enrolCourse("comps6666","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t10 = () -> {
            try {
                compServent.enrolCourse("comps7777","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t11 = () -> {
            try {
                compServent.enrolCourse("comps8888","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t2);
        Thread thread3 = new Thread(t3);
        Thread thread4 = new Thread(t4);
        Thread thread5 = new Thread(t5);
        Thread thread6 = new Thread(t6);
        Thread thread7 = new Thread(t7);
        Thread thread8 = new Thread(t8);
        Thread thread9 = new Thread(t9);
        Thread thread10 = new Thread(t10);
        Thread thread11 = new Thread(t11);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();
        thread9.start();
        thread10.start();
        thread11.start();

    }

    @Test
    public void testListCourse() throws RemoteException {
        Client client = new Client();
        //connect comp
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        List<String> courselist = compServent.listCourseAvailability("fall");
        List<String> res = new ArrayList<>();
        for(int i = 0 ; i < courselist.size() ; i++){
            if (courselist.get(i).contains("comp1")){
                res.add(courselist.get(i));
            }
        }
        Assert.assertEquals(0,res.size());
    }
}
