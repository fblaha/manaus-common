package cz.fb.manaus.reactor.charge;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertTrue;


public class MarketChargeSimulatorTest extends AbstractLocalTestCase {

    public static final Map<Long, Double> THREE_IMBALANCED = Map.of(
            CoreTestFactory.HOME, 0.5,
            CoreTestFactory.DRAW, 0.3,
            CoreTestFactory.AWAY, 0.2);

    public static final Map<Long, Double> THREE_BALANCED = Map.of(
            CoreTestFactory.HOME, 0.34,
            CoreTestFactory.DRAW, 0.33,
            CoreTestFactory.AWAY, 0.33);

    public static final Map<Long, Double> TWO_BALANCED = Map.of(
            CoreTestFactory.HOME, 0.5,
            CoreTestFactory.AWAY, 0.5);

    @Autowired
    private MarketChargeSimulator simulator;
    @Autowired
    private ExchangeProvider provider;


    private void assertDown(double before, double after) {
        assertTrue(after < before);
    }

    private void assertUp(double before, double after) {
        assertTrue(after > before);
    }

    @Test
    public void testCounterMatching() {
        var bets = LinkedListMultimap.<Long, Price>create();
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(1.8d, 2, Side.LAY), bets, THREE_IMBALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(2.2d, 2, Side.BACK), bets, THREE_IMBALANCED, this::assertDown);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(9d, 2, Side.BACK), bets, THREE_IMBALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(6d, 2, Side.LAY), bets, THREE_IMBALANCED, this::assertDown);
    }

    @Test
    public void testBackMatchingThree() {
        var bets = LinkedListMultimap.<Long, Price>create();
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(3.5, 2, Side.BACK), bets, THREE_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.DRAW, new Price(3.5, 2, Side.BACK), bets, THREE_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(3.5, 2, Side.BACK), bets, THREE_BALANCED, this::assertDown);
    }

    @Test
    public void testLowMatchedAmount() {
        var bets = LinkedListMultimap.<Long, Price>create();
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(2.1, 0.75, Side.BACK), bets, TWO_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(1.9, 2, Side.LAY), bets, TWO_BALANCED, this::assertUp);

        checkSimulatedCharge(CoreTestFactory.HOME, new Price(2.5, 1, Side.BACK), bets, TWO_BALANCED, this::assertDown);
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(2.5, 1, Side.BACK), bets, TWO_BALANCED, this::assertUp);
    }

    @Test
    public void testBackMatchingTwo() {
        var bets = LinkedListMultimap.<Long, Price>create();
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(2.1, 2, Side.BACK), bets, TWO_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(2.1, 2, Side.BACK), bets, TWO_BALANCED, this::assertDown);
    }

    @Test
    public void testLayMatchingTwo() {
        ListMultimap<Long, Price> bets = LinkedListMultimap.create();
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(1.8, 2, Side.LAY), bets, TWO_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(1.9, 2, Side.LAY), bets, TWO_BALANCED, this::assertDown);
    }

    @Test
    public void testAllGreenCrossMatching() {
        ListMultimap<Long, Price> bets = LinkedListMultimap.create();
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(1.8, 2, Side.LAY), bets, TWO_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(2.2, 2, Side.BACK), bets, TWO_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(1.9, 2, Side.LAY), bets, TWO_BALANCED, this::assertDown);
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(2.3, 2, Side.BACK), bets, TWO_BALANCED, this::assertDown);
    }

    @Test
    public void testLayMatchingThree() {
        ListMultimap<Long, Price> bets = LinkedListMultimap.create();
        checkSimulatedCharge(CoreTestFactory.HOME, new Price(2.7, 2.5, Side.LAY), bets, THREE_BALANCED, this::assertUp);
        checkSimulatedCharge(CoreTestFactory.DRAW, new Price(3, 2, Side.LAY), bets, THREE_BALANCED, this::assertDown);
        checkSimulatedCharge(CoreTestFactory.AWAY, new Price(2.9, 2, Side.LAY), bets, THREE_BALANCED, this::assertDown);
    }

    private void checkSimulatedCharge(long selection, Price newBet, ListMultimap<Long, Price> bets,
                                      Map<Long, Double> probabilities, BiConsumer<Double, Double> assertion) {
        double before = simulator.getChargeMean(1, provider.getChargeRate(), probabilities, bets);
        bets.put(selection, newBet);
        double after = simulator.getChargeMean(1, provider.getChargeRate(), probabilities, bets);
        assertion.accept(before, after);
    }
}