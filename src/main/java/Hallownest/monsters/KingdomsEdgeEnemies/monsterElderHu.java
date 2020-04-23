package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.infoHiveGuardian;
import Hallownest.powers.powerHivesBlood;
import Hallownest.powers.powerSageRings;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveAllPowersAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterElderHu extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterElderHu");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterElderHu.monsterStrings.NAME;
    public static final String[] MOVES = monsterElderHu.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterElderHu.monsterStrings.DIALOG;


    private static final byte SLAM_MOVE = 0;






    //Values
    private int  Plates_VAL = 4;
    private int  Ring_BLOCK = 12;
    private int  Ring_HEAL = 10;








    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 54;
    private int maxHP = 58;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SlamAnim = "Slam";
    private String HitAnim = "Hit";



    public monsterElderHu() {
        this(0.0f, 0.0F);
    }

    public monsterElderHu(float x, float y) {
        super(monsterElderHu.NAME, ID, 130, 0, 0, 125.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/ElderHu/ElderHu.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(1.00f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 4;
            this.maxHP += 4;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Ring_BLOCK+=1;
        }



        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Ring_HEAL+=5;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerSageRings(this, this.Ring_HEAL), this.Ring_HEAL));
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case SLAM_MOVE:{
                runAnim(SlamAnim);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BeeBuff.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.HuRings.getKey(),1.3F);
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m,this, Ring_BLOCK));
                        if (m.currentHealth < (m.maxHealth)){
                            int missingHP = ((m.maxHealth - m.currentHealth));
                            //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this, new WeakPower(m, 1, true), 1));
                            AbstractDungeon.actionManager.addToBottom(new HealAction(m,this, ((missingHP * this.Ring_HEAL)/100)));
                        }


                    }
                }
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        this.setMove(MOVES[SLAM_MOVE],SLAM_MOVE,Intent.DEFEND);
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

        private monsterElderHu character;

        public AnimationListener(monsterElderHu character) {
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