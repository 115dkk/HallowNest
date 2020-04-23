package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class SheosBrushRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("SheosBrushRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("relicSheosBrush.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("relicSheosBrush.png"));

    public SheosBrushRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        if (card.costForTurn >=3){
                this.flash();
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 3), 3));

        }
    }


    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
