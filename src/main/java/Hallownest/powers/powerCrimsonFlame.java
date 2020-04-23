package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static Hallownest.HallownestMod.makePowerPath;

public class powerCrimsonFlame extends AbstractPower implements CloneablePowerInterface{
        public AbstractCreature source;
        public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());

        public static final String POWER_ID = HallownestMod.makeID("powerCrimsonFlame");
        private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        public static final String NAME = powerStrings.NAME;
        public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;


        // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.

        private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerCrimsonFlame84.png"));
        private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerCrimsonFlame32.png"));

        public powerCrimsonFlame(final AbstractCreature owner, final AbstractCreature source, int amount) {
            this.name = NAME;
            this.ID = POWER_ID;

            this.owner = owner;
            this.source = source;
            this.amount = amount;
            //might change this later
            this.isTurnBased = false;
            this.type = PowerType.DEBUFF;

            if (this.amount >= 999) {
                this.amount = 999;
            }

            // We load those textures here.
            this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

            updateDescription();
        }

        @Override
        public void stackPower(int stackAmount)
        {
            this.fontScale = 8.0F;
            this.amount += stackAmount;

            if (this.amount >= 999) {
                this.amount = 999;
            }

        }
        @Override
        public void atStartOfTurn()
        { // At the end of your turn
           AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this, 1));
        }

        @Override
        public float atDamageReceive(final float damage, final DamageInfo.DamageType type) {
            if (type != DamageInfo.DamageType.NORMAL) {
            return damage;
            }
            return damage + (float)this.amount;
        }



        @Override
        public void updateDescription()
        {
            this.description = (DESCRIPTIONS[0] + amount + DESCRIPTIONS[1]);
        }

        @Override
        public AbstractPower makeCopy() {
            return new powerCrimsonFlame(owner,source, amount);
        }
}
