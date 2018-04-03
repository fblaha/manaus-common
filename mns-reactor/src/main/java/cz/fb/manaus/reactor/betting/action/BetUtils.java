package cz.fb.manaus.reactor.betting.action;

import com.google.common.base.Splitter;
import com.google.common.collect.Ordering;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class BetUtils {

    public List<BetAction> getCurrentActions(List<BetAction> bets) {
        LinkedList<BetAction> lastUpdates = new LinkedList<>();
        if (bets != null) {
            BetAction first = bets.get(0);
            for (BetAction bet : bets) {
                validate(first, bet);
                if (bet.getBetActionType() != BetActionType.UPDATE) lastUpdates.clear();
                lastUpdates.addLast(bet);
            }
        }
        checkState(Ordering.from(comparing(BetAction::getActionDate)).isStrictlyOrdered(lastUpdates));
        return lastUpdates;
    }

    private void validate(BetAction first, BetAction bet) {
        checkArgument(first.getPrice().getSide() == bet.getPrice().getSide());
        checkArgument(first.getSelectionId() == bet.getSelectionId());
    }

    public List<Bet> getUnknownBets(List<Bet> bets, Set<String> myBets) {
        return bets.stream().filter(bet -> !myBets.contains(bet.getBetId())).collect(toList());
    }

    public List<String> parseProposers(String proposers) {
        return Splitter.on(',').omitEmptyStrings().trimResults().splitToList(proposers);
    }

    public List<Bet> filterDuplicates(List<Bet> bets) {
        List<Bet> result = new LinkedList<>();
        for (Collection<Bet> theSameBets : bets.stream().collect(groupingBy(Bet::getBetId)).values()) {
            result.add(theSameBets.stream().max(Comparator.comparingDouble(Bet::getMatchedAmount)).get());
        }
        return result;
    }

    public SettledBet ceilAmount(double ceiling, SettledBet bet) {
        Price origPrice = bet.getPrice();
        double amount = origPrice.getAmount();
        if (ceiling < amount) {
            double rate = ceiling / amount;
            SettledBet copy = new SettledBet();
            BeanUtils.copyProperties(bet, copy);
            copy.setProfitAndLoss(rate * bet.getProfitAndLoss());
            copy.setProfitAndLoss(rate * bet.getProfitAndLoss());
            Price newPrice = new Price();
            BeanUtils.copyProperties(origPrice, newPrice);
            newPrice.setAmount(ceiling);
            copy.setPrice(newPrice);
            return copy;
        }
        return bet;
    }
}
