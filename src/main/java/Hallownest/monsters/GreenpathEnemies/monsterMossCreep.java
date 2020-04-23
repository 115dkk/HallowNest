package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerPlatedThorns;
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

public class monsterMossCreep extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMossCreep");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte HIDE_MOVE = 0;
    private static final byte WANDER_MOVE = 1;
    private static final byte SPIKES_MOVE = 2;





    //Hornet Values
    private int  Wander_DMG = 3;
    private int  Wander_TIMES = 1;
    private int  Hide_BLOCK = 8;
    private int  Hide_UP = 1;
    private int  Spikes_VAL = 2;






    private boolean Hidden = false;
    private boolean Spiked = false;





    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 34;
    private int minHP = 30;
    private int FullHP;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int chargeCount = 0;

    private int chargeMax = 3;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String HideAnim = "Hide";
    private String WanderAnim = "Wander";
    private String SpikeAnim = "Spikes";
    private String HitAnim = "Hit";

    private String HideIdleAnim = "HideIdle";
    private String HideHitAnim = "HideHit";
    private String HideOutAnim = "HideOut";

    private String QueuedAnim = IdleAnim;

    public monsterMossCreep() {
        this(0.0f, 0.0f);
    }

    public monsterMossCreep(float x) {
        this(x, 0.0f);
    }

    public monsterMossCreep(float x, float y) {
        super(monsterMossCreep.NAME, ID, 25, 0, 0, 100.0f, 175.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/MossCreep/MossCreep.scml");
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
            this.Wander_DMG +=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.chargeCount+=1;
            this.Hide_BLOCK +=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Wander_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        this.FullHP = this.currentHealth;
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x - 25) * Settings.scale;
                this.dialogY = (this.hb_y + 50) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0f));
            }
        }
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case HIDE_MOVE:{
                this.Hidden = true;
                runAnim(HideAnim);
                CardCrawlGame.sound.playV(SoundEffects.MossHide.getKey(),1.4F);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Hide_BLOCK));
                if (this.chargeCount < this.chargeMax){
                    this.Wander_TIMES += this.Hide_UP;
                    this.Hide_BLOCK += this.Hide_UP;
                    this.chargeCount++;
                }
                break;
            }
            case WANDER_MOVE:{
                if (Hidden){
                    this.QueuedAnim = WanderAnim;
                    runAnim(HideOutAnim);
                } else {
                    runAnim(WanderAnim);
                }
                for (int i = 0; i < this.Wander_TIMES; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                break;
            }
            case SPIKES_MOVE:{
                if (Hidden){
                    this.QueuedAnim = SpikeAnim;
                    runAnim(HideOutAnim);
                } else {
                    runAnim(SpikeAnim);
                }
                this.Spiked = true;
                CardCrawlGame.sound.playV(SoundEffects.MossEek.getKey(),1.4F);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerPlatedThorns(this, this.Spikes_VAL),this.Spikes_VAL));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if ((this.currentHealth <= ((this.FullHP/3)*2))&& (!this.Spiked)){
            this.setMove(SPIKES_MOVE, Intent.BUFF);
            return;
        }
        if ((this.numTurns == 1) || lastMove(HIDE_MOVE)){
            this.setMove(WANDER_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Wander_TIMES, true);
        } else {
            this.setMove(HIDE_MOVE, Intent.DEFEND);
        }
    }


    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterMossCreep");
        NAME = monsterMossCreep.monsterStrings.NAME;
        MOVES = monsterMossCreep.monsterStrings.MOVES;
        DIALOG = monsterMossCreep.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            if (!Hidden){
                runAnim(HitAnim);
            } else {
                runAnim(HideHitAnim);
                this.Hidden = false;
            }
        }

    }

    @Override
    public void die() {
            super.die();
            this.stopAnimation();
            this.useFastShakeAnimation(1.0f);
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

        private monsterMossCreep character;

        public AnimationListener(monsterMossCreep character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){

            if (animation.name.equals(HideOutAnim)){
                runAnim(QueuedAnim);
                return;
            }



            if (animation.name.equals(HideAnim)) {
                runAnim(HideIdleAnim);
            } else {
                if ((!animation.name.equals(IdleAnim)) && (!animation.name.equals(HideIdleAnim)))   {
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