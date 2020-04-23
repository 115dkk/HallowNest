package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.infoCreeper;
import Hallownest.powers.powerExposure;
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
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterWanderingHusk extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterWanderingHusk");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterWanderingHusk.monsterStrings.NAME;
    public static final String[] MOVES = monsterWanderingHusk.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterWanderingHusk.monsterStrings.DIALOG;

    private static final byte ATTACK_MOVE = 0;
    private static final byte WANDER_MOVE = 1;
    private static final byte CATTACK_MOVE = 2;
    private static final byte EMERGE_MOVE = 3;






    //Values
    private int  Attack_DMG = 8;
    //private int  Attack_VAL = 1;
    private int  Wander_VAL = 1;
    private int  CAttack_DMG = 7;
    private int  CAttack_HITS = 2;


    private boolean isCreeper;
    private boolean canRevive;
    private boolean ScaryMode = false;

    public boolean Respawning = false;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 30;
    private int maxHP = 32;
    private int CminHP = 36;
    private int CmaxHP = 38;
    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String AttackAnim = "Attack";
    private String WanderAnim = "Sadness";
    private String HitAnim = "Hit";
    //Creeper anims

    private String DieAnim = "Die";
    private String DeadAnim = "Dead";
    private String SpawnAnim = "Spawn";
    private String CIdleAnim = "CIdle";
    private String CHitAnim = "CHit";
    private String CAttackAnim = "CAttack";







    public monsterWanderingHusk() {
        this(0.0f, 0.0F, false);
    }

    public monsterWanderingHusk(float x, float y, boolean creeper) {
        super(monsterWanderingHusk.NAME, ID, 55, 0, 0, 125.0f, 175.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/WanderingHusks/WanderingHusk.scml");
        //((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);
        this.type = EnemyType.NORMAL;
        this.isCreeper = creeper;
        this.canRevive = creeper;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;
            this.CmaxHP +=4;
            this.CminHP +=4;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Attack_DMG+=1;
            this.CAttack_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.CmaxHP+=6;
            this.CminHP+=3;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.CAttack_DMG)); // attack 1 damage



    }

    @Override
    public void usePreBattleAction() {
        if (isCreeper) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new infoCreeper(this)));
        }
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x) * Settings.scale;
                this.dialogY = (this.hb_y) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0F));
            }
        }


    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case ATTACK_MOVE:{
                runAnim(AttackAnim);
                //CardCrawlGame.sound.playV(SoundEffects.MantisWarriorSlice.getKey(),1.3F);
                CardCrawlGame.sound.playV(SoundEffects.HuskAttack.getKey(),1.3F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));


                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case WANDER_MOVE:{
                runAnim(WanderAnim);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new WeakPower(p,this.Wander_VAL, true), this.Wander_VAL));


                //CardCrawlGame.sound.playV(SoundEffects.MantisWarriorSlice.getKey(),1.3F);



                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                break;
            }
            case CATTACK_MOVE:{
                runAnim(CAttackAnim);
                CardCrawlGame.sound.playV(SoundEffects.CorpseAttack.getKey(),1.3F);
                for (int i = 0; i < this.CAttack_HITS; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL,true));
                }

                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }
            case EMERGE_MOVE:{
                runAnim(SpawnAnim);
                CardCrawlGame.sound.playV(SoundEffects.CorpseEmerge.getKey(),1.3F);
                this.Respawning = false;
                this.halfDead = false;
                AbstractDungeon.actionManager.addToTop(new RemoveAllPowersAction(this, false));
                setHp(this.CminHP,this.CmaxHP);
                this.hb.update();
                this.updateHealthBar();
                healthBarUpdatedEvent();
                this.showHealthBar();





                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if (this.Respawning){
            return;
        }

        if (ScaryMode){
            this.setMove(CATTACK_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base, this.CAttack_HITS, true);
            return;
        }

        if (this.numTurns == 1 ){
            this.setMove(ATTACK_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            return;
        }

        if ((this.lastMove(ATTACK_MOVE)) && (num < 80)) {
            this.setMove(WANDER_MOVE, Intent.DEBUFF);
        } else {
            this.setMove(ATTACK_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
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


    private void respawn(){

        this.Respawning = true;
        this.setHp(999);
        runAnim(DieAnim);
        AbstractDungeon.actionManager.addToTop(new RemoveAllPowersAction(this, false));
        this.nextMove = EMERGE_MOVE;
        this.setMove(EMERGE_MOVE, Intent.UNKNOWN);
        this.intent = (Intent.UNKNOWN);
        this.createIntent();
        this.canRevive = false;
        this.IdleAnim = this.CIdleAnim;
        this.HitAnim = this.CHitAnim;
        this.ScaryMode = true;
        this.halfDead = true;
        this.updateHealthBar();
        healthBarUpdatedEvent();
        this.hideHealthBar();

    }

    @Override
    public void die() {
        if (this.canRevive){
         respawn();
        } else {
            this.stopAnimation();
            useShakeAnimation(1.0F);
            //runAnim("Defeat");
            super.die();
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

        private monsterWanderingHusk character;

        public AnimationListener(monsterWanderingHusk character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (!Respawning){
                if (!animation.name.equals(IdleAnim)) {
                    character.resetAnimation();
                }
            } else{
                if (!animation.name.equals(DeadAnim)) {
                    character.DeadAnimation();
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