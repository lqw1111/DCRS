package Server;

import LogTest.LoggerFormatter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class departmentServer {
    public departmentServent servent;
    public Logger logger;

    public departmentServer(departmentServent servent, Logger logger) throws IOException {
        this.servent = servent;
        this.logger = logger;
    }

    private void startRegistry(int RMIPortNum)
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

    private void startCompRmiServer(int port, String department){
        try{
            departmentServent exportedObj = this.servent;
            startRegistry(port);

            String registryURL = "rmi://localhost:" + Integer.toString(port) + "/" + department;
            Naming.rebind(registryURL, exportedObj);
            logger.info(department + " Server ready.");
        }
        catch (Exception re) {
            System.out.println("Exception in SomeServer.main: " + re);
        }
    }

    private void startUpdServer(int udpPort) throws Exception{
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket packet = null;
        byte[] data = null;
        int count = 0;
        logger.info(" Upd Server Start");
        while(true)
        {
            data = new byte[1024];//创建字节数组，指定接收的数据包的大小
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);//此方法在接收到数据报之前会一直阻塞

            Thread thread = new Thread(new UpdThread(socket, packet, this.servent));
            thread.start();
            count++;
            System.out.println("服务器端被连接过的次数：" + count);
            InetAddress address = packet.getAddress();
            System.out.println("当前客户端的IP为："+address.getHostAddress());

        }
    }

    public static void configLogger(String department , Logger logger) throws IOException {
        logger.setLevel(Level.ALL);
        FileHandler compFileHandler = new FileHandler(department + ".log");
        compFileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(compFileHandler);
    }

    public static void main(String[] args) throws Exception {

        Logger compLogger = Logger.getLogger("comp.server.log");
        configLogger("compServer",compLogger);

        Logger soenLogger = Logger.getLogger("soen.server.log");
        configLogger("soenServer",soenLogger);

        Logger inseLogger = Logger.getLogger("inse.server.log");
        configLogger("inseServer",inseLogger);

        departmentServent compServent = new departmentServent("comp", compLogger);
        departmentServer compServer = new departmentServer(compServent, compLogger);

        departmentServent soenServent = new departmentServent("soen", soenLogger);
        departmentServer soenServer = new departmentServer(soenServent, soenLogger);

        departmentServent inseServent = new departmentServent("inse", inseLogger);
        departmentServer inseServer = new departmentServer(inseServent , inseLogger);

        Runnable compTask = () -> {
            try {
                compServer.startUpdServer(1112);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Runnable compTask2 = () -> {
            compServer.startCompRmiServer(1111, "comp");
        };

        Runnable soneTask = () -> {
            try {
                soenServer.startUpdServer(2223);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Runnable soneTask2 = () -> {
            soenServer.startCompRmiServer(2222, "soen");
        };

        Runnable inseTask = () -> {
            try {
                inseServer.startUpdServer(3334);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Runnable inseTask2 = () -> {
            inseServer.startCompRmiServer(3333, "inse");
        };

        Thread compThread = new Thread(compTask);
        Thread compThread2 = new Thread(compTask2);

        Thread soenThread = new Thread(soneTask);
        Thread soenThread2 = new Thread(soneTask2);

        Thread inseThread = new Thread(inseTask);
        Thread inseThread2 = new Thread(inseTask2);

        compThread.start();
        compThread2.start();

        soenThread.start();
        soenThread2.start();

        inseThread.start();
        inseThread2.start();

//        try{
//            CompServent exportedObj = CompServent.getInstance();
//            startRegistry(1234);
//
//            String registryURL = "rmi://localhost:1234/comp";
//            Naming.rebind(registryURL, exportedObj);
//            System.out.println("Comp Server ready.");
//        }
//        catch (Exception re) {
//            System.out.println("Exception in SomeServer.main: " + re);
//        }
    }
}
