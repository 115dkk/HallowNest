package Hallownest.events.KingdomsEdgeEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.*;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class CorniferEvent3 extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("CorniferEvent3");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("KECorniferA.png");
    public static final String IMG2 = makeEventPath("KECorniferB.png");
//



    private final static int OPTION_SCREEN = 0;
    private final static int END_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options

    private int mapcost = 85;



    private int screenNum = 0;

    public CorniferEvent3() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]);


    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpCorniferHum.getKey(), 1.4F);
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:

                switch (buttonPressed) {
                    case 0: //Encounter a normal enemy
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpCornifer.getKey(), 1.3F);
                        }
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.loadImage(IMG2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();

                        if (AbstractDungeon.player.gold >= mapcost) {
                            // now we set button 1 as the upgrade option, but only if there are upgradable cars in the deck.
                            this.imageEventText.setDialogOption(OPTIONS[2] + mapcost + OPTIONS[3], CardLibrary.getCopy(skillKingdomsEdgeMap.ID));
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[4] + mapcost + OPTIONS[5], true); //this is the text shown if you can't upgrade anything.
                        }
                        this.imageEventText.setDialogOption(OPTIONS[0]);




                        screenNum = END_SCREEN;
                        break;
                    case 1: // Encounter an elite enemy
                        screenNum = 2;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        break;
                }
                break;

            case END_SCREEN:
                switch (buttonPressed) {
                    case 0: //upgrade 2 random attacks


                        AbstractDungeon.player.loseGold(mapcost);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new skillKingdomsEdgeMap(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 2;
                        break;
                    case 1: // transform a starting strike

                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
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


}
