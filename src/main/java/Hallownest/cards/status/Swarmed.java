package Hallownest.cards.status;

import Hallownest.HallownestMod;
import Hallownest.cards.AbstractDefaultCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Iterator;

import static Hallownest.HallownestMod.makeCardPath;

public class Swarmed extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(Swarmed.class.getSimpleName());
    public static final String IMG = makeCardPath("Swarmed.png");

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);


    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;


    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.STATUS;
    public static final CardColor COLOR = CardColor.COLORLESS;

    private static final int COST = 1;
    private static final int DAMAGE = 0;

    public Swarmed() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        magicNumber = DAMAGE;
        exhaust = true;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
       // this.addToBot(new DamageAction(AbstractDungeon.player, new DamageInfo(AbstractDungeon.player, 6, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
       // AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetDontTriggerAction((AbstractCard)this, false));
    }

    private int countSwarmsinHand(){
        int count = 0;
        Iterator var1 = AbstractDungeon.player.hand.group.iterator();

        AbstractCard c;
        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (c instanceof Swarmed) {
               count++;
            }
        }

        return count;
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        //this.dontTriggerOnUseCard = true;
        //AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem((AbstractCard)this, true));
        this.magicNumber = countSwarmsinHand();
        this.addToBot(new DamageAction(AbstractDungeon.player, new DamageInfo(AbstractDungeon.player, this.magicNumber, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

    }


    @Override
    public void upgrade() {
    }
}
