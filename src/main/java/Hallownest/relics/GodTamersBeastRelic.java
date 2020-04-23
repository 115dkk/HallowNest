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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.Iterator;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class GodTamersBeastRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("GodTamersBeastRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("relicGodTamersBeast.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("relicGodTamersBeast.png"));

    public GodTamersBeastRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onShuffle() {
        Iterator var1 = AbstractDungeon.getCurrRoom().monsters.monsters.iterator();

        while(var1.hasNext()) {
            AbstractMonster mo = (AbstractMonster)var1.next();
            this.addToBot(new RelicAboveCreatureAction(mo, this));
            this.addToBot(new ApplyPowerAction(mo, AbstractDungeon.player, new ConstrictedPower(mo, AbstractDungeon.player,3), 3, true));
        }
    }



    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
