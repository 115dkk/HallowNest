package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerFlying;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class minionWingedZoteling extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("minionWingedZoteling");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = minionWingedZoteling.monsterStrings.NAME;
    public static final String[] MOVES = minionWingedZoteling.monsterStrings.MOVES;
    public static final String[] DIALOG = minionWingedZoteling.monsterStrings.DIALOG;

    private static final byte ATTACK_MOVE = 0;
    private static final byte BUFF_MOVE = 1;
    private static final byte FLIGHT_MOVE = 2;
    private static final byte GROUNDED_MOVE = 3;







    //Values
    private int  Scythe_DMG = 4;
    private int  Scythe_HITS = 3;
    private int  Buff_VAL = 1;
    private int  Flight_VAL = 2;
    private boolean isFlying = false;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 50;
    private int maxHP = 54;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String IdleGroundAnim = "Ground";
    private String BuffAnim = "Buff";
    private String AttackAnim = "Attack";
    private String TakeFlightAnim = "Fly";
    private String HitAnim = "Hit";



    public minionWingedZoteling() {
        this(0.0f, 35.0F);
    }

    public minionWingedZoteling(float x) {
        this(x, 35.0f);
    }

    public minionWingedZoteling(float x, float y) {
        super(minionWingedZoteling.NAME, ID, 55, 10.0F, -35.0F, 100.0f, 175.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/GreyPrince/Zotelings/WingedZoteling.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 9)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 3;
            this.maxHP += 3;

        }

        if (AbstractDungeon.ascensionLevel >= 4)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Scythe_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 19)
        {
            this.Flight_VAL+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Scythe_DMG)); // attack 0 damage



    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerFlying(this, Flight_VAL)));
    }


    public void Grounded(){
        this.isFlying = false;
        this.updateHitbox(10.0F, -35.0F, 100.0f, 125.0f);
        this.setMove(GROUNDED_MOVE, Intent.STUN);
        this.createIntent();
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case ATTACK_MOVE:{
                runAnim(AttackAnim);
                CardCrawlGame.sound.playV(SoundEffects.Zoteling01.getKey(),1.3F);

                for (int i = 0; i < this.Scythe_HITS; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL,true));
                }

                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case BUFF_MOVE:{
                runAnim(BuffAnim);
                CardCrawlGame.sound.playV(SoundEffects.Zoteling02.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.Buff_VAL),this.Buff_VAL));

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                break;
            }
            case FLIGHT_MOVE:{
                this.isFlying = true;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerFlying(this, Flight_VAL)));
                runAnim(TakeFlightAnim);
                this.updateHitbox(10.0F, 15.0F, 100.0f, 175.0f);

                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }
            case GROUNDED_MOVE:{

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

        if (!this.isFlying){
            this.setMove(FLIGHT_MOVE, Intent.UNKNOWN);
            return;
        }

        if ((num < 40) && (!this.lastMove(BUFF_MOVE))){
            this.setMove(BUFF_MOVE, Intent.DEBUFF);
        } else {
            this.setMove(ATTACK_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Scythe_HITS, true);
        }
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            if (isFlying){
                runAnim(HitAnim);
            }

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

    public void GroundedresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(IdleGroundAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private minionWingedZoteling character;

        public AnimationListener(minionWingedZoteling character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (!isFlying){
                if (!animation.name.equals(IdleGroundAnim)) {
                    character.GroundedresetAnimation();
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