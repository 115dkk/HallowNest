package Hallownest.cards;

import Hallownest.HallownestMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makeCardPath;

public class attackFallenFury extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(attackFallenFury.class.getSimpleName());
    public static final String IMG = makeCardPath("attackFallenFury.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = CardColor.COLORLESS;

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;


    private static final int DAMAGE = 10;
    private static final int BONUS_DAMAGE_PER = 5;
    private static final int COST = 0;

    public attackFallenFury() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = baseMagicNumber = BONUS_DAMAGE_PER;
        this.baseDamage = DAMAGE;
        this.exhaust = true;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int lowHPmod = (AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth)/(AbstractDungeon.player.maxHealth/4);

        int newDamage = (damage + (magicNumber * lowHPmod));
        AbstractDungeon.actionManager.addToBottom(
                new DamageAction(m, new DamageInfo(p, newDamage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL));



    }

    public void applyPowers()
    {
        super.applyPowers();
        int lowHPmod = (AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth)/(AbstractDungeon.player.maxHealth/4);
        int newval = this.magicNumber;
        this.defaultBaseSecondMagicNumber = (magicNumber * lowHPmod);

        if (this.defaultBaseSecondMagicNumber > 0)
        {
            this.rawDescription = (DESCRIPTION + EXTENDED_DESCRIPTION[0]);
            initializeDescription();
        }
    }



    public void onMoveToDiscard()
    {
        this.rawDescription = DESCRIPTION;
        initializeDescription();
        rawDescription = (UPGRADE_DESCRIPTION);
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
