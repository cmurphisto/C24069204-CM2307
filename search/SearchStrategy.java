package studentrentals.search;

import studentrentals.model.property.Room;

import java.util.List;

public interface SearchStrategy {
    List<Room> search(SearchQuery q);
    String name();
}
