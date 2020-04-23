package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.Swarmed;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.RemoveAllTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;

public class minionFly extends CustomMonster {
    public static final String ID = HallownestMod.makeID("minionFly");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;




    private int maxHP = 12;
    private int minHP = 9;


    private static final byte POON_MOVE = 0;
    private static final byte SWARM_MOVE = 1;


    private String IdleAnim = "Idle";
    private String PoonAnim = "Poon";
    private String SwarmAnim = "Swarm";
    private String HitAnim = "Hit";
    private String DeadAnim = "Dead";

    private int numTurns = 0;

    private int Poon_DMG = 12;
    private int Poon_Chance = 25;

    public minionFly() {
        this(0.0f, 0.0f);
    }
    public minionFly(float x, float y) {
        super(NAME, ID, 16, 0.0F, 0.0F, 100.0F, 200.0F, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/VengeflyKing/Vengefly.scml");
        this.type = EnemyType.NORMAL;

        setHp(this.minHP,this.maxHP);

        this.damage.add(new DamageInfo(this, this.Poon_DMG)); // attack 0 damage


        Player.PlayerListener listener = new minionFly.FLyListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }



    @Override
    public void die() {
        super.die();

    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        switch(this.nextMove) {
            case POON_MOVE:
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXFlyPoon.getKey()));
                runAnim(PoonAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new RemoveAllTemporaryHPAction(this, this));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this));


                break;
            case SWARM_MOVE:
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXFlyAttack.getKey()));
                runAnim(SwarmAnim);
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Swarmed(),1));

            break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }
    public void damage(DamageInfo info)
    {
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (!info.owner.isDying) && !info.owner.isDead && (info.output > 0))
        {
            runAnim(HitAnim);
        }
    }

    @Override
    protected void getMove(int num) {
        this.numTurns++;
        if (num < +((this.numTurns -1) * Poon_Chance)){
            this.setMove(POON_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else {
            this.setMove(SWARM_MOVE, Intent.DEBUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:minionFly");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }


    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(IdleAnim);
    }

    public void AltresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(DeadAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class FLyListener implements Player.PlayerListener {

        private minionFly character;

        public FLyListener(minionFly character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (animation.name.equals(PoonAnim)){
                character.AltresetAnimation();
            }else if (!animation.name.equals(IdleAnim)) {
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

