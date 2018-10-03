package LogTest;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;


public class LogDemo {
    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger("com.xiya.test.LogDemo");
        logger.setLevel(Level.ALL);
//        ConsoleHandler consoleHandler = new ConsoleHandler();
//        logger.addHandler(consoleHandler);
        FileHandler fileHandler = new FileHandler("testLog.log");
        fileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(fileHandler);
        logger.info("hi");
    }
}
