package Main;

import java.time.*;

/**
 * Conversion Time for the application
 * @author Elton Mannil
 */
public class ConversionTime {
    /**
     * Converts the time variable to Eastern Time.
     * Typically used for testing whether an appointment falls outside of business hours.
     * @param date
     * @param time
     * @return
     */
    public static LocalTime convertToEasternTime(LocalDate date, LocalTime time){
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime convertedZone = zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalTime easternTime = convertedZone.toLocalTime();
        return easternTime;
    }

    /**
     * Converts the date variable to Eastern Time zone.
     * Commonly utilized to check if an appointment falls on a weekend day.
     * @param date
     * @param time
     * @return
     */
    public static LocalDate convertToEasternDate(LocalDate date, LocalTime time){
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime convertedZone = zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDate easternDate = convertedZone.toLocalDate();
        return easternDate;
    }
}
