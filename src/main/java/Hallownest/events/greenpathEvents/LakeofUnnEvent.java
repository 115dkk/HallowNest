package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.cards.*;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import javax.smartcardio.Card;

import java.util.ArrayList;

import static Hallownest.HallownestMod.makeEventPath;

public class LakeofUnnEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("LakeofUnnEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("UnnEventA.png");
//



    private final static int OPTION_SCREEN = 0;
    private final static int END_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int NAIL_BUTTON = 0;
    private final static int CREATE_BUTTON = 1;
    private final static int PAINTBRUSH_BUTTON = 2;
    private final static int LEAVE_BUTTON = 3;


    private boolean optionsExist = false;
    private int mapcost = 75;



    private int screenNum = 0;

    public LakeofUnnEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[0], CardLibrary.getCopy(skillSlugHide.ID));
        this.imageEventText.setDialogOption(OPTIONS[1], CardLibrary.getCopy(skillLeafShield.ID));
        this.imageEventText.setDialogOption(OPTIONS[2]);

    }

    public void onEnterRoom() {

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPTION_SCREEN:

                switch (buttonPressed) {
                    case 0: //Slugshides
                        if (Settings.AMBIANCE_ON) {
                            //CardCrawlGame.sound.playV(SoundEffects.EvGpCornifer.getKey(), 1.4F);
                        }

                        replaceAttacks(new skillSlugHide());
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]);




                        screenNum = END_SCREEN;
                        break;
                    case 1: //LeafShields a normal enemy

                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        replaceAttacks(new skillLeafShield());
                        //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]);





                        screenNum = END_SCREEN;
                        break;
                    case 2: // Encounter an elite enemy
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]);


                        screenNum = END_SCREEN;
                        break;
                }
                break;

            case END_SCREEN:
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }

    private void replaceAttacks(AbstractCard c) {
        ArrayList<AbstractCard> masterDeck = AbstractDungeon.player.masterDeck.group;

        int i;
        for(i = masterDeck.size() - 1; i >= 0; --i) {
            AbstractCard card = (AbstractCard)masterDeck.get(i);
            if (card.tags.contains(AbstractCard.CardTags.STARTER_DEFEND)) {
                AbstractDungeon.player.masterDeck.removeCard(card);
            }
        }

        if (c.cardID == skillSlugHide.ID){
            for(i = 0; i < 5; ++i) {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new skillSlugHide() , (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            }
        } else {
            for(i = 0; i < 5; ++i) {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new skillLeafShield(), (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            }
        }



    }


}
