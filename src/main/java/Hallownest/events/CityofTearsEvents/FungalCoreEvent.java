package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.attackFallenFury;
import Hallownest.cards.pwrSporeShroom;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import javax.smartcardio.Card;

import static Hallownest.HallownestMod.makeEventPath;

public class FungalCoreEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("FungalCoreEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTShroomA.png");
    public static final String IMG2 = makeEventPath("CoTShroomB.png");

    private boolean pickCard;
    private int HPLoss = 4;
    private int CardBlockMin = 9;

    //Screens

    private final static int INTRO_SCREEN = 0;
    private final static int OPTION_SCREEN = 1;

    private final static int LEAVE_SCREEN = 2;
    //choice ints for easier reading
    //Screen Button Options
    private final static int BLOCK_BUTTON = 0;
    private final static int FORCE_BUTTON = 1;
    private final static int CARDS_BUTTON = 2;
    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public FungalCoreEvent() {

        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.HPLoss += 2;
        }

        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]);



    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case INTRO_SCREEN:

                switch (buttonPressed) {
                    case 0: // ventured in
                        this.imageEventText.loadImage(IMG2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();

                        if (GetHighBlockCards(AbstractDungeon.player.masterDeck).size() > 0){
                            this.imageEventText.setDialogOption(OPTIONS[2], CardLibrary.getCopy(pwrSporeShroom.ID));
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[3] + CardBlockMin + OPTIONS[4], true); //this is the text shown if you can't upgrade anything.
                        }
                        this.imageEventText.setDialogOption(OPTIONS[5] + HPLoss + OPTIONS[6], CardLibrary.getCopy(pwrSporeShroom.ID));
                        this.imageEventText.setDialogOption(OPTIONS[7]);


                        this.screenNum = OPTION_SCREEN;

                        return;
                    case 1: // Buy
                        this.openMap();
                        break;
                }
                break;



            case OPTION_SCREEN:

                switch (buttonPressed) {
                    case BLOCK_BUTTON:


                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new pwrSporeShroom(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;

                        break;
                    case FORCE_BUTTON: // Buy

                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.HPLoss));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new pwrSporeShroom(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;

                        break;
                    case CARDS_BUTTON: // Leave
                        //Effect!

                        reward(1);
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
            default:
                this.openMap();
        }
    }

    private void reward(int num) {
        AbstractDungeon.getCurrRoom().rewards.clear();

        for(int i = 0; i < num; ++i) {
            AbstractDungeon.getCurrRoom().addCardReward(new RewardItem((AbstractDungeon.player.getCardColor())));
        }
        AbstractDungeon.combatRewardScreen.open();
    }


    private CardGroup GetHighBlockCards(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if (c.baseBlock >= CardBlockMin)  {
                ret.group.add(c);
            }
        }
        return ret;
    }



}
