package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

import static Hallownest.HallownestMod.makePowerPath;

public class powerRich extends TwoAmountPower {
    public static final String POWER_ID = HallownestMod.makeID("powerRich");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int threshold;
    private int GoldPerStrength = 10;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("RichPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("RichPower32.png"));

    public powerRich(AbstractCreature owner, int threshold, int maxGold) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = maxGold;
        this.threshold = threshold;
        this.amount2 = threshold;
        this.type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            this.flashWithoutSound();
            if (damageAmount > this.owner.currentHealth) {
                damageAmount = this.owner.currentHealth; //prevents overkill damage from contributing
            }
            this.reducePower(damageAmount);
            this.updateDescription();
        }
        return damageAmount;
    }

    @Override
    public void reducePower(int reduceAmount) {
        if (this.amount2 - reduceAmount <= 0) {
            this.fontScale = 8.0F;
            this.amount2 = threshold;
            AbstractDungeon.actionManager.addToBottom(new GainGoldAction(this.GoldPerStrength));
            CardCrawlGame.sound.play("GOLD_JINGLE");
            for(int i = 0; i <10; ++i) {
                AbstractDungeon.actionManager.addToBottom(new VFXAction(
                        new GainPennyEffect(this.owner.hb.cX, this.owner.hb.cY)));
            }


            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -1), -1, true, AbstractGameAction.AttackEffect.NONE));

            if ((this.amount - this.GoldPerStrength) <=0){
                if (this.owner.hasPower(this.ID)) {
                    AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this));
                }
            } else {
                this.amount -= this.GoldPerStrength;
            }

        } else {
            this.fontScale = 8.0F;
            this.amount2 -= reduceAmount;
        }

    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount2 + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}
