package studentrentals.service;

import studentrentals.model.user.*;
import studentrentals.repo.UserRepository;
import studentrentals.util.DomainException;
import studentrentals.util.Ids;

import java.util.Optional;

public final class AuthService {
    private final UserRepository userRepo;
    private final PasswordHasher hasher;

    private User currentUser;

    public AuthService(UserRepository userRepo, PasswordHasher hasher) {
        this.userRepo = userRepo;
        this.hasher = hasher;
    }

    public User currentUser() { return currentUser; }
    public boolean isLoggedIn() { return currentUser != null; }

    public Student registerStudent(String name, String email, String password, String university, String studentIdNo) {
        requireUniqueEmail(email);
        String hash = hasher.sha256(password);
        Student s = new Student(Ids.newId(), name, email, hash, university, studentIdNo);
        userRepo.save(s);
        return s;
    }

    public Homeowner registerHomeowner(String name, String email, String password) {
        requireUniqueEmail(email);
        String hash = hasher.sha256(password);
        Homeowner h = new Homeowner(Ids.newId(), name, email, hash);
        userRepo.save(h);
        return h;
    }

    public Admin bootstrapAdminIfMissing(String name, String email, String password) {
        Optional<User> existing = userRepo.findByEmail(email);
        if (existing.isPresent()) return (Admin) existing.get();
        String hash = hasher.sha256(password);
        Admin a = new Admin(Ids.newId(), name, email, hash);
        userRepo.save(a);
        return a;
    }

    public void login(String email, String password) {
        User u = userRepo.findByEmail(email).orElseThrow(() -> new DomainException("No such user."));
        if (!u.isActive()) throw new DomainException("Account deactivated.");
        String hash = hasher.sha256(password);
        if (!hash.equals(u.passwordHash())) throw new DomainException("Invalid password.");
        currentUser = u;
    }

    public void logout() { currentUser = null; }

    private void requireUniqueEmail(String email) {
        if (userRepo.findByEmail(email).isPresent()) throw new DomainException("Email already registered.");
    }
}
