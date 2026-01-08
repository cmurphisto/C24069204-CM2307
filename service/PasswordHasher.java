package studentrentals.service; 

public class PasswordHasher {
    public String hashPassword(String password) {

        return "hashed_" + password;
    }

    public boolean verifyPassword(String password, String hashed) {

        return hashed.equals(hashPassword(password));
    }
}