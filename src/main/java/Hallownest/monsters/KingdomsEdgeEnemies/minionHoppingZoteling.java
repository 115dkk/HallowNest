package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerExposure;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class minionHoppingZoteling extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("minionHoppingZoteling");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = minionHoppingZoteling.monsterStrings.NAME;
    public static final String[] MOVES = minionHoppingZoteling.monsterStrings.MOVES;
    public static final String[] DIALOG = minionHoppingZoteling.monsterStrings.DIALOG;

    private static final byte SHIVER_MOVE = 0;
    private static final byte HOP_MOVE = 1;
    private static final byte AWKWARD_MOVE = 2;






    //Values
    private int  Shiver_CARDS = 2;
    private int  Hop_DMG = 7;
    private int  Hop_VAL = 1;
    private int  Awkward_PER = 4;






    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 64;
    private int maxHP = 68;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int AwkCounter = 0;
    private int AwkTimer= 2;

    private boolean isDead = false;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String ShiverAnim = "Shiver";
    private String HopAnim = "Hop";
    private String AwkwardAnim = "AwkwardHop";
    private String HitAnim = "Hit";



    public minionHoppingZoteling() {
        this(0.0f, 0.0F);
    }

    public minionHoppingZoteling(float x, float y) {
        super(minionHoppingZoteling.NAME, ID, 130, 0, 0, 100.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/GreyPrince/Zotelings/HoppingZoteling.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 9)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;

        }

        if (AbstractDungeon.ascensionLevel >= 19)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Awkward_PER-=2;
        }


        /*
        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Dance_VAL+=1;
            add a healing to the smash
        }
        */

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
        this.damage.add(new DamageInfo(this, this.Hop_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case SHIVER_MOVE:{
                runAnim(ShiverAnim);
                CardCrawlGame.sound.playV(SoundEffects.Zoteling03.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Dazed(), this.Shiver_CARDS,true,false));
                break;
            }
            case HOP_MOVE:{
                runAnim(HopAnim);
                CardCrawlGame.sound.playV(SoundEffects.Zoteling02.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT,true));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new powerExposure(p, this, Hop_VAL), this.Hop_VAL));

                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }
            case AWKWARD_MOVE:{
                runAnim(AwkwardAnim);
                int HPVal = (this.currentHealth/this.Awkward_PER);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BeeBuff.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.Zoteling02.getKey(),1.3F);
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m != null && !m.isDying && m.id == BossGreyPrinceZote.ID) {
                        AbstractDungeon.actionManager.addToBottom(new HealAction(m,this, (HPVal*2)));
                    }
                }
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this,this, HPVal, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            }


        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        if (numTurns > 2){
            this.AwkCounter++;
        }

        if (AwkCounter >= AwkTimer){
            this.setMove(AWKWARD_MOVE, Intent.BUFF);
            this.AwkCounter = 0;
            return;
        }

        if ((num < 66) && (!this.lastMove(HOP_MOVE))){
            this.setMove(HOP_MOVE,Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        } else {
            this.setMove(SHIVER_MOVE, Intent.DEBUFF);
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

        private minionHoppingZoteling character;

        public AnimationListener(minionHoppingZoteling character) {
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