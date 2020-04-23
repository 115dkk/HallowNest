package Hallownest.events.greenpathEvents;


import Hallownest.HallownestMod;
import Hallownest.cards.status.curseDreamersLament;
import Hallownest.relics.DreamNailRelic;
import Hallownest.relics.TeacherSealRelic;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Hallownest.HallownestMod.makeEventPath;

public class DreamersSpecialEvent extends AbstractImageEvent {


    public static final String ID = HallownestMod.makeID("DreamersSpecialEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("3DreamersA.png");
    public static final String IMG2 = makeEventPath("3DreamersB.png");
    public static final String IMG3 = makeEventPath("3DreamersC.png");
    public static final String IMG4 = makeEventPath("3DreamersD.png");
    public static final String IMG5 = makeEventPath("3DreamersE.png");
    private static int damagetotake = 10;
    public int stations = 0;

    private int screenNum = 0; // The initial screen we will see when encountering the event - screen 0;


    public DreamersSpecialEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        imageEventText.setDialogOption(OPTIONS[1]); //Examine the seal


    }


    @Override
    protected void buttonEffect(int i) { // This is the event:
        switch (screenNum) {
            case 0: //After Clicking Examine the Seal
                this.imageEventText.clearAllDialogs();

                this.imageEventText.setDialogOption(OPTIONS[0]);
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                screenNum = 2;
                //this.imageEventText.loadImage(IMG3);


                break;
            case 1: // Leave Screen
                this.openMap();
                break;
            case 2: // 2nd page of reading, now settuing up the dreamers stuff

                if (Settings.AMBIANCE_ON) {
                    CardCrawlGame.sound.playV(SoundEffects.EvGpDreamerAmbience.getKey(), 1.4F);
                }


                this.imageEventText.clearAllDialogs();

                this.imageEventText.setDialogOption(OPTIONS[0]);
                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                screenNum = 3;
                this.imageEventText.loadImage(IMG2);







                break;
            case 3: // Trapped in dream now
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.MED, false);

                this.imageEventText.clearAllDialogs();

                this.imageEventText.setDialogOption(OPTIONS[4] + damagetotake + OPTIONS[5] );
                this.imageEventText.setDialogOption(OPTIONS[2]);
                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                screenNum = 4;
                this.imageEventText.loadImage(IMG3);
                break;

            case 4: //Awaiting 1
                switch (i) {
                    case 0: // Proceed

                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.damagetotake));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        this.imageEventText.loadImage(IMG);

                        screenNum = 1;
                        break; // Onto screen 1 we go.
                    case 1: // Proceed
                        this.imageEventText.clearAllDialogs();

                        this.imageEventText.setDialogOption(OPTIONS[4] + damagetotake + OPTIONS[5] );
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        screenNum = 5;
                        break; // Onto screen 1 we go.
                }


               break;
            case 5: // awaiting 2 setting up the moth or leave
                switch (i) {
                    case 0: // Proceed
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new curseDreamersLament(),Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.damagetotake));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        this.imageEventText.loadImage(IMG);

                        screenNum = 1;
                        break; // Onto screen 1 we go.
                    case 1: // Proceed


                        this.imageEventText.clearAllDialogs();

                        this.imageEventText.setDialogOption(OPTIONS[4] + damagetotake + OPTIONS[5] );
                        this.imageEventText.setDialogOption(OPTIONS[6]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                        this.imageEventText.loadImage(IMG4);
                        screenNum = 6;
                        break; // Onto screen 1 we go.
                    }
                break;
            case 6: //Moth Guiding
                switch (i) {
                    case 0: // Proceed
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.damagetotake));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        this.imageEventText.loadImage(IMG);

                        screenNum = 1;
                        break; // Onto screen 1 we go.
                    case 1: // Proceed
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpDreamerSeer.getKey(), 1.4F);
                        }

                        this.imageEventText.clearAllDialogs();

                        this.imageEventText.setDialogOption(OPTIONS[8] );
                        this.imageEventText.setDialogOption(OPTIONS[7]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        this.imageEventText.loadImage(IMG5);
                        screenNum = 7;
                        break; // Onto screen 1 we go.
                }
                break;
            case 7: //Dream Nail


                switch (i) {
                    case 0: // Proceed
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.damagetotake));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        this.imageEventText.loadImage(IMG);

                        screenNum = 1;
                        break; // Onto screen 1 we go.
                    case 1: // Proceed
                        if (Settings.AMBIANCE_ON) {
                            CardCrawlGame.sound.playV(SoundEffects.EvGpDreamerEnter.getKey(), 1.4F);
                        }
                        this.imageEventText.clearAllDialogs();

                        if (!AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new DreamNailRelic());
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Circlet());
                        }

                        this.imageEventText.setDialogOption(OPTIONS[9] );
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.imageEventText.loadImage(IMG);
                        screenNum = 1;
                        break; // Onto screen 1 we go.
                }

                break;
            case 8: //Leaving
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[0]);
                screenNum = 1;
                //this.imageEventText.loadImage(IMG3);

                break;
        }
    }


}
