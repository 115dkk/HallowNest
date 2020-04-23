package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerFlying;
import Hallownest.powers.powerGrandChallenge;
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
import com.megacrit.cardcrawl.powers.FlightPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterMantisYouth extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMantisYouth");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterMantisYouth.monsterStrings.NAME;
    public static final String[] MOVES = monsterMantisYouth.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterMantisYouth.monsterStrings.DIALOG;

    private static final byte SCYTHE_MOVE = 0;
    private static final byte DIVE_MOVE = 1;
    private static final byte FLIGHT_MOVE = 2;
    private static final byte GROUNDED_MOVE = 3;







    //Values
    private int  Scythe_DMG = 5;
    private int  Scythe_HITS = 2;
    private int  Dive_VAL = 1;
    private int  Flight_VAL = 2;
    private boolean isFlying = true;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 42;
    private int minHP = 38;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String IdleGroundAnim = "Idleground";
    private String DiveAnim = "Divebomb";
    private String ScytheAnim = "Scythe";
    private String TakeFlightAnim = "Fly";
    private String HitAnim = "Hit";
    private String HitGroundAnim = "Hitground";



    public monsterMantisYouth() {
        this(0.0f, 100.0F);
    }

    public monsterMantisYouth(float x) {
        this(x, 100.0f);
    }

    public monsterMantisYouth(float x, float y) {
        super(monsterMantisYouth.NAME, ID, 55, 10.0F, 25.0F, 125.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/MantisYouth/MantisYouth.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);
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
            this.Scythe_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
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


    public void Grounded(){
        this.isFlying = false;
        this.updateHitbox(10.0F, -55.0F, 125.0f, 175.0f);
        this.setMove(GROUNDED_MOVE, Intent.STUN);
        this.createIntent();



    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case SCYTHE_MOVE:{
                runAnim(ScytheAnim);
                CardCrawlGame.sound.playV(SoundEffects.MantisYouthScythe.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case DIVE_MOVE:{
                runAnim(DiveAnim);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new WeakPower(p, Dive_VAL, true),Dive_VAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new FrailPower(p, Dive_VAL, true),Dive_VAL));

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                break;
            }
            case FLIGHT_MOVE:{
                this.isFlying = true;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerFlying(this, Flight_VAL)));
                runAnim(TakeFlightAnim);
                this.updateHitbox(10.0F, 25.0F, 125.0f, 200.0f);

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

        if ((num < 70) &&(numTurns <=1)){
            this.setMove(DIVE_MOVE, Intent.DEBUFF);
            return;
        }

        if (((num < 35) && (!this.lastMove(DIVE_MOVE))) || (num < 10)){
            this.setMove(DIVE_MOVE, Intent.DEBUFF);
        } else {
            this.setMove(SCYTHE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Scythe_HITS, true);
        }
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            if (!isFlying){
                runAnim(HitGroundAnim);
            } else {
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

        private monsterMantisYouth character;

        public AnimationListener(monsterMantisYouth character) {
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