package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProbabilityComparatorTest extends AbstractLocalTestCase {

    @Autowired
    private ReactorTestFactory testFactory;

    @Test
    public void testCompare() throws Exception {
        RunnerPrices first = testFactory.newRP(1, 1.4d, 1.6d);
        RunnerPrices second = testFactory.newRP(2, 2.8d, 3.3d);
        assertThat(getFirstSelection(asList(first, first)), is(1l));
        assertThat(getFirstSelection(asList(first, second)), is(1l));
        assertThat(getFirstSelection(asList(second, first)), is(1l));
        assertThat(getFirstSelection(asList(second, second)), is(2l));
    }

    private long getFirstSelection(List<RunnerPrices> lists) {
        return ProbabilityComparator.COMPARATORS.get(Side.BACK).immutableSortedCopy(lists).get(0).getSelectionId();
    }
}