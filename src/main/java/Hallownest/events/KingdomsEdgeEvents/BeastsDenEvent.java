package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.status.curseDreamersLament;
import Hallownest.relics.*;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class BeastsDenEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("BeastsDenEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("KEDreamerA.png");
    public static final String IMG2 = makeEventPath("KEDreamerB1.png");
    public static final String IMG3 = makeEventPath("KEDreamerB.png");
    public static final String IMG4 = makeEventPath("KEDreamerC.png");
    public static final String IMG5 = makeEventPath("KEDreamerD.png");
//



    private final static int REST_SCREEN = 0;
    private final static int HERRAH_SCREEN = 1;
    private final static int TANK_SCREEN= 2;
    private final static int HERRAH_AFTER_SCREEN= 3;
    private final static int HORNET_SCREEN= 4;
    private final static int CONCLUSION_SCREEN= 5;


    //choice ints for easier reading
    //Screen Button Options
    private final static int NAIL_BUTTON = 0;
    private final static int CREATE_BUTTON = 1;
    private final static int PAINTBRUSH_BUTTON = 2;
    private final static int LEAVE_BUTTON = 3;


    private boolean DreamNailed = false;



    private int screenNum = 0;

    public BeastsDenEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2],true);
        this.imageEventText.setDialogOption(OPTIONS[3],true);


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case REST_SCREEN:

                switch (buttonPressed) {
                    case 0: //Encounter a normal enemy
                        /*
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpQuirrel.getKey(), 1.4F);
                        }
                        */
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        //this.imageEventText.loadImage(IMG2);
                        this.imageEventText.loadImage(IMG2);

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[4]);
                        //this.imageEventText.setDialogOption(OPTIONS[0]);

                        /*
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                         */

                        screenNum = HERRAH_SCREEN;
                        break;
                    case 1: // Fuck off from here
                        openMap();
                        break;
                }
                break;

            case HERRAH_SCREEN:
                switch (buttonPressed) {
                    case 0: //Decided to Fight with Quiirrel

                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.loadImage(IMG3);
                        if (DreamNailed){
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            this.imageEventText.setDialogOption(OPTIONS[8], RelicLibrary.getRelic(BeastSealRelic.ID));
                            this.imageEventText.setDialogOption(OPTIONS[7]);

                        } else {
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            this.imageEventText.setDialogOption(OPTIONS[5], CardLibrary.getCopy(curseDreamersLament.ID), RelicLibrary.getRelic(BeastSealRelic.ID));
                            this.imageEventText.setDialogOption(OPTIONS[7]);
                            if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
                                this.imageEventText.setDialogOption(OPTIONS[6]);
                            }
                        }
                        /*
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                         */

                        screenNum = HERRAH_AFTER_SCREEN;
                        break;
                    case 1: // Fuck off from here
                        openMap();
                        break;
                }
                break;
            case HERRAH_AFTER_SCREEN:
                switch (buttonPressed) {
                    case 0: //Broke the Seal without dream nail. so gets curse

                        this.imageEventText.loadImage(IMG4);

                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        if (!DreamNailed) {
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new curseDreamersLament(),Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        }

                        if (!AbstractDungeon.player.hasRelic(BeastSealRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new BeastSealRelic());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }

                        if ((AbstractDungeon.player.hasRelic(TeacherSealRelic.ID)) && (AbstractDungeon.player.hasRelic(TeacherSealRelic.ID)) && AbstractDungeon.player.hasRelic(BeastSealRelic.ID)){
                            this.imageEventText.setDialogOption(OPTIONS[10]);
                        }
                        screenNum = HORNET_SCREEN;

                        break;
                    case 1: //Decided to Fuck off after all that
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[4]);
                        /*
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                         */

                        screenNum = CONCLUSION_SCREEN;
                        break;
                    case 2: // Reset Screen with Dream NAil choices
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpDreamerEnter.getKey(), 1.4F);
                        }
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.setDialogOption(OPTIONS[8], RelicLibrary.getRelic(BeastSealRelic.ID));
                        this.imageEventText.setDialogOption(OPTIONS[7]);
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        /*
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                         */
                        DreamNailed = true;



                        screenNum = HERRAH_AFTER_SCREEN;
                        return;
                }
                break;
            case HORNET_SCREEN:

                switch (buttonPressed) {
                    case 0: // Fuck off from here
                        openMap();
                        break;
                    case 1: //Encounter a normal enemy
                        /*
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpQuirrel.getKey(), 1.4F);
                        }
                        */
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        //this.imageEventText.loadImage(IMG2);
                        this.imageEventText.loadImage(IMG5);

                        this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[11], RelicLibrary.getRelic(BlackEggRelic.ID));
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        //this.imageEventText.setDialogOption(OPTIONS[0]);

                        /*
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                         */

                        screenNum = 6;
                        break;

                }
                break;

            case CONCLUSION_SCREEN:
                this.openMap();
                break;
            case 6:
                switch (buttonPressed) {
                    case 0: //Decided to Fight with Quiirrel
                        if (!AbstractDungeon.player.hasRelic(BlackEggRelic.ID)){
                            AbstractDungeon.player.loseRelic(BeastSealRelic.ID);
                            AbstractDungeon.player.loseRelic(TeacherSealRelic.ID);
                            AbstractDungeon.player.loseRelic(WatcherSealRelic.ID);
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new BlackEggRelic());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless

                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        screenNum = CONCLUSION_SCREEN;
                        break;
                    case 1: // Fuck off from here
                        openMap();
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }



}
