package studentrentals;

import studentrentals.cli.ConsoleUI;
import studentrentals.repo.*;
import studentrentals.search.*;
import studentrentals.service.*;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();
        PropertyRepository propertyRepo = new PropertyRepository();
        RoomRepository roomRepo = new RoomRepository();
        BookingRepository bookingRepo = new BookingRepository();
        RoomSearchIndex index = new RoomSearchIndex();

        AuthService auth = new AuthService(userRepo, new PasswordHasher());
        BookingService bookingService = new BookingService(bookingRepo, roomRepo, propertyRepo);
        PropertyService propertyService = new PropertyService(propertyRepo, roomRepo, index);
        SearchService searchService = new SearchService(propertyRepo, roomRepo,
                new IndexedSearchStrategy(index, bookingService));
        ReviewService reviewService = new ReviewService(bookingRepo, propertyRepo);

        auth.bootstrapAdminIfMissing("Admin", "admin@test.com", "admin");

        new ConsoleUI(auth, propertyService, searchService, bookingService, reviewService).run();
    }
}
