package studentrentals.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class Dates {
    private Dates() {}

    public static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    //Print a date string in ISO format
    public static LocalDate parse(String isoDate) {
        try {
            return LocalDate.parse(isoDate, FMT);
        } catch (DateTimeParseException e) {
            throw new DomainException("Invalid date. Use YYYY-MM-DD.");
        }
    }
    //Ensure start date is before or same as end date
    public static void requireStartBeforeEnd(LocalDate start, LocalDate end, String label) {
        if (start == null || end == null) throw new DomainException(label + " dates cannot be null.");
        if (!start.isBefore(end) && !start.isEqual(end)) {
            throw new DomainException(label + " start date must be before or equal to end date.");
        }
    }

    //Check if the dates overlap
    public static boolean overlaps(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return !(aEnd.isBefore(bStart) || bEnd.isBefore(aStart));
    }
}
