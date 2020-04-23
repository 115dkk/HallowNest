package Hallownest.events.CityofTearsEvents;

import Hallownest.HallownestMod;
import Hallownest.relics.DreamNailRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static Hallownest.HallownestMod.makeEventPath;

public class EmilitiaEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("EmilitiaEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CoTEmilitiaA.png");

//
    private AbstractRelic C1;
    private AbstractRelic C2;
    private AbstractRelic C3;

    private AbstractRelic U1;
    private AbstractRelic U2;
    private AbstractRelic U3;

    private AbstractCard Curse1;
    private AbstractCard Curse2;
    private AbstractCard Curse3;

    private int Price1;
    private int Price2;
    private int Price3;
    private final static int OPTION_SCREEN = 0;
    private final static int COMMON_SCREEN = 1;
    private final static int UNCOMMON_SCREEN = 2;



    private final static int COMMON = 0;
    private final static int UNCOMMON_CURSE = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int SOLD1 = 0;
    private final static int SOLD2 = 1;
    private final static int SOLD3 = 2;
    private final static int FUCKED_OFF = 3;


    private int currRelics;
    private int charmsNeeded = 4;




    private int screenNum = 0;

    public EmilitiaEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.imageEventText.setDialogOption(OPTIONS[1]); // button 2
        this.imageEventText.setDialogOption(OPTIONS[2]); // button 2
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
                    case COMMON:


                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();


                        ArrayList<AbstractRelic> relics = new ArrayList();
                        relics.addAll(AbstractDungeon.player.relics);
                        ArrayList<AbstractRelic> NewCommonRelics = new ArrayList();

                        for (String item : AbstractDungeon.commonRelicPool) {
                            if (!relics.contains(RelicLibrary.getRelic(item))) {
                                NewCommonRelics.add(RelicLibrary.getRelic(item));
                            }
                        }

                        Collections.shuffle(NewCommonRelics, new Random(AbstractDungeon.miscRng.randomLong()));

                        if (NewCommonRelics.size() > 1) {
                            this.C1 = (AbstractRelic) NewCommonRelics.get(0);
                        }
                        if (NewCommonRelics.size() > 2) {
                            this.C2 = (AbstractRelic) NewCommonRelics.get(1);
                        }
                        if (NewCommonRelics.size() > 3) {
                            this.C3 = (AbstractRelic) NewCommonRelics.get(2);
                        }

                        this.imageEventText.setDialogOption(OPTIONS[3] + FontHelper.colorString(this.C1.name, "g"),RelicLibrary.getRelic(C1.relicId));

                        this.imageEventText.setDialogOption(OPTIONS[3] + FontHelper.colorString(this.C2.name, "g"),RelicLibrary.getRelic(C2.relicId));

                        this.imageEventText.setDialogOption(OPTIONS[3] + FontHelper.colorString(this.C3.name, "g"),RelicLibrary.getRelic(C3.relicId));

                        this.imageEventText.setDialogOption(OPTIONS[0]);




                    //case SOLD1: //bought an account but didn't deposit more gold
                        screenNum = COMMON_SCREEN;
                        break;
                    case UNCOMMON_CURSE:


                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();


                        ArrayList<AbstractRelic> Playerrelics = new ArrayList();
                        Playerrelics.addAll(AbstractDungeon.player.relics);
                        ArrayList<AbstractRelic> NewUncommonRelics = new ArrayList();

                        for (String item : AbstractDungeon.uncommonRelicPool) {
                            if (!Playerrelics.contains(RelicLibrary.getRelic(item))) {
                                NewUncommonRelics.add(RelicLibrary.getRelic(item));
                            }
                        }

                        Collections.shuffle(NewUncommonRelics, new Random(AbstractDungeon.miscRng.randomLong()));

                        if (NewUncommonRelics.size() > 1) {
                            this.U1 = (AbstractRelic) NewUncommonRelics.get(0);
                        }
                        if (NewUncommonRelics.size() > 2) {
                            this.U2 = (AbstractRelic) NewUncommonRelics.get(1);
                        }
                        if (NewUncommonRelics.size() > 3) {
                            this.U3 = (AbstractRelic) NewUncommonRelics.get(2);
                        }

                        ArrayList<AbstractCard> RandomCurses = new ArrayList<>();
                        RandomCurses.add(new Clumsy());
                        RandomCurses.add(new Decay());
                        RandomCurses.add(new Doubt());
                        RandomCurses.add(new Injury());
                        RandomCurses.add(new Pain());
                        RandomCurses.add(new Shame());
                        RandomCurses.add(new Writhe());
                        RandomCurses.add(new Regret());


                        Collections.shuffle(RandomCurses, new Random(AbstractDungeon.miscRng.randomLong()));

                        this.Curse1 = (AbstractCard) RandomCurses.get(0);
                        this.Curse2 = (AbstractCard) RandomCurses.get(1);
                        this.Curse3 = (AbstractCard) RandomCurses.get(2);

                        this.imageEventText.setDialogOption(OPTIONS[3] + FontHelper.colorString(this.U1.name, "g") + OPTIONS[4] + FontHelper.colorString(this.Curse1.name, "r"),CardLibrary.getCard(Curse1.cardID), RelicLibrary.getRelic(U1.relicId));

                        this.imageEventText.setDialogOption(OPTIONS[3] + FontHelper.colorString(this.U2.name, "g") + OPTIONS[4] + FontHelper.colorString(this.Curse2.name, "r"),CardLibrary.getCard(Curse2.cardID), RelicLibrary.getRelic(U2.relicId));

                        this.imageEventText.setDialogOption(OPTIONS[3] + FontHelper.colorString(this.U3.name, "g") + OPTIONS[4] + FontHelper.colorString(this.Curse3.name, "r"),CardLibrary.getCard(Curse3.cardID), RelicLibrary.getRelic(U3.relicId));

                        this.imageEventText.setDialogOption(OPTIONS[0]);




                        //case SOLD1: //bought an account but didn't deposit more gold
                        screenNum = UNCOMMON_SCREEN;
                        break;
                }
                break;
            case COMMON_SCREEN:
                switch (buttonPressed){
                    case SOLD1: // Encounter an elite enemy
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, C1);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 4;
                        break;
                    case SOLD2: // Encounter an elite enemy
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, C2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 4;
                        break;
                    case SOLD3: // Encounter an elite enemy
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, C3);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 4;
                        break;
                    case FUCKED_OFF: //

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);


                        screenNum = 4;
                        break;
                }
                break;
            case UNCOMMON_SCREEN:
                switch (buttonPressed) {
                    case SOLD1: // Encounter an elite enemy
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, U1);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(Curse1, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 4;
                        break;
                    case SOLD2: // Encounter an elite enemy
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, U2);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(Curse2, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 4;
                        break;
                    case SOLD3: // Encounter an elite enemy
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, U3);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(Curse3, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));


                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);
                        screenNum = 4;
                        break;
                    case FUCKED_OFF: //

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[0]);


                        screenNum = 4;
                        break;
                }
                break;
            case 4:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }


}
