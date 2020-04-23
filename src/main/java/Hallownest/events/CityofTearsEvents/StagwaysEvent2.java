package Hallownest.events.CityofTearsEvents;


import Hallownest.HallownestMod;
import Hallownest.relics.StagBellRelic1;
import Hallownest.relics.StagBellStandRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;

import static Hallownest.HallownestMod.makeEventPath;

public class StagwaysEvent2 extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("StagwaysEvent2");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTStagwaysA.png");
    public static final String IMG2 = makeEventPath("CoTStagwaysB.png");
    public static final String IMG3 = makeEventPath("CoTStagwaysC.png");
    private static int goldcost = 45;
    private int MaxHPgain = 6 ;
    public int stations = 0;
    private boolean IsUpgrading = false;

    private AbstractRelic RelicToRecieve1;
    private AbstractRelic RelicToRecieve2;

    private int screenNum = 0; // The initial screen we will see when encountering the event - screen 0;


    public StagwaysEvent2() {
        super(NAME, DESCRIPTIONS[0], IMG);
        // declare variables for relics, and give players circlets if they alredy own the relci they will acquire

        RelicToRecieve1 = new StagBellRelic1();
        if (AbstractDungeon.player.hasRelic(StagBellStandRelic.ID)){
            RelicToRecieve2 = new Circlet();
        } else {
            RelicToRecieve2 = new StagBellStandRelic();
        }





        if (AbstractDungeon.player.gold >= goldcost) {
            this.imageEventText.setDialogOption(OPTIONS[1] + goldcost + OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[6] + goldcost + OPTIONS[7], true);
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
                            this.imageEventText.setDialogOption(OPTIONS[6] + goldcost +OPTIONS[7], true);
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
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
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
            case 2: // Queens Station
                switch (i) {
                    case 0: // On to Queen Garden

                        //CardCrawlGame.sound.playV(SoundEffects.Stag1.getKey(), 2.0F);
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpStag1.getKey(), 1.4F);
                        }
                        AbstractDungeon.player.loseGold(goldcost);
                        this.stations += 1;

                        this.imageEventText.clearAllDialogs();

                        this.imageEventText.setDialogOption(OPTIONS[8]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        screenNum = 3;
                        this.imageEventText.loadImage(IMG3);


                        break;
                    case 1: // leave from dirtmouth, gain max HP
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        AbstractDungeon.player.increaseMaxHp(MaxHPgain, true);
                        screenNum = 1;

                        break; // Onto screen 1 we go.
                }
                break;
            case 3: //  Kings Station 3
                switch (i) {
                    case 0: // Proceed
                        //CardCrawlGame.sound.playV(SoundEffects.Stag2.getKey(), 2.0F);
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpStag3.getKey(), 1.4F);
                        }
                        this.stations += 1;

                        //ADD EVENT TWO TO THE POOL
                        this.imageEventText.clearAllDialogs();
                        //might want to add on a (or stags egg relic for endless?)
                        if (AbstractDungeon.player.hasRelic(StagBellRelic1.ID)){
                            this.imageEventText.setDialogOption(OPTIONS[5], RelicLibrary.getRelic(StagBellStandRelic.ID));
                            IsUpgrading = true;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);

                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[4], RelicLibrary.getRelic(StagBellRelic1.ID));
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);

                        }
                        this.imageEventText.setDialogOption(OPTIONS[0]);

                        screenNum = 4;


                        break; // Onto screen 1 we go.
                }
                break;
            case 4: // Rewarding Text
                switch (i) {
                    case 0: // Proceed
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpStag2.getKey(), 1.4F);
                        }
                        if (IsUpgrading){
                            AbstractDungeon.player.loseRelic(StagBellRelic1.ID);
                            if (!AbstractDungeon.player.hasRelic(StagBellStandRelic.ID)){
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new StagBellStandRelic());
                            } else {
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                            }
                        } else {
                            if (!AbstractDungeon.player.hasRelic(StagBellRelic1.ID)){
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new StagBellRelic1());
                            } else {
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                            }
                        }
                        //CardCrawlGame.sound.playV(SoundEffects.Stag3.getKey(), 2.0F);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break; // Onto screen 1 we go.
                    case 1: // leave from dirtmouth, gain max HP
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        AbstractDungeon.player.increaseMaxHp(MaxHPgain, true);
                        screenNum = 1;

                        break; // Onto screen 1 we go.
                }
                break;
        }
    }


}
