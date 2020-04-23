package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.infoFlukes;
import Hallownest.powers.powerExposure;
import Hallownest.powers.powerInfection;
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
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class monsterFlukemon extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterFlukemon");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte TACKLE_MOVE = 0;
    private static final byte JUMP_MOVE = 1;





    //Hornet Values
    private int  Tackle_DMG = 5;
    private int  Tackle_HITS = 2;
    private int  Tackle_Anims;
    private int  Jump_VAL = 2;



    private int  Death_DMG = 11;




    private boolean isDead = false;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 48;
    private int minHP = 44;

    private int  DeathHP = 100;

    private float thisx;
    private float thisy;




    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int chargeCount = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String DeadIdleAnim = "DeadIdle";
    private String TackleAnim = "Tackle";
    private String JumpAnim = "Jump";
    private String HitAnim = "Hit";
    private String DieAnim = "Die";

    private boolean willspawn;


    public monsterFlukemon() {
        this(0.0f, 0.0f);
    }

    public monsterFlukemon(float x, float y) {
        super(monsterFlukemon.NAME, ID, 46, 0, 0, 150.0f, 250.0f, null, x, y);
        this.thisx = x;
        this.thisy = y;
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/Flukemon/Flukemon.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;
        this.willspawn = true;
        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 3;
            this.maxHP += 3;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Tackle_DMG +=1;
        }



        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Tackle_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {

        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this, new infoFlukes(this)));

        AbstractDungeon.getCurrRoom().cannotLose = true;
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

    public void spawnHalves(){
        this.isDead = true;
        this.halfDead = true;
        this.hideHealthBar();
        runAnim(DieAnim);
        this.willspawn = false;
        CardCrawlGame.sound.playV(SoundEffects.FlukeAttack1.getKey(),1.5F);
        AbstractDungeon.actionManager.addToTop(new SpawnMonsterAction(new monsterFluketop(this.thisx - 100, this.thisy + 75), false));
        AbstractDungeon.actionManager.addToTop(new SpawnMonsterAction(new monsterFlukebot(this.thisx +100, this.thisy), false));
        AbstractDungeon.getCurrRoom().cannotLose = false;
        this.halfDead = false;
        AbstractDungeon.actionManager.addToBottom(new SuicideAction(this));
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case TACKLE_MOVE:{
                this.Tackle_Anims = Tackle_HITS-1;
                runAnim(TackleAnim);
                CardCrawlGame.sound.playV(SoundEffects.FlukeAttack1.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.9f));

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                //CardCrawlGame.sound.playV(SoundEffects.MossPoof.getKey(),1.4F);
                break;
            }
            case JUMP_MOVE:{
                runAnim(JumpAnim);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.MossFury.getKey()));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new powerExposure(p, this, Jump_VAL),Jump_VAL));

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        if (numTurns ==1){
            this.damage.set(0, new DamageInfo(this, (this.Tackle_DMG -2)));
        } else if (numTurns==2){
            this.damage.set(0, new DamageInfo(this, (this.Tackle_DMG -1)));
        } else {
            this.damage.set(0, new DamageInfo(this, (this.Tackle_DMG)));

        }
        if ((!this.lastTwoMoves(TACKLE_MOVE))){
            this.setMove(TACKLE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, Tackle_HITS, true);
        } else {
            this.setMove(JUMP_MOVE, Intent.DEBUFF);
        }
    }
    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterFlukemon");
        NAME = monsterFlukemon.monsterStrings.NAME;
        MOVES = monsterFlukemon.monsterStrings.MOVES;
        DIALOG = monsterFlukemon.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            if (!isDead){
                runAnim(HitAnim);
            }
        }

    }

    @Override
    public void die() {
        if ((!AbstractDungeon.getCurrRoom().cannotLose) && !this.willspawn){
            super.die();
            this.stopAnimation();
            this.useFastShakeAnimation(1.0f);
        } else {
            spawnHalves();
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
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(DeadIdleAnim);
    }

    public void TackledresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(TackleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterFlukemon character;

        public AnimationListener(monsterFlukemon character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if ((animation.name.equals(TackleAnim)) && Tackle_Anims > 0){
                character.TackledresetAnimation();
                Tackle_Anims--;
                return;
            }


            if (isDead) {
                if (!animation.name.equals(DeadIdleAnim)) {
                    character.DieresetAnimation();
                }
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