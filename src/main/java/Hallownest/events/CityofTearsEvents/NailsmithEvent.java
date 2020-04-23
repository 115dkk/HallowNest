package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.attackPureNailEdge;
import Hallownest.relics.BankAccountRelic;
import Hallownest.relics.SheosBrushRelic;
import Hallownest.relics.TeacherSealRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.WarpedTongs;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class NailsmithEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("NailsmithEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTNailsmithB.png");
    public static final String IMG2 = makeEventPath("CoTNailsmithC.png");



    private int GoldLost;
    private int GoldToGain;
    private int GoldonHit;
    private int HealVal = 14;


    //Screens

    private final static int OUTCOME_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    private final static int FOLLOW_SCREEN = 2;
    private final static int DECISION_SCREEN = 3;


    //choice ints for easier reading
    //Screen Button Options
    private final static int FORGE_BUTTON = 0;
    private final static int UPGRADE_BUTTON = 1;
    private final static int STEAL_BUTTON = 2;
    private final static int LEAVE_BUTTON = 3;

    private boolean cardpicked;
    private boolean GoldLeft = false;

    private AbstractCard cardtolose;
    private AbstractCard cardtoGain;






    private int screenNum = 0; // the first screen and options by default
    //so for this, the screen with the 3 options. Transorm, Upgrade, Remove.





    public NailsmithEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);



        if ((getWorthyCards(AbstractDungeon.player.masterDeck).size()) > 0){
            cardtolose = getWorthyCards(AbstractDungeon.player.masterDeck).getTopCard();
            cardtoGain = new attackPureNailEdge();
            cardtoGain.upgrade();

            this.imageEventText.setDialogOption((OPTIONS[1] +  FontHelper.colorString(this.cardtolose.name, "r") + OPTIONS[2]), cardtoGain);

        } else {
            this.imageEventText.setDialogOption(OPTIONS[3],true);
        }
        if (AbstractDungeon.player.masterDeck.hasUpgradableCards()) {
            this.imageEventText.setDialogOption(OPTIONS[4]);

        } else {
            this.imageEventText.setDialogOption(OPTIONS[10] ,true);
        }
        this.imageEventText.setDialogOption(OPTIONS[5], CardLibrary.getCopy(Shame.ID), RelicLibrary.getRelic(WarpedTongs.ID));
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OUTCOME_SCREEN:
                switch (buttonPressed) {
                    case FORGE_BUTTON:


                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EventNailsmithTalk.getKey(), 1.4F);
                        }
                        AbstractDungeon.player.masterDeck.removeCard(cardtolose);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(cardtoGain, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[7]);
                        this.screenNum = FOLLOW_SCREEN;
                        break;
                    case UPGRADE_BUTTON:
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EventNailsmithTalk.getKey(), 1.4F);
                        }
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getUpgradableCards()), 1, OPTIONS[6], true, false, false, false);
                        this.cardpicked = true;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.screenNum = LEAVE_SCREEN;
                        break;
                    case STEAL_BUTTON:
                        if (!AbstractDungeon.player.hasRelic(WarpedTongs.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new WarpedTongs());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Shame(),Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.screenNum = LEAVE_SCREEN;
                        break;
                    case LEAVE_BUTTON:
                        this.openMap();
                        break;
                }
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;

            case FOLLOW_SCREEN:
                this.imageEventText.loadImage(IMG2);
                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[9]);
                this.imageEventText.setDialogOption(OPTIONS[8]);
                this.screenNum = DECISION_SCREEN;
                break;
            case DECISION_SCREEN:
                switch (buttonPressed) {
                    case 0: // Spare
                        if (AbstractDungeon.player.hasRelic(SheosBrushRelic.ID)){
                            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                        } else {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        }
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case 1: // Kill
                        CardCrawlGame.sound.play("ATTACK_FAST");
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }


    public void update() {
        super.update();
        if (this.cardpicked && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractDungeon.effectsQueue.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractCard c = ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            c.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(c);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(((c)).makeStatEquivalentCopy()));
            this.cardpicked = false;
            }
        }


    private static CardGroup getWorthyCards(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if ((c.cost >= 3) && (c.type == AbstractCard.CardType.ATTACK) && c.upgraded)  {
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }





}
