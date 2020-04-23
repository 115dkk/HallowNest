package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.powerRagin;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;

public class eliteCrystalGuardian extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("eliteCrystalGuardian");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte RAGE_MOVE = 0;
    private static final byte BLAST_MOVE = 1;
    private static final byte HUH_MOVE = 2;



  
    private int  Blast_DMG = 11;
    private int  Huh_VAL = 1;
    private int  Huh_Count = 3;
    private int  Huh_Timer = Huh_Count;
    private int DamageThreshhold = 12;
    
    private boolean Enraged = false;
    
    private int turnStrength;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 114;
    private int minHP = 110;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String RageAnim = "Rage";
    private String BlastAnim = "Blast";
    private String HitAnim = "Hit";


    public eliteCrystalGuardian() {
        this(0.0f, 0.0f);
    }

    public eliteCrystalGuardian(final float x, final float y) {
        super(eliteCrystalGuardian.NAME, ID, 85, 0, 0, 250.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/CrystalGuardian/CrystalGuardian.scml");
        this.type = EnemyType.ELITE;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 8) // Elites are tougher at Asc 8
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 8;
            this.maxHP += 8;
        }
        if (AbstractDungeon.ascensionLevel >=3) //Elites are deadlier at 3
        {
            //increases the power of his multihit and debufff for higher ascensions
            this.Blast_DMG+= 1;
        }
        if (AbstractDungeon.ascensionLevel >= 18) //18 says elites have harder move sets so do something fancy
        {
            //??
            this.DamageThreshhold -=2;
        }


        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Blast_DMG)); // attack 0 damage


        Player.PlayerListener listener = new GuardianListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }


    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("GPEliteBGM");

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

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {


            case RAGE_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingSwoop.getKey()));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new IntenseZoomEffect(this.hb.cX, this.hb.cY, true), 0.6f, true));
                CardCrawlGame.sound.playV(SoundEffects.SFXCrystalRage.getKey(),1.4F);
                runAnim(RageAnim);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerRagin(this, 1, this.DamageThreshhold ),1));
                this.Enraged = true;
                break;
            }
            case BLAST_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXCrystalBlast.getKey()));
                runAnim(BlastAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));

                break;
            }
            case HUH_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new VulnerablePower(p, Huh_VAL, true),Huh_VAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new VulnerablePower(this, Huh_VAL, true),Huh_VAL));

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        if (numTurns > 1){
            Huh_Timer--;
        }
        this.turnStrength = numTurns;
        this.damage.set(0, new DamageInfo(this, (this.Blast_DMG + turnStrength)));

        if (!Enraged){
            this.setMove(MOVES[RAGE_MOVE],RAGE_MOVE, Intent.BUFF);
            return;
        }

        if (Huh_Timer <= 0){
            this.setMove(HUH_MOVE, Intent.MAGIC);
            this.Huh_Timer = Huh_Count;
            return;
        } else {
            this.setMove(BLAST_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);

        }
       
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:eliteCrystalGuardian");
        NAME = eliteCrystalGuardian.monsterStrings.NAME;
        MOVES = eliteCrystalGuardian.monsterStrings.MOVES;
        DIALOG = eliteCrystalGuardian.monsterStrings.DIALOG;
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

    public class GuardianListener implements Player.PlayerListener {

        private eliteCrystalGuardian character;

        public GuardianListener(eliteCrystalGuardian character) {
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