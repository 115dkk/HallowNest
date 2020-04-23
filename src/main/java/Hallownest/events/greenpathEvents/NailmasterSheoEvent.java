package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.SheosBrushRelic;
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
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class NailmasterSheoEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("NailmasterSheoEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("SheoEventA.png");
    public static final String IMG2 = makeEventPath("SheoEventB.png");
//



    private final static int OPTION_SCREEN = 0;
    private final static int END_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int NAIL_BUTTON = 0;
    private final static int CREATE_BUTTON = 1;
    private final static int PAINTBRUSH_BUTTON = 2;
    private final static int LEAVE_BUTTON = 3;


    private boolean optionsExist = false;



    private int screenNum = 0;

    public NailmasterSheoEvent() {
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
                            CardCrawlGame.sound.playV(SoundEffects.EvGpSheo.getKey(), 1.4F);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.loadImage(IMG2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();




                        if (getUpgradableAttacks(AbstractDungeon.player.masterDeck).size() >=2) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[2]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[7], true); //this is the text shown if you can't upgrade anything.
                        }

                        if (getStartingStrikes(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[3]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[6], true); //this is the text shown if you can't upgrade anything.
                        }

                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            optionsExist = true;
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[4], RelicLibrary.getRelic(SheosBrushRelic.ID));
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }

                        if (!optionsExist){
                            this.imageEventText.setDialogOption(OPTIONS[0]);
                        }

                        screenNum = 1;
                        break;
                    case 1: // Encounter an elite enemy
                        screenNum = 2;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        break;
                }
                break;

            case END_SCREEN:
                switch (buttonPressed) {
                    case NAIL_BUTTON: //upgrade 2 random attacks
                        for (int i = 0; i < 2; i++)
                        {
                            AbstractCard c = getUpgradableAttacks(AbstractDungeon.player.masterDeck).getTopCard();
                            AbstractDungeon.effectsQueue.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                            c.upgrade();
                            AbstractDungeon.player.bottledCardUpgradeCheck(c);
                            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(((c)).makeStatEquivalentCopy()));
                        }
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                    case CREATE_BUTTON: // transform a starting strike

                        AbstractCard c = getStartingStrikes(AbstractDungeon.player.masterDeck).getTopCard();
                        AbstractDungeon.player.masterDeck.removeCard(c);
                        AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
                        AbstractCard transCard = AbstractDungeon.getTransformedCard();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                    case PAINTBRUSH_BUTTON: // gain the relic


                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new SheosBrushRelic());

                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                    case LEAVE_BUTTON: // leave


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


    private static CardGroup getHighCostCards(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if ((c.cost >= 3))  {
                ret.group.add(c);
            }
        }
        return ret;
    }

    private static CardGroup getStartingStrikes(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if ((c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)))  {
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }

    private static CardGroup getUpgradableAttacks(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if (c.canUpgrade() && (c.type == AbstractCard.CardType.ATTACK))  {
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }


}
