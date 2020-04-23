package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.skillGreenpathMap;
import Hallownest.relics.BankAccountRelic;
import Hallownest.relics.DreamNailRelic;
import Hallownest.relics.SheosBrushRelic;
import Hallownest.relics.TeacherSealRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class BankEvent1 extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("BankEvent1");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("GPMillibelleA.png");

//



    private final static int OPTION_SCREEN = 0;
    private final static int OUTCOME_SCREEN = 1;
    private final static int LEAVE_SCREEN = 1;
    private final static int DREAM_LEAVE_SCREEN = 3;

    //choice ints for easier reading
    //Screen Button Options
    private final static int ACCOUNT_CHOSEN = 0;
    private final static int DEPOSIT_CHOSEN = 1;
    private final static int LEAVE_CHOSEN = 2;

    private final static int DREAM_NAIL = 1;
    private final static int FINISHED = 0;

    private boolean optionsExist = false;
    private int accountCost = 75;
    private int Fullgold;



    private int screenNum = 0;

    public BankEvent1() {
        super(NAME, DESCRIPTIONS[0], IMG);
        // button 0 for if we have 75 gold
        if (AbstractDungeon.player.gold >= accountCost) {
            this.imageEventText.setDialogOption((OPTIONS[1] + accountCost + OPTIONS[2]), RelicLibrary.getRelic(BankAccountRelic.ID)); // Regular Account
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5] + accountCost + OPTIONS[6], true);
        }
        //button 1 for if we have 100 gold
        if (AbstractDungeon.player.gold >= (accountCost + 25)) {
            this.imageEventText.setDialogOption(OPTIONS[3], RelicLibrary.getRelic(BankAccountRelic.ID)); // Full Service
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5] + (accountCost + 25) + OPTIONS[6], true);
        }
        //button 2 for leaving
        this.imageEventText.setDialogOption(OPTIONS[0]); // leave


    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpBanker.getKey(), 1.6F);
        }
        Fullgold = AbstractDungeon.player.gold;

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:

                switch (buttonPressed) {
                    case ACCOUNT_CHOSEN: //bought an account but didn't deposit more gold
                        AbstractDungeon.player.loseGold(accountCost);
                        if (!AbstractDungeon.player.hasRelic(BankAccountRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new BankAccountRelic(0));
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        }
                        screenNum = LEAVE_SCREEN;
                        break;
                    case DEPOSIT_CHOSEN: // Encounter an elite enemy
                        AbstractDungeon.player.loseGold(Fullgold);

                        if (!AbstractDungeon.player.hasRelic(BankAccountRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new BankAccountRelic(Fullgold));
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }

                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        }
                        screenNum = LEAVE_SCREEN;
                        break;
                    case LEAVE_CHOSEN: // Encounter an elite enemy

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        screenNum = LEAVE_SCREEN;
                        break;
                }
                break;
            case LEAVE_SCREEN:
                switch (buttonPressed) {
                    case 0: //Regular Leave
                        this.openMap();
                        break;
                    case 1: // Refresh Leave with Dream Nail Dialogue
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = DREAM_LEAVE_SCREEN;
                        break;
                }
                break;
            case DREAM_LEAVE_SCREEN:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }


}
