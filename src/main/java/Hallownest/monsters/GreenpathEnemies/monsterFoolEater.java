package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerFoolEater;
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
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterFoolEater extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterFoolEater");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte WAIT_MOVE = 0;
    private static final byte TRAP_MOVE = 1;





    //Hornet Values
    private int  Trap_DMG = 11;
    private int  Trap_CARDS = 3;
    private int  Original_Attack;
    private int GainedSTR = 0;
    private int StrPerWait = 2;





    private boolean Leafed = true;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 58;
    private int minHP = 54;
    private int FullHP;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    private int chargeMax = 3;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SnapAnim = "Snap";



    public monsterFoolEater() {
        this(0.0f);
    }

    public monsterFoolEater(float x) {
        super(monsterFoolEater.NAME, ID, 40, 0, -15.0f, 175.0f, 150.0f, null, x, 0.0F);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/FoolEater/FoolEater.scml");
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
            this.Trap_DMG +=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Trap_CARDS -=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Trap_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerFoolEater(this, Trap_CARDS,Trap_DMG)));

    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case WAIT_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.StrPerWait),this.StrPerWait));
                break;
            }
            case TRAP_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY,true));
                runAnim(SnapAnim);
                if (this.hasPower(powerFoolEater.POWER_ID)){
                    ((powerFoolEater)this.getPower(powerFoolEater.POWER_ID)).ResetTrap();
                }
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        this.setMove(WAIT_MOVE, Intent.BUFF);


    }

    public void TrapIntent(int newdamage){
        this.nextMove = 1;
        this.damage.set(0, new DamageInfo(this, (newdamage)));
        this.setMove(TRAP_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        this.createIntent();
        this.intent = (Intent.ATTACK);

        
    }


    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterFoolEater");
        NAME = monsterFoolEater.monsterStrings.NAME;
        MOVES = monsterFoolEater.monsterStrings.MOVES;
        DIALOG = monsterFoolEater.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
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

        private monsterFoolEater character;

        public AnimationListener(monsterFoolEater character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            
                if ((!animation.name.equals(IdleAnim))) {
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