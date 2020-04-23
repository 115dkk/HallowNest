package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static Hallownest.HallownestMod.makePowerPath;

public class powerCollectedCard extends AbstractPower {
    public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());

    public static final String POWER_ID = HallownestMod.makeID("powerCollectedCard");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerCollectedCard84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerCollectedCard32.png"));

    private AbstractCard card;
    private int StrUp = 1;
    private int maxhits;
    private boolean Returned = false;

    public powerCollectedCard (AbstractCreature owner, AbstractCard card, int ReturnHits) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.card = card;
        this.amount = ReturnHits;
        this.maxhits = ReturnHits;
        this.updateDescription();
        this.Returned = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }



    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + FontHelper.colorString(this.card.name, "y") + powerStrings.DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            this.amount--;
            this.flashWithoutSound();
        }
        if ((this.amount <=0) && (!this.Returned)){
            if (AbstractDungeon.player.hand.size() != 10) {
                this.addToBot(new MakeTempCardInHandAction(this.card, false, true));
            } else {
                this.addToBot(new MakeTempCardInDiscardAction(this.card, true));
            }
            this.Returned = true;
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner,this.owner,this));
        }
        return damageAmount;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        int str = 0;
        if (card.cost > str){
            str = card.cost;
        }
        if (str > 0) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner,this.owner, new StrengthPower(this.owner, str), str));
        }

        this.maxhits--;
        this.amount = this.maxhits;
        if (this.amount <=0){
            if (AbstractDungeon.player.hand.size() != 10) {
                this.addToBot(new MakeTempCardInHandAction(this.card, false, true));
            } else {
                this.addToBot(new MakeTempCardInDiscardAction(this.card, true));
            }
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner,this.owner,this));
        }
        this.flashWithoutSound();
        this.updateDescription();

    }

}
