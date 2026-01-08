package studentrentals.util; 
import java.util.*; 

public class Ids {
    public static String newId() {
        return UUID.randomUUID().toString();
    }
}