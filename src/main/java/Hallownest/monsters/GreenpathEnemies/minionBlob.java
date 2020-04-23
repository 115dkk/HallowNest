package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.powers.powerInfection;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedEffect;
import Hallownest.vfx.InfectedProjectileEffect;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class minionBlob extends CustomMonster {
    public static final String ID = HallownestMod.makeID("minionBlob");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;




    private int maxHP = 22;
    private int minHP = 18;


    private static final byte INFECT_MOVE = 0;
    private static final byte POP_MOVE = 1;


    private String IdleAnim = "Idle";
    private String InfectAnim = "Infect";
    private String PopAnim = "Pop";

    private int numTurns = 0;

    private int PopTurns = 2;
    private int Infect_VAL = 2;
    private int Pop_Buff = 1;
    private int Pop_Chance = 15;

    public minionBlob() {
        this(0.0f, 0.0f);
    }
    public minionBlob(float x, float y) {
        super(NAME, ID, 20, 0.0F, 50.0F, 100.0F, 175.0F, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/BrokenVessel/Blob.scml");
        this.type = EnemyType.NORMAL;

        setHp(this.minHP,this.maxHP);

        Player.PlayerListener listener = new minionBlob.BlobListender(this);
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
            case INFECT_MOVE:
                runAnim(InfectAnim);
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedProjectileEffect(this.hb.cX, this.hb.cY, 0.1f)));
                AbstractDungeon.actionManager.addToBottom(new ApplyInfectionAction(p, this, Infect_VAL));
                //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new powerInfection(p, this,Infect_VAL),Infect_VAL));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedEffect(), 0.05f));



                break;
            case POP_MOVE:
                runAnim(PopAnim);

                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (m.isDying) {
                    continue;
                    } else if (m.id ==BossBrokenVessel.ID){
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this, new StrengthPower(m, Pop_Buff),Pop_Buff));
                    }
                }
                CardCrawlGame.sound.playV(SoundEffects.SFXBlobPop.getKey(),2.0F);


                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this));
            break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        numTurns++;
        if ((numTurns > PopTurns) && (num < +(numTurns * Pop_Chance))){
            this.setMove(POP_MOVE, Intent.MAGIC);
        } else {
            this.setMove(INFECT_MOVE, Intent.DEBUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:minionBlob");
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

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class BlobListender implements Player.PlayerListener {

        private minionBlob character;

        public BlobListender(minionBlob character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (animation.name.equals(PopAnim)){
                stopAnimation();
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

