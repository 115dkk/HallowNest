package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.SalubrasBlessingRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.EffectHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.OddMushroom;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class IseldasKeepingEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("IseldasKeepingEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("IseldaEventA.png");

//



    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;

    //choice ints for easier reading
    //Screen Button Options
    private final static int TAKE_COIN = 0;
    private final static int UPGRADE_SKILL = 1;
    private final static int LEAVE = 2;

    private int currFloor;
    private int coinPerFloor = 10;



    private int screenNum = 0;

    public IseldasKeepingEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

       this.currFloor = AbstractDungeon.floorNum;
        // Options without Dreamnailing
        //random relic button

        this.imageEventText.setDialogOption(OPTIONS[1] + (currFloor * coinPerFloor) + OPTIONS[2]);
        // mushroom relic button
        if (getUpgradableSkills(AbstractDungeon.player.masterDeck).size() >= 1) {
            this.imageEventText.setDialogOption(OPTIONS[3]); //Pay for 50% missing HP restored
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4], true);
        }
        //leave button
        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2



    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpIselda.getKey(), 1.6F);
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case TAKE_COIN: //bought an account but didn't deposit more gold
                        int newcoin = (currFloor * coinPerFloor);
                        EffectHelper.gainGold(AbstractDungeon.player, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, newcoin);
                        AbstractDungeon.player.gainGold(newcoin);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case UPGRADE_SKILL: // Encounter an elite enemy
                        AbstractCard c = getUpgradableSkills(AbstractDungeon.player.masterDeck).getTopCard();
                        AbstractDungeon.effectsQueue.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        c.upgrade();
                        AbstractDungeon.player.bottledCardUpgradeCheck(c);
                        AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(((c)).makeStatEquivalentCopy()));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case LEAVE: // Encounter an elite enemy
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2


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

    private static CardGroup getUpgradableSkills(CardGroup group)
    {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : group.group) {
            if (c.canUpgrade() && (c.type == AbstractCard.CardType.SKILL))  {
                ret.group.add(c);
            }
        }
        ret.shuffle();
        return ret;
    }


}
