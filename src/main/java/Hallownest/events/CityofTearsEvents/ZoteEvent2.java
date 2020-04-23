package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.dungeon.EncounterIDs;
import Hallownest.monsters.CityofTearsEnemies.eliteSoulWarrior;
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
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class ZoteEvent2 extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("ZoteEvent2");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTZoteEventA.png");


    private boolean pickCard;
    private int GoldToLose = 45;



    //Screens
    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int CURSE_BUTTON = 0;
    private final static int COMBAT_BUTTON = 1;
    private final static int LOSE_GOLD_BUTTON = 2;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public ZoteEvent2() {
        super(NAME, DESCRIPTIONS[0], IMG);
        AbstractPlayer p = AbstractDungeon.player;



        imageEventText.setDialogOption(OPTIONS[1], CardLibrary.getCopy(Doubt.ID)); // Transform a Card is now button 0

        imageEventText.setDialogOption(OPTIONS[2]); // Remove a card is now button case 2

        if (AbstractDungeon.player.gold >= GoldToLose) {
            this.imageEventText.setDialogOption(OPTIONS[3] + GoldToLose + OPTIONS[4]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5] + GoldToLose  +OPTIONS[6], true);
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case CURSE_BUTTON:



                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }

                        AbstractCard curse = new Doubt();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = 2;
                        break;
                    case COMBAT_BUTTON: // Buy
                        this.screenNum = 2;
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(EncounterIDs.FLUKES);
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().rewardAllowed = false;
                        this.enterCombatFromImage();
                        this.imageEventText.clearRemainingOptions();
                        break;

                    case LOSE_GOLD_BUTTON: // Leave
                        //Effect!
                        AbstractDungeon.player.loseGold(GoldToLose);

                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpZote1.getKey(), 1.4F);
                        }
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
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
                this.imageEventText.setDialogOption(OPTIONS[7]);
                this.imageEventText.clearRemainingOptions();

                screenNum = LEAVE_SCREEN;


                break;

            case 3:

                this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[7]);
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


    public void reopen() {
        if (this.screenNum != LEAVE_SCREEN) {
            AbstractDungeon.resetPlayer();
            AbstractDungeon.player.drawX = (float)Settings.WIDTH * 0.25F;
            AbstractDungeon.player.preBattlePrep();
            this.enterImageFromCombat();
            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[0]);
            this.imageEventText.clearRemainingOptions();
        }

    }



}
