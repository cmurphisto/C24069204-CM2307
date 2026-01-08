package studentrentals.model.booking;

import java.time.LocalDate;

public final class Booking {
    private final String id;
    private final String roomId;
    private final String propertyId;
    private final String studentId;
    private final String homeownerId;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private BookingStatus status;

    public Booking(String id, String roomId, String propertyId, String studentId, String homeownerId,
                   LocalDate startDate, LocalDate endDate, BookingStatus status) {
        this.id = id;
        this.roomId = roomId;
        this.propertyId = propertyId;
        this.studentId = studentId;
        this.homeownerId = homeownerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String id() { return id; }
    public String roomId() { return roomId; }
    public String propertyId() { return propertyId; }
    public String studentId() { return studentId; }
    public String homeownerId() { return homeownerId; }
    public LocalDate startDate() { return startDate; }
    public LocalDate endDate() { return endDate; }
    public BookingStatus status() { return status; }

    public void setStatus(BookingStatus status) { this.status = status; }

    public boolean endedBefore(LocalDate date) {
        return endDate.isBefore(date);
    }
}
