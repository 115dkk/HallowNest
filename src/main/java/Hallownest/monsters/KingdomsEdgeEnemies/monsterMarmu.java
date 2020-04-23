package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.infoMarmu;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.*;

public class monsterMarmu extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMarmu");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte ATTACK_MOVE = 0;
    private static final byte RECOVER_MOVE = 1;





    //Hornet Values
    private int  Attack_DMG = 15;
    private int  Attack_BLOCK = 9;
    private int  Original_Attack;
    private int  GainedSTR = 0;

    private int StrengthDown = 9;





    private boolean Broken = false;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int minHP = 60;
    private int maxHP = 64;

    private int FullHP;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    private int chargeMax = 3;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String AttackAnim = "Attack";
    private String HitAnim = "Hit";




    public monsterMarmu() {
        this(0.0f, 0.0f);
    }

    public monsterMarmu(float x, float y) {
        super(monsterMarmu.NAME, ID, 20, 0, 20.0f, 125.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/Marmu/Marmu.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 3;
            this.maxHP += 3;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Attack_DMG +=1;
            this.Attack_BLOCK+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.StrengthDown =-1;
        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new infoMarmu(this)));
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Attack_BLOCK));

    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case ATTACK_MOVE:{
                runAnim(AttackAnim);
                CardCrawlGame.sound.playV(SoundEffects.MarmuBall.getKey(),1.2F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY,true));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Attack_BLOCK));

                this.Broken = false;
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        this.Attack_DMG++;
        this.StrengthDown++;
        this.damage.set(0, new DamageInfo(this, (this.Attack_DMG)));
        this.setMove(ATTACK_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterMarmu");
        NAME = monsterMarmu.monsterStrings.NAME;
        MOVES = monsterMarmu.monsterStrings.MOVES;
        DIALOG = monsterMarmu.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);

         if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0)){
            runAnim(HitAnim);
             if (!this.Broken) {
                 if (this.currentBlock <= 0) {
                     AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 0 - this.StrengthDown), 0 - this.StrengthDown, true, AbstractGameAction.AttackEffect.NONE));
                     AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new GainStrengthPower(this, this.StrengthDown), this.StrengthDown, true, AbstractGameAction.AttackEffect.NONE));
                     this.Broken = true;
                     return;
                 }
             }
        }
        //just checks to make sure the attack came from the plaer basically.

    }

    @Override
    public void die() {
            super.die();
            this.stopAnimation();
            this.useShakeAnimation(1.0f);
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(IdleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterMarmu character;

        public AnimationListener(monsterMarmu character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){

                if ((!animation.name.equals(IdleAnim)))   {
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