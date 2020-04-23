package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.curses.Writhe;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class MarissasSongEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("MarissasSongEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public static final String IMG = makeEventPath("MarissaEventA.png");
    public static final String IMG2 = makeEventPath("MarissaEventB.png");
//



    private final static int OFFER_SCREEN = 0;
    private final static int MEMORY_SCREEN = 2;
    private final static int LEAVE_SCREEN = 1;

    //choice ints for easier reading
    //Screen Button Options
    private int ESSENCE = 5;
    private final static int HEAL = 1;
    private final static int REGEN_POT = 2;
    private final static int DREAM_NAILED = 3;

    private final static int DREAM_NAIL = 1;
    private final static int FINISHED = 0;

    private boolean optionsExist = false;
    private int HealCost = 50;
    private int HealVal;
    private int MaxHPUp;
    private int Fullgold;



    private int screenNum = 0;

    public MarissasSongEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        // Options without Dreamnailing
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            this.imageEventText.setDialogOption(OPTIONS[2]); // button 3
        }

    }

    public void onEnterRoom() {


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OFFER_SCREEN:
                switch (buttonPressed) {
                    case 0: //Listen to her song remember potions etc

                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EventMarissaSong.getKey(), 1.5F);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();

                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        this.imageEventText.setDialogOption(OPTIONS[4]);
                        this.imageEventText.setDialogOption(OPTIONS[5]);

                        screenNum = MEMORY_SCREEN;
                        break;
                    case 1: // Encounter an elite enemy
                        this.openMap();
                        break;
                    case 2: // Dispelled Marissa
                        this.imageEventText.loadImage(IMG2);
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.DreamNailSound.getKey(), 1.2F);
                        }
                        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
                            AbstractDungeon.player.getRelic(DreamNailRelic.ID).setCounter((AbstractDungeon.player.getRelic(DreamNailRelic.ID).counter + ESSENCE));
                        }

                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        screenNum = LEAVE_SCREEN;
                        break;
                }
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;
            case MEMORY_SCREEN:
                switch (buttonPressed) {
                    case 0: //bought an account but didn't deposit more gold
                        if (AbstractDungeon.player.hasRelic("Sozu")) {
                            AbstractDungeon.player.getRelic("Sozu").flash();
                        } else {
                            AbstractPotion p = PotionHelper.getPotion(AncientPotion.POTION_ID);
                            AbstractDungeon.player.obtainPotion(p);
                        }
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case 1: // Encounter an elite enemy
                        if (AbstractDungeon.player.hasRelic("Sozu")) {
                            AbstractDungeon.player.getRelic("Sozu").flash();
                        } else {
                            AbstractPotion p = PotionHelper.getPotion(LiquidMemories.POTION_ID);
                            AbstractDungeon.player.obtainPotion(p);
                        }
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        screenNum = LEAVE_SCREEN;
                        break;
                    case 2: // Encounter an elite enemy

                        if (AbstractDungeon.player.hasRelic("Sozu")) {
                            AbstractDungeon.player.getRelic("Sozu").flash();
                        } else {
                            AbstractPotion p = PotionHelper.getPotion(EntropicBrew.POTION_ID);
                            AbstractDungeon.player.obtainPotion(p);
                        }

                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        screenNum = LEAVE_SCREEN;
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }


}
