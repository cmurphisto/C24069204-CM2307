package studentrentals.service;

import studentrentals.model.property.Property;
import studentrentals.model.property.Room;
import studentrentals.model.property.RoomType;
import studentrentals.repo.PropertyRepository;
import studentrentals.repo.RoomRepository;
import studentrentals.search.RoomSearchIndex;
import studentrentals.util.DomainException;
import studentrentals.util.Ids;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public final class PropertyService {
    private final PropertyRepository propertyRepo;
    private final RoomRepository roomRepo;
    private final RoomSearchIndex index;

    public PropertyService(PropertyRepository propertyRepo, RoomRepository roomRepo, RoomSearchIndex index) {
        this.propertyRepo = propertyRepo;
        this.roomRepo = roomRepo;
        this.index = index;
    }

    public Property createProperty(String homeownerId, String address, String cityOrArea, String description) {
        Property p = new Property(Ids.newId(), homeownerId, address, cityOrArea, description);
        propertyRepo.save(p);
        return p;
    }

    public Room addRoom(String homeownerId, String propertyId, RoomType type, int rent, String amenities,
                        LocalDate from, LocalDate to) {
        Property p = propertyRepo.findById(propertyId).orElseThrow(() -> new DomainException("Property not found."));
        if (!p.homeownerId().equals(homeownerId)) throw new DomainException("Not your property.");

        Room r = new Room(Ids.newId(), propertyId, type, rent, amenities, from, to);
        roomRepo.save(r);
        p.addRoomId(r.id());
        propertyRepo.save(p);

        index.add(r, p.cityOrArea());
        return r;
    }

    public void updateRoom(String homeownerId, String roomId, RoomType type, int rent, String amenities,
                           LocalDate from, LocalDate to) {
        Room r = roomRepo.findById(roomId).orElseThrow(() -> new DomainException("Room not found."));
        Property p = propertyRepo.findById(r.propertyId()).orElseThrow(() -> new DomainException("Property missing."));
        if (!p.homeownerId().equals(homeownerId)) throw new DomainException("Not your property.");

        index.remove(r, p.cityOrArea());
        r.setType(type);
        r.setMonthlyRent(rent);
        r.setAmenities(amenities);
        r.setAvailability(from, to);
        roomRepo.save(r);
        index.add(r, p.cityOrArea());
    }

    public void removeRoom(String homeownerId, String roomId) {
        Room r = roomRepo.findById(roomId).orElseThrow(() -> new DomainException("Room not found."));
        Property p = propertyRepo.findById(r.propertyId()).orElseThrow(() -> new DomainException("Property missing."));
        if (!p.homeownerId().equals(homeownerId)) throw new DomainException("Not your property.");

        index.remove(r, p.cityOrArea());
        roomRepo.delete(roomId);
        p.removeRoomId(roomId);
        propertyRepo.save(p);
    }

    public void removeProperty(String homeownerId, String propertyId) {
        Property p = propertyRepo.findById(propertyId).orElseThrow(() -> new DomainException("Property not found."));
        if (!p.homeownerId().equals(homeownerId)) throw new DomainException("Not your property.");

        for (String roomId : List.copyOf(p.roomIds())) {
            Room r = roomRepo.findById(roomId).orElse(null);
            if (r != null) {
                index.remove(r, p.cityOrArea());
                roomRepo.delete(roomId);
            }
        }
        propertyRepo.delete(propertyId);
    }

    public List<Property> myProperties(String homeownerId) {
        return propertyRepo.findAll().stream()
                .filter(p -> p.homeownerId().equals(homeownerId))
                .collect(Collectors.toList());
    }
}
