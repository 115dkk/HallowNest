package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.monsters.GreenpathEnemies.monsterFoolEater;
import Hallownest.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static Hallownest.HallownestMod.makePowerPath;

public class powerReadjusting extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = HallownestMod.makeID("powerReadjusting");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int StrVal;
    private int HealVal;
    private int BlockVal;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("ReadjustPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("ReadjustPower32.png"));

    public powerReadjusting(AbstractCreature owner, int blocks, int strength, int heals) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.BlockVal = blocks;
        this.HealVal = heals;
        this.StrVal = strength;
        this.type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }
    @Override
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        this.flashWithoutSound();
        if (card.type == AbstractCard.CardType.ATTACK){
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this.owner,this.BlockVal));
        } else if (card.type == AbstractCard.CardType.SKILL){
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner,this.owner,new StrengthPower(this.owner, this.StrVal), this.StrVal));
        } else if (card.type == AbstractCard.CardType.POWER){
            if(this.owner.currentHealth < this.owner.maxHealth){
                AbstractDungeon.actionManager.addToBottom(new HealAction(this.owner,this.owner, this.HealVal));
            } else {
                AbstractDungeon.actionManager.addToBottom(new AddTemporaryHPAction(this.owner,this.owner, this.HealVal));

            }
        }
    }


    @Override
    public void updateDescription() {
            this.description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new powerReadjusting(owner, BlockVal, StrVal, HealVal);
    }

}
