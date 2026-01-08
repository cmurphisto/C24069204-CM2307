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

        PasswordHasher hasher = new PasswordHasher();
        AuthService authService = new AuthService(userRepo, hasher);
        BookingService bookingService = new BookingService(bookingRepo, roomRepo, propertyRepo);
        PropertyService propertyService = new PropertyService(propertyRepo, roomRepo, index);

        SearchStrategy strategy = new IndexedSearchStrategy(index, bookingService);
        SearchService searchService = new SearchService(propertyRepo, roomRepo, strategy);

        ReviewService reviewService = new ReviewService(bookingRepo, propertyRepo);

        authService.bootstrapAdminIfMissing(
                "Admin",
                "admin@studentrentals.local",
                "admin"
        );

        new ConsoleUI(
                authService,
                propertyService,
                searchService,
                bookingService,
                reviewService
        ).run();
    }
}
