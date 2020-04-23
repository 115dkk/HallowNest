package Hallownest.cards;

import Hallownest.HallownestMod;
import Hallownest.powers.powerPlatedThorns;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ReduceCostAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makeCardPath;

public class skillSlugHide extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(skillSlugHide.class.getSimpleName());
    public static final String IMG = makeCardPath("skillUnnHide.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = CardColor.COLORLESS;

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;


    private static final int COST = 1;  // COST = 2

    private static final int BLOCK = 4;    // DAMAGE = 7
    private static final int BLOCK_UP = 2;  // UPGRADE_PLUS_DMG = 3
    private static final int COSTDOWN = 1;    // DAMAGE = 7


    public skillSlugHide() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.baseBlock = BLOCK;
        this.magicNumber = COSTDOWN;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        //AbstractDungeon.actionManager.addToBottom(new SFXAction(SoundEffects.Cyclone.getKey()));
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p,p, this.block));
        this.addToBot(new ReduceCostAction(this.uuid, this.magicNumber));
    }


    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(BLOCK_UP);
            initializeDescription();
        }
    }
}