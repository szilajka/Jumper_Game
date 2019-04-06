package jumper.helpers;

import java.util.concurrent.TimeUnit;

/**
 * A {@code Time Helper} class.
 * <p>
 * This class helps to convert elapsed seconds seconds into Days Hours:Minutes:Seconds format.
 */
public class TimeHelper {
    /**
     * Converts the given elapsed time (in seconds) to {@code Days HH:mm:ss} format.
     *
     * @param elapsedSeconds seconds to convert into the format
     * @return the formatted {@code string}
     */
    public static String convertSecondsToDuration(int elapsedSeconds) {
        long elapsedLong = Long.valueOf(elapsedSeconds);
        long days = TimeUnit.SECONDS.toDays(elapsedLong);
        long hours = TimeUnit.SECONDS.toHours(elapsedLong) -
            TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.SECONDS.toMinutes(elapsedLong)
            - (TimeUnit.DAYS.toMinutes(days) + TimeUnit.HOURS.toMinutes(hours));
        long seconds = elapsedLong - (TimeUnit.DAYS.toSeconds(days)
            + TimeUnit.HOURS.toSeconds(hours) + TimeUnit.MINUTES.toSeconds(minutes));

        return String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
    }
}
