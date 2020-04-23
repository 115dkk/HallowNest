package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.infoHiveGuardian;
import Hallownest.powers.powerFlying;
import Hallownest.powers.powerHivesBlood;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveAllPowersAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ExplosivePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class minionVolatileZoteling extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("minionVolatileZoteling");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = minionVolatileZoteling.monsterStrings.NAME;
    public static final String[] MOVES = minionVolatileZoteling.monsterStrings.MOVES;
    public static final String[] DIALOG = minionVolatileZoteling.monsterStrings.DIALOG;

    private static final byte TWO_MOVE = 0;
    private static final byte ONE_MOVE = 1;
    private static final byte BOOM_MOVE = 2;






    //Values
    private int  Boom_DMG = 30;






    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 56;
    private int maxHP = 60;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    private boolean isDead = false;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String PopAnim = "Pop";
    private String ChargeAnim = "Charge";
    private String DeadAnim = "Gone";
    private String HitAnim = "Hit";



    public minionVolatileZoteling() {
        this(0.0f, 0.0F);
    }

    public minionVolatileZoteling(float x, float y) {
        super(minionVolatileZoteling.NAME, ID, 130, 0, 0, 100.0f, 195.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/GreyPrince/Zotelings/VolatileZoteling.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(1.00f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 9)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;

        }

        if (AbstractDungeon.ascensionLevel >= 19)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Boom_DMG+=4;
        }


        /*
        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Dance_VAL+=1;
            add a healing to the smash
        }
        */

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
        this.damage.add(new DamageInfo(this, this.Boom_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case TWO_MOVE:{
                runAnim(ChargeAnim);

                break;
            }
            case ONE_MOVE:{
                runAnim(ChargeAnim);
                CardCrawlGame.sound.playV(SoundEffects.EvGpZote2.getKey(),1.3F);

                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }
            case BOOM_MOVE:{
                this.isDead = true;
                runAnim(PopAnim);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BeeBuff.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.ZoteBalloonPop.getKey(),1.3F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE,true));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this));
                break;
            }


        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if ((this.lastMove(ONE_MOVE))){
            this.setMove(MOVES[BOOM_MOVE],BOOM_MOVE,Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else if (this.lastMove(TWO_MOVE)){
            this.setMove(MOVES[ONE_MOVE],ONE_MOVE, Intent.UNKNOWN);
        } else {
            this.setMove(MOVES[TWO_MOVE],TWO_MOVE, Intent.UNKNOWN);
        }
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            runAnim(HitAnim);

        }
    }

    @Override
    public void die() {
        this.stopAnimation();
        useShakeAnimation(1.0F);
        //runAnim("Defeat");
        super.die();
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(IdleAnim);
    }

    public void DeadAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(DeadAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private minionVolatileZoteling character;

        public AnimationListener(minionVolatileZoteling character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (isDead){
                character.DeadAnimation();
            } else if (!animation.name.equals(IdleAnim)) {
                character.resetAnimation();
            }

        }

        //UNUSED
        public void animationChanged(Animation var1, Animation var2){

        }

        //UNUSED
        public void preProcess(Player var1){

        }

        //UNUSED
        public void postProcess(Player var1){

        }

        //UNUSED
        public void mainlineKeyChanged(com.brashmonkey.spriter.Mainline.Key var1, com.brashmonkey.spriter.Mainline.Key var2){

        }
    }
}