package studentrentals.model.user;

import studentrentals.model.role.Role;

public final class Student extends User {
    private String universityName;
    private String studentIdNumber;

    public Student(String id, String name, String email, String passwordHash,
                   String universityName, String studentIdNumber) {
        super(id, name, email, passwordHash);
        this.universityName = universityName;
        this.studentIdNumber = studentIdNumber;
    }

    @Override public Role role() { return Role.STUDENT; }

    public String universityName() { return universityName; }
    public String studentIdNumber() { return studentIdNumber; }

    public void setUniversityName(String universityName) { this.universityName = universityName; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }
}
