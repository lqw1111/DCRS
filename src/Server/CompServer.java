package Server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CompServer {
    private static void startRegistry(int RMIPortNum)
            throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
        }
        catch (RemoteException ex) {
            System.out.println(
                    "RMI registry cannot be located at port "
                            + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println(
                    "RMI registry created at port " + RMIPortNum);
        }
    }
    public static void main(String[] args) {

        try{
            CompServent exportedObj = new CompServent();
            startRegistry(1234);

            String registryURL = "rmi://localhost:1234/comp";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Comp Server ready.");
        }
        catch (Exception re) {
            System.out.println("Exception in SomeServer.main: " + re);
        }
    }
}
