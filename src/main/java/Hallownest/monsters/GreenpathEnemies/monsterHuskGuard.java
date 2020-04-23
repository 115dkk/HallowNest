package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
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

public class monsterHuskGuard extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterHuskGuard");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte FLOP_MOVE = 0;
    private static final byte CHARGE_MOVE = 1;
    private static final byte SMASH_MOVE = 2;




    //Hornet Values
    private int  Flop_DMG = 7;
    private int  Flop_BLOCK = 7;
    private int  Smash_DMG = 17;
    private int  ChargePlus = 1;

    private boolean RePositioner = false;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 50;
    private int minHP = 44;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int chargeCount = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String FlopAnim = "Flop";
    private String SmashAnim = "Smash";
    private String HitAnim = "Hit";


    public monsterHuskGuard() {
        this(0.0f, 0.0f);
    }

    public monsterHuskGuard(final float x, final float y) {
        super(monsterHuskGuard.NAME, ID, 45, 0, 0, 200.0f, 325.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/HuskGuard/HuskGuard.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Flop_BLOCK+= 1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.ChargePlus+=1;
        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Flop_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Smash_DMG)); // attack 0 damage
        
    }

    @Override
    public void usePreBattleAction() {
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x) * Settings.scale;
                this.dialogY = (this.hb_y) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }

        CardCrawlGame.sound.playV(SoundEffects.VOGuardHello.getKey(),1.4F);
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case FLOP_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.SFXGuardLand.getKey(),1.4F);
                runAnim(FlopAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Flop_BLOCK));
                break;
            }
            case CHARGE_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXGuardCharge.getKey()));
                break;
            }
            case SMASH_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXMotherSwarm.getKey()));
                runAnim(SmashAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        this.damage.set(1, new DamageInfo(this, (this.Smash_DMG + this.chargeCount)));
        if (lastMove(CHARGE_MOVE)){
            this.setMove(SMASH_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
            return;
        }
        if (numTurns == 1 || (num < 55) && (!lastTwoMoves(FLOP_MOVE))){
            this.setMove(FLOP_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
        } else {
            this.setMove(MOVES[CHARGE_MOVE],CHARGE_MOVE, Intent.UNKNOWN);
            this.chargeCount += this.ChargePlus;
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterHuskGuard");
        NAME = monsterHuskGuard.monsterStrings.NAME;
        MOVES = monsterHuskGuard.monsterStrings.MOVES;
        DIALOG = monsterHuskGuard.monsterStrings.DIALOG;
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

        private monsterHuskGuard character;

        public AnimationListener(monsterHuskGuard character) {
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