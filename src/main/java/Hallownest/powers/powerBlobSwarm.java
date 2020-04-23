package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.cards.status.Swarmed;
import Hallownest.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static Hallownest.HallownestMod.makePowerPath;


public class powerBlobSwarm extends AbstractPower {

    public static final String POWER_ID = HallownestMod.makeID("powerBlobSwarm");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerPotentInfection84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerPotentInfection32.png"));

    public powerBlobSwarm(AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;

        type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type != DamageInfo.DamageType.THORNS) {
           AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Swarmed(), 1));

        }

    }


    public void updateDescription() {
            description = DESCRIPTIONS[0];
    }
}
