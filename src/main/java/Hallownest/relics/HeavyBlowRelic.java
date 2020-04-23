package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class HeavyBlowRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("HeavyBlowRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("HeavyBlow.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("HeavyBlow.png"));

    public HeavyBlowRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        if ((card.costForTurn >=3) && (card.type.equals(AbstractCard.CardType.ATTACK))){
                this.flash();
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 5 ));
        }
    }


    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
