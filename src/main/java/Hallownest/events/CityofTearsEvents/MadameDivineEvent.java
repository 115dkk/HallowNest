package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.SheosBrushRelic;
import Hallownest.relics.StagBellStandRelic;
import Hallownest.relics.UnbreakableCardRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class MadameDivineEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("MadameDivineEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTDivineA.png");
    public static final String IMG2 = makeEventPath("CoTDivineB.png");
//



    private final static int OPTION_SCREEN = 0;
    private final static int SELL_OR_REFINE_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int REFINE_BUTTON = 0;
    private final static int SELL_BUTTON = 1;
    private final static int LEAVE_BUTTON = 2;


    private final static int SELL = 1;
    private final static int REFINE = 0;

    private boolean optionsExist = false;

    private boolean pickCard;
    private int Choice;
    private int RefineCost = 250;




    private int screenNum = 0;

    public MadameDivineEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]);


    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:

                switch (buttonPressed) {
                    case 0: //Encounter a normal enemy
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EventDivineTalk.getKey(), 1.4F);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.loadImage(IMG2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();



                        if (AbstractDungeon.player.gold >= (RefineCost)) {
                            if (getCardsWithExhaust(AbstractDungeon.player.masterDeck).size() >=1) {
                                optionsExist = true;
                                // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.


                                this.imageEventText.setDialogOption((OPTIONS[2] + RefineCost + OPTIONS[3]), RelicLibrary.getRelic(UnbreakableCardRelic.ID));
                            } else {
                                this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                            }
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[9], true);
                        }


                        if (getCardsWithExhaust(AbstractDungeon.player.masterDeck).size() >=1) { // Lose a card and heal to full
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                            this.imageEventText.setDialogOption(OPTIONS[0]);

                        if (!optionsExist) {
                            this.imageEventText.setDialogOption(OPTIONS[6]);//?? something else for your trouble i guess
                        }


                        screenNum = 1;
                        break;
                    case 1: // Just Leave
                        this.openMap();
                        break;
                }
                break;

            case SELL_OR_REFINE_SCREEN:
                switch (buttonPressed) {
                    case REFINE_BUTTON: //upgrade 2 random attacks
                        this.Choice = REFINE;
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(getCardsWithExhaust(AbstractDungeon.player.masterDeck)), 1, OPTIONS[7], false, false, false, true);


                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        pickCard = true;
                        screenNum = 2;
                        break;
                    case SELL_BUTTON: // transform a starting strike
                        this.Choice = SELL;
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(getCardsWithExhaust(AbstractDungeon.player.masterDeck)), 1, OPTIONS[8], false, false, false, true);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        pickCard = true;
                        break;
                    case LEAVE_BUTTON: // leave


                        this.openMap();
                        break;
                    case 3: // No other choice? something kinda nice.
                        // maybe heal to full.
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);

                        AbstractDungeon.player.heal(5);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                }
                break;
            case 2:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            switch(this.Choice) {
                case REFINE:
                    AbstractDungeon.player.loseGold(RefineCost);
                    CardCrawlGame.sound.play("CARD_EXHAUST");
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                    AbstractDungeon.player.masterDeck.removeCard((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new UnbreakableCardRelic());
                    break;
                case SELL:
                    CardCrawlGame.sound.play("CARD_EXHAUST");
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                    AbstractDungeon.player.masterDeck.removeCard((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                    AbstractDungeon.player.heal((AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth));
                    break;

            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.pickCard = false;
        }

    }


    private static CardGroup getCardsWithExhaust(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if (c.exhaust){
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }





}
