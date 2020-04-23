package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.Swarmed;
import Hallownest.powers.powerInfection;
import Hallownest.powers.powerSpewing;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedEffect;
import Hallownest.vfx.InfectedProjectileEffect;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;

public class BossBrokenVessel extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("BossBrokenVessel");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte SEAL_MOVE = 0;
    private static final byte FLAIL_MOVE = 1;
    private static final byte SLASH_MOVE = 2;
    private static final byte CASCADE_MOVE = 3;
    private static final byte SLAM_MOVE = 4;



    //Hornet Values
    private int  Seal_Multiplier = 2;
    private int  Seal_Limit = 10;
    private int  Flail_TIMES = 3;
    private int  Flail_DMG = 4;
    private int  Slash_DMG = 6;
    private int  Slash_DEBUFF = 1;
    private int  Cascade_TIMES = 2;
    private int  Cascade_INF = 4;
    private int  Cascade_VULN = 1;
    private int  Slam_DMG = 8;
    private int  Slam_SPAWNS = 1;
    private int  Spew_Timer = 2;
    private int  Spew_Counter = 0;
    private int  Cascade_Counter = 0;

    private int riposteTrigger;
    private int RIPOSTE_RETAL;
    private int demonStrength;
    private int lionDamage;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 230;
    private int minHP = 220;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int cascadeTimer = 5;
    private int flailTimer = 3;
    private int infectionLimit = 10;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SlashAnim = "Slash";
    private String HeadbangAnim = "HeadBang";
    private String SlamAnim = "Slam";
    private String FlailAnim = "Flail";
    private String SealAnim = "Seal";
    private String DashAnim = "Dash";


    public static final float[] POSX;
    public static final float[] POSY;

    private int SpawnLimit = 4;
    private AbstractMonster[] Spawned = new AbstractMonster[5];



    public BossBrokenVessel() {
        this(0.0f, 0.0f);
    }

    public BossBrokenVessel(final float x, final float y) {
        super(BossBrokenVessel.NAME, ID, 230, 0, 0, 250.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/BrokenVessel/BrokenVessel.scml");
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

            this.Flail_TIMES +=1;
            this.Slam_DMG +=2;
            this.Slash_DMG+=2;


        }
        if (AbstractDungeon.ascensionLevel >= 19)
        {
            this.Spew_Timer -= 1;
        }

        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Flail_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Slash_DMG)); // attack 1 damage
        this.damage.add(new DamageInfo(this, this.Slam_DMG)); //attack 2 damagee


        Player.PlayerListener listener = new VesselListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    private int numBalloons() {
        int count = 0;
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != null && m != this && !m.isDying) {
                ++count;
            }
        }
        return count;
    }

    private void SpawnBalloon() {
        int i = 0;
        boolean hasSpawned = false;
        boolean hasSwarmed = false;

        while ((!hasSwarmed) && !hasSpawned){
            if (i > SpawnLimit ) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Swarmed(),1));
                hasSwarmed = true;
            } else if (this.Spawned[i] == null || this.Spawned[i].isDeadOrEscaped()) {
                minionBlob minionToSpawn = new minionBlob(POSX[i], POSY[i]);
                this.Spawned[i] = minionToSpawn;
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(minionToSpawn, true));
                hasSpawned = true;
            }
            i++;
        }

    }

    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("VesselBGM");
        CardCrawlGame.sound.playV(SoundEffects.VOBrkSeal.getKey(),2.0F);
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerSpewing(this, this.Spew_Timer+1)));
        //mask = FOX_MASK;
    }
    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.
        if (Spew_Counter >= Spew_Timer){
            SpawnBalloon();
            Spew_Counter = 0;
        } else {
            Spew_Counter++;
        }


        switch (this.nextMove) {


            case SEAL_MOVE:{
                //Whenever the player has more thna 10 Infection, the next turn the Broken Vessel will absorb it all and heal for 2x that amount.
                //It's kinda a self check for letting the boss stack up too much infection unlimited. So this is way better.

                runAnim(SealAnim);
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new IntenseZoomEffect(this.hb.cX, this.hb.cY, true), 0.2f, true));
                CardCrawlGame.sound.playV(SoundEffects.VOBrkSeal.getKey(),1.5F);
                //NEW SFX
                if (p.hasPower(powerInfection.POWER_ID)){
                    int newhp = p.getPower(powerInfection.POWER_ID).amount;
                    AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p,this,powerInfection.POWER_ID));
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new FlyingOrbEffect(p.hb.cX , p.hb.cY), 0.05F));
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new FlyingOrbEffect(p.hb.cX , p.hb.cY), 0.1F));

                    AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, (newhp * Seal_Multiplier)));
                }
                break;
            }
            case FLAIL_MOVE:{

                runAnim(FlailAnim);
                for (int i = 0; i < this.Flail_TIMES; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL,true));
                }
                break;
            }
            case SLASH_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXBrkDash.getKey()));
                runAnim(SlashAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new FrailPower(p, Slash_DEBUFF, true),Slash_DEBUFF));
                break;
            }
            case CASCADE_MOVE:{
                runAnim(HeadbangAnim);
                CardCrawlGame.sound.playV(SoundEffects.SFXBrkCascade.getKey(),1.2F);
                for (int i = 0; i < this.Cascade_TIMES; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedEffect(), 0.00f));
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedProjectileEffect(this.hb.cX, this.hb.cY, 0.00f)));

                    AbstractDungeon.actionManager.addToBottom(new ApplyInfectionAction(p, this, this.Cascade_INF));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new VulnerablePower(p, Cascade_VULN, true),Cascade_VULN));
                }

                //Maybe have a Infection Effect?
                break;
            }
            case SLAM_MOVE:{

                CardCrawlGame.sound.playV(SoundEffects.SFXBrkSlam.getKey(),1.2F);
                runAnim(SlamAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(2), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                SpawnBalloon();

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        AbstractPlayer p = AbstractDungeon.player;
        this.numTurns++;
        this.Cascade_Counter++;

        if ((p.hasPower(powerInfection.POWER_ID)) && !lastMove(SEAL_MOVE)){
            if (p.getPower(powerInfection.POWER_ID).amount > Seal_Limit){
                this.setMove(MOVES[SEAL_MOVE],SEAL_MOVE, Intent.UNKNOWN);
                return;
            }
        }

        if (this.numTurns > 3) {
            this.flailTimer++;
        }

        if (this.Cascade_Counter >= 5){
            this.setMove(MOVES[CASCADE_MOVE],CASCADE_MOVE, Intent.STRONG_DEBUFF);
            this.Cascade_Counter = 0;
            return;
        }


        if (this.flailTimer >=4){
            this.setMove(FLAIL_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Flail_TIMES, true);
            this.flailTimer = 0;
            return;
        }

        if ((numBalloons() <=1) && (num % 2 == 0)){
            this.setMove(SLAM_MOVE, Intent.ATTACK_BUFF, (this.damage.get(2)).base);

        } else {
            this.setMove(SLASH_MOVE, Intent.ATTACK_DEBUFF, (this.damage.get(1)).base);

        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:BossBrokenVessel");
        NAME = BossBrokenVessel.monsterStrings.NAME;
        MOVES = BossBrokenVessel.monsterStrings.MOVES;
        DIALOG = BossBrokenVessel.monsterStrings.DIALOG;
        POSX = new float[]{325.0F, -200.0F, 200.0F, -325.0F, -600.0F, 0.0F};
        POSY = new float[]{75.0F, 150.0F, 150.0F, 75.0F, 20.0f, 175.0f};
    }

    @Override
    public void die() {
        stopAnimation();
        useShakeAnimation(5.0F);
        //runAnim("Defeat");
        super.die();
        for (final AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDying) {
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
            }
        }
        this.onBossVictoryLogic();
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

    public class VesselListener implements Player.PlayerListener {

        private BossBrokenVessel character;

        public VesselListener(BossBrokenVessel character) {
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