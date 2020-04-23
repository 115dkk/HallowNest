package Hallownest.cards;

import Hallownest.HallownestMod;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static Hallownest.HallownestMod.makeCardPath;

public class skillLightsAllure extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(skillLightsAllure.class.getSimpleName());
    public static final String IMG = makeCardPath("skillLightsAllure.png");

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = CardColor.COLORLESS;

    private static final int STRENGTH = 2;
    private static final int STR_UP = 1;
    private static final int HEALTH = 5;
    private static final int HEALTH_DOWN = 1;
    private static final int COST = 0;

    public skillLightsAllure() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = baseMagicNumber = STRENGTH;
        this.defaultSecondMagicNumber = this.defaultBaseSecondMagicNumber = HEALTH;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new LoseHPAction(p, p, this.defaultSecondMagicNumber));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,p,new StrengthPower(p, this.magicNumber),this.magicNumber));
        
    }



    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDefaultSecondMagicNumber(HEALTH_DOWN);
            upgradeMagicNumber(STR_UP);
            initializeDescription();
        }
    }
    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        AbstractCreature p = AbstractDungeon.player;
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, this.defaultSecondMagicNumber));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,p,new StrengthPower(p, (0-this.magicNumber)),(0-this.magicNumber)));

    }
}
