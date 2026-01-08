package studentrentals;

import studentrentals.cli.ConsoleUI;
import studentrentals.repo.*;
import studentrentals.search.*;
import studentrentals.service.*;

public final class Main {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();
        PropertyRepository propertyRepo = new PropertyRepository();
        RoomRepository roomRepo = new RoomRepository();
        BookingRepository bookingRepo = new BookingRepository();

        RoomSearchIndex index = new RoomSearchIndex();

        PasswordHasher hasher = new PasswordHasher();
        AuthService auth = new AuthService(userRepo, hasher);
        BookingService bookingService = new BookingService(bookingRepo, roomRepo, propertyRepo);
        PropertyService propertyService = new PropertyService(propertyRepo, roomRepo, index);

        SearchStrategy indexed = new IndexedSearchStrategy(index, bookingService);
        SearchService searchService = new SearchService(propertyRepo, roomRepo, indexed);

        ReviewService reviewService = new ReviewService(bookingRepo, propertyRepo);

        auth.bootstrapAdminIfMissing("Admin", "admin@studentrentals.local", "adminpass");

        ConsoleUI ui = new ConsoleUI(auth, propertyService, searchService, bookingService, reviewService);
        ui.run();
    }
}
