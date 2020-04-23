package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.monsters.GreenpathEnemies.monsterFoolEater;
import Hallownest.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makePowerPath;

public class powerFoolEater extends TwoAmountPower {
    public static final String POWER_ID = HallownestMod.makeID("powerFoolEater");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int cardthreshold;
    public boolean triggered = false;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("FoolEaterPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("FoolEaterPower32.png"));
    private static final Texture traptex84 = TextureLoader.getTexture(makePowerPath("FoolEaterTrap84.png"));
    private static final Texture traptex32 = TextureLoader.getTexture(makePowerPath("FoolEaterTrap32.png"));

    public powerFoolEater(AbstractCreature owner, int cardthreshold, int startingDamage) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = startingDamage;
        this.cardthreshold = cardthreshold;
        this.amount2 = cardthreshold;
        this.type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }
    @Override
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        this.flash();
        if (!this.triggered){
            this.reducePower(1);
            updateDescription();
        }
    }

    public void ResetTrap(){
        triggered = false;
        updateDescription();
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    @Override
    public void reducePower(int reduceAmount) {
        if (this.amount2 - reduceAmount <= 0) {
            this.fontScale = 8.0F;
            this.cardthreshold++;
            this.amount2 = cardthreshold;
            ((monsterFoolEater)this.owner).TrapIntent(this.amount);
            this.amount +=2;
            this.region128 = new TextureAtlas.AtlasRegion(traptex84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(traptex32, 0, 0, 32, 32);
            this.triggered = true;
        } else {
            this.fontScale = 8.0F;
            this.amount2 -= reduceAmount;
        }
    }


    @Override
    public void updateDescription() {
        if (triggered){
         this.description = DESCRIPTIONS[3];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount2 + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }
}
