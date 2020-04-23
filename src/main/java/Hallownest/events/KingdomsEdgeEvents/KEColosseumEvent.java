package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.attackMasterCycloneSlash;
import Hallownest.cards.skillHoundingGlaives;
import Hallownest.dungeon.EncounterIDs;
import Hallownest.monsters.KingdomsEdgeEnemies.EventZote;
import Hallownest.relics.GodTamersBeastRelic;
import Hallownest.relics.NailmastersGloryRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class KEColosseumEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("KEColosseumEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("KEColosseum.png");


    private boolean pickCard;
    private int GoldToLose = 45;

    private int Trial;
    private int Warrior = 1;
    private int WarriorGold = 80;
    private int Conqueror = 2;
    private int ConquerorGold = 120;
    private int Fool = 3;
    private int FoolGold = 160;
    private int fightNum = 0;



    //Screens
    private final static int SELECT_SCREEN = 0;
    private final static int WARRIOR_SCREEN = 1;
    private final static int CONQUEROR_SCREEN = 2;
    private final static int FOOL_SCREEN = 3;
    private final static int ZOTE_SCREEN = 4;

    private final static int LEAVE_SCREEN = 5;


    //choice ints for easier reading
    //Screen Button Options
    private final static int NEXT_OR_REWARD_SCREEN = 0;
    private final static int LEAVE_BUTTON = 1;
    private final static int REWARD_BUTTON = 0;


    private final static int WARRIOR_BUTTON = 0;
    private final static int CONQUEROR_BUTTON = 1;
    private final static int FOOL_BUTTON = 2;
    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public KEColosseumEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        AbstractPlayer p = AbstractDungeon.player;
        imageEventText.setDialogOption(OPTIONS[0]); // Transform a Card is now button 0
        imageEventText.setDialogOption(OPTIONS[1], CardLibrary.getCopy(skillHoundingGlaives.ID));
        imageEventText.setDialogOption(OPTIONS[2], RelicLibrary.getRelic(GodTamersBeastRelic.ID)); // Remove a card is now button case 2


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case SELECT_SCREEN:
                switch (buttonPressed) {
                    case WARRIOR_BUTTON: // Buy
                        this.Trial = Warrior;
                        break;
                    case CONQUEROR_BUTTON: // Buy
                        this.Trial = Conqueror;
                        break;
                    case FOOL_BUTTON: // Buy
                        this.Trial = Fool;
                        break;
                }
                this.screenNum = WARRIOR_SCREEN;
                AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(EncounterIDs.FOOLS_ONE);
                AbstractDungeon.getCurrRoom().rewards.clear();
                AbstractDungeon.getCurrRoom().rewardAllowed = false;
                this.enterCombatFromImage();
                this.imageEventText.clearRemainingOptions();
                break;


            case WARRIOR_SCREEN:
                switch (buttonPressed) {
                    case NEXT_OR_REWARD_SCREEN: // Buy
                        if (Trial > Warrior){
                            this.screenNum = CONQUEROR_SCREEN;
                            AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(EncounterIDs.FOOLS_TWO);
                            AbstractDungeon.getCurrRoom().rewards.clear();
                            AbstractDungeon.getCurrRoom().rewardAllowed = false;
                            this.enterCombatFromImage();
                            this.imageEventText.clearRemainingOptions();
                        } else {
                            AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new EventZote());
                            AbstractDungeon.getCurrRoom().rewards.clear();
                            AbstractDungeon.getCurrRoom().rewardAllowed = false;
                            this.enterCombatFromImage();
                            this.imageEventText.clearRemainingOptions();
                            this.screenNum = ZOTE_SCREEN;
                        }
                        break;
                    case LEAVE_BUTTON: // Buy
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;

                        break;
                }
                break;
            case CONQUEROR_SCREEN:
                switch (buttonPressed) {
                    case NEXT_OR_REWARD_SCREEN: // Buy
                        if (Trial > Conqueror){
                            this.screenNum = FOOL_SCREEN;
                            AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(EncounterIDs.FOOLS_FULL);
                            AbstractDungeon.getCurrRoom().rewards.clear();
                            AbstractDungeon.getCurrRoom().rewardAllowed = false;
                            this.enterCombatFromImage();
                            this.imageEventText.clearRemainingOptions();
                        } else {
                            AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new EventZote());
                            AbstractDungeon.getCurrRoom().rewards.clear();
                            AbstractDungeon.getCurrRoom().rewardAllowed = false;
                            this.enterCombatFromImage();
                            this.imageEventText.clearRemainingOptions();
                            this.screenNum = ZOTE_SCREEN;
                        }
                        break;
                    case LEAVE_BUTTON: // Buy
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;

                        break;
                }
                break;
            case FOOL_SCREEN:
                switch (buttonPressed) {
                    case NEXT_OR_REWARD_SCREEN: // Buy
                        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new EventZote());
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().rewardAllowed = false;
                        this.enterCombatFromImage();
                        this.imageEventText.clearRemainingOptions();
                        this.screenNum = ZOTE_SCREEN;
                        break;
                    case LEAVE_BUTTON: // Buy
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;

                        break;
                }
                break;
            case ZOTE_SCREEN:
                switch (buttonPressed) {
                    case NEXT_OR_REWARD_SCREEN: // Buy
                        if (Trial == Warrior) {
                            AbstractDungeon.player.gainGold(WarriorGold);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(this.WarriorGold));
                        } else if (Trial == Conqueror) {
                            AbstractDungeon.player.gainGold(ConquerorGold);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(this.ConquerorGold));
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new skillHoundingGlaives(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        } else if (Trial == Fool){
                            AbstractDungeon.player.gainGold(FoolGold);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(this.FoolGold));
                            if (!AbstractDungeon.player.hasRelic(GodTamersBeastRelic.ID)){
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new GodTamersBeastRelic());
                            } else {
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                            }
                        }
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;
                        break;
                    case LEAVE_BUTTON: // Buy
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
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

    public void onEnterRoom() {
    }


    public void reopen() {
        AbstractDungeon.resetPlayer();
        AbstractDungeon.player.drawX = (float)Settings.WIDTH * 0.25F;
        AbstractDungeon.player.preBattlePrep();
        this.enterImageFromCombat();
        this.imageEventText.clearAllDialogs();
        switch (this.screenNum) {
            case WARRIOR_SCREEN:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                if (Trial > Warrior) {
                    this.imageEventText.setDialogOption(OPTIONS[3]);
                } else {
                    this.imageEventText.setDialogOption(OPTIONS[10]);
                }
                this.imageEventText.setDialogOption(OPTIONS[4]);
                break;
            case CONQUEROR_SCREEN:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                if (Trial > Conqueror) {
                    this.imageEventText.setDialogOption(OPTIONS[3]);
                } else {
                    this.imageEventText.setDialogOption(OPTIONS[10]);
                }
                this.imageEventText.setDialogOption(OPTIONS[4]);
                break;
            case FOOL_SCREEN:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.setDialogOption(OPTIONS[10]);
                this.imageEventText.setDialogOption(OPTIONS[4]);
                break;
            case ZOTE_SCREEN:
                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                if (Trial == Warrior) {
                    this.imageEventText.setDialogOption(OPTIONS[5] + WarriorGold + OPTIONS[6]);
                } else if (Trial == Conqueror) {
                    this.imageEventText.setDialogOption(OPTIONS[7] + ConquerorGold + OPTIONS[8],CardLibrary.getCopy(skillHoundingGlaives.ID));
                } else if (Trial == Fool){
                    this.imageEventText.setDialogOption(OPTIONS[7] + FoolGold + OPTIONS[8],RelicLibrary.getRelic(GodTamersBeastRelic.ID));
                }
                this.imageEventText.setDialogOption(OPTIONS[4]);
                break;
            }
    }
}
