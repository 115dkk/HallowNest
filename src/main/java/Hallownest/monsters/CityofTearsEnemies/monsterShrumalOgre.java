package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.cards.status.Swarmed;
import Hallownest.powers.powerInfection;
import Hallownest.powers.powerTragicImmortality;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedProjectileEffect;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterShrumalOgre extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterShrumalOgre");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterShrumalOgre.monsterStrings.NAME;
    public static final String[] MOVES = monsterShrumalOgre.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterShrumalOgre.monsterStrings.DIALOG;

    private static final byte THRASH_MOVE = 0;
    private static final byte SPIT_MOVE = 1;
    private static final byte SCARED_MOVE = 2;
    private static final byte JUMP_MOVE = 3;





    //Values
    private int  Thrash_DMG = 6;
    private int  Thrash_HITS = 2;
    private int  ThrashAnims = 0;
    private int  Jump_VAL = 1;
    private int  Scared_VAL = 3;
    private int  Scared_BLOCK = 9;
    private int  Spit_INF = 5;
    private int  Spit_Val = 1;


    private int  ThrashTimer = 0;
    private int  ThrashLimit = 3;

    private int  Thrashcountdown = 0;
    private int  ThrashTurnTimer = 3;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 110;
    private int minHP = 100;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String EmpowerAnim = "Empower";
    private String ThrashAnim = "Thrash";
    private String SpitAnim = "Spit";
    private String ScaredAnim = "Scared";
    private String HitAnim = "Hit";



    public monsterShrumalOgre() {
        this(0.0f, 0.0F);
    }

    public monsterShrumalOgre(float x, float y) {
        super(monsterShrumalOgre.NAME, ID, 130, 0, 0, 175.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/ShrumalOgre/ShrumalOgre.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 8;
            this.maxHP += 8;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Spit_INF+=1;
            this.Thrash_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.ThrashLimit-=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Thrash_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
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

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case THRASH_MOVE:{
                this.ThrashAnims = (this.Thrash_HITS -1);
                runAnim(ThrashAnim);

                for (int i = 0; i < this.Thrash_HITS; ++i) {
                    CardCrawlGame.sound.playV(SoundEffects.OgreThrash.getKey(),1.3F);

                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3f));

                }
                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case SPIT_MOVE:{
                runAnim(SpitAnim);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedProjectileEffect(this.hb.cX, this.hb.cY, 0.1f)));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new WeakPower(p,Spit_Val,true),Spit_Val));
                AbstractDungeon.actionManager.addToBottom(new ApplyInfectionAction(p, this, Spit_INF));





                break;
            }
            case SCARED_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this, new PlatedArmorPower(this,Scared_VAL),Scared_VAL));

                break;
            }
            case JUMP_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, Scared_BLOCK));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new VulnerablePower(p,Jump_VAL,true),Jump_VAL));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        this.Thrashcountdown++;
        this.ThrashTimer++;
        if (this.ThrashTimer >= this.ThrashLimit){
            this.Thrash_HITS++;
            this.ThrashTimer = 0;
        }

        if ((this.numTurns ==1) || (this.Thrashcountdown >= this.ThrashTurnTimer)) {
            this.setMove(THRASH_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Thrash_HITS, true);
            this.Thrashcountdown = 0;
            return;
        }

        if ((num < 35 ) && (!this.lastMove(JUMP_MOVE)) && (!this.lastMoveBefore(JUMP_MOVE))){
            this.setMove(JUMP_MOVE, Intent.DEFEND_DEBUFF);
        }  else if ((num < 60) && this.lastMove(SCARED_MOVE)){
            this.setMove(SCARED_MOVE, Intent.BUFF);
        } else {
            this.setMove(SPIT_MOVE, Intent.DEBUFF);
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

    public void ThrashingAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(ThrashAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterShrumalOgre character;

        public AnimationListener(monsterShrumalOgre character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){

            if ((animation.name.equals(ThrashAnim)) && ThrashAnims > 0){
                character.ThrashingAnimation();
                ThrashAnims--;
                return;
            }
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