package studentrentals.model.user;

import studentrentals.model.role.Role;

public final class Admin extends User {
    public Admin(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
    }

    @Override public Role role() { return Role.ADMIN; }
}
