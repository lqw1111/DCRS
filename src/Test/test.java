package Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class test {
    public static void main(String[] args) throws IOException {
        Map<String,Integer> map = new HashMap<>();
        map.put("1",1);

        Thread thread1 = new Thread(new Thread1(map));
        Thread thread2 = new Thread(new Thread1(map));
        thread1.start();
        thread2.start();
    }
}

class Thread1 implements Runnable{

    public Map<String, Integer> map;

    public Thread1(Map<String, Integer> map) {
        this.map = map;
    }

    @Override
    public void run() {
        for(int i = 0 ; i < 100 ; i ++){
            System.out.println(map.get("1"));
            int num = map.get("1");
            num = num + 1;
            map.put("1", num);
        }
    }
}