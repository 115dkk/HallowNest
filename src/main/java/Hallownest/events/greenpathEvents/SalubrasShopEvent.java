package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.BankAccountRelic;
import Hallownest.relics.DreamNailRelic;
import Hallownest.relics.SalubrasBlessingRelic;
import Hallownest.relics.TeacherSealRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.curses.Writhe;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.RegenPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.OddMushroom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class SalubrasShopEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("SalubrasShopEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("SalubraShopA.png");

//



    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;

    //choice ints for easier reading
    //Screen Button Options
    private final static int BUY_RANDOM = 0;
    private final static int BUY_MUSHROOM = 1;
    private final static int GET_BLESSING = 2;
    private final static int LEAVE = 3;


    private int randomCost = 75;
    private int mushroomCost = 115;

    private int currCharms;
    private int charmsNeeded = 3;



    private int screenNum = 0;

    public SalubrasShopEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if ((randomCost > AbstractDungeon.player.gold) || (AbstractDungeon.player.gold > 10)){
            this.randomCost = AbstractDungeon.player.gold;
        }

       this.currCharms = AbstractDungeon.player.getRelicNames().size();
        // Options without Dreamnailing
        //random relic button
        if (AbstractDungeon.player.gold >= (randomCost)) {
            this.imageEventText.setDialogOption(OPTIONS[1] + randomCost + OPTIONS[2]); //Pay for 50% missing HP restored
        } else {
            this.imageEventText.setDialogOption(OPTIONS[7] + (randomCost) + OPTIONS[8], true);
        }
        // mushroom relic button
        if (AbstractDungeon.player.gold >= (mushroomCost)) {
            this.imageEventText.setDialogOption(OPTIONS[3] + mushroomCost + OPTIONS[4]); //Pay for 50% missing HP restored
        } else {
            this.imageEventText.setDialogOption(OPTIONS[7] + (mushroomCost) + OPTIONS[8], true);
        }
        //salubras blessing button
        if (currCharms >= charmsNeeded) {
            this.imageEventText.setDialogOption(OPTIONS[5], RelicLibrary.getRelic(SalubrasBlessingRelic.ID)); //Pay for 50% missing HP restored
        } else {
            this.imageEventText.setDialogOption(OPTIONS[6], true);
        }
        //leave button
        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2



    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpSalubraGreet.getKey(), 1.5F);
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case BUY_RANDOM: //bought an account but didn't deposit more gold
                        AbstractDungeon.player.loseGold(randomCost);
                        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.COMMON);
                        if (!AbstractDungeon.player.hasRelic(r.relicId)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, r);
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case BUY_MUSHROOM: // Encounter an elite enemy
                        AbstractDungeon.player.loseGold(mushroomCost);
                        if (!AbstractDungeon.player.hasRelic(OddMushroom.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new OddMushroom());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case GET_BLESSING: // Encounter an elite enemy

                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpSalubraKiss.getKey(), 2.0F);
                        }

                        if (!AbstractDungeon.player.hasRelic(SalubrasBlessingRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new SalubrasBlessingRelic());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case LEAVE: // Encounter an elite enemy

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2


                        screenNum = LEAVE_SCREEN;
                        break;
                }
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }


}
