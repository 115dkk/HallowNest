package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class NailmastersGloryRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("NailmastersGloryRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("NailmastersGloryRelic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("NailmastersGloryRelic.png"));

    public NailmastersGloryRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }
    private boolean thisTurn = false;

    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        if ((card.costForTurn >=3) && (!thisTurn)){
                this.flash();
                this.stopPulse();
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                thisTurn = true;
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
        }
    }

    @Override
    public void atPreBattle() {
        thisTurn = false; // Make sure usedThisTurn is set to false at the start of each combat.
        beginLongPulse();     // Pulse while the player can click on it.
    }

    @Override
    public void atTurnStart() {
        thisTurn = false; // Make sure usedThisTurn is set to false at the start of each combat.
        beginLongPulse();
    }



    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
