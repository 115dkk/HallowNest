package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static Hallownest.HallownestMod.makePowerPath;


public class powerSageRings extends AbstractPower {

    public static final String POWER_ID = HallownestMod.makeID("powerSageRings");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerSageRings84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerSageRings32.png"));

    public powerSageRings(AbstractCreature owner, int Heal) {
        name = NAME;
        ID = POWER_ID;
        this.amount = Heal;

        this.owner = owner;

        type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }


    public void updateDescription() {
            description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
