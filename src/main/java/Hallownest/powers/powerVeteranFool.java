package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static Hallownest.HallownestMod.makePowerPath;

public class powerVeteranFool extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = HallownestMod.makeID("powerVeteranFool");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int StrVal;
    private int HealVal;
    private int BlockVal;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerVeteranFool84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerVeteranFool32.png"));

    public powerVeteranFool(AbstractCreature owner, int blocks) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = blocks;
        this.type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this.owner,this.amount));
    }

    @Override
    public void updateDescription() {
            this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new powerVeteranFool(owner, amount);
    }

}
