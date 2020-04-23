package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.monsters.CityofTearsEnemies.monsterMistake;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.PoisonLoseHpAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static Hallownest.HallownestMod.makePowerPath;

public class powerTragicImmortality extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = HallownestMod.makeID("powerTragicImmortality");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int HealVal;
    private int HealAmount;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("TragicImmortalityPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("TragicImmortalityPower32.png"));

    public powerTragicImmortality(AbstractCreature owner, int HealPer) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.HealVal = HealPer;
        this.type = PowerType.BUFF;
        this.HealAmount = 0;
        this.amount = 0;
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }
    @Override
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        if ((card.costForTurn == 0) || (card.costForTurn == 1)){
            this.HealAmount++;
            updateDescription();
        }
    }

    public void atStartOfTurn() {
        if (this.HealAmount >0) {
            this.flashWithoutSound();
            ((monsterMistake)this.owner).TriggerRegen();
            int newheal = this.HealVal * this.HealAmount;
            this.addToBot(new HealAction(this.owner, this.owner, newheal));
            this.HealAmount = 0;
            updateDescription();
        }
    }




    @Override
    public void updateDescription() {
        this.amount = this.HealAmount * this.HealVal;
        this.description = DESCRIPTIONS[0] + this.HealVal + DESCRIPTIONS [1] + amount;
    }

    @Override
    public AbstractPower makeCopy() {
        return new powerTragicImmortality(this.owner, this.HealVal);
    }

}
