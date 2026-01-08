package studentrentals.repo;

import studentrentals.model.property.Property;

import java.util.*;

public final class PropertyRepository {
    private final Map<String, Property> byId = new HashMap<>();

    public void save(Property property) { byId.put(property.id(), property); }
    public Optional<Property> findById(String id) { return Optional.ofNullable(byId.get(id)); }
    public Collection<Property> findAll() { return Collections.unmodifiableCollection(byId.values()); }

    public void delete(String propertyId) { byId.remove(propertyId); }
}
