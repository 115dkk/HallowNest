package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.ThornsPower;

public class monsterWingmould extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterWingmould");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte GUARD_MOVE = 0;
    private static final byte RECOVER_MOVE = 1;





    //Hornet Values
    private int Buzz_SELF_BLOCK = 12;
    private int  Buzz_BLOCK = 9;
    private int  Hurt_STR = 1;
    private int  Hurt_VAL = 3;
    private int GainedThorns = 0;
    private int Starting_BLOCK = 12;





    private boolean Shut = true;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 60;
    private int maxHP = 70;
    private int FullHP;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    private int chargeMax = 3;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String OpenAnim = "IdletoHit";
    private String CloseAnim = "HittoIdle";
    private String OpenIdle = "HitIdle";
    private String QueuedAnim = IdleAnim;



    public monsterWingmould() {
        this(0.0f, 0.0f);
    }

    public monsterWingmould(float x, float y) {
        super(monsterWingmould.NAME, ID, 20, 0, 0.0f, 100.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/Wingsmould/WingMould.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 4;
            this.maxHP += 4;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Buzz_SELF_BLOCK +=2;
            this.Buzz_BLOCK+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Hurt_STR +=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        //this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Starting_BLOCK));
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case GUARD_MOVE:{
                //CardCrawlGame.sound.playV(SoundEffects.ChargeAttack.getKey(),1.2F);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Buzz_SELF_BLOCK));
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m != null && !m.isDying) {
                        if (m.id == monsterKingsmould.ID){ // Switch this after making kingsmould
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.Buzz_BLOCK));
                        }
                    }
                }
                break;
            }
            case RECOVER_MOVE:{
                this.Shut = true;
                runAnim(CloseAnim);
                //CardCrawlGame.sound.playV(SoundEffects.ChargerRecover.getKey(),1.2F);
                if (this.hasPower(ThornsPower.POWER_ID)){
                    AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this, this, ThornsPower.POWER_ID, this.GainedThorns ));
                }
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if (Shut){
            this.setMove(GUARD_MOVE, Intent.DEFEND_BUFF);
            return;
        } else {
            this.setMove(RECOVER_MOVE, Intent.DEFEND);
        }

    }

    private void transform ()
    {
        this.Shut = false;

        runAnim(OpenAnim);
        //CardCrawlGame.sound.playV(SoundEffects.ChargeShift.getKey(),1.6F);
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != null && !m.isDying) {
                if (m.id == monsterKingsmould.ID){ // Switch this after making kingsmould
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.Hurt_STR), this.Hurt_STR));
                }
            }
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ThornsPower(this, Hurt_VAL), this.Hurt_VAL));
        this.GainedThorns = 3;
        this.nextMove = RECOVER_MOVE;
        this.setMove(RECOVER_MOVE, Intent.DEFEND_BUFF);
        this.intent = (Intent.DEFEND_BUFF);
        this.createIntent();


    }


    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterWingmould");
        NAME = monsterWingmould.monsterStrings.NAME;
        MOVES = monsterWingmould.monsterStrings.MOVES;
        DIALOG = monsterWingmould.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
        if (this.Shut){
            if ((this.currentBlock<=0) &&(info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0)){
               transform();
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

    public void WeakresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(OpenIdle);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterWingmould character;

        public AnimationListener(monsterWingmould character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){

            if (!Shut){
                if ((!animation.name.equals(OpenIdle)))   {
                    character.WeakresetAnimation();
                }
            } else {
                if ((!animation.name.equals(IdleAnim)))   {
                    character.resetAnimation();
                }
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