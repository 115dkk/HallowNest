package Hallownest.cards;

import Hallownest.HallownestMod;
import Hallownest.powers.powerSporeShroom;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makeCardPath;

public class pwrSporeShroom extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(pwrSporeShroom.class.getSimpleName());
    public static final String IMG = makeCardPath("powerSporeShroom.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = CardColor.COLORLESS;

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;



    private static final int HEAL = 2;
    private static final int POISON = 1;
    private static final int UP_POISON = 1;

    private static final int COST = 1;

    public pwrSporeShroom() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = baseMagicNumber = POISON;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,p, new powerSporeShroom(p,magicNumber,HEAL), magicNumber));
    }



    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(UP_POISON);
            initializeDescription();
        }
    }
}
