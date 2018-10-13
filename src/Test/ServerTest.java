
import Client.Client;
import RemoteObjectInterface.Servent;
import org.junit.Before;
import org.junit.Test;
import java.rmi.RemoteException;

public class ServerTest {

    @Test
    public void testConcurrent() {
        Client client = new Client();
        //connect comp
        Servent compServent = client.connect(Client.getRmiPort("comp"),"comp");
        try {
            compServent.addCourse("comp1","fall");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Runnable t1 = () -> {
            try {
                compServent.enrolCourse("comps1111","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t2 = () -> {
            try {
                compServent.enrolCourse("comps1111","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        Runnable t3 = () -> {
            try {
                compServent.enrolCourse("comps1111","comp1","fall");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread compThread = new Thread(t1);
        Thread compThread2 = new Thread(t2);
        Thread compThread3 = new Thread(t3);

        compThread.start();
        compThread2.start();
        compThread3.start();
    }
}
