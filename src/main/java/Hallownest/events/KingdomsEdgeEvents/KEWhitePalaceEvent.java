package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.KingsIdolRelic;
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
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Hallownest.HallownestMod.makeEventPath;

public class KEWhitePalaceEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("KEWhitePalaceEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG0 = makeEventPath("WhitePalaceAEvent.png");
    public static final String IMG1 = makeEventPath("WhitePalaceB1Event.png");
    public static final String IMG2 = makeEventPath("WhitePalaceBEvent.png");
    public static final String IMG3 = makeEventPath("WhitePalaceC1Event.png");
    public static final String IMG4  = makeEventPath("WhitePalaceCEvent.png");
    public static final String IMG5 = makeEventPath("WhitePalaceDEvent.png");


    private int Dodges;
    private int Retries;

    private int CurrChance;

    private int DodgeChance = 20;
    private int Basechance = 35;
    private int DamageChance = 5;
    private int basedamage = 4;

    private boolean Dodged = false;
    private boolean Reset = false;


    //Screens
    private final static int TRIAL1_SCREEN = 0;
    private final static int TRIAL2_SCREEN = 1;
    private final static int TRIAL3_SCREEN = 2;
    private final static int TRIAL4_SCREEN = 3;
    private final static int LEAVE_SCREEN = 5;
    private final static int SUCCESS_SCREEN = 4;
    //choice ints for easier reading
    //Screen Button Options
    private final static int ATTEMPT_BUTTON = 0;
    private final static int DODGE_BUTTON = 1;
    private final static int BAIL_BUTTON = 2;
    private boolean attack;
    private boolean skill;
    private boolean power;
    private boolean cardsSelected;
    private AbstractCard attackCard;
    private AbstractCard skillCard;
    private AbstractCard cardToDown;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public KEWhitePalaceEvent() {
        super(NAME, DESCRIPTIONS[0], IMG0);
        Dodges = getDodges(AbstractDungeon.player.masterDeck).size();
        Retries =  1 + getRetries(AbstractDungeon.player.masterDeck).size();



        
        
        this.imageEventText.setDialogOption(OPTIONS[0] + Retries + OPTIONS[1] + Dodges + OPTIONS[2], RelicLibrary.getRelic(KingsIdolRelic.ID));
        this.imageEventText.setDialogOption(OPTIONS[10]);






    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case TRIAL1_SCREEN:
                switch (buttonPressed) {
                    case 0: //pressed start challenge

                        CurrChance = Basechance;
                        /*

                        */

                        this.imageEventText.loadImage(IMG1);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();

                        if (this.Retries >0){
                            this.imageEventText.setDialogOption(OPTIONS[3] + Basechance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[7], true);
                        }
                        if (this.Dodges >0){
                            this.imageEventText.setDialogOption(OPTIONS[8] + Dodges);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[9], true);
                        }
                        this.imageEventText.setDialogOption(OPTIONS[10]);

                        screenNum = TRIAL2_SCREEN;
                        break;



                    case 1: //pressed leave
                        this.openMap();
                        break;

                }
                break;
            case TRIAL2_SCREEN: //this will either recursively update the first trial or load trial 2.
                switch (buttonPressed) {
                    case ATTEMPT_BUTTON:

                        int random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.CurrChance) {
                            this.imageEventText.loadImage(IMG2);
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            this.CurrChance = this.Basechance;
                            this.imageEventText.clearAllDialogs();
                            if (this.Retries >0){
                                this.imageEventText.setDialogOption(OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[7], true);
                            }
                            if (this.Dodges >0){
                                this.imageEventText.setDialogOption(OPTIONS[8] + Dodges);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[9], true);
                            }
                            this.imageEventText.setDialogOption(OPTIONS[10]);
                            this.screenNum = TRIAL3_SCREEN;
                        } else {
                            if (random < DamageChance){
                                CardCrawlGame.sound.play("ATTACK_FAST");
                                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.basedamage));
                            }
                            this.Retries--;
                            if (this.Retries >0){
                                this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                            } else {
                                this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                            }

                        }

                        return;
                    case DODGE_BUTTON: // Dodge but go to Button 1 after somehow?

                        this.Dodges--;
                        CurrChance += DodgeChance;
                        if (this.Retries >0){
                            this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                        } else {
                            this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                        }
                        if (this.Dodges >0){
                            this.imageEventText.updateDialogOption(1,OPTIONS[8] + Dodges);
                        } else {
                            this.imageEventText.updateDialogOption(1,OPTIONS[9], true);
                        }

                        //this.buttonEffect(ATTEMPT_BUTTON);

                        return;
                    case 2: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;
                        break;

                }
                break;
            case TRIAL3_SCREEN:
                switch (buttonPressed) {
                    case ATTEMPT_BUTTON:

                        int random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.CurrChance) {
                            this.imageEventText.loadImage(IMG3);
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            this.CurrChance = this.Basechance;
                            this.imageEventText.clearAllDialogs();
                            if (this.Retries >0){
                                this.imageEventText.setDialogOption(OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[7], true);
                            }
                            if (this.Dodges >0){
                                this.imageEventText.setDialogOption(OPTIONS[8] + Dodges);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[9], true);
                            }
                            this.imageEventText.setDialogOption(OPTIONS[10]);
                            this.screenNum = TRIAL4_SCREEN;
                        } else {
                            if (random < DamageChance){
                                CardCrawlGame.sound.play("ATTACK_FAST");
                                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.basedamage));
                            }
                            this.Retries--;
                            if (this.Retries >0){
                                this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                            } else {
                                this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                            }

                        }

                        return;
                    case DODGE_BUTTON: // Dodge but go to Button 1 after somehow?

                        this.Dodges--;
                        CurrChance += DodgeChance;
                        if (this.Retries >0){
                            this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                        } else {
                            this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                        }
                        if (this.Dodges >0){
                            this.imageEventText.updateDialogOption(1,OPTIONS[8] + Dodges);
                        } else {
                            this.imageEventText.updateDialogOption(1,OPTIONS[9], true);
                        }

                        //this.buttonEffect(ATTEMPT_BUTTON);

                        return;
                    case 2: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;

                }
                break;

            case TRIAL4_SCREEN:
                switch (buttonPressed) {
                    case ATTEMPT_BUTTON:

                        int random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.CurrChance) {
                            this.imageEventText.loadImage(IMG4);
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                            this.CurrChance = this.Basechance;
                            this.imageEventText.clearAllDialogs();
                            if (this.Retries >0){
                                this.imageEventText.setDialogOption(OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[7], true);
                            }
                            if (this.Dodges >0){
                                this.imageEventText.setDialogOption(OPTIONS[8] + Dodges);
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[9], true);
                            }
                            this.imageEventText.setDialogOption(OPTIONS[10]);

                            this.screenNum = SUCCESS_SCREEN;
                        } else {
                            if (random < DamageChance){
                                CardCrawlGame.sound.play("ATTACK_FAST");
                                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.basedamage));
                            }
                            this.Retries--;
                            if (this.Retries >0){
                                this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                            } else {
                                this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                            }

                        }

                        return;
                    case DODGE_BUTTON: // Dodge but go to Button 1 after somehow?

                        this.Dodges--;
                        CurrChance += DodgeChance;
                        if (this.Retries >0){
                            this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                        } else {
                            this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                        }
                        if (this.Dodges >0){
                            this.imageEventText.updateDialogOption(1,OPTIONS[8] + Dodges);
                        } else {
                            this.imageEventText.updateDialogOption(1,OPTIONS[9], true);
                        }

                        //this.buttonEffect(ATTEMPT_BUTTON);

                        return;
                    case 2: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;

                }
                break;
            case SUCCESS_SCREEN: //loads up the thronerooms finally
                switch (buttonPressed) {
                    case ATTEMPT_BUTTON:

                        int random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.CurrChance) {
                            this.imageEventText.loadImage(IMG5);
                            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[11], RelicLibrary.getRelic(KingsIdolRelic.ID));
                            this.imageEventText.setDialogOption(OPTIONS[10]);
                            this.screenNum = 6;
                        } else {
                            if (random < DamageChance){
                                CardCrawlGame.sound.play("ATTACK_FAST");
                                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.basedamage));
                            }
                            this.Retries--;
                            if (this.Retries >0){
                                this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                            } else {
                                this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                            }

                        }

                        return;
                    case DODGE_BUTTON: // Dodge but go to Button 1 after somehow?

                        this.Dodges--;
                        CurrChance += DodgeChance;
                        if (this.Retries >0){
                            this.imageEventText.updateDialogOption(0,OPTIONS[3] + CurrChance + OPTIONS[4] + basedamage + OPTIONS[5] + DamageChance + OPTIONS[6] + Retries);
                        } else {
                            this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                        }
                        if (this.Dodges >0){
                            this.imageEventText.updateDialogOption(1,OPTIONS[8] + Dodges);
                        } else {
                            this.imageEventText.updateDialogOption(1,OPTIONS[9], true);
                        }

                        //this.buttonEffect(ATTEMPT_BUTTON);

                        return;
                    case 2: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;

                }
                break;

            case 6:
                switch (buttonPressed) {
                    case 0:// accept relic
                        if (!AbstractDungeon.player.hasRelic(KingsIdolRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new KingsIdolRelic());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;
                        break;
                    case 1: //leave like a moron?
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[10]);
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

    private static CardGroup getDodges(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if ((c.baseBlock > 0))  {
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }

    private static CardGroup getRetries(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if (c.cost == 0)  {
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }






}
