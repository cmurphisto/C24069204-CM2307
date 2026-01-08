package studentrentals.repo;

import java.util.*;
import studentrentals.model.booking.Booking;

public final class BookingRepository {
    private final Map<String, Booking> byId = new HashMap<>();
    private final Map<String, List<String>> bookingIdsByRoomId = new HashMap<>();
    private final Map<String, List<String>> bookingIdsByStudentId = new HashMap<>();
    private final Map<String, List<String>> bookingIdsByHomeownerId = new HashMap<>();

    public void save(Booking booking) {
        byId.put(booking.id(), booking);

        bookingIdsByRoomId.computeIfAbsent(booking.roomId(), k -> new ArrayList<>()).add(booking.id());
        bookingIdsByStudentId.computeIfAbsent(booking.studentId(), k -> new ArrayList<>()).add(booking.id());
        bookingIdsByHomeownerId.computeIfAbsent(booking.homeownerId(), k -> new ArrayList<>()).add(booking.id());
    }

    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<Booking> findByRoomId(String roomId) {
        return idsToBookings(bookingIdsByRoomId.getOrDefault(roomId, List.of()));
    }

    public List<Booking> findByStudentId(String studentId) {
        return idsToBookings(bookingIdsByStudentId.getOrDefault(studentId, List.of()));
    }

    public List<Booking> findByHomeownerId(String homeownerId) {
        return idsToBookings(bookingIdsByHomeownerId.getOrDefault(homeownerId, List.of()));
    }

    private List<Booking> idsToBookings(List<String> ids) {
        List<Booking> out = new ArrayList<>();
        for (String id : ids) {
            Booking b = byId.get(id);
            if (b != null) out.add(b);
        }
        return out;
    }
}
