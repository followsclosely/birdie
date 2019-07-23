package org.mlw.birdie;

public interface ScoringStrategy {
    int[] calculate(Hand hand);
}