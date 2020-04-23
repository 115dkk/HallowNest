package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class ZoteEvent1 extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("ZoteEvent1");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("GPZoteA.png");


    private boolean pickCard;
    private int damageToTake = 9;



    //Screens
    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int UNUPGRADE_BUTTON = 0;
    private final static int DOUBT_BUTTON = 1;
    private final static int TAKE_DAMAGE_BUTTON = 2;
    private boolean attack;
    private boolean skill;
    private boolean power;
    private boolean upgraded;
    private AbstractCard attackCard;
    private AbstractCard skillCard;
    private AbstractCard cardToDown;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public ZoteEvent1() {
        super(NAME, DESCRIPTIONS[0], IMG);
        AbstractPlayer p = AbstractDungeon.player;

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.damageToTake += 2;
        }
        if (getUpgradedAttackCards(AbstractDungeon.player.masterDeck).size() > 0) {
            this.cardToDown = getUpgradedAttackCards(AbstractDungeon.player.masterDeck).getTopCard();
            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
            this.imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(this.cardToDown.name, "r"), this.cardToDown.makeStatEquivalentCopy());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[2], true); //this is the text shown if you can't upgrade anything.
        }
        imageEventText.setDialogOption(OPTIONS[3], CardLibrary.getCopy(Doubt.ID)); // Transform a Card is now button 0
        imageEventText.setDialogOption(OPTIONS[4] + this.damageToTake + OPTIONS[5] ); // Remove a card is now button case 2

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case UNUPGRADE_BUTTON:


                        AbstractDungeon.effectList.add(new ExhaustCardEffect(cardToDown));
                        DowngradeAttackCard(this.cardToDown);

                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }


                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = 2;
                        break;
                    case DOUBT_BUTTON: // Buy


                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }

                        AbstractCard curse = new Doubt();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = 2;

                        break;
                    case TAKE_DAMAGE_BUTTON: // Leave
                        //Effect!


                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }



                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.damageToTake));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = 2;

                        break;

                }
                this.pickCard = true;
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;
            case 2:

                this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[6]);
                this.imageEventText.clearRemainingOptions();

                screenNum = LEAVE_SCREEN;


                break;
            default:
                this.openMap();
        }
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpZote2.getKey(), 1.4F);
        }

    }


    private static CardGroup getUpgradedAttackCards(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if ((c.upgraded) && (c.type == AbstractCard.CardType.ATTACK))  {
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }

    public void DowngradeAttackCard(AbstractCard oldc) {

        /*
        AbstractCard newc = oldc.makeSameInstanceOf();

        newc.upgraded = rawc.upgraded;
        newc.timesUpgraded = rawc.timesUpgraded;
        newc.name = rawc.name;
        newc.rawDescription = rawc.rawDescription;
        newc.target = rawc.target;
        newc.baseDamage = rawc.baseDamage;
        newc.baseBlock = rawc.baseBlock;
        newc.baseMagicNumber = rawc.baseMagicNumber;
        newc.cost = rawc.cost;
        */

        AbstractCard rawc = oldc.makeCopy();
        AbstractDungeon.player.masterDeck.removeCard(oldc);
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(rawc, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }




}
