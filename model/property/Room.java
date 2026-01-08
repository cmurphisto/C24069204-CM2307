package studentrentals.model.property;

import java.time.LocalDate;
import studentrentals.util.Dates;
import studentrentals.util.DomainException;

public final class Room {
    private final String id;
    private final String propertyId;

    private RoomType type;
    private int monthlyRent;
    private String amenities;
    private LocalDate availableFrom;
    private LocalDate availableTo;

    public Room(String id, String propertyId, RoomType type, int monthlyRent, String amenities,
                LocalDate availableFrom, LocalDate availableTo) {
        this.id = id;
        this.propertyId = propertyId;
        this.type = type;
        setMonthlyRent(monthlyRent);
        this.amenities = amenities;
        setAvailability(availableFrom, availableTo);
    }

    public String id() { return id; }
    public String propertyId() { return propertyId; }

    public RoomType type() { return type; }
    public int monthlyRent() { return monthlyRent; }
    public String amenities() { return amenities; }
    public LocalDate availableFrom() { return availableFrom; }
    public LocalDate availableTo() { return availableTo; }

    public void setType(RoomType type) { this.type = type; }

    public void setMonthlyRent(int monthlyRent) {
        if (monthlyRent < 0) throw new DomainException("Monthly rent must be >= 0.");
        this.monthlyRent = monthlyRent;
    }

    public void setAmenities(String amenities) { this.amenities = amenities; }

    public void setAvailability(LocalDate from, LocalDate to) {
        Dates.requireStartBeforeEnd(from, to, "Room availability");
        this.availableFrom = from;
        this.availableTo = to;
    }

    public boolean windowContains(LocalDate start, LocalDate end) {
        return (start.isAfter(availableFrom) || start.isEqual(availableFrom))
                && (end.isBefore(availableTo) || end.isEqual(availableTo));
    }
}
