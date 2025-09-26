package com.rewards.util;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "rewards")
public class RewardProperties {

    /**
     * Minimum spend to start earning points
     */
    @Min(0)
    private int minAmtSpendForPoints;

    /**
     * Minimum spend to start earning bonus points
     */
    @Min(0)
    private int minAmtSpendForBonus;

    /**
     * Multiplier for dollars spent above bonus threshold
     */
    @Min(1)
    private int multiplier;

    public int getMinAmtSpendForPoints() {
        return minAmtSpendForPoints;
    }

    public void setMinAmtSpendForPoints(int minAmtSpendForPoints) {
        this.minAmtSpendForPoints = minAmtSpendForPoints;
    }

    public int getMinAmtSpendForBonus() {
        return minAmtSpendForBonus;
    }

    public void setMinAmtSpendForBonus(int minAmtSpendForBonus) {
        this.minAmtSpendForBonus = minAmtSpendForBonus;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}
