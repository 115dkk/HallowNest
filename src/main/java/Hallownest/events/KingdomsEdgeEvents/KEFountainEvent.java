package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.SoulVesselRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Hallownest.HallownestMod.makeEventPath;

public class KEFountainEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("KEFountainEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("KEFountainA.png");


    private boolean pickCard;
    private int damageToTake = 9;
    private int baseRiskDamage = 4;
    private int baseHeal = 10;


    private int Tier1Max = 40;
    private int Tier1;
    private int Tier1Heal = 1;


    private int Tier2Max = 80;
    private int Tier2;
    private int Tier2Heal = 3;


    private int Tier3Max = 120;
    private int Tier3;
    private int Tier3Heal = 5;




    private int LowGold = 10;
    private int HighGold = 25;


    private int BaseGold = AbstractDungeon.miscRng.random(15,25);
    private int CurrGold = 0;

    private int RewardTier = 0;
    private int RewardTierHPMod = 0;




    //Screens
    private final static int INTRO_SCREEN = 0;
    private final static int REWARDS_SCREEN = 1;
    private final static int LEAVE_SCREEN = 2;
    private final static int SUCCESS_SCREEN = 3;
    //choice ints for easier reading
    //Screen Button Options
    private final static int LOW_GOLD = 0;
    private final static int BIG_GOLD = 1;
    private final static int TAKE_BUTTON = 2;
    private final static int REWARDS_BUTTON = 3;
    
    private boolean attack;
    private boolean skill;
    private boolean power;
    private boolean cardsSelected;
    private AbstractCard attackCard;
    private AbstractCard skillCard;
    private AbstractCard cardToDown;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public KEFountainEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        
        
        this.Tier1 = AbstractDungeon.miscRng.random(1,Tier1Max);
        this.Tier2 = AbstractDungeon.miscRng.random(Tier1, Tier2Max);
        this.Tier3 = AbstractDungeon.miscRng.random(Tier2, Tier3Max);
        
        
        

        if (AbstractDungeon.player.gold >= this.LowGold) {
            this.imageEventText.setDialogOption(OPTIONS[1] + this.LowGold + OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[10], true);
        }
        if (AbstractDungeon.player.gold >= this.HighGold) {
            this.imageEventText.setDialogOption(OPTIONS[1] + this.HighGold + OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[10], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[3] + this.BaseGold + OPTIONS[4]);
        this.imageEventText.setDialogOption(OPTIONS[5]);


        if (AbstractDungeon.ascensionLevel >= 15) {
            this.baseHeal -= 3;
        }


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
           
            case INTRO_SCREEN:
                switch (buttonPressed) {
                    case LOW_GOLD:
                        this.imageEventText.clearAllDialogs();

                        this.CurrGold +=this.LowGold;
                        AbstractDungeon.player.loseGold(this.LowGold);

                        CardCrawlGame.sound.play("GOLD_GAIN");
                        if (this.CurrGold> (Tier3 + 25)){
                            this.imageEventText.setDialogOption(OPTIONS[11], true);
                            this.imageEventText.setDialogOption(OPTIONS[11], true);
                        } else {
                            if (AbstractDungeon.player.gold >= this.LowGold) {
                                this.imageEventText.setDialogOption(OPTIONS[1] + this.LowGold + OPTIONS[2]);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[10], true);
                            }
                            if (AbstractDungeon.player.gold >= this.HighGold) {
                                this.imageEventText.setDialogOption(OPTIONS[1] + this.HighGold + OPTIONS[2]);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[10], true);
                            }
                        }
                        this.imageEventText.setDialogOption(OPTIONS[3] + (this.BaseGold + this.CurrGold) + OPTIONS[4]);
                        this.imageEventText.setDialogOption(OPTIONS[5]);

                        return;
                    case BIG_GOLD: // Buy
                        this.imageEventText.clearAllDialogs();
                        AbstractDungeon.player.loseGold(this.HighGold);

                        this.CurrGold +=this.HighGold;
                        CardCrawlGame.sound.play("GOLD_GAIN_3");
                        if (this.CurrGold> (Tier3 + 25)){
                            this.imageEventText.setDialogOption(OPTIONS[11], true);
                            this.imageEventText.setDialogOption(OPTIONS[11], true);
                        } else {
                            if (AbstractDungeon.player.gold >= this.LowGold) {
                                this.imageEventText.setDialogOption(OPTIONS[1] + this.LowGold + OPTIONS[2]);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[10], true);
                            }
                            if (AbstractDungeon.player.gold >= this.HighGold) {
                                this.imageEventText.setDialogOption(OPTIONS[1] + this.HighGold + OPTIONS[2]);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[10], true);
                            }
                        }
                        this.imageEventText.setDialogOption(OPTIONS[3] + (this.BaseGold + this.CurrGold) + OPTIONS[4]);
                        this.imageEventText.setDialogOption(OPTIONS[5]);

                        return;
                    case TAKE_BUTTON: // Leave
                        //Effect!
                        AbstractDungeon.player.gainGold(this.BaseGold + this.CurrGold);
                        AbstractDungeon.effectList.add(new RainingGoldEffect((this.CurrGold + this.BaseGold)));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;

                        break;

                    case REWARDS_BUTTON: // Leave
                        //Effect!
                        if (this.CurrGold >= Tier3){
                            this.RewardTier = 3;
                            this.RewardTierHPMod = Tier3Heal;
                        } else if (this.CurrGold >= Tier2){
                            this.RewardTier = 2;
                            this.RewardTierHPMod = Tier2Heal;

                        } else if (this.CurrGold >= Tier1){
                            this.RewardTier = 1;
                            this.RewardTierHPMod = Tier1Heal;

                        } else {this.RewardTier = 0;}

                        switch (this.RewardTier){
                            case 0:
                                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                                this.imageEventText.clearAllDialogs();
                                this.imageEventText.setDialogOption(OPTIONS[0]);
                                this.imageEventText.clearRemainingOptions();
                                break;
                            case 1:
                                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                                this.imageEventText.clearAllDialogs();
                                this.imageEventText.setDialogOption(OPTIONS[0]);
                                this.imageEventText.setDialogOption(OPTIONS[8] + (this.baseHeal * this.RewardTierHPMod) + OPTIONS[9]);
                                break;
                            case 2:
                                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                                this.imageEventText.clearAllDialogs();
                                this.imageEventText.setDialogOption(OPTIONS[0]);
                                this.imageEventText.setDialogOption(OPTIONS[8] + (this.baseHeal * this.RewardTierHPMod) + OPTIONS[9]);
                                this.imageEventText.setDialogOption(OPTIONS[6]);
                                break;
                            case 3:
                                this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                                this.imageEventText.clearAllDialogs();
                                this.imageEventText.setDialogOption(OPTIONS[0]);
                                this.imageEventText.setDialogOption(OPTIONS[8] + (this.baseHeal * this.RewardTierHPMod) + OPTIONS[9]);
                                this.imageEventText.setDialogOption(OPTIONS[6]);
                                this.imageEventText.setDialogOption(OPTIONS[7], RelicLibrary.getRelic(SoulVesselRelic.ID));
                                break;
                        }
                        screenNum = REWARDS_SCREEN;

                        break;


                }
                break;

            case REWARDS_SCREEN:
                switch (buttonPressed) {
                    case 0:
                        this.openMap();
                        break;
                    case 1:
                        AbstractDungeon.player.heal((this.baseHeal * this.RewardTierHPMod), true);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;
                        break;
                    case 2:
                        AbstractRelic r1 = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.UNCOMMON);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r1);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;
                        break;
                    case 3:
                        AbstractRelic r2 = new SoulVesselRelic();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r2);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
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




}
