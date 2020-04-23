package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.CollectCardAction;
import Hallownest.actions.CollectedMinionAction;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.Obsession;
import Hallownest.cards.status.Swarmed;
import Hallownest.monsters.GreenpathEnemies.minionFly;
import Hallownest.monsters.GreenpathEnemies.monsterBaldur;
import Hallownest.powers.powerCollectedCard;
import Hallownest.powers.powerPrecepts;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.powers.StrengthPower;


public class BossGreyPrinceZote extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("BossGreyPrinceZote");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte SPIT_MOVE = 0;
    private static final byte SHADOW_MOVE = 1;
    private static final byte HOP_MOVE = 2;
    private static final byte FLAIL_MOVE = 3;
    private static final byte BOMB_MOVE = 4;
    


    //might change how his orb empowers his attacks for both clarity and counterplay. Give him a power that makes him gain strength whenever you draw (or maybe play) a Stolen Soul


    private int  Hop_DMG= 12;
    private int  Hop_BLOCK= 14;

    private int  Shadow_VAL =1;

    private int  Flail_DMG = 9;
    private int  Flail_HITS = 2;

    private int  Bombs_VAL = 3 ;

    private int SpawnCounter = 1;
    private int SpawnTimer = 3;

    private boolean bombtime = false;

    private int turnStrength;

    public static final float[] POSX;
    public static final float[] POSY;
    private AbstractMonster[] Spawned = new AbstractMonster[5];

    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 390;
    private int maxHP = 400;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String HopAnim = "Hop";
    private String ShadowSlamAnim = "ShadowSlam";
    private String FlailAnim = "Flail";
    private String SpitAnim = "Spit";
    private String BombAnim = "BombSummon";
    private String HitAnim = "Hit";


    public BossGreyPrinceZote() {
        this(0.0f, 0.0f);
    }

    public BossGreyPrinceZote(final float x, final float y) {
        super(BossGreyPrinceZote.NAME, ID, 85, 0, 0, 175.0f, 350.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/GreyPrince/GreyPrinceZote.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.95f);

        this.type = EnemyType.BOSS;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        this.dialogY -= (this.hb_y + 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 9) // Elites are tougher at Asc 8
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 15;
            this.maxHP += 15;
        }
        if (AbstractDungeon.ascensionLevel >=4) //Elites are deadlier at 3
        {
            //increases the power of his multihit and debufff for higher ascensions
            this.Hop_BLOCK+= 2;
            this.Flail_DMG+=2;
        }
        if (AbstractDungeon.ascensionLevel >= 19) //18 says elites have harder move sets so do something fancy
        {
            //??
            this.Bombs_VAL+=1;
            this.Shadow_VAL+=1;
        }


        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Hop_DMG));
        this.damage.add(new DamageInfo(this, this.Flail_DMG)); // attack 0 damage// attack 0 damage


        Player.PlayerListener listener = new AnimationInfection(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }


    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("GPZoteBGM");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this,new powerPrecepts(this,DIALOG.length)));

    }

    public void Precept(int PreceptsLeft){
        int dialogoptions = DIALOG.length;
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[dialogoptions - PreceptsLeft], 1.5f, 1.5f));
    }
    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {


            case SPIT_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingSwoop.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.SpitSound.getKey(),1.2F);
                runAnim(SpitAnim);
                this.SpawnZotelings();
                this.SpawnZotelings();
                
                break;
            }
            case SHADOW_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.ZoteShadow.getKey()));
                runAnim(ShadowSlamAnim);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new IntangiblePower(this,this.Shadow_VAL),this.Shadow_VAL));
                break;
            }
            case HOP_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.ZoteHop.getKey()));
                runAnim(HopAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.Hop_BLOCK));

                break;
            }
            case FLAIL_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.ZoteAttack.getKey(),1.2F);
                runAnim(FlailAnim);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));



                break;
            }
            case BOMB_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingSwoop.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.ZoteBombSummon.getKey(),1.2F);
                runAnim(BombAnim);
                this.SpawnBombs();
                this.SpawnBombs();

                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m != null && !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, Bombs_VAL), Bombs_VAL));
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
        this.SpawnCounter++;
        
        if (this.lastMove(FLAIL_MOVE) && !this.lastMoveBefore(FLAIL_MOVE)){
            this.setMove(FLAIL_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base, this.Flail_HITS, true);
            return;
        }
        
        if (this.SpawnCounter >= this.SpawnTimer){
            if (this.bombtime){
                this.bombtime = false;
                this.setMove(BOMB_MOVE, Intent.MAGIC);
            } else {
                this.setMove(SPIT_MOVE, Intent.UNKNOWN);
                this.bombtime = true;
            }
            this.SpawnCounter = 0;
            return;
        }
        
        if ((num < 65 ) && (num % 2 ==0) && !this.lastMove(SHADOW_MOVE)){
            setMove(SHADOW_MOVE,Intent.BUFF);
        } else if ((num < 60 )) {
            this.setMove(FLAIL_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base, this.Flail_HITS, true);
        } else {
            this.setMove(HOP_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
        }
    }

    private int numSpawns() {
        int count = 0;
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != null && m != this && !m.isDying) {
                ++count;
            }
        }
        return count;
    }

    private void SpawnZotelings(){
        if (numSpawns() <4) {

            int i = 0;
            boolean hasSpawned = false;
            while (!hasSpawned){
                if (this.Spawned[i] == null || this.Spawned[i].isDeadOrEscaped()) {
                    AbstractMonster creatureToSpawn;
                    int rando = AbstractDungeon.monsterRng.random(0, 1);
                    if (rando < 1) {
                        creatureToSpawn = new minionWingedZoteling(POSX[i]);
                    } else {
                        creatureToSpawn = new minionHoppingZoteling(POSX[i], POSY[i]);
                    }
                    this.Spawned[i] = creatureToSpawn;
                    AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(creatureToSpawn, true));
                    //AbstractDungeon.actionManager.addToBottom(new CollectedMinionAction(this));
                    hasSpawned = true;
                }
                i++;
            }

        } else {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Swarmed(), 1, true,true));
        }
    }



    private void SpawnBombs(){
        if (numSpawns() <4) {

            int i = 0;
            boolean hasSpawned = false;
            while (!hasSpawned){
                if (this.Spawned[i] == null || this.Spawned[i].isDeadOrEscaped()) {
                    AbstractMonster creatureToSpawn;
                    creatureToSpawn = new minionVolatileZoteling(POSX[i], POSY[i] +50);
                    this.Spawned[i] = creatureToSpawn;
                    AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(creatureToSpawn, true));
                    //AbstractDungeon.actionManager.addToBottom(new CollectedMinionAction(this));
                    hasSpawned = true;
                }
                i++;
            }

        } else {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Swarmed(), 1, true,true));
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:BossGreyPrinceZote");
        NAME = BossGreyPrinceZote.monsterStrings.NAME;
        MOVES = BossGreyPrinceZote.monsterStrings.MOVES;
        DIALOG = BossGreyPrinceZote.monsterStrings.DIALOG;
        POSX = new float[]{-110.0F, -245.0F, -365.0F, -510.0F };
        POSY = new float[]{1.0F, -10.0F, 5.0F, -8.0f };
    }

    public void damage(DamageInfo info)
    {
        if (info.output > 0 && this.hasPower(IntangiblePower.POWER_ID)) {
            info.output = 1;
        }
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            runAnim(HitAnim);

        }
    }

    @Override
    public void die() {
        stopAnimation();
        useShakeAnimation(4.0F);
        //runAnim("Defeat");
        super.die();
        this.onBossVictoryLogic();
        this.onFinalBossVictoryLogic();
        for (final AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDying) {
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
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

    public class AnimationInfection implements Player.PlayerListener {

        private BossGreyPrinceZote character;

        public AnimationInfection(BossGreyPrinceZote character) {
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