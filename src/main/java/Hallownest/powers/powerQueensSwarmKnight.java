package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
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

public class powerQueensSwarmKnight extends AbstractPower implements CloneablePowerInterface{
        public AbstractCreature source;
        public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());

        public static final String POWER_ID = HallownestMod.makeID("powerQueensSwarmKnight");
        private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        public static final String NAME = powerStrings.NAME;
        public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;


        // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.

        private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerQueensSwarmKnight84.png"));
        private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerQueensSwarmKnight32.png"));

        public powerQueensSwarmKnight(final AbstractCreature owner, final AbstractCreature source, int amount) {
            this.name = NAME;
            this.ID = POWER_ID;

            this.owner = owner;
            this.source = source;
            this.amount = amount;
            this.isTurnBased = true;
            this.type = PowerType.BUFF;
            this.priority = 3;

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

        }

        @Override
        public void atEndOfTurnPreEndTurnCards(boolean isPlayer)
        { // At the end of your turn
            /*if (AbstractDungeon.player.hasRelic(IsmasTearRelic.ID)) {
                InfDamage = InfDamage - 2;
            }
            */
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this.owner, this.amount));
            //I'm hoping this makes the Broken Vessel power work better to deal damage to everyone whenever you take damage.
            //in theory this should deal damage (not lose hp but deal blockable damage) to the player at the end of his turn, and then decrement the value of infection by 1.
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this, 1));

            //There might be a need to add some shit to check for specific powers or something at some point

        }

        @Override
        public void updateDescription()
        {
            this.description = (DESCRIPTIONS[0] + amount + DESCRIPTIONS[1]);
        }

        @Override
        public AbstractPower makeCopy() {
            return new powerQueensSwarmKnight(owner,source, amount);
        }
}
