package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.StolenSoul;
import Hallownest.cards.status.Swarmed;
import Hallownest.powers.powerReadjusting;
import Hallownest.powers.powerTragicImmortality;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterMistake extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMistake");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterMistake.monsterStrings.NAME;
    public static final String[] MOVES = monsterMistake.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterMistake.monsterStrings.DIALOG;

    private static final byte WHIMPER_MOVE = 0;
    private static final byte CHARGE_MOVE = 1;
    private static final byte NO_MOVE = 2;





    //Values
    private int  Charge_DMG = 10;
    private int  Whimper_CARDS = 1;
    private int  Regen_PER = 2;
    public int nextRegen = 0;

    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 60;
    private int minHP = 56;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String RegenAnim = "Regen";
    private String ChargeAnim = "Attack";
    private String WhimperAnim = "Whimper";
    private String HitAnim = "Hit";



    public monsterMistake() {
        this(0.0f, 0.0F);
    }

    public monsterMistake(float x, float y) {
        super(monsterMistake.NAME, ID, 26, 0, 0, 150.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/Mistakes/Mistake.scml");
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
            this.Charge_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Regen_PER+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Charge_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerTragicImmortality(this, this.Regen_PER)));
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
    public void TriggerRegen(){
        runAnim(RegenAnim);
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case WHIMPER_MOVE:{
                runAnim(WhimperAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.MistakeSound2.getKey(),2.2f));
                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new StolenSoul(), Whimper_CARDS));

                break;
            }
            case CHARGE_MOVE:{
                runAnim(ChargeAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.MistakeSound.getKey(),2.2f));


                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));



                break;
            }
            case NO_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                if (!this.hasPower(powerTragicImmortality.POWER_ID)){
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerTragicImmortality(this, Regen_PER)));
                }

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        if (this.numTurns !=1) {
            if (!this.hasPower(powerTragicImmortality.POWER_ID)) {
                this.setMove(NO_MOVE, Intent.UNKNOWN);
                return;
            }
        }
        //if (numTurns == 6){
        //this.setMove(SECRET_MOVE, Intent.MAGIC);
        //}
        int phealthdown = (100 * ((AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth) / AbstractDungeon.player.maxHealth)/3);
        if ((num<phealthdown) && (!this.lastMove(NO_MOVE)) && (this.numTurns !=1)){
            this.setMove(NO_MOVE, Intent.STUN);
        }else if (((num < 50) && (!this.lastTwoMoves(CHARGE_MOVE))) || (this.lastMove(NO_MOVE))){
            this.setMove(CHARGE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else {
            this.setMove(WHIMPER_MOVE, Intent.DEBUFF);
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

    @Override
    public void die() {
        this.stopAnimation();
        useShakeAnimation(1.0F);
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

    public class AnimationListener implements Player.PlayerListener {

        private monsterMistake character;

        public AnimationListener(monsterMistake character) {
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