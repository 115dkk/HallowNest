package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static Hallownest.dungeon.EncounterIDs.ASPID_NEST;

public class KEAspidNestEvent extends AbstractEvent {
    public static final String ID = HallownestMod.makeID("KEAspidNestEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String INTRO_MSG = eventStrings.DESCRIPTIONS[0];
    private int screenNum = 0;
    private final static int INTRO = 0;
    private final static int PRE_COMBAT = 1;
    private final static int END = 2;

    public KEAspidNestEvent() {
        this.screenNum = INTRO;
        //this.initializeImage("images/events/sphereClosed.png", 1120.0F * Settings.scale, AbstractDungeon.floorY - 50.0F * Settings.scale);
        this.body = INTRO_MSG;
        this.roomEventText.addDialogOption(OPTIONS[0]);
        this.roomEventText.addDialogOption(OPTIONS[1]);
        this.hasDialog = true;
        this.hasFocus = true;
        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(ASPID_NEST);
    }

    public void update() {
        super.update();
        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screenNum) {
            case INTRO:
                switch(buttonPressed) {
                    case 0:
                        this.screenNum = PRE_COMBAT;
                        this.roomEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                        this.roomEventText.clearRemainingOptions();
                        //logMetric("Mysterious Sphere", "Fight");
                        return;
                    case 1:
                        this.screenNum = END;
                        this.roomEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.roomEventText.updateDialogOption(0, OPTIONS[2]);
                        this.roomEventText.clearRemainingOptions();
                        //logMetricIgnored("Mysterious Sphere");
                        return;
                    default:
                        return;
                }
            case PRE_COMBAT:
                if (Settings.isDailyRun) {
                    AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(50));
                } else {
                    AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(55, 65));
                }

                AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.RARE));
                if (this.img != null) {
                    this.img.dispose();
                    this.img = null;
                }

                //this.img = ImageMaster.loadImage("images/events/sphereOpen.png");
                this.enterCombat();
                AbstractDungeon.lastCombatMetricKey = ASPID_NEST;
                break;
            case END:
                this.openMap();
        }

    }
}
