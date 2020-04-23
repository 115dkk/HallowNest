package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Hallownest.HallownestMod.makeEventPath;

public class GrimmEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("GrimmEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("PerformanceA.png");


    private boolean pickCard;
    private int damageToTake = 9;
    private int baseRiskDamage = 4;
    private int baseRiskReward = 5;
    private int MaxLoss = 7;
    private int DefyDamage = 9;

    //Screens
    private final static int INTRO_SCREEN = 0;
    private final static int OPTION_SCREEN = 1;
    private final static int LEAVE_SCREEN = 2;
    private final static int SUCCESS_SCREEN = 3;
    //choice ints for easier reading
    //Screen Button Options
    private final static int RISK_BUTTON = 0;
    private final static int BANISH_BUTTON = 1;
    private final static int DEFY_BUTTON = 2;
    private boolean attack;
    private boolean skill;
    private boolean power;
    private boolean cardsSelected;
    private AbstractCard attackCard;
    private AbstractCard skillCard;
    private AbstractCard cardToDown;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public GrimmEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[0]);


        if (AbstractDungeon.ascensionLevel >= 15) {
            this.MaxLoss += 1;
            this.DefyDamage +=2;
        }


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case INTRO_SCREEN:

                if (Settings.AMBIANCE_ON) {
                    CardCrawlGame.sound.playV(SoundEffects.EvGpGrimm.getKey(), 1.4F);
                }
                this.imageEventText.loadImage(makeEventPath("PerformanceB.png"));
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[1] + baseRiskDamage + OPTIONS[2] + baseRiskReward + OPTIONS[3]);
                this.imageEventText.setDialogOption(OPTIONS[4] + MaxLoss + OPTIONS[5]);
                this.imageEventText.setDialogOption(OPTIONS[6] + DefyDamage + OPTIONS[7]);
                screenNum = OPTION_SCREEN;


                break;
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case RISK_BUTTON:

                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.baseRiskDamage));
                        CardCrawlGame.sound.play("ATTACK_FIRE");
                        int random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.baseRiskReward) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            AbstractRelic r1 = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r1);
                            AbstractRelic r2 = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r2);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[9]);
                            this.imageEventText.clearRemainingOptions();
                            this.screenNum = LEAVE_SCREEN;
                        } else {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                            this.baseRiskReward += 5;
                            this.baseRiskDamage++;
                            this.baseRiskDamage++;
                            this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.baseRiskDamage + OPTIONS[2] + this.baseRiskReward + OPTIONS[3]);
                        }

                        return;
                    case BANISH_BUTTON: // Buy


                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpGrimm2.getKey(), 1.4F);
                        }
                        AbstractDungeon.player.maxHealth -= this.MaxLoss;
                        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth) {
                            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
                        }

                        if (AbstractDungeon.player.maxHealth < 1) {
                            AbstractDungeon.player.maxHealth = 1;
                        }

                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 2, OPTIONS[8], false, false, false, true);

                        this.remove();

                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;
                    case DEFY_BUTTON: // Leave
                        //Effect!
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpGrimm2.getKey(), 1.4F);
                        }

                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.DefyDamage));
                        CardCrawlGame.sound.play("ATTACK_FIRE");

                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
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
            case 3:

                this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[6]);
                this.imageEventText.clearRemainingOptions();

                screenNum = LEAVE_SCREEN;


                break;
            default:
                this.openMap();
        }
    }


    private void remove() {
        if (!AbstractDungeon.isScreenUp) {
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 2, OPTIONS[8], false, false, false, true);
        } else {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 2, OPTIONS[8], false, false, false, true);
        }

    }

    public void update() {
        super.update();
        if (!this.cardsSelected) {
            List<String>removedCards = new ArrayList();
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 2) {
                this.cardsSelected = true;
                float displayCount = 0.0F;
                Iterator i = AbstractDungeon.gridSelectScreen.selectedCards.iterator();

                while(i.hasNext()) {
                    AbstractCard card = (AbstractCard)i.next();
                    card.untip();
                    card.unhover();
                    removedCards.add(card.cardID);
                    AbstractDungeon.player.masterDeck.removeCard(card);
                    AbstractDungeon.effectList.add(new ExhaustCardEffect(card));
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }

    }





}
