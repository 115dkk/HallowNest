package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.*;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.*;

public class BossHornet extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("BossHornet");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte PARRY_MOVE = 0;
    private static final byte DASH_MOVE = 1;
    private static final byte NEEDLE_MOVE = 2;
    private static final byte SPIKES_MOVE = 3;
    private static final byte STORM_MOVE = 4;
    private static final byte RIPOSTE_MOVE = 5;



    //Hornet Values
    private int  STARTING_PLATED_THORNS = 4;
    private int  STORM_TIMES = 3;
    private int  STORM_DMG_EACH = 5;
    private int  STORM_BLOCK_EACH = 5;
    private int  NEEDLE_DMG = 9;
    private int  NEEDLE_DEBUFF = 1;
    private int  SPIKES_BLOCK = 12;
    private int  SPIKES_BUFF = 4;
    private int  DASH_DMG = 7;
    private int  DASH_HITS = 2;
    private int  RIPOSTE_BASE = 17;
    private int  Parry_BLOCK = 10;


    private int riposteTrigger;
    private int RIPOSTE_RETAL;
    private int demonStrength;
    private int lionDamage;



    private int maxHP = 242;
    private int minHP = 234;


    //Custom Variables for the backend calculations
    private Boolean ParryStance = false;
    private Boolean lowHP = false;
    private int numTurns = 0;
    private int spikeTimer = 2;
    private int startinghp;
    private int FullHP;

    //Name of Anims (so you can close program if necessary)
    private String ParryIdleAnim = "ParryIdle";
    private String RiposteAnim = "ParryRiposte";
    private String SpikesAnim = "Spikes";
    private String GossimerAnim = "Hasha";
    private String NeedleAnim = "Needle";
    private String DashAnim = "Dash";


    public BossHornet() {
        this(0.0f, 0.0f);
    }

    public BossHornet(final float x, final float y) {
        super(BossHornet.NAME, ID, 210, 0, 0, 225.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/Hornet/HornetBoss.scml");
        this.type = EnemyType.BOSS;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >=9)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 15;
            this.maxHP += 15;

        }
        if (AbstractDungeon.ascensionLevel >=4)
        {

            this.STORM_TIMES +=1;
            this.DASH_DMG +=2;
            this.NEEDLE_DMG+=2;


        }
        if (AbstractDungeon.ascensionLevel >= 19)
        {
            this.RIPOSTE_BASE += 4;
            this.SPIKES_BUFF+=1;
            this.Parry_BLOCK+=5;
        }

        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.DASH_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.NEEDLE_DMG)); // attack 1 damage
        this.damage.add(new DamageInfo(this, this.STORM_DMG_EACH)); //attack 2 damagee
        this.damage.add(new DamageInfo(this, this.RIPOSTE_BASE)); //attack 3 damagee


        Player.PlayerListener listener = new HornetListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.getCurrRoom().playBgmInstantly("HornetBGM");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new infoHornet(this)));
        this.FullHP = this.currentHealth;
        CardCrawlGame.sound.playV(SoundEffects.VOHornetIntro.getKey(),2.0F);
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerPlatedThorns(this, STARTING_PLATED_THORNS),STARTING_PLATED_THORNS));
        //Generic dream nail speech action.
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x -75) * Settings.scale;
                this.dialogY = (this.hb_y+ 50) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }


        //mask = FOX_MASK;
    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        this.startinghp = this.currentHealth;
        switch (this.nextMove) {


            case PARRY_MOVE:{
                //This Move intent should be overwritten and triggered whenever she takes more than 10% max HP damage in a turn.
                //Apply the Parry Power where she will gain strength when damaged.
                CardCrawlGame.sound.playV(SoundEffects.VOHornetDash.getKey(),1.7F);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerHornetParry(this)));
                break;
            }
            case DASH_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.VOHornetDash.getKey(),1.7F);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXHornetDash.getKey()));
                runAnim(DashAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXHornetDash.getKey()));
                runAnim(DashAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

                //Deal low damage x 2, really this is her primary (hit me some now) opportunity
                runAnim(DashAnim);
                break;
            }
            case NEEDLE_MOVE:{
                //Deal meh damage and apply Weak 1 and Frail 1
                CardCrawlGame.sound.playV(SoundEffects.VOHornetNeedle.getKey(),1.7F);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXHornetNeedle.getKey()));
                runAnim(NeedleAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new FrailPower(p, NEEDLE_DEBUFF, true),NEEDLE_DEBUFF));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new WeakPower(p, NEEDLE_DEBUFF, true),NEEDLE_DEBUFF));
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXHornetCatch.getKey()));
                break;
            }
            case SPIKES_MOVE:{
                //Gain Block and stack Plated Throns Power
                runAnim(SpikesAnim);
                CardCrawlGame.sound.playV(SoundEffects.SFXHornetSpikes.getKey(),1.2F);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.SPIKES_BLOCK));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerPlatedThorns(this, this.SPIKES_BUFF),this.SPIKES_BUFF));
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXHornetCatch.getKey()));

                break;
            }
            case STORM_MOVE:{
                //Damage and Block X Times
                CardCrawlGame.sound.playV(SoundEffects.VOHornetGoss.getKey(),1.2F);

                runAnim(GossimerAnim);
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new DieDieDieEffect(), 0.3f));

                for (int i = 0; i < this.STORM_TIMES; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(2), AbstractGameAction.AttackEffect.NONE,true));
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.STORM_BLOCK_EACH));
                }
                break;
            }
            case RIPOSTE_MOVE:{
                //Strike and Remove Parry Power
                this.RIPOSTE_RETAL = 0;
                if (this.hasPower(powerHornetParry.POWER_ID)){
                    this.RIPOSTE_RETAL += this.getPower(powerHornetParry.POWER_ID).amount;
                }
                runAnim(RiposteAnim);
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, powerHornetParry.POWER_ID));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(3), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this, this, StrengthPower.POWER_ID, this.RIPOSTE_RETAL));
                this.RIPOSTE_BASE+=2;
                this.damage.set(3, new DamageInfo(this, (this.RIPOSTE_BASE)));
                this.ParryStance = false;

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;


        if (this.currentHealth <= (this.maxHealth * .60)) {
            this.spikeTimer++;
            this.lowHP = true;
        } else {
            this.lowHP = false;
            this.spikeTimer = 2;
        }
        //prioritize the riposte follow up
        if (this.lastMove(PARRY_MOVE)) {
            this.setMove(MOVES[RIPOSTE_MOVE],RIPOSTE_MOVE, Intent.ATTACK, (this.damage.get(3)).base);
            return;
        }
        //otherwise prioritize the parry stance
        //finally prioritize turn order and timer stuff.
        if ((this.spikeTimer >= 3) || ((this.lowHP) && this.lastMove(RIPOSTE_MOVE))) {
            this.setMove(MOVES[SPIKES_MOVE], SPIKES_MOVE, Intent.DEFEND_BUFF);
            this.spikeTimer = 0;
            return;
        }

        if ((num < 45) && !this.lastMove(NEEDLE_MOVE) ){
            this.setMove(NEEDLE_MOVE, Intent.ATTACK_DEBUFF, (this.damage.get(1)).base);
        } else if ((num < 75 ) && (!this.lastMove(STORM_MOVE)) && !this.lastMoveBefore(STORM_MOVE)) {
            this.setMove(MOVES[STORM_MOVE],STORM_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(2)).base, this.STORM_TIMES, true);
        } else {
            this.setMove(DASH_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.DASH_HITS, true);
        }
    }


    public void damage(DamageInfo info)
    {
        super.damage(info);
        if (currentHealth > (maxHealth * 0.10)) {
            if ((!this.ParryStance) && ((!this.isDead) || (!this.isDying))) {
                if (startinghp - currentHealth >= (maxHealth * 0.15)) {
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Parry_BLOCK));
                    if (this.hasPower(infoHornet.POWER_ID)){
                        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this , infoHornet.POWER_ID));
                    }
                    ParryStance = true;
                    runAnim(ParryIdleAnim);
                    this.nextMove = PARRY_MOVE;
                    this.setMove(PARRY_MOVE, Intent.MAGIC);
                    this.createIntent();



                }
            }
        }
    }


    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:BossHornet");
        NAME = BossHornet.monsterStrings.NAME;
        MOVES = BossHornet.monsterStrings.MOVES;
        DIALOG = BossHornet.monsterStrings.DIALOG;
    }

    @Override
    public void die() {
        stopAnimation();
        useShakeAnimation(5.0F);
        //runAnim("Defeat");

        super.die();
        this.onBossVictoryLogic();
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation("Idle");
    }

    public void AltresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(ParryIdleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class HornetListener implements Player.PlayerListener {

        private BossHornet character;

        public HornetListener(BossHornet character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (ParryStance) {
                character.AltresetAnimation();
            } else if (!animation.name.equals("Idle")) {
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