package cz.fb.manaus.reactor.betting.action;

import com.google.common.base.Joiner;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.fb.manaus.core.category.categorizer.WeekDayCategorizer.getWeekDay;
import static java.util.Objects.requireNonNull;

@DatabaseComponent
public class ActionSaver {

    public static final String PROPOSER_STATS = "proposer.stats";
    private static final Logger log = Logger.getLogger(ActionSaver.class.getSimpleName());
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private MarketPricesDao pricesDao;
    @Autowired
    private PropertiesService service;
    @Autowired
    private BetUtils betUtils;

    public void setBetId(String betId, int actionId, String marketId, long selectionId) {
        replaceExistingBetId(betId, marketId, selectionId);
        betActionDao.setBetId(actionId, betId);
    }

    public void saveAction(BetAction action) {
        MarketPrices prices = action.getMarketPrices();
        if (!Optional.ofNullable(prices.getId()).isPresent()) {
            pricesDao.saveOrUpdate(prices);
            requireNonNull(prices.getId());
        }

        Date date = new Date();
        String proposers = action.getProperties().get(BetAction.PROPOSER_PROP);
        String side = action.getPrice().getSide().name().toLowerCase();
        for (String proposer : betUtils.parseProposers(proposers)) {
            String key = Joiner.on('.').join(PROPOSER_STATS, getWeekDay(date), side, proposer);
            service.incrementAntGet(key, Duration.ofDays(1));
        }
        betActionDao.saveOrUpdate(action);
    }

    private void replaceExistingBetId(String betId, String marketId, long selectionId) {
        long time = Instant.now().getEpochSecond();
        String previousBetId = betId + "_" + Long.toHexString(time);
        int updatedCount = betActionDao.updateBetId(betId, previousBetId, marketId, selectionId);
        if (updatedCount > 0) {
            log.log(Level.INFO, "Previous action bet id set to ''{0}''", previousBetId);
        }
    }

}
