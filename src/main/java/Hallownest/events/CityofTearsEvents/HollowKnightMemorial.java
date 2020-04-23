package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.StagBellStandRelic;
import Hallownest.relics.UnbreakableCardRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.EffectHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.RedMask;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class HollowKnightMemorial extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("HollowKnightMemorial");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("FountainEventA.png");
    public static final String IMG2 = makeEventPath("FountainEventB.png");
//



    private final static int HORNET_SCREEN = 0;
    private final static int CHOICE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int PURIFY_BUTTON = 0;
    private final static int PERSIST_BUTTON = 1;


    private int GoldOffered = 150;




    private int screenNum = 0;

    public HollowKnightMemorial() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]);


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:

                switch (buttonPressed) {
                    case 0: //Encounter a normal enemy
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EventHornetTalk.getKey(), 1.4F);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.loadImage(IMG2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();


                        this.imageEventText.setDialogOption(OPTIONS[4], RelicLibrary.getRelic(RedMask.ID));
                        this.imageEventText.setDialogOption(OPTIONS[2] + GoldOffered + OPTIONS[3]);
                        screenNum = 1;
                        break;
                    case 1: // Just Leave
                        this.openMap();
                        break;
                }
                break;

            case CHOICE_SCREEN:
                switch (buttonPressed) {
                    case PURIFY_BUTTON: //upgrade 2 random attacks
                        if (!AbstractDungeon.player.hasRelic(StagBellStandRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new RedMask());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }


                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                    case PERSIST_BUTTON: // transform a starting strike

                        EffectHelper.gainGold(AbstractDungeon.player, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, GoldOffered);
                        AbstractDungeon.player.gainGold(GoldOffered);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                }
                break;
            case 2:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }

}
