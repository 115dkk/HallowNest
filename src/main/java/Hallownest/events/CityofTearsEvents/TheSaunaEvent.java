package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.BankAccountRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Hallownest.HallownestMod.makeEventPath;

public class TheSaunaEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("TheSaunaEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTSaunaA.png");
    public static final String IMG2 = makeEventPath("CoTSaunaB.png");
    public static final String IMG3 = makeEventPath("CoTSaunaC.png");


    private int GoldLost;
    private int GoldToGain;
    private int GoldonHit;
    private int HealVal = 14;


    //Screens
    private final static int INTRO_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    private final static int MILLIBELlE_SCREEN = 2;
    //choice ints for easier reading
    //Screen Button Options
    private final static int HEAL_BUTTON = 0;
    private final static int REMOVE_BUTTON = 1;
    private final static int LEAVE_BUTTON = 2;
    private final static int BANKER_BUTTON = 3;

    private boolean cardpicked;
    private boolean GoldLeft = false;





    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public TheSaunaEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);


        this.imageEventText.setDialogOption(OPTIONS[1] + HealVal + OPTIONS[2]);

        this.imageEventText.setDialogOption(OPTIONS[3]);


        this.imageEventText.setDialogOption(OPTIONS[0]);

        if (AbstractDungeon.player.hasRelic(BankAccountRelic.ID)){
            this.imageEventText.setDialogOption(OPTIONS[4]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[6],true);
        }





    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case INTRO_SCREEN:
                switch (buttonPressed) {
                    case HEAL_BUTTON:
                        AbstractDungeon.player.heal(HealVal, true);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.screenNum = LEAVE_SCREEN;
                        break;
                    case REMOVE_BUTTON:
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[12], false, false, false, true);
                        this.cardpicked = true;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.screenNum = LEAVE_SCREEN;
                        break;
                    case LEAVE_BUTTON:
                        this.openMap();
                        break;
                    case BANKER_BUTTON:
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EventBankerTalk.getKey(), 1.4F);
                        }

                        GoldLost = ((BankAccountRelic)AbstractDungeon.player.getRelic(BankAccountRelic.ID)).getGoldLost();

                        if (GoldLost > 0){
                            GoldToGain = ((GoldLost * 3)/2);
                            GoldonHit = PimpSlap();
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            this.imageEventText.loadImage(IMG2);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[9] + GoldonHit + OPTIONS[10]);
                            this.imageEventText.setDialogOption(OPTIONS[7]);
                            this.screenNum = MILLIBELlE_SCREEN;

                        } else {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            this.imageEventText.loadImage(IMG2);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[1] + HealVal + OPTIONS[2]);
                            this.imageEventText.setDialogOption(OPTIONS[3]);
                            this.imageEventText.setDialogOption(OPTIONS[0]);
                            this.imageEventText.setDialogOption(OPTIONS[8], true);
                            this.screenNum = INTRO_SCREEN;
                        }
                        break;
                }
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;

            case MILLIBELlE_SCREEN:
                switch (buttonPressed) {
                    case 0: // Bitchslapped the banker
                        if (this.GoldToGain > 0) {
                            GoldLeft = true;
                            if (Settings.AMBIANCE_ON) {
                                CardCrawlGame.sound.playV(SoundEffects.EventBankerHit.getKey(), 1.4F);
                            }
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
                            CardCrawlGame.sound.play("ATTACK_FAST");
                            AbstractDungeon.player.gainGold(GoldonHit);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(this.GoldonHit));
                            this.GoldToGain -= GoldonHit;
                            GoldonHit = PimpSlap();
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                            this.imageEventText.loadImage(IMG3);
                            this.imageEventText.updateDialogOption(0, OPTIONS[9] + GoldonHit + OPTIONS[10]);
                            //Method copied here for easy viewing it's actually not in this if statement obviously
                            /*
                             private int PimpSlap(){
                                int goldback = AbstractDungeon.miscRng.random(20,30);
                                if (goldback > GoldToGain){
                                    goldback = GoldToGain;
                                }
                                return goldback;
                            }
                             */
                        } else {
                            GoldLeft = false;
                            if (Settings.AMBIANCE_ON) {
                                CardCrawlGame.sound.playV(SoundEffects.EventBankerTalk2.getKey(), 1.4F);
                            }
                            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                            this.imageEventText.loadImage(IMG2);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[1] + HealVal + OPTIONS[2]);
                            this.imageEventText.setDialogOption(OPTIONS[3]);
                            this.imageEventText.setDialogOption(OPTIONS[0]);
                            this.screenNum = INTRO_SCREEN;
                        }
                        if (GoldLeft){
                            return;
                        } else {
                            break;
                        }
                    case 1: // mercy on millibelle
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EventBankerTalk2.getKey(), 1.4F);
                        }

                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.loadImage(IMG);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[1] + HealVal + OPTIONS[2]);
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = INTRO_SCREEN;
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    private int PimpSlap(){
        int goldback = AbstractDungeon.miscRng.random(20,30);
        if (goldback > GoldToGain){
            goldback = GoldToGain;
        }
        return goldback;
    }


    public void update() {
        super.update();
        if (this.cardpicked && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardCrawlGame.sound.play("CARD_EXHAUST");
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));

            AbstractDungeon.player.masterDeck.removeCard((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.cardpicked = false;
            }
        }






}
