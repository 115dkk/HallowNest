package Hallownest.cards;

import Hallownest.HallownestMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makeCardPath;

public class attackAdjustingAction extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(attackAdjustingAction.class.getSimpleName());
    public static final String IMG = makeCardPath("attackReadjust.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = CardColor.COLORLESS;

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;


    private static final int VALUE = 8;
    private static final int UPGRADE_VALUE = 3;
    private static final int COST = 1;

    public attackAdjustingAction() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.baseDamage = VALUE;
        this.baseBlock = VALUE;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        if (m.intent.equals(AbstractMonster.Intent.ATTACK) || m.intent.equals(AbstractMonster.Intent.ATTACK_DEBUFF) || m.intent.equals(AbstractMonster.Intent.ATTACK_DEFEND) || m.intent.equals(AbstractMonster.Intent.ATTACK_BUFF)){
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p,block));
        } else {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }


    }



    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_VALUE);
            upgradeDamage(UPGRADE_VALUE);
            initializeDescription();
        }
    }
}
