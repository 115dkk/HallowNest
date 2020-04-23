package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
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

public class monsterMossCharger extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMossCharger");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte ATTACK_MOVE = 0;
    private static final byte RECOVER_MOVE = 1;





    //Hornet Values
    private int  Attack_DMG = 6;
    private int  Starting_ARMOR = 6;
    private int  Starting_BLOCK = 22;
    private int  Original_Attack;
    private int GainedSTR = 0;





    private boolean Leafed = true;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 22;
    private int minHP = 20;
    private int FullHP;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    private int chargeMax = 3;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String AttackAnim = "Attack";
    private String ShiftAnim = "Shift";
    private String RecoverAnim = "Recover";
    private String HitAnim = "Hit";

    private String WeakIdle = "WeakIdle";
    private String QueuedAnim = IdleAnim;



    public monsterMossCharger() {
        this(0.0f);
    }

    public monsterMossCharger(float x) {
        super(monsterMossCharger.NAME, ID, 20, 0, -20.0f, 200.0f, 225.0f, null, x, 0.0F);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/Charger/MossCharger.scml");
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
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Starting_ARMOR +=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, this.Starting_ARMOR)));
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Starting_BLOCK));

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x - 75) * Settings.scale;
                this.dialogY = (this.hb_y = 25) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
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
                CardCrawlGame.sound.playV(SoundEffects.ChargeAttack.getKey(),1.2F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY,true));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 1),1));
                this.GainedSTR++;
                break;
            }
            case RECOVER_MOVE:{
                this.Leafed = true;
                runAnim(RecoverAnim);
                CardCrawlGame.sound.playV(SoundEffects.ChargerRecover.getKey(),1.2F);
                this.updateHitbox( 0, -20.0f, 200.0f, 225.0f);
                this.updateHealthBar();
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, this.Starting_ARMOR)));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Starting_BLOCK/2));
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if (Leafed){
            this.setMove(ATTACK_MOVE, Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
            return;
        } else {
            this.setMove(RECOVER_MOVE, Intent.DEFEND_BUFF);
        }

    }

    private void transform ()
    {
        this.Leafed = false;
        CardCrawlGame.sound.playV(SoundEffects.ChargeShift.getKey(),1.6F);

        if (this.hasPower(MetallicizePower.POWER_ID)){
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, MetallicizePower.POWER_ID));
        }
        if (this.hasPower(BarricadePower.POWER_ID)){
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, BarricadePower.POWER_ID));
        }
        if (this.hasPower(StrengthPower.POWER_ID)){
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this, this, StrengthPower.POWER_ID, GainedSTR ));
        }
        this.GainedSTR = 0;
        runAnim(ShiftAnim);
        this.updateHitbox(-25.0F, -20.0F, 125.0f, 125.0f);
        this.updateHealthBar();

        this.nextMove = 1;
        this.setMove(RECOVER_MOVE, Intent.BUFF);
        this.intent = (Intent.BUFF);
        this.createIntent();


    }


    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterMossCharger");
        NAME = monsterMossCharger.monsterStrings.NAME;
        MOVES = monsterMossCharger.monsterStrings.MOVES;
        DIALOG = monsterMossCharger.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
        if (this.Leafed){
            if (this.currentBlock<=0){
                this.transform();
                return;
            }
        } else if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0)){
            runAnim(HitAnim);
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
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(WeakIdle);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterMossCharger character;

        public AnimationListener(monsterMossCharger character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){

            if (!Leafed){
                if ((!animation.name.equals(WeakIdle)))   {
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