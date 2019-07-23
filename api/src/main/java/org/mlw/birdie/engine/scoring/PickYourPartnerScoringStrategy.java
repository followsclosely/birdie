package org.mlw.birdie.engine.scoring;

import org.mlw.birdie.Hand;
import org.mlw.birdie.ScoringStrategy;

public class PickYourPartnerScoringStrategy implements ScoringStrategy {
    @Override public int[] calculate(Hand hand) {
        return hand.getScores();
    }
}
