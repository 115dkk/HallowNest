package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.monsters.GreenpathEnemies.BossBrokenVessel;
import Hallownest.powers.powerGrandChallenge;
import Hallownest.powers.powerHornetParry;
import Hallownest.powers.powerPlatedThorns;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.DieDieDieEffect;

public class BossMantisLord extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("BossMantisLord");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte COVER_MOVE = 0;
    private static final byte DROP_MOVE = 1;
    private static final byte WIND_SCYTHE_MOVE = 2;
    private static final byte BOW_MOVE = 3;



    //Hornet Values
    private int  Cover_DMG = 10;
    private int  Cover_BLOCK = 10;
    private int  Wind_DMG = 4;
    private int  Wind_HITS = 2;
    private int  Drop_DMG = 5;
    private int  Death_HEAL = 15;
    private int  Death_BUFF = 2;
    private int  Drop_VAL = 1;
    private int  Bow_VAL = 1;


    private int maxHP = 105;
    private int minHP = 95;

    public boolean isPlaying = false;


    //Custom Variables for the backend calculations
    private int numTurns = 0;
    private int bow_timer = 5;
    private int bow_counter = 0;


    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String WindScytheAnim = "WindScythe";
    private String CoverAnim = "CoverStrike";
    private String BowAnim = "Bow";
    private String DropAnim = "Dive";
    private String HitAnim = "Hit";


    public BossMantisLord() {
        this(0.0f, 0.0f, false);
    }

    public BossMantisLord(final float x, final float y, boolean Playmusic) {
        super(BossMantisLord.NAME, ID, 130, 0, 0, 125.0f, 375.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/mantislords/MantisLord.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.85f);
        this.type = EnemyType.BOSS;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;
        this.isPlaying = Playmusic;

        if (AbstractDungeon.ascensionLevel >=9)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 10;
            this.maxHP += 10;

        }
        if (AbstractDungeon.ascensionLevel >=4)
        {

            //this.STORM_TIMES +=1;
            //this.DASH_DMG +=2;
            //this.NEEDLE_DMG+=2;


        }
        if (AbstractDungeon.ascensionLevel >= 19)
        {
           // this.RIPOSTE_BASE += 12;
            //this.SPIKES_BUFF+=1;
        }

        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Cover_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Wind_DMG)); // attack 1 damage
        this.damage.add(new DamageInfo(this, this.Drop_DMG)); //attack 2 damagee


        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {


        if (isPlaying) {
            AbstractDungeon.getCurrRoom().playBgmInstantly("MantisBG");
        }


        //Generic dream nail speech action.
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x -75) * Settings.scale;
                this.dialogY = (this.hb_y+ 100) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0f));
            }
        }


        //mask = FOX_MASK;
    }
    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        switch (this.nextMove) {


            case COVER_MOVE:{
                runAnim(CoverAnim);
                CardCrawlGame.sound.playV(SoundEffects.BossMantisDash.getKey(),1.7F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else if (m != this){
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m,this,Cover_BLOCK));
                    }
                }
                break;
            }
            case WIND_SCYTHE_MOVE:{
                runAnim(WindScytheAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BossMantisWind.getKey()));

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));


                break;
            }
            case DROP_MOVE:{
                //Deal meh damage and apply Weak 1 and Frail 1
                runAnim(DropAnim);
                CardCrawlGame.sound.playV(SoundEffects.BossMantisDrop.getKey(),1.7F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(2), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, Bow_VAL),Bow_VAL));
                break;
            }
            case BOW_MOVE:{
                //Gain Block and stack Plated Throns Power
                runAnim(BowAnim);

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new powerGrandChallenge(p, this, Drop_VAL),Drop_VAL));




                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        bow_counter++;
        if (bow_counter >= bow_timer){
            this.setMove(BOW_MOVE, Intent.DEBUFF);
            bow_counter = 0;
            return;
        }

        if (num < 35 && (!this.lastTwoMoves(WIND_SCYTHE_MOVE))){
            this.setMove(WIND_SCYTHE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base, this.Wind_HITS, true);
            return;
        } else if ((num < 70) && (!this.lastMove(COVER_MOVE))) {
            this.setMove(COVER_MOVE, Intent.ATTACK_DEFEND, (this.damage.get(0)).base);
            return;
        } else if((!this.lastMove(DROP_MOVE)) && !this.lastMoveBefore(DROP_MOVE)) {
            this.setMove(DROP_MOVE, Intent.ATTACK_BUFF, (this.damage.get(2)).base);
            return;
        } else { //do cover strike
            this.setMove(COVER_MOVE, Intent.ATTACK_DEFEND, (this.damage.get(0)).base);
        }
        //otherwise prioritize the parry stance
    }


    public void damage(DamageInfo info)
    {
        super.damage(info);
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (!info.owner.isDying) && !info.owner.isDead && (info.output > 0))
        {
            runAnim(HitAnim);
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:BossMantisLord");
        NAME = BossMantisLord.monsterStrings.NAME;
        MOVES = BossMantisLord.monsterStrings.MOVES;
        DIALOG = BossMantisLord.monsterStrings.DIALOG;
    }

    @Override
    public void die() {
        super.die();
        CardCrawlGame.sound.playV(SoundEffects.BossMantisDie.getKey(),1.4F);
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            stopAnimation();
            useShakeAnimation(5.0F);
            this.onBossVictoryLogic();
            //runAnim("Defeat");
        } else {
            for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (m.isDying) {
                    continue;
                } else if (m != this){
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this, new StrengthPower(m, Death_BUFF),Death_BUFF));
                    AbstractDungeon.actionManager.addToBottom(new HealAction(m,this,Death_HEAL));
                }
            }
        }



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

        private BossMantisLord character;

        public AnimationListener(BossMantisLord character) {
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