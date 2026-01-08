package studentrentals.search;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import studentrentals.model.property.Room;
import studentrentals.service.BookingService;

public final class IndexedSearchStrategy implements SearchStrategy {
    private final RoomSearchIndex index;
    private final BookingService bookingService;

    public IndexedSearchStrategy(RoomSearchIndex index, BookingService bookingService) {
        this.index = index;
        this.bookingService = bookingService;
    }

    @Override
    public List<Room> search(SearchQuery q) {
        Set<String> s1 = index.byCity(q.cityOrArea());
        Set<String> s2 = index.byType(q.roomType());
        Set<String> s3 = index.byPriceRange(q.minPrice(), q.maxPrice());

        Set<String> ids = intersect(s1, s2, s3);

        List<Room> rooms = index.resolveRoomIds(ids);

        if (q.startDate().isPresent() && q.endDate().isPresent()) {
            LocalDate s = q.startDate().get();
            LocalDate e = q.endDate().get();
            rooms.removeIf(r -> !r.windowContains(s, e) || !bookingService.isRoomFree(r.id(), s, e));
        }

        return rooms;
    }

    private Set<String> intersect(Set<String> a, Set<String> b, Set<String> c) {
        Set<String> out = new HashSet<>(a);
        out.retainAll(b);
        out.retainAll(c);
        return out;
    }

    @Override
    public String name() { return "Indexed"; }
}
