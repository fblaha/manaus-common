package cz.fb.manaus.manila;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DatabaseComponent
public class BestChanceLayBettor extends AbstractUpdatingBettor {

    @ManilaBet
    @Autowired
    public BestChanceLayBettor(List<Validator> validators,
                               BestChanceLayAdviser adviser) {
        super(Side.LAY, validators, adviser);
    }
}