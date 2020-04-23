package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class ElderbugEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("ElderbugEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("ElderbugEventA.png");


    private boolean pickCard;
    private int Choice;



    //Screens
    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int TRANSFORM_BUTTON = 0;
    private final static int UPGRADE_BUTTON = 1;
    private final static int REMOVE_BUTTON = 2;



    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public ElderbugEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        AbstractPlayer p = AbstractDungeon.player;



        imageEventText.setDialogOption(OPTIONS[1]); // Transform a Card is now button 0
        if (AbstractDungeon.player.masterDeck.hasUpgradableCards()) { // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
            this.imageEventText.setDialogOption(OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[7], true); //this is the text shown if you can't upgrade anything.
        }
        imageEventText.setDialogOption(OPTIONS[3]); // Remove a card is now button case 2

    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpElder.getKey(), 1.4F);
        }

    }

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            switch(this.Choice) {
                case REMOVE_BUTTON:
                    CardCrawlGame.sound.play("CARD_EXHAUST");
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));

                    AbstractDungeon.player.masterDeck.removeCard((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                    break;
                case TRANSFORM_BUTTON:
                    AbstractCard c = (AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
                    AbstractCard transCard = AbstractDungeon.getTransformedCard();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transCard, c.current_x, c.current_y));
                    break;
                case UPGRADE_BUTTON:
                    AbstractDungeon.effectsQueue.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0)).upgrade();
                    AbstractCard upgCard = (AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                    AbstractDungeon.player.bottledCardUpgradeCheck(upgCard);
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.pickCard = false;
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case TRANSFORM_BUTTON:
                        Choice = TRANSFORM_BUTTON;
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[4], false, true, false, false);
                        }

                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;
                        break;
                    case UPGRADE_BUTTON: // Buy
                        Choice = UPGRADE_BUTTON;
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getUpgradableCards(), 1, OPTIONS[5], true, false, false, false);
                        }

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;
                    case REMOVE_BUTTON: // Leave
                        Choice = REMOVE_BUTTON;
                        //Effect!
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[6], false, false, false, true);
                        }

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();

                        screenNum = LEAVE_SCREEN;

                        break;

                }
                this.pickCard = true;
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }


}
