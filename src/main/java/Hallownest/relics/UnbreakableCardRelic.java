package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class UnbreakableCardRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("UnbreakableCardRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("UnbreakableCardRelic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("UnbreakableCardRelic.png"));
    private boolean shuffled = false;



    public UnbreakableCardRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);



    }

    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        if ((card.exhaust) && (shuffled) ){
            this.flash();
            action.exhaustCard = false;
            this.shuffled = false;
            this.stopPulse();
        }
    }

    public void onShuffle() {
        this.shuffled = true;
        this.beginPulse();
    }


    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
