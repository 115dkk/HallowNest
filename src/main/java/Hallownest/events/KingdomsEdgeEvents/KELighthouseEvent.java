package Hallownest.events.KingdomsEdgeEvents;

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
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Hallownest.HallownestMod.makeEventPath;

public class KELighthouseEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("KELighthouseEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("KELighthouseA.png");


    private boolean pickCard;
    private int HealVal = 15;
    private int baseRiskDamage = 4;
    private int baseRiskReward = 5;
    private int MaxLoss = 7;
    private int DefyDamage = 9;

    //Screens
    private final static int INTRO_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    private final static int SUCCESS_SCREEN = 3;
    //choice ints for easier reading
    //Screen Button Options
    private final static int REST_BUTTON = 0;
    private final static int VOID_BUTTON = 1;
    private final static int LEAVE_BUTTON = 2;
    private boolean attack;
    private boolean skill;
    private boolean power;
    private boolean cardsSelected;
    private AbstractCard CardtoTriple1;
    private AbstractCard CardtoTriple2;
    private AbstractCard CardtoTriple3;
    private boolean tripled = false;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public KELighthouseEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);


        if (AbstractDungeon.ascensionLevel >= 15) {
            this.HealVal = (HealVal - 5);
        }

        this.imageEventText.setDialogOption(OPTIONS[1] + HealVal + OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
        this.imageEventText.setDialogOption(OPTIONS[0]);




    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case INTRO_SCREEN:
                switch (buttonPressed) {
                    case REST_BUTTON:

                        AbstractDungeon.player.heal(HealVal,true);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = LEAVE_SCREEN;
                        break;
                    case VOID_BUTTON: // Buy
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 3, OPTIONS[4], false, false, false, true);
                        this.remove();
                        tripled = true;

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;
                    case LEAVE_BUTTON: // Leave
                        //Effect!

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[5]);
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


    private void remove() {
        if (!AbstractDungeon.isScreenUp) {
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 3, OPTIONS[4], false, false, false, true);
        } else {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 3, OPTIONS[4], false, false, false, true);
        }

    }

    public void update() {
        super.update();
        if (!this.cardsSelected) {
            List<String>removedCards = new ArrayList();
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 3) {
                int cardindex = AbstractDungeon.miscRng.random(0,2);
                CardtoTriple1 = AbstractDungeon.gridSelectScreen.selectedCards.get(cardindex).makeStatEquivalentCopy();
                CardtoTriple2 = AbstractDungeon.gridSelectScreen.selectedCards.get(cardindex).makeStatEquivalentCopy();
                AbstractDungeon.gridSelectScreen.selectedCards.remove(cardindex);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(CardtoTriple1, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, true));
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(CardtoTriple2, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, true));
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
