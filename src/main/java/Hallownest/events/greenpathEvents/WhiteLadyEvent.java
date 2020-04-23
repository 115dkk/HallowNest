package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.status.curseDreamersLament;
import Hallownest.relics.BankAccountRelic;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.cards.curses.Writhe;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.RegenPotion;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.List;

import static Hallownest.HallownestMod.makeEventPath;

public class WhiteLadyEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("WhiteLadyEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("WhiteLadyA.png");

//



    private final static int OPTION_SCREEN = 0;
    private final static int DREAM_NAILED_OPTIONS = 2;
    private final static int LEAVE_SCREEN = 1;

    //choice ints for easier reading
    //Screen Button Options
    private final static int MAX_HP = 0;
    private final static int HEAL = 1;
    private final static int REGEN_POT = 2;
    private final static int DREAM_NAILED = 3;

    private final static int DREAM_NAIL = 1;
    private final static int FINISHED = 0;

    private boolean optionsExist = false;
    private int HealCost = 40;
    private int HealVal;
    private int MaxHPUp;
    private int Fullgold;



    private int screenNum = 0;

    public WhiteLadyEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.HealVal = ((AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth)/2);
        this.MaxHPUp = (AbstractDungeon.player.maxHealth/8);
        // Options without Dreamnailing
        this.imageEventText.setDialogOption(OPTIONS[0] + MaxHPUp +OPTIONS[1], CardLibrary.getCopy(Writhe.ID));
        //button 1 for if we have 100 gold
        if (AbstractDungeon.player.gold >= (HealCost)) {
            this.imageEventText.setDialogOption(OPTIONS[4] + HealCost + OPTIONS[5] + HealVal); //Pay for 50% missing HP restored
        } else {
            this.imageEventText.setDialogOption(OPTIONS[8] + (HealCost) + OPTIONS[9], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[6]); // button 2

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            this.imageEventText.setDialogOption(OPTIONS[7]); // button 3
        }


    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpQueen1.getKey(), 1.5F);
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case MAX_HP: //bought an account but didn't deposit more gold
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Writhe(),Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        AbstractDungeon.player.increaseMaxHp(MaxHPUp, true);
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
                        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        }
                        screenNum = LEAVE_SCREEN;
                        break;
                    case HEAL: // Encounter an elite enemy
                        AbstractDungeon.player.loseGold(HealCost);
                        AbstractDungeon.player.heal(HealVal, true);
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);

                        screenNum = LEAVE_SCREEN;
                        break;
                    case REGEN_POT: // Encounter an elite enemy
                        if (AbstractDungeon.player.hasRelic("Sozu")) {
                            AbstractDungeon.player.getRelic("Sozu").flash();
                        } else {
                            AbstractPotion p = PotionHelper.getPotion(RegenPotion.POTION_ID);
                            AbstractDungeon.player.obtainPotion(p);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);

                        screenNum = LEAVE_SCREEN;
                        break;
                    case DREAM_NAILED: // Encounter an elite enemy

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.setDialogOption(OPTIONS[0] + MaxHPUp +OPTIONS[3]);
                        if (AbstractDungeon.player.gold >= (HealCost-10)) {
                            this.imageEventText.setDialogOption(OPTIONS[4] + (HealCost-10) + OPTIONS[5] + HealVal); //Pay for 50% missing HP restored
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[8] + (HealCost-10) + OPTIONS[9], true);
                        }
                        this.imageEventText.setDialogOption(OPTIONS[6]); // button 2


                        screenNum = DREAM_NAILED_OPTIONS;
                        break;
                }
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;
            case DREAM_NAILED_OPTIONS:
                switch (buttonPressed) {
                    case MAX_HP: //bought an account but didn't deposit more gold
                        AbstractDungeon.player.increaseMaxHp(MaxHPUp, true);
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case HEAL: // Encounter an elite enemy
                        AbstractDungeon.player.loseGold(HealCost-10);
                        AbstractDungeon.actionManager.addToBottom(new HealAction(AbstractDungeon.player, AbstractDungeon.player, HealVal));
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);

                        screenNum = LEAVE_SCREEN;
                        break;
                    case REGEN_POT: // Encounter an elite enemy

                        if (AbstractDungeon.player.hasRelic("Sozu")) {
                            AbstractDungeon.player.getRelic("Sozu").flash();
                        } else {
                            AbstractPotion p = PotionHelper.getPotion(RegenPotion.POTION_ID);
                            AbstractDungeon.player.obtainPotion(p);
                        }

                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);

                        screenNum = LEAVE_SCREEN;
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }


}
