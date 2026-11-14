package ua.edu.nupp.kpp.server;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
class RiskCalculatorTest {

    @Test
    void controlExample() {
        // 0,40·52 + 0,35·60 + 0,25·70 = 20,8 + 21 + 17,5
        assertEquals(59.30, RiskCalculator.calculateIntegral(52, 60, 70), 0.001);
    }

    @Test
    void allFactorsZeroGiveZero() {
        assertEquals(0.0, RiskCalculator.calculateIntegral(0, 0, 0), 0.001);
    }

    @Test
    void allFactorsMaxGiveHundred() {
        assertEquals(100.0, RiskCalculator.calculateIntegral(100, 100, 100), 0.001);
    }

    @Test
    void weightsSumToOne() {
        assertEquals(1.0,
                RiskCalculator.W_F1 + RiskCalculator.W_F2 + RiskCalculator.W_F3, 1e-9);
    }

    @Test
    void resultIsRoundedToTwoDecimals() {
        assertEquals(62.58, RiskCalculator.calculateIntegral(70, 55, 61.3), 0.0001);
    }

    @Test
    void factorAboveRangeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculateIntegral(101, 50, 50));
    }

    @Test
    void negativeFactorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculateIntegral(50, -1, 50));
    }

    @Test
    void waitingTimeIsNormalizedAndCapped() {
        LocalDate today = LocalDate.of(2026, 11, 11);
        assertEquals(0.0, RiskCalculator.calculateF3(today, today), 0.001);
        assertEquals(50.0, RiskCalculator.calculateF3(today.minusDays(15), today), 0.001);
        assertEquals(100.0, RiskCalculator.calculateF3(today.minusDays(90), today), 0.001);
    }
}