package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.status.curseDreamersLament;
import Hallownest.relics.DreamNailRelic;
import Hallownest.relics.NailmastersGloryRelic;
import Hallownest.relics.TeacherSealRelic;
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
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class TeachersArchiveEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("TeachersArchiveEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("TeacherEventA.png");
    public static final String IMG2 = makeEventPath("TeacherEventB.png");
    public static final String IMG3 = makeEventPath("TeacherEventC.png");
//



    private final static int POSTINTRO_SCREEN = 0;
    private final static int BATTLE_SCREEN = 1;
    private final static int TANK_SCREEN= 2;
    private final static int TANK_OUTCOME_SCREEN= 3;
    private final static int DREAM_NAIL_SCREEN= 4;
    private final static int CONCLUSION_SCREEN= 5;


    //choice ints for easier reading
    //Screen Button Options
    private final static int NAIL_BUTTON = 0;
    private final static int CREATE_BUTTON = 1;
    private final static int PAINTBRUSH_BUTTON = 2;
    private final static int LEAVE_BUTTON = 3;


    private boolean DreamNailed = false;



    private int screenNum = 0;

    public TeachersArchiveEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]);


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case POSTINTRO_SCREEN:

                switch (buttonPressed) {
                    case 0: //Encounter a normal enemy
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpQuirrel.getKey(), 1.4F);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        //this.imageEventText.loadImage(IMG2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[2]);
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        /*
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                         */

                        screenNum = BATTLE_SCREEN;
                        break;
                    case 1: // Fuck off from here
                        openMap();
                        break;
                }
                break;

            case BATTLE_SCREEN:
                switch (buttonPressed) {
                    case 0: //Decided to Fight with Quiirrel
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.JellyFloat.getKey(), 1.4F);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.loadImage(IMG2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        /*
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                         */

                        screenNum = TANK_SCREEN;
                        break;
                    case 1: // Fuck off from here
                        openMap();
                        break;
                }
                break;
            case TANK_SCREEN:
                if (Settings.AMBIANCE_ON) {
                    CardCrawlGame.sound.playV(SoundEffects.EvGpQuirrel2.getKey(), 1.4F);
                }
                //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                this.imageEventText.loadImage(IMG3);
                if (DreamNailed){
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                    this.imageEventText.setDialogOption(OPTIONS[7], RelicLibrary.getRelic(TeacherSealRelic.ID));
                    this.imageEventText.setDialogOption(OPTIONS[4]);
                    this.imageEventText.setDialogOption(OPTIONS[5]);

                } else {
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                    this.imageEventText.setDialogOption(OPTIONS[3], CardLibrary.getCopy(curseDreamersLament.ID), RelicLibrary.getRelic(TeacherSealRelic.ID));
                    this.imageEventText.setDialogOption(OPTIONS[4]);
                    this.imageEventText.setDialogOption(OPTIONS[5]);
                    if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
                        this.imageEventText.setDialogOption(OPTIONS[6]);
                    }
                }




                screenNum = TANK_OUTCOME_SCREEN;
                break;
            case TANK_OUTCOME_SCREEN:
                switch (buttonPressed) {
                    case 0: //Broke the Seal without dream nail. so gets curse


                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        if (!DreamNailed) {
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new curseDreamersLament(),Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        }

                        if (!AbstractDungeon.player.hasRelic(TeacherSealRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new TeacherSealRelic());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }

                        screenNum = CONCLUSION_SCREEN;
                        break;
                    case 1: //Talked with quirrel, so colorless card and on to leave.
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpQuirrel.getKey(), 1.4F);
                        }
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);


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
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(AbstractCard.CardColor.COLORLESS));

                        AbstractDungeon.combatRewardScreen.open();


                        screenNum = CONCLUSION_SCREEN;
                        break;
                    case 2: //Decided to Fuck off after all that
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
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
                    case 3: // Reset Screen with Dream NAil choices
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpDreamerEnter.getKey(), 1.4F);
                        }



                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        this.imageEventText.setDialogOption(OPTIONS[7], RelicLibrary.getRelic(TeacherSealRelic.ID));
                        this.imageEventText.setDialogOption(OPTIONS[4]);
                        this.imageEventText.setDialogOption(OPTIONS[5]);
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

                        screenNum = TANK_OUTCOME_SCREEN;
                        return;
                }
                break;
            case CONCLUSION_SCREEN:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }



}
