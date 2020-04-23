package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static Hallownest.HallownestMod.makePowerPath;

public class powerRagin extends TwoAmountPower implements CloneablePowerInterface{
        public AbstractCreature source;
        public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());

        public static final String POWER_ID = HallownestMod.makeID("powerRagin");
        private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        public static final String NAME = powerStrings.NAME;
        public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
        private int AltAmount;
        private int DamageThreshold;
        private int maxDamage;


        // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.

        private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerRagin_84.png"));
        private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerRagin_32.png"));

        public powerRagin(final AbstractCreature owner, final int amount, int Threshhold) {
            this.name = NAME;
            this.ID = POWER_ID;
            this.owner = owner;
            this.amount = amount;
            this.type = PowerType.BUFF;
            this.AltAmount = 0;
            this.amount2 = Threshhold;
            this.DamageThreshold = Threshhold;
            this.maxDamage = Threshhold;

            // We load those textures here.
            this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

            updateDescription();
        }


        @Override
        public void reducePower(int reduceAmount) {
            if (this.amount2 - reduceAmount <= 0) {
                this.fontScale = 8.0F;
                this.DamageThreshold = maxDamage - (reduceAmount - amount2);
                this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount, true));
                this.amount2 = this.DamageThreshold;
                this.DamageThreshold = maxDamage;
            } else {
                this.fontScale = 8.0F;
                this.amount2 -= reduceAmount;
            }

        }


        @Override
        public int onAttacked(DamageInfo info, int damageAmount) {
            if (damageAmount > 0) {
                this.flashWithoutSound();
                if (damageAmount > this.owner.currentHealth) {
                    damageAmount = this.owner.currentHealth; //Thank god for Darkglade
                }
                this.reducePower(damageAmount);
                this.updateDescription();
            }
            return damageAmount;
        }

        @Override
        public void atEndOfTurn(boolean isPlayer){

            this.amount2 = this.DamageThreshold;
            this.updateDescription();
        }


        @Override
        public void updateDescription()
        {
            this.description = (DESCRIPTIONS[0] + this.maxDamage + DESCRIPTIONS[1] + amount2 + DESCRIPTIONS[2] + amount + DESCRIPTIONS[3]);
        }

        @Override
        public AbstractPower makeCopy() {
            return new powerRagin(owner, amount, maxDamage);
        }
}
