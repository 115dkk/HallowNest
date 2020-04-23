package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class SalubrasBlessingRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("SalubrasBlessingRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("SalubraRelic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("SalubraRelic.png"));

    public SalubrasBlessingRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    public void onRest() {

        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.playV(SoundEffects.EvGpSalubraGreet.getKey(), 1.4F);
        }
        AbstractDungeon.player.increaseMaxHp(2, true);

        int toHeal = AbstractDungeon.player.getRelicNames().size();

        AbstractDungeon.actionManager.addToBottom(new HealAction(AbstractDungeon.player, AbstractDungeon.player, toHeal));

    }


    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
