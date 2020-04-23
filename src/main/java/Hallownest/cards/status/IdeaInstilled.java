package Hallownest.cards.status;

import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.cards.AbstractDefaultCard;
import Hallownest.powers.powerInfection;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.SetDontTriggerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makeCardPath;

public class IdeaInstilled extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(IdeaInstilled.class.getSimpleName());
    public static final String IMG = makeCardPath("IdeaInstilled.png");

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.STATUS;
    public static final CardColor COLOR = CardColor.COLORLESS;

    private static final int DISCARD = 1;
    private static final int COST = 3;

    public IdeaInstilled() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = baseMagicNumber = DISCARD;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upgrade() {
    }

    public void triggerOnOtherCardPlayed(AbstractCard c) {
        AbstractDungeon.actionManager.addToTop(new ApplyInfectionAction(AbstractDungeon.player, AbstractDungeon.player, 2));
            //this.addToTop( new ApplyPowerAction(AbstractDungeon.player,AbstractDungeon.player,new powerInfection(AbstractDungeon.player, AbstractDungeon.player, 2),2));
    }

    public void triggerWhenDrawn() {
        this.addToBot(new SetDontTriggerAction(this, false));
    }
}
