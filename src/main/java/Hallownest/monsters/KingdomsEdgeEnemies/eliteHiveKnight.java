package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.StolenSoul;
import Hallownest.powers.*;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedEffect;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class eliteHiveKnight extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("eliteHiveKnight");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte SPEW_MOVE = 0;
    private static final byte LUNGE_MOVE = 1;
    private static final byte LEAP_MOVE = 2;
    private static final byte SPIKES_MOVE = 3;


    //might change how his orb empowers his attacks for both clarity and counterplay. Give him a power that makes him gain strength whenever you draw (or maybe play) a Stolen Soul


    private int  Lunge_DMG= 24;
    private int  Leap_BLOCK = 24;
    private int Leap_VAL = 3;
    private int  Spike_VAL = 4;
    private int  Swarm_VAL = 11;

    private int CycleThreshold = 15;


    private boolean Spewing = false;

    private boolean Offense = true;
    private boolean Defense = false;

    private boolean LastTarget = false;

    private int SwarmLimit = 4;
    private int SwarmCounter = 0;





    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 265;
    private int maxHP = 275;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SpewAnim = "Swarm";
    private String LungeAnim = "Lunge";
    private String LeapAnim = "Leap";
    private String SpikesAnim = "HoneySpikes";
    private String HitAnim = "Hit";


    public eliteHiveKnight() {
        this(0.0f, 0.0f);
    }

    public eliteHiveKnight(final float x, final float y) {
        super(eliteHiveKnight.NAME, ID, 85, 0, 0, 250.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/HiveKnight/HiveKnight.scml");
        this.type = EnemyType.ELITE;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 8) // Elites are tougher at Asc 8
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 10;
            this.maxHP += 10;
        }
        if (AbstractDungeon.ascensionLevel >=3) //Elites are deadlier at 3
        {
            //increases the power of his multihit and debufff for higher ascensions
            this.Lunge_DMG+=2;
            this.Leap_BLOCK+=2;
            this.Spike_VAL +=1;

        }
        if (AbstractDungeon.ascensionLevel >= 18) //18 says elites have harder move sets so do something fancy
        {
            //??
            this.Swarm_VAL+= 1;
            this.CycleThreshold +=5;
        }


        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Lunge_DMG)); // attack 0 damage// attack 0 damage


        Player.PlayerListener listener = new AnimationInfection(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }


    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("KEEliteBGM");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerVigilant(this, this.CycleThreshold)));


        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x-75) * Settings.scale;
                this.dialogY = (this.hb_y+75) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }
    }
    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        if (!this.hasPower(powerVigilant.POWER_ID)){
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerVigilant(this, this.CycleThreshold)));
        }

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {


            case SPEW_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingSwoop.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.SwarmSound.getKey(),1.4F);
                runAnim(SpewAnim);
                //AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedEffect(), 0.05f));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerQueensSwarmKnight(this,this, this.Swarm_VAL),this.Swarm_VAL));
                this.Spewing = false;
                break;
            }
            case LUNGE_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.HiveKnightAttack.getKey()));
                runAnim(LungeAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                SwapSwarm(p);
                break;
            }
            case LEAP_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.HiveKnightAttack2.getKey()));
                runAnim(LeapAnim);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,this.Leap_BLOCK));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerQueensSwarmKnight(this,this, this.Leap_VAL),this.Leap_VAL));

                SwapSwarm(this);
                break;
            }
            case SPIKES_MOVE:{
                runAnim(SpikesAnim);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerPlatedThorns(this,this.Spike_VAL),this.Spike_VAL));

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void SwapSwarm (AbstractCreature target){
        int swapval = 0;
        if (target == AbstractDungeon.player){
            if (this.hasPower(powerQueensSwarmKnight.POWER_ID)){
                swapval = this.getPower(powerQueensSwarmKnight.POWER_ID).amount;
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this,this,powerQueensSwarmKnight.POWER_ID));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target,this,new powerQueensSwarmPlayer(target,this,swapval),swapval));
            }
        } else if (target == this){
            if (AbstractDungeon.player.hasPower(powerQueensSwarmPlayer.POWER_ID)){
                swapval = AbstractDungeon.player.getPower(powerQueensSwarmPlayer.POWER_ID).amount;
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(AbstractDungeon.player,this,powerQueensSwarmPlayer.POWER_ID));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target,this,new powerQueensSwarmKnight(target,this,swapval),swapval));
            }
        }
    }


    public void CycleIntent() {

        if (!this.Spewing) {
            switch (this.nextMove) {
                case LUNGE_MOVE: {
                    this.nextMove = LEAP_MOVE;
                    this.setMove(MOVES[LEAP_MOVE],LEAP_MOVE, Intent.DEFEND);
                    this.intent = (Intent.DEFEND);
                    this.createIntent();
                    break;
                }
                case LEAP_MOVE: {
                    this.nextMove = SPIKES_MOVE;
                    this.setMove(MOVES[SPIKES_MOVE],SPIKES_MOVE, Intent.BUFF);
                    this.intent = (Intent.BUFF);
                    this.createIntent();

                    break;
                }
                case SPIKES_MOVE: {
                    this.nextMove = LUNGE_MOVE;
                    this.setMove(MOVES[LUNGE_MOVE],LUNGE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
                    this.intent = (Intent.ATTACK);
                    this.createIntent();

                    break;
                }
            }

        }
    }


    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        this.SwarmCounter++;

        if (this.SwarmCounter >= this.SwarmLimit){
            this.Spewing = true;
            this.setMove(MOVES[SPEW_MOVE], SPEW_MOVE, Intent.MAGIC);
            this.SwarmCounter = 0;
            return;
        }
        if (numTurns == 1 || this.lastMove(SPEW_MOVE)){
            this.setMove(MOVES[LUNGE_MOVE],LUNGE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            return;
        }
        if (this.lastMove(SPIKES_MOVE)){
            this.setMove(MOVES[LUNGE_MOVE],LUNGE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else if (this.lastMove(LUNGE_MOVE)){
            this.setMove(MOVES[LEAP_MOVE],LEAP_MOVE, Intent.DEFEND);
        } else { // spikemove
            this.setMove(MOVES[SPIKES_MOVE],SPIKES_MOVE, Intent.BUFF);
        }
       
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:eliteHiveKnight");
        NAME = eliteHiveKnight.monsterStrings.NAME;
        MOVES = eliteHiveKnight.monsterStrings.MOVES;
        DIALOG = eliteHiveKnight.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            runAnim(HitAnim);

        }
    }

    @Override
    public void die() {
        stopAnimation();
        useShakeAnimation(2.0F);
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

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationInfection implements Player.PlayerListener {

        private eliteHiveKnight character;

        public AnimationInfection(eliteHiveKnight character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (!animation.name.equals(IdleAnim)) {
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