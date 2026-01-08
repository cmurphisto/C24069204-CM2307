package studentrentals.service;

import studentrentals.model.booking.Booking;
import studentrentals.model.booking.BookingStatus;
import studentrentals.model.property.Property;
import studentrentals.model.review.Review;
import studentrentals.repo.BookingRepository;
import studentrentals.repo.PropertyRepository;
import studentrentals.util.DomainException;
import studentrentals.util.Ids;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ReviewService {
    private final BookingRepository bookingRepo;
    private final PropertyRepository propertyRepo;
    private final List<Review> reviews = new ArrayList<>();

    public ReviewService(BookingRepository bookingRepo, PropertyRepository propertyRepo) {
        this.bookingRepo = bookingRepo;
        this.propertyRepo = propertyRepo;
    }

    public Review leaveReview(String studentId, String bookingId, int stars, String comment, LocalDate today) {
        if (stars < 1 || stars > 5) throw new DomainException("Stars must be 1..5.");
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new DomainException("Booking not found."));
        if (!b.studentId().equals(studentId)) throw new DomainException("Not your booking.");
        if (b.status() != BookingStatus.ACCEPTED) throw new DomainException("Can only review an ACCEPTED booking.");
        if (!b.endedBefore(today)) throw new DomainException("Can only review after the booking period has ended.");

        Property p = propertyRepo.findById(b.propertyId()).orElseThrow(() -> new DomainException("Property not found."));
        Review r = new Review(Ids.newId(), b.propertyId(), b.roomId(), studentId, stars, comment, today);
        reviews.add(r);

        p.addReview(r.id(), stars);
        propertyRepo.save(p);

        return r;
    }

    public List<Review> reviewsForProperty(String propertyId) {
        List<Review> out = new ArrayList<>();
        for (Review r : reviews) {
            if (r.propertyId().equals(propertyId)) out.add(r);
        }
        return out;
    }
}
