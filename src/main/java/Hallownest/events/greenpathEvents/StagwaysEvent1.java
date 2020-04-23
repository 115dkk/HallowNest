package Hallownest.events.greenpathEvents;


import Hallownest.HallownestMod;
import Hallownest.relics.StagBellRelic1;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Hallownest.HallownestMod.makeEventPath;

public class StagwaysEvent1 extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("StagwaysEvent1");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("GPStagwaysA.png");
    public static final String IMG2 = makeEventPath("GPStagwaysB.png");
    public static final String IMG3 = makeEventPath("GPStagwaysC.png");
    private static int goldcost = 35;
    public int stations = 0;

    private int screenNum = 0; // The initial screen we will see when encountering the event - screen 0;


    public StagwaysEvent1() {
        super(NAME, DESCRIPTIONS[0], IMG);


        if (AbstractDungeon.player.gold >= goldcost) {
            this.imageEventText.setDialogOption(OPTIONS[1] + goldcost + OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5] + goldcost + OPTIONS[6], true);
        }
        imageEventText.setDialogOption(OPTIONS[0]); // Leave


    }

    public void onEnterRoom()
    {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpStagRumble.getKey(), 1.4F);
        }
    }

    @Override
    protected void buttonEffect(int i) { // This is the event:
        switch (screenNum) {
            case 0: // While you are on screen number 0 (The starting screen)
                switch (i) { // Set up the stuff if you click these choices
                    case 0: // Proceed this should set up the buttons you will see at dirtmouth.

                        AbstractDungeon.player.loseGold(goldcost);
                        this.stations += 1;

                        this.imageEventText.clearAllDialogs();
                        if (AbstractDungeon.player.gold >= goldcost) {
                            this.imageEventText.setDialogOption(OPTIONS[1] + goldcost + OPTIONS[2]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[5] + goldcost +OPTIONS[6], true);
                        }
                        this.imageEventText.setDialogOption( OPTIONS[3]);

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 2;
                        this.imageEventText.loadImage(IMG2);

                        // Screen set the screen number to 1. Once we exit the switch (i) statement,
                        // we'll still continue the switch (screenNum) statement. It'll find screen 1 and do it's actions
                        // (in our case, that's the final screen, but you can chain as many as you want like that)



                        break; // Onto screen 1 we go.
                    case 1: // If you press button the second button (Button at index 1), in this case: Deinal
                        this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;

                        // Same as before. A note here is that you can also do
                        // imageEventText.clearAllDialogs();
                        // imageEventText.setDialogOption(OPTIONS[1]);
                        // imageEventText.setDialogOption(OPTIONS[4]);
                        // (etc.)
                        // And that would also just set them into slot 0, 1, 2... in order, just like what we do in the very beginning

                        break; // Onto screen 1 we go.
                   /* case 2: // If you press button the third button (Button at index 2), in this case: Acceptance
                        CardCrawlGame.sound.playV(SoundEffects.DreamNail.getKey(), 1.4F);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 5;
                        break; */
                }
                break;
            case 1: // Leave Screen
                this.openMap();
                break;
            case 2: // Dirtmouth, time to set up the Queen garden stuff or up the players max hp
                switch (i) {
                    case 0: // On to Queen Garden

                        //CardCrawlGame.sound.playV(SoundEffects.Stag1.getKey(), 2.0F);
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpStag1.getKey(), 1.4F);
                        }
                        AbstractDungeon.player.loseGold(goldcost);
                        this.stations += 1;

                        this.imageEventText.clearAllDialogs();

                        this.imageEventText.setDialogOption(OPTIONS[7]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        screenNum = 3;
                        this.imageEventText.loadImage(IMG3);


                        break;
                    case 1: // leave from dirtmouth, gain max HP
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        AbstractDungeon.player.increaseMaxHp(5, true);
                        screenNum = 1;

                        break; // Onto screen 1 we go.
                }
                break;
            case 3: // Station 3
                switch (i) {
                    case 0: // Proceed
                        //CardCrawlGame.sound.playV(SoundEffects.Stag2.getKey(), 2.0F);
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpStag3.getKey(), 1.4F);
                        }
                        this.stations += 1;

                        //ADD EVENT TWO TO THE POOL
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[4]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);

                        screenNum = 4;


                        break; // Onto screen 1 we go.
                }
                break;
            case 4: // Station 4
                switch (i) {
                    case 0: // Proceed
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpStag2.getKey(), 1.4F);
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new StagBellRelic1());
                        //CardCrawlGame.sound.playV(SoundEffects.Stag3.getKey(), 2.0F);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break; // Onto screen 1 we go.
                }
                break;
        }
    }


}
