package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.dungeon.EncounterIDs;
import Hallownest.monsters.KingdomsEdgeEnemies.monsterMarmu;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.EffectHelper;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Hallownest.HallownestMod.makeEventPath;

public class GhostsofGreenpath extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("GhostsofGreenpath");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("GPGhostsA.png");


    private boolean pickCard;
    private int GoldGain;
    private int SmallChance = 30;
    private int BigChance = 75;

    private boolean cardsSelected;



    //Screens
    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int GOLD_BUTTON = 0;
    private final static int SMALL_BUTTON = 1;
    private final static int BIG_BUTTON = 2;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public GhostsofGreenpath() {
        super(NAME, DESCRIPTIONS[0], IMG);
        AbstractPlayer p = AbstractDungeon.player;
        GoldGain = AbstractDungeon.miscRng.random(35, 75);
        
        imageEventText.setDialogOption(OPTIONS[0] + GoldGain + OPTIONS[1]); // Transform a Card is now button 0
        imageEventText.setDialogOption(OPTIONS[2] + SmallChance + OPTIONS[3]); // Remove a card is now button case 2
        imageEventText.setDialogOption(OPTIONS[4] + BigChance + OPTIONS[3]); // Remove a card is now button case 2
      
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case GOLD_BUTTON:

                        EffectHelper.gainGold(AbstractDungeon.player, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, GoldGain);
                        AbstractDungeon.player.gainGold(GoldGain);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[6]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;
                        break;
                    case SMALL_BUTTON: // Buy
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[7], false, false, false, true);
                        this.remove();
                        int random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.SmallChance) {
                            this.screenNum = 2;
                            AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new monsterMarmu(-50.0f, 100.0f));
                            AbstractDungeon.getCurrRoom().rewardAllowed = true;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[5]);
                            this.imageEventText.clearRemainingOptions();
                        } else {
                            this.screenNum = LEAVE_SCREEN;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[6]);
                            this.imageEventText.clearRemainingOptions();
                        }

                        break;

                    case BIG_BUTTON: // Leave
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 2, OPTIONS[8], false, false, false, true);
                        this.remove();
                        int random2 = AbstractDungeon.miscRng.random(0, 99);
                        if (random2 >= 99 - this.BigChance) {
                            this.screenNum = 2;
                            AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new monsterMarmu(-50.0f, 100.0f));
                            AbstractDungeon.getCurrRoom().rewardAllowed = true;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[5]);
                            this.imageEventText.clearRemainingOptions();
                        } else {
                            this.screenNum = LEAVE_SCREEN;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[6]);
                            this.imageEventText.clearRemainingOptions();
                        }
                        break;

                }
                this.pickCard = true;
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;
            case 2:
                this.enterCombatFromImage();
                AbstractDungeon.lastCombatMetricKey = monsterMarmu.ID;
                this.imageEventText.clearRemainingOptions();
                break;
            case 3:
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
            List<String> removedCards = new ArrayList();
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

            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
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



    public void reopen() {
        if (this.screenNum != LEAVE_SCREEN) {
            AbstractDungeon.resetPlayer();
            AbstractDungeon.player.drawX = (float)Settings.WIDTH * 0.25F;
            AbstractDungeon.player.preBattlePrep();
            this.enterImageFromCombat();
            this.screenNum = LEAVE_SCREEN;
            this.openMap();
            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[6]);
            this.imageEventText.clearRemainingOptions();
        }

    }



}
