package Hallownest.cards.status;

import Hallownest.HallownestMod;
import Hallownest.cards.AbstractDefaultCard;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Random;

import static Hallownest.HallownestMod.makeCardPath;

public class Obsession extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(Obsession.class.getSimpleName());
    public static final String IMG = makeCardPath("Obsession.png");

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;


    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.STATUS;
    public static final CardColor COLOR = CardColor.COLORLESS;

    //private static final int OBSESSED = 1;
    private static final int COST = 2;

    private AbstractCard frozen;
    private boolean DefaultRetain = false;


    public Obsession() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        //this.magicNumber = baseMagicNumber = OBSESSED;
        this.exhaust = true;
        this.frozen = this;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed) {
        if (this.frozen != null) {
            if (cardPlayed == this.frozen) {
                if (AbstractDungeon.player.hand.size() >0){
                    this.frozen = AbstractDungeon.player.hand.getRandomCard(AbstractDungeon.cardRandomRng);
                    this.frozen.setCostForTurn(this.frozen.cost + 1);
                    this.rawDescription = (DESCRIPTION + EXTENDED_DESCRIPTION[0] + this.frozen.name);
                    initializeDescription();
                }
            }
        }
    }

    @Override
    public void triggerWhenDrawn() {
        if (AbstractDungeon.player.hand.size() >0){
            this.frozen = (AbstractDungeon.player.hand).getRandomCard(AbstractDungeon.cardRandomRng);
            this.frozen.setCostForTurn(this.frozen.cost + 1);
            this.rawDescription = (DESCRIPTION + EXTENDED_DESCRIPTION[0] + this.frozen.name);
            initializeDescription();
        }
    }

    @Override
    public void triggerOnExhaust() {
        if (this.frozen != null) {
            this.frozen.resetAttributes();
            //this.frozen = null;
            this.rawDescription = DESCRIPTION;
            initializeDescription();
        }
    }


    @Override
    public void onMoveToDiscard() {
        if (this.frozen != null) {
            this.frozen.resetAttributes();
            //this.frozen = null;
            this.rawDescription = DESCRIPTION;
            initializeDescription();
        }
    }

}
