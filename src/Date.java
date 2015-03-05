import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;


public class Date {
    private static final int DEFAULT_YEAR = Year.now().getValue();
    private static final String INPUT_FORMAT_PATTERN = "d MMM[ y]";
    private static final String WARNING_DATE_BEFORE_TODAY = "Date must be after today";

    public Date(String input) throws DateTimeException {
        DateTimeFormatter formatter = createFormatter();

        LocalDate date = LocalDate.parse(input, formatter);
        if (date.isBefore(LocalDate.now())) {
            throw new DateTimeException(WARNING_DATE_BEFORE_TODAY);
        }
    }

    private DateTimeFormatter createFormatter() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        return builder.parseCaseInsensitive()
                      .appendPattern(INPUT_FORMAT_PATTERN)
                      .parseDefaulting(ChronoField.YEAR_OF_ERA, DEFAULT_YEAR)
                      .toFormatter();
    }

}
