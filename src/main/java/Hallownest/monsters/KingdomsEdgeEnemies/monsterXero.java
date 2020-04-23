package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerGrandChallenge;
import Hallownest.powers.powerNailReturn;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterXero extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterXero");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterXero.monsterStrings.NAME;
    public static final String[] MOVES = monsterXero.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterXero.monsterStrings.DIALOG;

    private static final byte ATTACK_MOVE = 0;
    private static final byte DEBUFF_MOVE = 1;





    //Values
    private int  Attack_DMG = 7;
    private int  Attack_VAL = 1;
    private int  Debuff_VAL = 1;
   



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int minHP = 60;
    private int maxHP = 64;



    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String AttackAnim = "Attack";
    private String DebuffAnim = "Attack2";
    private String HitAnim = "Hit";



    public monsterXero() {
        this(0.0f, 0.0F);
    }

    public monsterXero(float x, float y) {
        super(monsterXero.NAME, ID, 55, 0, 0, 150.0f, 325.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/Xero/Xero.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(1.00f);
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
            this.Attack_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Debuff_VAL+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage



    }

    @Override
    public void usePreBattleAction() {
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case ATTACK_MOVE:{
                runAnim(AttackAnim);
                CardCrawlGame.sound.playV(SoundEffects.XeroOne.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new powerNailReturn(p, this,  this.damage.get(0).output), this.damage.get(0).output));

                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case DEBUFF_MOVE:{
                runAnim(DebuffAnim);
                CardCrawlGame.sound.playV(SoundEffects.XeroTwo.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new FrailPower(p, this.Debuff_VAL, true), this.Debuff_VAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new WeakPower(p, this.Attack_VAL, true), this.Attack_VAL));

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if (this.numTurns == 1 ){
            this.setMove(ATTACK_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
            return;
        }
        if (this.lastMove(ATTACK_MOVE)){
                this.setMove(DEBUFF_MOVE, Intent.DEBUFF);
        } else {
                this.setMove(ATTACK_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
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

        private monsterXero character;

        public AnimationListener(monsterXero character) {
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