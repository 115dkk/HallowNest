package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.attackMasterCycloneSlash;
import Hallownest.cards.attackMasterDashSlash;
import Hallownest.relics.HeavyBlowRelic;
import Hallownest.relics.NailmastersGloryRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class NailmasterOroEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("NailmasterOroEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("OroEventA.png");
    public static final String IMG2 = makeEventPath("OroEventB.png");
//



    private final static int OPTION_SCREEN = 0;
    private final static int END_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int NAIL_ART_BUTTON = 0;
    private final static int SAGE_RELIC_BUTTON = 1;
    private final static int LEAVE_BUTTON = 2;

    private static int goldcost = 80;



    private boolean optionsExist = false;



    private int screenNum = 0;

    public NailmasterOroEvent() {
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




                        if (AbstractDungeon.player.gold >= goldcost) {
                            this.imageEventText.setDialogOption(OPTIONS[2] + goldcost + OPTIONS[3] , CardLibrary.getCopy(attackMasterDashSlash.ID));
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[4] + goldcost + OPTIONS[5], true);
                        }
                        if (getHighCostCards(AbstractDungeon.player.masterDeck).size() >0) {
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[6], RelicLibrary.getRelic(HeavyBlowRelic.ID));
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[7], true); //this is the text shown if you can't upgrade anything.
                        }

                            this.imageEventText.setDialogOption(OPTIONS[0]);

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
                    case NAIL_ART_BUTTON: //upgrade 2 random attacks
                        AbstractDungeon.player.loseGold(goldcost);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new attackMasterDashSlash(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                    case SAGE_RELIC_BUTTON: // gain the relic

                        if (!AbstractDungeon.player.hasRelic(HeavyBlowRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new HeavyBlowRelic());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }

                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                    case LEAVE_BUTTON: // leave

                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
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
