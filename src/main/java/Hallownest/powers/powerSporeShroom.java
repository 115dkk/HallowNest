package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;

import static Hallownest.HallownestMod.makePowerPath;

public class powerSporeShroom extends TwoAmountPower {
    public static final String POWER_ID = HallownestMod.makeID("powerSporeShroom");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int cardthreshold;
    public boolean active = true;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerSporeShroom84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerSporeShroom32.png"));

    public powerSporeShroom(AbstractCreature owner, int poison, int health) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount2 = health;
        this.amount = poison;
        this.type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }
    @Override
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        this.flash();
        if (card.type.equals(AbstractCard.CardType.ATTACK)){
            this.active = false;
            flashWithoutSound();
        }
    }
    @Override
    public void atStartOfTurn() {
    this.active = true;
    }

    @Override
    public void stackPower(int stackAmount) {
        this.amount += stackAmount;
        this.amount2 += 2;
        this.updateDescription();
    }



    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (active){
            flash();
            AbstractDungeon.actionManager.addToBottom(new HealAction(this.owner, this.owner, this.amount2));
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if ((!monster.isDead) && (!monster.isDying)) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, this.owner , new PoisonPower(monster, this.owner, this.amount), this.amount)); }
            }


        }
    }



    @Override
    public void updateDescription() {
            this.description = DESCRIPTIONS[0] + this.amount2 + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}
