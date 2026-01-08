package studentrentals.model.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentrentals.util.DomainException;

public final class Property {
    private final String id;
    private final String homeownerId;

    private String address;
    private String cityOrArea;
    private String description;

    private final List<String> roomIds = new ArrayList<>();
    private final List<String> reviewIds = new ArrayList<>();
    private int reviewCount = 0;
    private int reviewStarSum = 0;

    public Property(String id, String homeownerId, String address, String cityOrArea, String description) {
        if (address == null || address.isBlank()) throw new DomainException("Address required.");
        if (cityOrArea == null || cityOrArea.isBlank()) throw new DomainException("City/area required.");
        this.id = id;
        this.homeownerId = homeownerId;
        this.address = address;
        this.cityOrArea = cityOrArea;
        this.description = description;
    }

    public String id() { return id; }
    public String homeownerId() { return homeownerId; }

    public String address() { return address; }
    public String cityOrArea() { return cityOrArea; }
    public String description() { return description; }

    public void setAddress(String address) { this.address = address; }
    public void setCityOrArea(String cityOrArea) { this.cityOrArea = cityOrArea; }
    public void setDescription(String description) { this.description = description; }

    public List<String> roomIds() { return Collections.unmodifiableList(roomIds); }
    public void addRoomId(String roomId) { roomIds.add(roomId); }
    public void removeRoomId(String roomId) { roomIds.remove(roomId); }

    public void addReview(String reviewId, int stars) {
        reviewIds.add(reviewId);
        reviewCount++;
        reviewStarSum += stars;
    }

    public double averageRating() {
        if (reviewCount == 0) return 0.0;
        return (double) reviewStarSum / (double) reviewCount;
    }

    public int reviewCount() { return reviewCount; }
}
