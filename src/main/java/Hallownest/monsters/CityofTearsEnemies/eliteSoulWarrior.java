package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.StolenSoul;
import Hallownest.powers.powerInfection;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedDripEffect;
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
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;

public class eliteSoulWarrior extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("eliteSoulWarrior");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte ORB_MOVE = 0;
    private static final byte DOWN_MOVE = 1;
    private static final byte SIDE_MOVE = 2;
    private static final byte SKITTER_MOVE = 3;


    //might change how his orb empowers his attacks for both clarity and counterplay. Give him a power that makes him gain strength whenever you draw (or maybe play) a Stolen Soul


    private int  Orb_INF= 4;
    private int  Orb_CARDS =1;
    private int  Down_DMG = 11;
    private int  Down_VAL = 1;
    private int  Side_DMG = 18;
    private int  Side_SCALING = 2;
    private int  Skitter_BLOCK = 24;

    private boolean Enraged = false;

    private int turnStrength;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 164;
    private int minHP = 158;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String OrbAnim = "SoulBall";
    private String SideAnim = "Sideslash";
    private String DownAnim = "Downslash";
    private String SkitterAnim = "Skitter";
    private String HitAnim = "Hit";


    public eliteSoulWarrior() {
        this(0.0f, 0.0f);
    }

    public eliteSoulWarrior(final float x, final float y) {
        super(eliteSoulWarrior.NAME, ID, 85, 0, 0, 250.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/SoulWarrior/SoulWarrior.scml");
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
            this.Orb_INF+= 2;
            this.Skitter_BLOCK+=3;
        }
        if (AbstractDungeon.ascensionLevel >= 18) //18 says elites have harder move sets so do something fancy
        {
            //??
            this.Side_SCALING +=1;
        }


        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Down_DMG));
        this.damage.add(new DamageInfo(this, this.Side_DMG)); // attack 0 damage// attack 0 damage


        Player.PlayerListener listener = new AnimationInfection(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }


    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("CoTEliteBGM");

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


            case ORB_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingSwoop.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.EliteSoulBall.getKey(),1.4F);
                runAnim(OrbAnim);
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedEffect(), 0.05f));
                AbstractDungeon.actionManager.addToBottom(new ApplyInfectionAction(p, this, Orb_INF));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new StolenSoul(), Orb_CARDS, true,true));
                this.Side_DMG += this.Side_SCALING;
                break;
            }
            case DOWN_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.EliteSoullSword.getKey()));
                runAnim(DownAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new VulnerablePower(p, Down_VAL, true),Down_VAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new WeakPower(p, Down_VAL, true),Down_VAL));
                break;
            }
            case SIDE_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.EliteSoullSword.getKey()));
                runAnim(SideAnim);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
            }
            case SKITTER_MOVE:{
                runAnim(SkitterAnim);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,this.Skitter_BLOCK));

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if ((num <30)&& (!this.lastMove(ORB_MOVE))){
            this.setMove(ORB_MOVE, Intent.DEBUFF);
        } else if ((num < 55) && (!this.lastMove(DOWN_MOVE))){
            this.setMove(DOWN_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        } else if (num < 77){
            this.damage.set(1, new DamageInfo(this, (this.Side_DMG)));
            this.setMove(SIDE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
        } else {
            this.setMove(SKITTER_MOVE, Intent.DEFEND);
        }
       
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:eliteSoulWarrior");
        NAME = eliteSoulWarrior.monsterStrings.NAME;
        MOVES = eliteSoulWarrior.monsterStrings.MOVES;
        DIALOG = eliteSoulWarrior.monsterStrings.DIALOG;
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

        private eliteSoulWarrior character;

        public AnimationInfection(eliteSoulWarrior character) {
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