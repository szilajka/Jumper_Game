package jumper.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineHelperTest {

  @Test
  void calculatePoints() {
    assertEquals(50, GameEngineHelper.calculatePoints(10, 5, 10),
        "Expected value is 50, but the actual is not that.");
    assertEquals(100, GameEngineHelper.calculatePoints(10, 3, 7),
        "Expected value is 100, but the actual is not that.");
  }
}
