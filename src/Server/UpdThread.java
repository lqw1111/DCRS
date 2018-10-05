package Server;

import entity.Course;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UpdThread implements Runnable {

    DatagramSocket socket = null;
    DatagramPacket packet = null;
    departmentServent servent = null;


    public UpdThread(DatagramSocket socket,DatagramPacket packet , departmentServent compServent) {
        this.socket = socket;
        this.packet = packet;
        this.servent = compServent;
    }

    @Override
    public void run() {
        String info = null;
        InetAddress address = null;
        int port = 8800;
        byte[] data2 = null;
        DatagramPacket packet2 = null;
        try {
            info = new String(packet.getData(), 0, packet.getLength());
            System.out.println("我是服务器，客户端说："+info);

            String[] command = info.split(" ");

            String result = "";

            switch(command[0]) {
                case "listCourseAvailability" :
                    System.out.println("list Course Availability");
                    List<String> courseList = servent.getLocalCourseList(command[1]);
                    for (String course :
                            courseList) {
                        result = result + course + " ";
                    }
                    break;
                case "enrolCourse" :
                    System.out.println("enroll Course");
                    result = servent.enrolCourse(command[1],command[2],command[3]);
                    break;
                case "dropCourse" :
                    System.out.println("Well done");
                    result = servent.dropLocalCourse(command[1],command[2],command[3]);
                    break;
                default :
                    System.out.println("Invalid Command!");
            }

            address = packet.getAddress();
            port = packet.getPort();

            data2 = result.getBytes();
            packet2 = new DatagramPacket(data2, data2.length, address, port);
            socket.send(packet2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //socket.close();不能关闭
    }

}
