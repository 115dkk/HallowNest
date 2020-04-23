package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.infoFlukes;
import Hallownest.powers.infoMosskin;
import Hallownest.powers.powerInfection;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedDripEffect;
import Hallownest.vfx.InfectedEffect;
import Hallownest.vfx.InfectedProjectileEffect;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterMosskin extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMosskin");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte POOF_MOVE = 0;
    private static final byte FURY_MOVE = 1;
    private static final byte HURT_MOVE = 2;
    private static final byte DEATH_POOF = 3;
    private static final byte NO_MOVE = 4;





    //Hornet Values
    private int  Poof_INF = 3;
    private int  Poof_STR = 2;
    private int  Fury_HEAL = 9;
    private int  Hurt_UP = 2;
    private int  Hurt_BLOCK = 6;
    private int  Hurt_DMG;



    private int  Death_DMG = 12;




    private boolean DeadMode = false;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 40;
    private int minHP = 38;

    private int  DeathHP = 100;



    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int chargeCount = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String PoofAnim = "Poof";
    private String FuryAnim = "Fury";
    private String HurtAnim = "Sacrifice";
    private String HitAnim = "Hit";

    private String DieIdleAnim = "DieIdle";
    private String DieAnim = "Die";
    private String DiePoof = "DeathPoof";


    public monsterMosskin() {
        this(0.0f);
    }

    public monsterMosskin(float x) {
        super(monsterMosskin.NAME, ID, 45, 0, 0, 100.0f, 200.0f, null, x, 0.0F);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/Mosskin/Mosskin.scml");
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
            this.Poof_INF +=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Fury_HEAL+=2;
            this.Death_DMG +=2;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Death_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        this.Hurt_DMG = (this.currentHealth/6);
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this, new infoMosskin(this)));

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x - 25) * Settings.scale;
                this.dialogY = (this.hb_y + 25) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case POOF_MOVE:{
                runAnim(PoofAnim);
                CardCrawlGame.sound.playV(SoundEffects.MossPoof.getKey(),1.4F);
                AbstractDungeon.actionManager.addToBottom(new ApplyInfectionAction(p, this, Poof_INF));
                //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new powerInfection(p, this,Poof_INF),Poof_INF));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedEffect(), 0.1f));
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else if (m != this){
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this, new StrengthPower(m, Poof_STR),Poof_STR));
                    }
                }
                break;
            }
            case FURY_MOVE:{
                runAnim(FuryAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.MossFury.getKey()));
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else {
                        AbstractDungeon.actionManager.addToBottom(new HealAction(m,this, Fury_HEAL));
                    }
                }


                break;
            }
            case HURT_MOVE:{
                runAnim(HurtAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.MossEek.getKey()));
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this, this, Hurt_DMG));
                this.Fury_HEAL += Hurt_UP;
                this.Poof_INF += Hurt_UP;
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else if (m != this){
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.Hurt_BLOCK));
                    }
                }
                break;
            }
            case DEATH_POOF:{
                runAnim(DiePoof);
                CardCrawlGame.sound.playV(SoundEffects.MossPoof.getKey(),1.4F);
                this.halfDead = false;
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this));
                break;
            }
            case NO_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.MossPrep.getKey(),1.4F);

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {

        if (DeadMode){
            this.setMove(DEATH_POOF, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            return;
        }
        if (num < 40 && (!lastMove(FURY_MOVE)) && (!lastMoveBefore(FURY_MOVE))){
            this.setMove(FURY_MOVE, Intent.BUFF);
            return;
        } else if (num < 75 && (!lastMove(POOF_MOVE))){
            this.setMove(POOF_MOVE, Intent.MAGIC);
            return;
        } else {
            this.setMove(HURT_MOVE, Intent.DEFEND_BUFF);
        }
    }

    private void transform ()
    {
        this.setHp(DeathHP);
        this.DeadMode = true;
        runAnim(DieAnim);
        this.updateHitbox(50.0f, 0.0f, 0.0f, 200.0f);
        this.hb_w = 0.0f;
        this.nextMove = 4;
        this.setMove(NO_MOVE, Intent.NONE);
        this.intent = (Intent.NONE);
        this.hb.update();
        this.updateHealthBar();
        this.halfDead = true;
        healthBarUpdatedEvent();
        this.hideHealthBar();
        AbstractDungeon.getCurrRoom().cannotLose = false;

    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterMosskin");
        NAME = monsterMosskin.monsterStrings.NAME;
        MOVES = monsterMosskin.monsterStrings.MOVES;
        DIALOG = monsterMosskin.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            if (!DeadMode){
                runAnim(HitAnim);
            }
        }

    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die();
            this.stopAnimation();
            this.useFastShakeAnimation(1.0f);
        } else {
            transform();
        }
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(IdleAnim);
    }

    public void DieresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(DieIdleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterMosskin character;

        public AnimationListener(monsterMosskin character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (animation.name.equals(DiePoof)){
                character.stopAnimation();
                return;
            }
            if (DeadMode) {
                character.DieresetAnimation();
            } else {
                if (!animation.name.equals(IdleAnim)) {
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