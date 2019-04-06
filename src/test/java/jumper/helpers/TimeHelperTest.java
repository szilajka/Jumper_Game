package jumper.helpers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeHelperTest {

    @Test
    void convertSecondsToDuration() {
        assertEquals("2 days 10:20:30", TimeHelper.convertSecondsToDuration(210030),
            "Expected String value is: '2 days 10:20:30', but the actual is not that.");
        assertEquals("0 days 10:20:30", TimeHelper.convertSecondsToDuration(37230),
            "Expected String value is: '0 days 10:20:30', but the actual is not that.");
    }
}
