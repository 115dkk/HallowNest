package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.cards.status.Burn;
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

public class ZoteEvent3 extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("ZoteEvent3");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("KEZoteA.png");


    private boolean pickCard;
    private int Max_Loss = 5;



    //Screens
    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int PRIDE_BUTTON = 0;
    private final static int BURNS_BUTTON = 1;
    private final static int HPLOSS_BUTTON = 2;
    private boolean attack;
    private boolean skill;
    private boolean power;
    private boolean upgraded;
    private AbstractCard attackCard;
    private AbstractCard skillCard;
    private AbstractCard cardToDown;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public ZoteEvent3() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.Max_Loss += 2;
        }
        imageEventText.setDialogOption(OPTIONS[1], CardLibrary.getCopy(Shame.ID));
        imageEventText.setDialogOption(OPTIONS[2], CardLibrary.getCopy(Burn.ID)); //this is the text shown if you can't upgrade anything.
        imageEventText.setDialogOption(OPTIONS[3] + this.Max_Loss + OPTIONS[4] ); // Remove a card is now button case 2

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case PRIDE_BUTTON:


                        AbstractCard curse = new Shame();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }


                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = 2;
                        break;
                    case BURNS_BUTTON: // Buy


                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }

                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Burn(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Burn(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = 2;

                        break;
                    case HPLOSS_BUTTON: // Leave
                        //Effect!
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }

                        AbstractDungeon.player.maxHealth -= this.Max_Loss;
                        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth) {
                            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
                        }

                        if (AbstractDungeon.player.maxHealth < 1) {
                            AbstractDungeon.player.maxHealth = 1;
                        }
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
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
                this.imageEventText.setDialogOption(OPTIONS[5]);
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

}
