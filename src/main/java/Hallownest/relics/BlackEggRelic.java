package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class BlackEggRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("BlackEggRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BlackEggRelic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("BlackEggRelic.png"));

    private int Turns = 2;
    private int cardsperTurn = 2;
    private int cardsThisturn =0;
    private boolean thiscard = false;

    public BlackEggRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        if (!card.purgeOnUse && this.counter > 0 && AbstractDungeon.actionManager.cardsPlayedThisTurn.size() - this.cardsThisturn <= this.cardsperTurn) {
            ++this.cardsThisturn;
            this.counter--;
            AbstractMonster m = null;
            if (action.target != null) {
                m = (AbstractMonster)action.target;
            }

            AbstractCard tmp = card.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToBottom(tmp);
            tmp.current_x = card.current_x;
            tmp.current_y = card.current_y;
            tmp.target_x = (float)Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            tmp.target_y = (float)Settings.HEIGHT / 2.0F;
            if (m != null) {
                tmp.calculateCardDamage(m);
            }

            tmp.purgeOnUse = true;
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
        }

    }

    @Override
    public void atTurnStart() {
        if (Turns > 0){
            this.cardsThisturn = 0;
            this.Turns--;
            this.counter = 2;
            getUpdatedDescription();
            } else {
            stopPulse();
        }
    }

    @Override
    public void atPreBattle() {
        Turns = 2; // Make sure usedThisTurn is set to false at the start of each combat.
        beginLongPulse();     // Pulse while the player can click on it.
    }

    @Override
    public void onVictory() {
        stopPulse();
    }

    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
