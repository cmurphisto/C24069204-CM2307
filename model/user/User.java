package studentrentals.model.user;

import studentrentals.model.role.Role;

public abstract class User {
    private final String id;
    private String name;
    private final String email;
    private String passwordHash;
    private boolean active = true;

    protected User(String id, String name, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email.toLowerCase();
        this.passwordHash = passwordHash;
    }

    public abstract Role role();

    public String id() { return id; }
    public String name() { return name; }
    public String email() { return email; }
    public String passwordHash() { return passwordHash; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void deactivate() { this.active = false; }
}
