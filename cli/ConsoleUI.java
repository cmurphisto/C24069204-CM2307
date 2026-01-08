package studentrentals.cli;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import studentrentals.model.booking.Booking;
import studentrentals.model.booking.BookingStatus;
import studentrentals.model.property.Property;
import studentrentals.model.property.Room;
import studentrentals.model.property.RoomType;
import studentrentals.model.role.Role;
import studentrentals.model.user.Homeowner;
import studentrentals.model.user.Student;
import studentrentals.model.user.User;
import studentrentals.search.SearchQuery;
import studentrentals.service.*;
import studentrentals.util.Dates;
import studentrentals.util.DomainException;

public final class ConsoleUI {
    private final AuthService auth;
    private final PropertyService propertyService;
    private final SearchService searchService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    private final Scanner sc = new Scanner(System.in);

    public ConsoleUI(AuthService auth,
                     PropertyService propertyService,
                     SearchService searchService,
                     BookingService bookingService,
                     ReviewService reviewService) {
        this.auth = auth;
        this.propertyService = propertyService;
        this.searchService = searchService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    public void run() {
        println("StudentRentals CLI (Java 17) - Prototype");
        while (true) {
            try {
                if (!auth.isLoggedIn()) guestMenu();
                else userMenu();
            } catch (DomainException e) {
                println("ERROR: " + e.getMessage());
            } catch (Exception e) {
                println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void guestMenu() {
        println("\n--- Guest Menu ---");
        println("1) Register Student");
        println("2) Register Homeowner");
        println("3) Login");
        println("0) Exit");
        switch (ask("Choose")) {
            case "1" -> registerStudent();
            case "2" -> registerHomeowner();
            case "3" -> login();
            case "0" -> { System.exit(0); }
            default -> println("Invalid option.");
        }
    }

    private void userMenu() {
        User u = auth.currentUser();
        println("\n--- User Menu (" + u.role() + ") ---");
        println("Logged in as: " + u.name() + " <" + u.email() + ">");
        println("Search strategy: " + searchService.strategyName());

        if (u.role() == Role.STUDENT) studentMenu((Student) u);
        else if (u.role() == Role.HOMEOWNER) homeownerMenu((Homeowner) u);
        else adminMenu(u);
    }

    private void studentMenu(Student s) {
        println("1) Search rooms");
        println("2) My bookings");
        println("3) Cancel booking");
        println("4) Leave review (after booking ended)");
        println("9) Logout");
        switch (ask("Choose")) {
            case "1" -> searchRooms();
            case "2" -> showBookingsStudent(s.id());
            case "3" -> cancelBookingStudent(s.id());
            case "4" -> leaveReview(s.id());
            case "9" -> auth.logout();
            default -> println("Invalid option.");
        }
    }

    private void homeownerMenu(Homeowner h) {
        println("1) Create property");
        println("2) My properties");
        println("3) Add room to property");
        println("4) Update room");
        println("5) Remove room");
        println("6) Remove property");
        println("7) Booking requests / decisions");
        println("8) All my bookings");
        println("9) Logout");
        switch (ask("Choose")) {
            case "1" -> createProperty(h.id());
            case "2" -> listMyProperties(h.id());
            case "3" -> addRoom(h.id());
            case "4" -> updateRoom(h.id());
            case "5" -> removeRoom(h.id());
            case "6" -> removeProperty(h.id());
            case "7" -> decideBookings(h.id());
            case "8" -> showBookingsHomeowner(h.id());
            case "9" -> auth.logout();
            default -> println("Invalid option.");
        }
    }

    private void adminMenu(User admin) {
        println("Admin menu not required for core prototype; admin account exists for extension.");
        println("9) Logout");
        if ("9".equals(ask("Choose"))) auth.logout();
    }

    private void registerStudent() {
        String name = ask("Name");
        String email = ask("Email");
        String password = ask("Password");
        String uni = ask("University name");
        String sid = ask("Student ID number");
        auth.registerStudent(name, email, password, uni, sid);
        println("Student registered.");
    }

    private void registerHomeowner() {
        String name = ask("Name");
        String email = ask("Email");
        String password = ask("Password");
        auth.registerHomeowner(name, email, password);
        println("Homeowner registered.");
    }

    private void login() {
        String email = ask("Email");
        String password = ask("Password");
        auth.login(email, password);
        println("Login successful.");
    }

    // Student actions
    private void searchRooms() {
        Optional<String> city = opt(ask("City/Area (blank for any)"));
        Optional<Integer> minP = optInt(ask("Min price (blank for any)"));
        Optional<Integer> maxP = optInt(ask("Max price (blank for any)"));
        Optional<LocalDate> start = optDate(ask("Move-in date YYYY-MM-DD (blank for any)"));
        Optional<LocalDate> end = optDate(ask("Move-out date YYYY-MM-DD (blank for any)"));
        Optional<RoomType> type = optRoomType(ask("Room type SINGLE/DOUBLE (blank for any)"));

        SearchQuery q = new SearchQuery(city, minP, maxP, start, end, type);
        List<Room> rooms = searchService.search(q);

        if (rooms.isEmpty()) {
            println("No rooms matched your search.");
            return;
        }

        println("\nResults:");
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            String roomCity = searchService.cityOfRoomId(r.id());
            println((i+1) + ") Room " + r.id() + " | " + r.type() + " | GBP " + r.monthlyRent()
                    + " | " + roomCity);
        }

        String pick = ask("Select result number to view details (or blank to return)");
        if (pick.isBlank()) return;
        int idx = Integer.parseInt(pick) - 1;
        if (idx < 0 || idx >= rooms.size()) { println("Invalid selection."); return; }

        Room chosen = rooms.get(idx);
        showRoomDetailsAndMaybeBook(chosen);
    }

    private void showRoomDetailsAndMaybeBook(Room r) {
        Property p = searchService.propertyOfRoom(r).orElse(null);
        println("\n--- Room Details ---");
        println("Room ID: " + r.id());
        println("Type: " + r.type());
        println("Rent: GBP " + r.monthlyRent());
        println("Amenities: " + r.amenities());
        println("Availability window: " + r.availableFrom() + " to " + r.availableTo());
        if (p != null) {
            println("\n--- Property ---");
            println("Property ID: " + p.id());
            println("Address: " + p.address());
            println("City/Area: " + p.cityOrArea());
            println("Description: " + p.description());
            println("Avg rating: " + String.format("%.2f", p.averageRating()) + " (" + p.reviewCount() + " reviews)");
        }

        String want = ask("Request booking? (y/n)");
        if (!want.equalsIgnoreCase("y")) return;

        LocalDate start = Dates.parse(ask("Start date YYYY-MM-DD"));
        LocalDate end = Dates.parse(ask("End date YYYY-MM-DD"));

        Booking b = bookingService.requestBooking(auth.currentUser().id(), r.id(), start, end);
        println("Booking requested. Booking ID: " + b.id());
    }

    private void showBookingsStudent(String studentId) {
        List<Booking> bs = bookingService.bookingsForStudent(studentId);
        if (bs.isEmpty()) { println("No bookings."); return; }
        for (Booking b : bs) {
            println(b.id() + " | Room " + b.roomId() + " | " + b.startDate() + " -> " + b.endDate()
                    + " | " + b.status());
        }
    }

    private void cancelBookingStudent(String studentId) {
        String bookingId = ask("Booking ID to cancel");
        bookingService.cancelByStudent(studentId, bookingId);
        println("Cancelled (if allowed).");
    }

    private void leaveReview(String studentId) {
        String bookingId = ask("Booking ID to review");
        int stars = Integer.parseInt(ask("Stars 1..5"));
        String comment = ask("Comment");
        reviewService.leaveReview(studentId, bookingId, stars, comment, LocalDate.now());
        println("Review submitted.");
    }

    private void createProperty(String homeownerId) {
        String address = ask("Address");
        String city = ask("City/Area");
        String desc = ask("Description");
        Property p = propertyService.createProperty(homeownerId, address, city, desc);
        println("Created property " + p.id());
    }

    private void listMyProperties(String homeownerId) {
        List<Property> props = propertyService.myProperties(homeownerId);
        if (props.isEmpty()) { println("No properties yet."); return; }
        for (Property p : props) {
            println(p.id() + " | " + p.cityOrArea() + " | " + p.address() + " | rooms=" + p.roomIds().size()
                    + " | avgRating=" + String.format("%.2f", p.averageRating()));
        }
    }

    private void addRoom(String homeownerId) {
        String propertyId = ask("Property ID");
        RoomType type = RoomType.valueOf(ask("RoomType SINGLE/DOUBLE").toUpperCase());
        int rent = Integer.parseInt(ask("Monthly rent"));
        String amenities = ask("Amenities text");
        LocalDate from = Dates.parse(ask("Available from YYYY-MM-DD"));
        LocalDate to = Dates.parse(ask("Available to YYYY-MM-DD"));
        Room r = propertyService.addRoom(homeownerId, propertyId, type, rent, amenities, from, to);
        println("Added room " + r.id());
    }

    private void updateRoom(String homeownerId) {
        String roomId = ask("Room ID");
        RoomType type = RoomType.valueOf(ask("RoomType SINGLE/DOUBLE").toUpperCase());
        int rent = Integer.parseInt(ask("Monthly rent"));
        String amenities = ask("Amenities text");
        LocalDate from = Dates.parse(ask("Available from YYYY-MM-DD"));
        LocalDate to = Dates.parse(ask("Available to YYYY-MM-DD"));
        propertyService.updateRoom(homeownerId, roomId, type, rent, amenities, from, to);
        println("Room updated.");
    }

    private void removeRoom(String homeownerId) {
        String roomId = ask("Room ID to remove");
        propertyService.removeRoom(homeownerId, roomId);
        println("Room removed.");
    }

    private void removeProperty(String homeownerId) {
        String propId = ask("Property ID to remove");
        propertyService.removeProperty(homeownerId, propId);
        println("Property removed.");
    }

    private void decideBookings(String homeownerId) {
        List<Booking> bs = bookingService.bookingsForHomeowner(homeownerId);
        if (bs.isEmpty()) { println("No bookings."); return; }

        println("REQUESTED bookings:");
        for (Booking b : bs) {
            if (b.status() == BookingStatus.REQUESTED) {
                println(b.id() + " | Room " + b.roomId() + " | " + b.startDate() + " -> " + b.endDate());
            }
        }

        String id = ask("Booking ID to decide (blank to return)");
        if (id.isBlank()) return;
        String ans = ask("Accept? (y/n)");
        bookingService.homeownerDecide(homeownerId, id, ans.equalsIgnoreCase("y"));
        println("Decision recorded.");
    }

    private void showBookingsHomeowner(String homeownerId) {
        List<Booking> bs = bookingService.bookingsForHomeowner(homeownerId);
        if (bs.isEmpty()) { println("No bookings."); return; }
        for (Booking b : bs) {
            println(b.id() + " | Room " + b.roomId() + " | " + b.startDate() + " -> " + b.endDate()
                    + " | " + b.status());
        }
    }
    private String ask(String label) {
        System.out.print(label + ": ");
        return sc.nextLine().trim();
    }

    private Optional<String> opt(String s) { return s == null || s.isBlank() ? Optional.empty() : Optional.of(s); }

    private Optional<Integer> optInt(String s) {
        if (s == null || s.isBlank()) return Optional.empty();
        return Optional.of(Integer.parseInt(s));
    }

    private Optional<LocalDate> optDate(String s) {
        if (s == null || s.isBlank()) return Optional.empty();
        return Optional.of(Dates.parse(s));
    }

    private Optional<RoomType> optRoomType(String s) {
        if (s == null || s.isBlank()) return Optional.empty();
        return Optional.of(RoomType.valueOf(s.toUpperCase()));
    }

    private void println(String s) { System.out.println(s); }
}
