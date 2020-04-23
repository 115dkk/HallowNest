package Hallownest.cards.status;

import Hallownest.HallownestMod;
import Hallownest.cards.AbstractDefaultCard;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.SetDontTriggerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Iterator;

import static Hallownest.HallownestMod.makeCardPath;

public class curseDreamersLament extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(curseDreamersLament.class.getSimpleName());
    public static final String IMG = makeCardPath("curseDreamersRemorse.png");

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);


    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;



    private static final CardRarity RARITY = CardRarity.CURSE;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.CURSE;
    public static final CardColor COLOR = CardColor.CURSE;

    private static final int COST = -2;
    private static final int DAMAGE = 0;
    private boolean cardplayed = false;

    public curseDreamersLament() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
       // this.addToBot(new DamageAction(AbstractDungeon.player, new DamageInfo(AbstractDungeon.player, 6, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
       // AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetDontTriggerAction((AbstractCard)this, false));
    }

    public void triggerOnOtherCardPlayed(AbstractCard c) {
        if (!this.cardplayed){
            int costHP = c.cost;
            if (c.isCostModifiedForTurn){
                costHP = c.costForTurn;
            } else if (costHP == -1){
                costHP = 1;
            } else if (costHP < 0){
                costHP = 0;
            }
            this.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, costHP));
            this.cardplayed = true;
        }

    }

    public void triggerWhenDrawn() {
        this.cardplayed = false;
        this.addToBot(new SetDontTriggerAction(this, false));
    }

    private int countSwarmsinHand(){
        int count = 0;
        Iterator var1 = AbstractDungeon.player.hand.group.iterator();

        AbstractCard c;
        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (c instanceof curseDreamersLament) {
               count++;
            }
        }

        return count;
    }

    @Override
    public void upgrade() {
    }
}
