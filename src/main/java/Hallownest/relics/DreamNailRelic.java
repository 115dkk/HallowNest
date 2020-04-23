package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class DreamNailRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("DreamNailRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("relicDreamNail.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("relicDreamNail.png"));
    private int damageforessence = 25;


    public DreamNailRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.counter = 0;

    }

    @Override
    public void onVictory() {

        int random = AbstractDungeon.miscRng.random(0, 99);

        if (random < 8){
            if (Settings.AMBIANCE_ON) {
                CardCrawlGame.sound.playV(SoundEffects.DreamNailSound.getKey(), 1.4F);
            }
            this.counter++;
            flash();
        }


    }



    @Override
    public void atBattleStartPreDraw() {
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.id == CorruptHeart.ID){
                flash();
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, (this.counter * this.damageforessence), DamageInfo.DamageType.THORNS)));
            }
        }
    }



    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
