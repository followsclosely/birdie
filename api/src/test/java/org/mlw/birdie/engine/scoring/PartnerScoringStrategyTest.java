package org.mlw.birdie.engine.scoring;

import org.junit.Test;
import org.mlw.birdie.Hand;
import org.mlw.birdie.RookTestUtils;

import static org.junit.Assert.assertEquals;

public class PartnerScoringStrategyTest {

    @Test
    public void calculate() {
        PartnerScoringStrategy strategy = new PartnerScoringStrategy();

        Hand hand = new RookTestUtils.HandBuilder(4,0)
                .hand(0,0,"R1", "R2", "R5", "R4")
                .hand(0,0,"B1", "B6", "B10", "B14")
                .hand(0,3,"G5", "G6", "G11", "G14")
                .build();
        int[] score = strategy.calculate(hand);

        assertEquals(55, score[0]);
        assertEquals(15, score[1]);
        assertEquals(55, score[2]);
        assertEquals(15, score[3]);
    }
}