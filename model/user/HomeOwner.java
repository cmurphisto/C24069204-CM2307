package studentrentals.model.user;

import studentrentals.model.role.Role;

public final class Homeowner extends User {
    public Homeowner(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
    }

    @Override public Role role() { return Role.HOMEOWNER; }
}
