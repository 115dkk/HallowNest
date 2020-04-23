package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.monsters.CityofTearsEnemies.monsterMantisYouth;
import Hallownest.monsters.KingdomsEdgeEnemies.minionWingedZoteling;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class powerFlying extends AbstractPower {
    private int storedAmount;

    public static final String POWER_ID = HallownestMod.makeID("powerFlying");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public powerFlying(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.storedAmount = amount;
        this.updateDescription();
        this.loadRegion("flight");
        this.priority = 50;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_FLIGHT", 0.05F);
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void atStartOfTurn() {
        this.amount = this.storedAmount;
        this.updateDescription();
    }

    public float atDamageFinalReceive(float damage, DamageType type) {
        return this.calculateDamageTakenAmount(damage, type);
    }

    private float calculateDamageTakenAmount(float damage, DamageType type) {
        return type != DamageType.HP_LOSS && type != DamageType.THORNS ? damage / 2.0F : damage;
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        Boolean willLive = this.calculateDamageTakenAmount((float)damageAmount, info.type) < (float)this.owner.currentHealth;
        if (info.owner != null && info.type != DamageType.HP_LOSS && info.type != DamageType.THORNS && damageAmount > 0 && willLive) {
            this.flash();
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }

        return damageAmount;
    }

    public void onRemove() {
        //trigger the  void in youyth
        if (this.owner.id == minionWingedZoteling.ID){
            ((minionWingedZoteling) this.owner).Grounded();
        } else {
            ((monsterMantisYouth) this.owner).Grounded();
        }

    }

}

