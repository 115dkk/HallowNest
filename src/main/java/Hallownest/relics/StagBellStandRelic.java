package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class StagBellStandRelic extends CustomRelic implements ClickableRelic { // You must implement things you want to use from StSlib
    /*
     * https://github.com/daviscook477/BaseMod/wiki/Custom-Relics
     * StSLib for Clickable Relics
     *
     * Usable once per turn. Right click: Evoke your rightmost orb.
     */

    // ID, images, text.
    public static final String ID = HallownestMod.makeID("StagBellStandRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("StagBell2.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("StagBell2.png"));
    private int CardDraw = 6;
    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    public StagBellStandRelic(){
        this(1);
    }


    public StagBellStandRelic(int charge) {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
        setCounter(charge);
        tips.clear();
        tips.add(new PowerTip(name, description));
    }


    @Override
    public void onRightClick() {// On right click
        if (!isObtained || (counter <1) || !isPlayerTurn) {
            // If it has been used this turn, the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }

        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) { // Only if you're in combat
            counter--;
            flash(); // Flash
            stopPulse(); // And stop the pulsing animation (which is started in atPreBattle() below)

            //Get Stag Sound for this
            CardCrawlGame.sound.playV(SoundEffects.EvGpStag3.getKey(), 1.4F);


            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(this.CardDraw));
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(3));


        }
        // See that talk action? It has DESCRIPTIONS[1] instead of just hard-coding "You are mine" inside.
        // DO NOT HARDCODE YOUR STRINGS ANYWHERE, it's really bad practice to have "Strings" in your code:

        /*
         * 1. It's bad for if somebody likes your mod enough (or if you decide) to translate it.
         * Having only the JSON files for translation rather than 15 different instances of "Dexterity" in some random cards is A LOT easier.
         *
         * 2. You don't have a centralised file for all strings for easy proof-reading. If you ever want to change a string
         * you don't have to go through all your files individually/pray that a mass-replace doesn't screw something up.
         *
         * 3. Without hardcoded strings, editing a string doesn't require a compile, saving you time (unless you clean+package).
         *
         */
    }

    @Override
    public void atPreBattle() {
        if (this.counter > 0) { // Make sure usedThisTurn is set to false at the start of each combat.
            beginLongPulse();
        }// Pulse while the player can click on it.
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
    }


    @Override
    public void onVictory() {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            if (this.counter < 1){
                this.counter++;
            }
            flash();
        }
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
    }

    // Description
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}