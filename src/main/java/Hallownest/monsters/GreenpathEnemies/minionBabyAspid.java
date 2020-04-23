package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.powerInfection;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class minionBabyAspid extends CustomMonster {
    public static final String ID = HallownestMod.makeID("minionBabyAspid");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;




    private int maxHP = 11;
    private int minHP = 8;


    private static final byte PROTECT_MOVE = 0;
    private static final byte DESTROY_MOVE = 1;


    private String IdleAnim = "Idle";
    private String ProtectAnim = "Protect";
    private String DestroyAnim = "Destroy";
    private String HitAnim = "Hit";

    private int numTurns = 0;

    private int Protect_BLOCK = 4;
    private int Protect_DEBUFF1 = 1;
    private int Protect_DEBUFF2 = 2;
    private int Destroy_DMG = 5;

    public minionBabyAspid() {
        this(0.0f, 0.0f);
    }
    public minionBabyAspid(float x, float y) {
        super(NAME, ID, 16, 0.0F, 0.0F, 75.0F, 100.0F, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/AspidMother/BabyAspid.scml");
        this.type = EnemyType.NORMAL;


        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Protect_BLOCK += 1;
            this.Destroy_DMG += 1;
        }

        setHp(this.minHP,this.maxHP);

        this.damage.add(new DamageInfo(this, this.Destroy_DMG)); // attack 0 damage


        Player.PlayerListener listener = new minionBabyAspid.FLyListener(this);
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
            case PROTECT_MOVE:
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXBabyProtect.getKey()));
                runAnim(ProtectAnim);
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else if (m.id ==monsterAspidMother.ID){
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m,Protect_BLOCK));
                    }
                }
                int protect_Val = AbstractDungeon.monsterHpRng.random(Protect_DEBUFF1, Protect_DEBUFF2);
                AbstractDungeon.actionManager.addToBottom(new ApplyInfectionAction(p, this, protect_Val));
                break;
            case DESTROY_MOVE:
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXBabyDestroy.getKey()));
                runAnim(DestroyAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

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
        if ((num < 60) && (!lastTwoMoves(DESTROY_MOVE))){
            this.setMove(DESTROY_MOVE, Intent.ATTACK,((DamageInfo) this.damage.get(0)).base);
        }else {
            this.setMove(PROTECT_MOVE, Intent.DEFEND_DEBUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:minionBabyAspid");
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

        private minionBabyAspid character;

        public FLyListener(minionBabyAspid character) {
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

