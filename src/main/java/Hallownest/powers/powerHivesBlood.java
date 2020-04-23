package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static Hallownest.HallownestMod.makePowerPath;

public class powerHivesBlood extends AbstractPower implements CloneablePowerInterface{
        public AbstractCreature source;
        public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());

        public static final String POWER_ID = HallownestMod.makeID("powerHivesBlood");
        private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        public static final String NAME = powerStrings.NAME;
        public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
        private boolean Damaged;
        private int DamageTrheshold;


        // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.

        private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerHiveBlood84.png"));
        private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerHiveBlood32.png"));

        public powerHivesBlood(final AbstractCreature owner) {
            this.name = NAME;
            this.ID = POWER_ID;
            this.owner = owner;
            this.amount = 0;
            this.type = PowerType.BUFF;
            this.Damaged = false;

            // We load those textures here.
            this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

            updateDescription();
        }




        @Override
        public int onAttacked(DamageInfo info, int damageAmount) {
            if (damageAmount > 0) {
                if (!this.Damaged){
                    this.amount = 0;
                    this.Damaged = true;
                }
                this.flashWithoutSound();
                if (damageAmount > this.owner.currentHealth) {
                    damageAmount = this.owner.currentHealth; //Thank god for Darkglade
                }
                this.amount += damageAmount;
                this.updateDescription();
            }
            return damageAmount;
        }

        @Override
        public void atEndOfTurn(boolean isPlayer){
                if ((!this.Damaged) && (this.amount >0)){
                    AbstractDungeon.actionManager.addToBottom(new HealAction(this.owner, this.owner, this.amount));
                    this.amount = 0;
                }
                this.Damaged = false;
                this.updateDescription();

        }



        @Override
        public void updateDescription()
        {
            this.description = (DESCRIPTIONS[0] + amount + DESCRIPTIONS[1]);
        }

        @Override
        public AbstractPower makeCopy() {
            return new powerHivesBlood(owner);
        }
}
