package Hallownest.cards;

import Hallownest.HallownestMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ModifyDamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

import static Hallownest.HallownestMod.makeCardPath;

public class attackMasterCycloneSlash extends AbstractDefaultCard {

    public static final String ID = HallownestMod.makeID(attackMasterCycloneSlash.class.getSimpleName());
    public static final String IMG = makeCardPath("attackMasterCycloneSlash.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = CardColor.COLORLESS;

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;


    private static final int COST = 3;  // COST = 2

    private static final int DAMAGE = 18;    // DAMAGE = 7
    private static final int UPGRADE_PLUS_DMG = 6;  // UPGRADE_PLUS_DMG = 3
    private static final int PLUS_DMG = 3;
    private static final int PLUS_DMG_UP = 3;  // UPGRADE_PLUS_DMG = 3

    public attackMasterCycloneSlash() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.baseDamage = DAMAGE;
        this.isMultiDamage = true;
        this.magicNumber = this.baseMagicNumber = PLUS_DMG;


    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        //AbstractDungeon.actionManager.addToBottom(new SFXAction(SoundEffects.Cyclone.getKey()));
        CardCrawlGame.sound.play("ATTACK_FAST");

        AbstractDungeon.actionManager.addToBottom(new VFXAction(p, new CleaveEffect(), 0.1F));
        AbstractDungeon.actionManager.addToBottom(
                new DamageAllEnemiesAction(p, multiDamage, damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        AbstractDungeon.actionManager.addToBottom(new ModifyDamageAction(this.uuid, magicNumber));
    }


    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
            upgradeMagicNumber(PLUS_DMG_UP);
            initializeDescription();
        }
    }
}