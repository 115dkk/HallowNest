package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.attackFallenFury;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class FallenFuryEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("FallenFuryEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("FuryEventA.png");


    private boolean pickCard;
    private int damageToTake = 4;

    private int MaxGold= 125;
    private int MinGold = 90;
    private int MaxLoss = 7;

    //Screens

    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int GAIN_BUTTON = 0;
    private final static int REMOVE_BUTTON = 1;
    private final static int GOLD_BUTTON = 2;
    private boolean attack;
    private boolean skill;
    private boolean power;
    private boolean cardsSelected;
    private AbstractCard attackCard;
    private AbstractCard skillCard;
    private AbstractCard cardReward;
    private int goldtogain = AbstractDungeon.miscRng.random(MinGold,MaxGold);


    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public FallenFuryEvent() {

        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.player.currentHealth <= (AbstractDungeon.player.maxHealth/2)) {
            this.cardReward = new attackFallenFury();
            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
            this.imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(this.cardReward.name, "g"), this.cardReward.makeStatEquivalentCopy());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[7], true); //this is the text shown if you can't upgrade anything.
        }

        imageEventText.setDialogOption(OPTIONS[2] + this.damageToTake + OPTIONS[3] ); // Remove a card is now button case 2
        imageEventText.setDialogOption(OPTIONS[4] + goldtogain + OPTIONS[5], CardLibrary.getCopy(Shame.ID)); // Transform a Card is now button 0


        if (AbstractDungeon.ascensionLevel >= 15) {
            this.damageToTake += 2;
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:

                switch (buttonPressed) {
                    case GAIN_BUTTON:


                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new attackFallenFury(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        this.screenNum = LEAVE_SCREEN;

                        return;
                    case REMOVE_BUTTON: // Buy

                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.damageToTake));
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[6], false, false, false, true);
                        }

                        this.pickCard = true;

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;
                    case GOLD_BUTTON: // Leave
                        //Effect!
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldtogain));
                        AbstractDungeon.player.gainGold(this.goldtogain);

                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Shame(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));


                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
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

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardCrawlGame.sound.play("CARD_EXHAUST");
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect((AbstractCard) AbstractDungeon.gridSelectScreen.selectedCards.get(0), (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
            AbstractDungeon.player.masterDeck.removeCard((AbstractCard) AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.pickCard = false;
        }

    }





}
