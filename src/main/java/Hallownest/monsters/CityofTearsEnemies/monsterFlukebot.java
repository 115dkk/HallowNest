package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.Swarmed;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterFlukebot extends CustomMonster {
    public static final String ID = HallownestMod.makeID("monsterFlukebot");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;




    private int maxHP = 30;
    private int minHP = 28;


    private static final byte ATTACK_MOVE = 0;
    private static final byte BUFF_MOVE = 1;


    private String IdleAnim = "Idle";
    private String BuffAnim = "Buff";
    private String AttackAnim = "Attack";
    private String HitAnim = "Hit";

    private int numTurns = 0;

    private int Buff_VAL = 2;
    private int Attack_DMG = 6;
    private int Attack_HITS = 2;
    private int Attack_VAL = 1;

    public monsterFlukebot() {
        this(0.0f, 0.0f);
    }
    public monsterFlukebot(float x, float y) {
        super(NAME, ID, 26, 0.0F, 0.0F, 100.0F, 125.0F, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/Flukemon/Flukebott.scml");
        this.type = EnemyType.NORMAL;

        setHp(this.minHP,this.maxHP);

        this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;

        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Attack_DMG +=1;
        }


        Player.PlayerListener listener = new monsterFlukebot.FLyListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }



    @Override
    public void die(boolean triggerRelics) {
        super.die(false);

    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        switch(this.nextMove) {
            case ATTACK_MOVE: {
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXFlyPoon.getKey()));
                runAnim(AttackAnim);
                CardCrawlGame.sound.playV(SoundEffects.FlukeAttack2.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new WeakPower(p, Attack_VAL, true), Attack_VAL));


                break;
            }

            case BUFF_MOVE: {
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXFlyAttack.getKey()));
                runAnim(BuffAnim);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, Buff_VAL), Buff_VAL));
                break;
            }
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
        if ((!this.lastTwoMoves(ATTACK_MOVE))){
            this.setMove(ATTACK_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Attack_HITS, true);
        } else {
            this.setMove(BUFF_MOVE, Intent.BUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterFlukebot");
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

    public class FLyListener implements Player.PlayerListener {

        private monsterFlukebot character;

        public FLyListener(monsterFlukebot character) {
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
