package studentrentals.service;

import studentrentals.model.property.Property;
import studentrentals.model.property.Room;
import studentrentals.repo.PropertyRepository;
import studentrentals.repo.RoomRepository;
import studentrentals.search.SearchQuery;
import studentrentals.search.SearchStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SearchService {
    private final PropertyRepository propertyRepo;
    private final RoomRepository roomRepo;

    private final Map<String, String> cityByRoomId = new HashMap<>();
    private SearchStrategy strategy;

    public SearchService(PropertyRepository propertyRepo, RoomRepository roomRepo, SearchStrategy strategy) {
        this.propertyRepo = propertyRepo;
        this.roomRepo = roomRepo;
        this.strategy = strategy;
        rebuildCityCache();
    }

    public void setStrategy(SearchStrategy strategy) { this.strategy = strategy; }
    public String strategyName() { return strategy.name(); }

    public java.util.List<Room> search(SearchQuery q) {
        rebuildCityCache();
        return strategy.search(q);
    }

    public Optional<Property> propertyOfRoom(Room r) {
        return propertyRepo.findById(r.propertyId());
    }

    public Optional<Room> roomById(String id) { return roomRepo.findById(id); }

    public String cityOfRoomId(String roomId) { return cityByRoomId.get(roomId); }

    private void rebuildCityCache() {
        cityByRoomId.clear();
        for (Property p : propertyRepo.findAll()) {
            for (String roomId : p.roomIds()) {
                cityByRoomId.put(roomId, p.cityOrArea());
            }
        }
    }
}
