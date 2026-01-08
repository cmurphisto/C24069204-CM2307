package studentrentals.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import studentrentals.model.booking.Booking;
import studentrentals.model.booking.BookingStatus;
import studentrentals.model.property.Property;
import studentrentals.model.property.Room;
import studentrentals.repo.BookingRepository;
import studentrentals.repo.PropertyRepository;
import studentrentals.repo.RoomRepository;
import studentrentals.util.Dates;
import studentrentals.util.DomainException;
import studentrentals.util.Ids;

public final class BookingService {
    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;
    private final PropertyRepository propertyRepo;

    public BookingService(BookingRepository bookingRepo, RoomRepository roomRepo, PropertyRepository propertyRepo) {
        this.bookingRepo = bookingRepo;
        this.roomRepo = roomRepo;
        this.propertyRepo = propertyRepo;
    }

    public boolean isRoomFree(String roomId, LocalDate start, LocalDate end) {
        List<Booking> all = bookingRepo.findByRoomId(roomId);
        for (Booking b : all) {
            if (b.status() != BookingStatus.ACCEPTED) continue;
            if (Dates.overlaps(b.startDate(), b.endDate(), start, end)) return false;
        }
        return true;
    }

    public Booking requestBooking(String studentId, String roomId, LocalDate start, LocalDate end) {
        Dates.requireStartBeforeEnd(start, end, "Booking");
        Room r = roomRepo.findById(roomId).orElseThrow(() -> new DomainException("Room not found."));
        if (!r.windowContains(start, end)) throw new DomainException("Requested dates outside room availability window.");
        if (!isRoomFree(roomId, start, end)) throw new DomainException("Room already booked for those dates.");

        Property p = propertyRepo.findById(r.propertyId()).orElseThrow(() -> new DomainException("Property not found."));

        Booking b = new Booking(
                Ids.newId(),
                roomId,
                p.id(),
                studentId,
                p.homeownerId(),
                start,
                end,
                BookingStatus.REQUESTED
        );
        bookingRepo.save(b);
        return b;
    }

    public void homeownerDecide(String homeownerId, String bookingId, boolean accept) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new DomainException("Booking not found."));
        if (!b.homeownerId().equals(homeownerId)) throw new DomainException("Not your booking request.");

        if (b.status() != BookingStatus.REQUESTED) throw new DomainException("Booking is not in REQUESTED state.");

        if (accept) {
            if (!isRoomFree(b.roomId(), b.startDate(), b.endDate())) {
                b.setStatus(BookingStatus.REJECTED);
                return;
            }
            b.setStatus(BookingStatus.ACCEPTED);
        } else {
            b.setStatus(BookingStatus.REJECTED);
        }
    }

    public void cancelByStudent(String studentId, String bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new DomainException("Booking not found."));
        if (!b.studentId().equals(studentId)) throw new DomainException("Not your booking.");
        if (b.status() == BookingStatus.CANCELLED) return;

        if (b.status() == BookingStatus.REJECTED) throw new DomainException("Cannot cancel a rejected booking.");
        b.setStatus(BookingStatus.CANCELLED);
    }

    public List<Booking> bookingsForStudent(String studentId) {
        return bookingRepo.findByStudentId(studentId);
    }

    public List<Booking> bookingsForHomeowner(String homeownerId) {
        return bookingRepo.findByHomeownerId(homeownerId);
    }

    public boolean isRoomFreeUsingTreeSet(String roomId, LocalDate start, LocalDate end) {
        TreeSet<Booking> accepted = new TreeSet<>(Comparator
                .comparing(Booking::startDate)
                .thenComparing(Booking::endDate)
                .thenComparing(Booking::id));

        for (Booking b : bookingRepo.findByRoomId(roomId)) {
            if (b.status() == BookingStatus.ACCEPTED) accepted.add(b);
        }

        for (Booking b : accepted) {
            if (Dates.overlaps(b.startDate(), b.endDate(), start, end)) return false;
            if (b.startDate().isAfter(end)) break;
        }
        return true;
    }
}
