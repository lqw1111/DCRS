package Client;

import RemoteObjectInterface.Servent;
import entity.certificateIdentity;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class AdvisorClient {

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

    public static void main(String[] args) throws RemoteException {
        System.out.println("Input your id:");
        Scanner sc = new Scanner(System.in);
        String cmd = sc.nextLine();
        System.out.println(cmd);

        String port = "";
        certificateIdentity certificateIdentity = parseStatus(cmd);
        if (certificateIdentity.getDepartment().equals("comp")){
            port = "1111";
        } else if (certificateIdentity.getDepartment().equals("inse")) {
            port = "2222";
        } else if (certificateIdentity.getDepartment().equals("soen")) {
            port = "3333";
        } else {
            System.out.println("Not Found");
            return;
        }

        Servent servent = connect(port, certificateIdentity.getDepartment());

        while (true) {
            cmd = sc.nextLine();
            System.out.println(cmd);

            if (cmd.equals("add")){
                servent.addCourse("","");
            }
        }
    }


}
