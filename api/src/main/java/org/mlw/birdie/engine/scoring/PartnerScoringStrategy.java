package org.mlw.birdie.engine.scoring;

import org.mlw.birdie.Hand;
import org.mlw.birdie.ScoringStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartnerScoringStrategy implements ScoringStrategy {

    private static final Logger log = LoggerFactory.getLogger(PartnerScoringStrategy.class);

    @Override
    public int[] calculate(Hand hand) {
        int[] scores = hand.getScores();
        int[] teams = new int[scores.length];
        for(int i=0, length=scores.length; i<length; i++){
            teams[i] = scores[i%length] + scores[(i+2)%length];
            log.info(i + " --> " + teams[i]);
        }

        return teams;
    }
}
