package studentrentals.util; 

import java.util.UUID; 

public class Ids {
    private Ids() {}   //Prevent instantiation
    public static String newId() {
        return UUID.randomUUID().toString();
    }
}
