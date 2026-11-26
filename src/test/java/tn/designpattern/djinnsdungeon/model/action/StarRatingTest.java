package tn.designpattern.djinnsdungeon.model.action;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StarRatingTest {
    @Test void lostGivesNoStar() { assertEquals(0, StarRating.stars(false, 0, 100, 10, 60)); }
    @Test void wonWithLowHpGivesOneStar() { assertEquals(1, StarRating.stars(true, 30, 100, 10, 60)); }
    @Test void healthyButSlowGivesTwoStars() { assertEquals(2, StarRating.stars(true, 80, 100, 90, 60)); }
    @Test void healthyAndFastGivesThreeStars() { assertEquals(3, StarRating.stars(true, 80, 100, 40, 60)); }
    @Test void exactlyHalfHpCountsAsHealthy() { assertEquals(2, StarRating.stars(true, 50, 100, 90, 60)); }
}