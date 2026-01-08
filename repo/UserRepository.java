package studentrentals.repo;

import java.util.*;
import studentrentals.model.user.User;

public final class UserRepository {
    private final Map<String, User> byId = new HashMap<>();
    private final Map<String, String> idByEmail = new HashMap<>();

    public Optional<User> findByEmail(String email) {
        String id = idByEmail.get(email.toLowerCase());
        if (id == null) return Optional.empty();
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public void save(User user) {
        byId.put(user.id(), user);
        idByEmail.put(user.email(), user.id());
    }

    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(byId.values());
    }
}
