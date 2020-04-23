package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static Hallownest.HallownestMod.makePowerPath;

public class powerSpewing extends AbstractPower implements CloneablePowerInterface{
        public AbstractCreature source;
        public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());

        public static final String POWER_ID = HallownestMod.makeID("powerSpewing");
        private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        public static final String NAME = powerStrings.NAME;
        public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
        private int timer;


        // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.

        private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("Spewingpower84.png"));
        private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("Spewingpower32.png"));

        public powerSpewing(final AbstractCreature owner, final int amount) {
            this.name = NAME;
            this.ID = POWER_ID;
            this.timer = amount;
            this.owner = owner;
            this.amount = amount;
            this.isTurnBased = true;
            this.type = PowerType.BUFF;

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
        public void atStartOfTurn() {
            this.flash();
            if (this.amount > 1){
                this.amount--;
            } else {
                this.amount = timer;
            }
            updateDescription();
        }

        @Override
        public void updateDescription()
        {
            this.description = (DESCRIPTIONS[0] + amount + DESCRIPTIONS[1]);
        }

        @Override
        public AbstractPower makeCopy() {
            return new powerSpewing(owner, amount);
        }
}
