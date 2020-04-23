package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.StolenSoul;
import Hallownest.cards.status.Swarmed;
import Hallownest.powers.powerInfection;
import Hallownest.powers.powerReinforcements;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.unique.RemoveAllPowersAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.IronWaveEffect;

public class eliteWatcherKnight extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("eliteWatcherKnight");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte SWING_MOVE = 0;
    private static final byte ROLL_MOVE = 1;
    private static final byte BOUNCE_MOVE = 2;
    private static final byte REVIVE_MOVE = 3;
    private static final byte DEAD_MOVE = 4;





    //Hornet Values
    private int  Swing_DMG = 7;
    private int  Swing_HITS = 2;
    private int  Bounce_DEBUFF = 2;
    private int  Bounce_CARDS = 1;
    private int  Roll_DMG = 9;
    private int  Roll_BLOCK = 7;
    private int  Revive_COUNTER = 2;
    private int  Revive_STR = 2;









    private boolean canDie = false;
    private boolean isDie = false;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 56;
    private int minHP = 52;

    //private int  DeathHP = 100;



    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int chargeCount = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SwingAnim = "Swing";
    private String RollAnim = "Roll";
    private String BounceAnim = "Bounce";
    private String HitAnim = "Hit";

    private String DieIdleAnim = "Dead";
    private String DieAnim = "Die";
    private String ReviveAnim = "Revive";

    public eliteWatcherKnight() {
        this( 0.0f);
    }
    public eliteWatcherKnight(float x) {
        this(x,0.0f);
    }

    public eliteWatcherKnight(float x, float y) {
        super(eliteWatcherKnight.NAME, ID, 60, 0, 0, 200.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/WatcherKnights/WatcherKnight.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);

        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 8)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 3;
            this.maxHP += 4;

        }

        if (AbstractDungeon.ascensionLevel >= 3)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Roll_BLOCK +=1;
            this.Swing_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 18)
        {
            this.Revive_STR+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Swing_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Roll_DMG)); // attack 1



    }

    @Override
    public void usePreBattleAction() {
        this.Revive_COUNTER = 2;
        AbstractDungeon.getCurrRoom().cannotLose = true;
        AbstractDungeon.getCurrRoom().playBgmInstantly("CoTEliteBGM");
        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this,this, new powerReinforcements(this, this.Revive_COUNTER)));

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x - 50) * Settings.scale;
                this.dialogY = (this.hb_y + 75) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }
    }

    private void randomDebuff (int whichone){

        AbstractPlayer p = AbstractDungeon.player;

        switch (whichone) {
            case 1: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new WeakPower(p, Bounce_DEBUFF,true),Bounce_DEBUFF));
                break;
            }
            case 2: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new FrailPower(p, Bounce_DEBUFF,true),Bounce_DEBUFF));
                break;
            }

        }


    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case SWING_MOVE:{
                runAnim(SwingAnim);
                //CardCrawlGame.sound.playV(SoundEffects.MossPoof.getKey(),1.4F);
                    //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.MossFury.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                break;
            }
            case ROLL_MOVE:{
                runAnim(RollAnim);
                CardCrawlGame.sound.playV(SoundEffects.EliteWatcherSpin.getKey(),1.4F);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.EliteWatcherSpin.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,this.Roll_BLOCK));


                break;
            }
            case BOUNCE_MOVE:{
                runAnim(BounceAnim);
                CardCrawlGame.sound.playV(SoundEffects.SFXGuardLand.getKey(),1.4F);

                AbstractDungeon.actionManager.addToBottom(new VFXAction(new IronWaveEffect(p.hb.cX, p.hb.cY, p.hb.cX)));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Swarmed(),Bounce_CARDS));
                randomDebuff(AbstractDungeon.miscRng.random(1,2));

                break;
            }
            case REVIVE_MOVE:{
                this.isDie = false;
                runAnim(ReviveAnim);
                this.Revive();
                break;
            }
            case DEAD_MOVE:{

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {

        if (lastMove(DEAD_MOVE)){
            this.setMove(REVIVE_MOVE, Intent.UNKNOWN);
            return;
        }
        if (num < 45 && (!lastMove(ROLL_MOVE))){
            this.setMove(ROLL_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(1)).base);

            return;
        } else if (num < 75 && (!lastMove(BOUNCE_MOVE))){
            this.setMove(BOUNCE_MOVE, Intent.DEBUFF);
            return;
        } else {
            this.setMove(SWING_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Swing_HITS, true);
        }
    }

    private void FakeDead ()
    {
        this.setHp(999);

        this.isDie = true;
        runAnim(DieAnim);
        this.nextMove = DEAD_MOVE;
        this.setMove(DEAD_MOVE, Intent.NONE);
        this.intent = (Intent.NONE);
        this.updateHitbox(125.0F, 0.0F, 0.0f, 300.0f);
        this.createIntent();
        this.hb.update();
        this.halfDead = true;
        this.updateHealthBar();
        healthBarUpdatedEvent();
        this.hideHealthBar();

    }

    private void Revive ()
    {
        this.halfDead = false;
        AbstractDungeon.actionManager.addToTop(new RemoveAllPowersAction(this, false));
        setHp(this.minHP,this.maxHP);
        this.hb.update();
        this.updateHealthBar();
        this.updateHitbox(0, 0, 200.0f, 300.0f);
        healthBarUpdatedEvent();
        this.showHealthBar();
        this.Revive_COUNTER--;
        if (this.Revive_COUNTER <= 0){
            AbstractDungeon.getCurrRoom().cannotLose = false;
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this, new powerReinforcements(this, this.Revive_COUNTER)));
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this, new StrengthPower(this, this.Revive_STR),this.Revive_STR));

        this.Revive_STR+=1;
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:eliteWatcherKnight");
        NAME = eliteWatcherKnight.monsterStrings.NAME;
        MOVES = eliteWatcherKnight.monsterStrings.MOVES;
        DIALOG = eliteWatcherKnight.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            if (!isDie){
                runAnim(HitAnim);
            }
        }

    }

    @Override
    public void die() {
        if ((!AbstractDungeon.getCurrRoom().cannotLose) && (this.Revive_COUNTER <= 0)) {
            super.die();
            this.stopAnimation();
            this.useFastShakeAnimation(1.0f);
        } else {
            FakeDead();
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

        private eliteWatcherKnight character;

        public AnimationListener(eliteWatcherKnight character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (isDie) {
                if (!animation.name.equals(IdleAnim)) {
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