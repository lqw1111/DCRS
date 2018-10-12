package LogTest;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String message = changeMessageFormat(record.getMessage());
        return "[" + new Date() + "]" + " [" + record.getLevel() + "] " + message + "\n";
    }

    private String changeMessageFormat(String message) {
        StringBuilder sb = new StringBuilder();
        String[] mess = message.split(":");
        for (String str :
                mess) {
            sb.append(addSpace(str, 40));
            sb.append("|");
        }
        return sb.toString();
    }

    private String addSpace(String str, Integer length){
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        int diff = length - str.length();
        for(int i = 0 ; i < diff ; i ++){
            sb.append(" ");
        }
        return sb.toString();
    }
}
