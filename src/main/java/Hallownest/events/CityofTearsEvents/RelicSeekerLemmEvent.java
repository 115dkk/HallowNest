package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.DreamNailRelic;
import Hallownest.relics.SalubrasBlessingRelic;
import Hallownest.relics.StagBellRelic1;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.OddMushroom;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static Hallownest.HallownestMod.makeEventPath;

public class RelicSeekerLemmEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("RelicSeekerLemmEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTLemmA.png");

//
    private AbstractRelic buy1;
    private AbstractRelic buy2;
    private AbstractRelic buy3;
    private int Price1;
    private int Price2;
    private int Price3;
    private final static int OPTION_SCREEN = 0;
    private final static int LEAVE_SCREEN = 1;
    private final static int DREAM_NAIL_SCREEN = 2;


    //choice ints for easier reading
    //Screen Button Options
    private final static int SOLD1 = 0;
    private final static int SOLD2 = 1;
    private final static int SOLD3 = 2;
    private final static int FUCKED_OFF = 3;


    private int currRelics;
    private int charmsNeeded = 4;




    private int screenNum = 0;

    public RelicSeekerLemmEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        ArrayList<AbstractRelic> relics = new ArrayList();
        relics.addAll(AbstractDungeon.player.relics);
        Collections.shuffle(relics, new Random(AbstractDungeon.miscRng.randomLong()));
        if (relics.size() > 1) {
            this.buy1 = (AbstractRelic) relics.get(0);
        }
        if (relics.size() > 2) {
            this.buy2 = (AbstractRelic) relics.get(1);
        }
        if (relics.size() > 3) {
            this.buy3 = (AbstractRelic) relics.get(2);
        }

        this.currRelics = relics.size();

        Price1 = (costCheck(buy1));
        Price2 = (costCheck(buy2));
        Price3 = (costCheck(buy3));


        if (currRelics > 1){
            this.imageEventText.setDialogOption(OPTIONS[1] + buy1.name + OPTIONS[2] + Price1 + OPTIONS[3]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4], true);
        }
        if (currRelics > 2){
            this.imageEventText.setDialogOption(OPTIONS[1] + buy2.name + OPTIONS[2] + Price2 + OPTIONS[3]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4], true);
        }

        if (currRelics > 3){
            this.imageEventText.setDialogOption(OPTIONS[1] + buy3.name + OPTIONS[2] + Price3 + OPTIONS[3]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4], true);
        }
        //leave button
        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            this.imageEventText.setDialogOption(OPTIONS[5]); // button 2
        }



    }

    private int costCheck(AbstractRelic checkthis){
        int addPrice1 = AbstractDungeon.miscRng.random(1,25);
        switch(checkthis.tier) {
            case STARTER:
                return (400 + addPrice1);
            case COMMON:
                return (250 + addPrice1);
            case UNCOMMON:
                return (350 + addPrice1);
            case RARE:
                return (450 + addPrice1);
            case SHOP:
                return (200 + addPrice1);
            case SPECIAL:
                return (450 + addPrice1);
            case BOSS:
                return (550 + addPrice1);
            case DEPRECATED:
                return -1;
            default:
                return (300 + addPrice1);
        }
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            //CardCrawlGame.sound.playV(SoundEffects.EvGpSalubraGreet.getKey(), 1.5F);
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case SOLD1: //bought an account but didn't deposit more gold
                        AbstractDungeon.player.loseRelic(buy1.relicId);

                        AbstractDungeon.player.gainGold(Price1);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.Price1));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case SOLD2: // Encounter an elite enemy
                        AbstractDungeon.player.loseRelic(buy2.relicId);

                        AbstractDungeon.player.gainGold(Price2);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.Price2));


                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case SOLD3: // Encounter an elite enemy
                        AbstractDungeon.player.loseRelic(buy3.relicId);

                        AbstractDungeon.player.gainGold(Price3);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.Price3));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case FUCKED_OFF: //

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2


                        screenNum = LEAVE_SCREEN;
                        break;
                    case 4: // Dreamnailed

                        Price1 += AbstractDungeon.miscRng.random(1,25);
                        Price2 += AbstractDungeon.miscRng.random(1,25);
                        Price3 += AbstractDungeon.miscRng.random(1,25);
                        this.imageEventText.clearAllDialogs();

                        if (currRelics > 1){
                            this.imageEventText.setDialogOption(OPTIONS[1] + buy1.name + OPTIONS[2] + Price1 + OPTIONS[3]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[4], true);
                        }
                        if (currRelics > 2){
                            this.imageEventText.setDialogOption(OPTIONS[1] + buy2.name + OPTIONS[2] + Price2 + OPTIONS[3]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[4], true);
                        }

                        if (currRelics > 3){
                            this.imageEventText.setDialogOption(OPTIONS[1] + buy3.name + OPTIONS[2] + Price3 + OPTIONS[3]);
                        } else {
                            this.imageEventText.setDialogOption(OPTIONS[4], true);
                        }
                        //leave button
                        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2

                        screenNum = DREAM_NAIL_SCREEN;
                        break;
                }
                break;
            case LEAVE_SCREEN:
                this.openMap();
                break;
            case DREAM_NAIL_SCREEN:
                switch (buttonPressed) {
                    case SOLD1: //bought an account but didn't deposit more gold
                        AbstractDungeon.player.loseRelic(buy1.relicId);

                        AbstractDungeon.player.gainGold(Price1);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.Price1));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case SOLD2: // Encounter an elite enemy
                        AbstractDungeon.player.loseRelic(buy2.relicId);

                        AbstractDungeon.player.gainGold(Price2);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.Price2));


                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case SOLD3: // Encounter an elite enemy
                        AbstractDungeon.player.loseRelic(buy3.relicId);

                        AbstractDungeon.player.gainGold(Price3);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.Price3));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = LEAVE_SCREEN;
                        break;
                    case FUCKED_OFF: // Encounter an elite enemy

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.setDialogOption(OPTIONS[0]); // button 2


                        screenNum = LEAVE_SCREEN;
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }


}
