package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.CollectCardAction;
import Hallownest.actions.CollectedMinionAction;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.Obsession;
import Hallownest.cards.status.StolenSoul;
import Hallownest.cards.status.Swarmed;
import Hallownest.monsters.CityofTearsEnemies.monsterFlukebot;
import Hallownest.monsters.GreenpathEnemies.minionFly;
import Hallownest.monsters.GreenpathEnemies.monsterBaldur;
import Hallownest.powers.powerCollected;
import Hallownest.powers.powerCollectedCard;
import Hallownest.powers.powerInfection;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedEffect;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MalleablePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class eliteCollector extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("eliteCollector");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte HOP_MOVE = 0;
    private static final byte GRAB_MOVE = 1;
    private static final byte DROP_MOVE = 2;
    private static final byte CALL_MOVE = 3;


    //might change how his orb empowers his attacks for both clarity and counterplay. Give him a power that makes him gain strength whenever you draw (or maybe play) a Stolen Soul


    private int  Hop_BLOCK= 25;
    private int  Drop_DMG =8;
    private int  Grab_DMG = 13;
    private int  Call_STR = 3;
    private int Call_VAL = 25;
    private int HitsToReturn = 5;

    private boolean Enraged = false;

    private int turnStrength;

    public static final float[] POSX;
    public static final float[] POSY;
    private AbstractMonster[] Spawned = new AbstractMonster[5];

    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 216;
    private int maxHP = 226;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String HopAnim = "Hop";
    private String GrabAnim = "Grab";
    private String DropAnim = "Drop";
    private String CallAnim = "Call";
    private String HitAnim = "Hit";


    public eliteCollector() {
        this(0.0f, 0.0f);
    }

    public eliteCollector(final float x, final float y) {
        super(eliteCollector.NAME, ID, 85, 0, 0, 250.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/Collector/Collector.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);

        this.type = EnemyType.ELITE;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 8) // Elites are tougher at Asc 8
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 8;
            this.maxHP += 8;
        }
        if (AbstractDungeon.ascensionLevel >=3) //Elites are deadlier at 3
        {
            //increases the power of his multihit and debufff for higher ascensions
            this.Hop_BLOCK+= 2;
            this.Drop_DMG+=1;
        }
        if (AbstractDungeon.ascensionLevel >= 18) //18 says elites have harder move sets so do something fancy
        {
            //??
            this.HitsToReturn +=1;
            this.Call_VAL+=2;
            this.Call_STR+=1;
        }


        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Drop_DMG));
        this.damage.add(new DamageInfo(this, this.Grab_DMG)); // attack 0 damage// attack 0 damage


        Player.PlayerListener listener = new AnimationInfection(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }


    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("KEEliteBGM");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MalleablePower(this)));
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x-75) * Settings.scale;
                this.dialogY = (this.hb_y+75) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }
    }
    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {


            case HOP_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingSwoop.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.CollectorHop.getKey(),1.2F);
                runAnim(HopAnim);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,this.Hop_BLOCK));
                break;
            }
            case GRAB_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.CollectorSteal.getKey()));
                runAnim(GrabAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new CollectCardAction(this, this.HitsToReturn));
                break;
            }
            case DROP_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.CollectorSummon.getKey()));
                runAnim(DropAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                this.SpawnCollected();

                break;
            }
            case CALL_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.CollectorRoar.getKey(),1.2F);
                runAnim(CallAnim);
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m != null && !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new AddTemporaryHPAction(m, this, Call_VAL));
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.Call_STR), this.Call_STR));


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


        if (!this.hasPower(powerCollectedCard.POWER_ID) &&  (num % 2 == 0)){
            this.setMove(GRAB_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(1)).base);
            return;
        }

        if ((numSpawns() >= 1) && (num <33) && !lastMove(CALL_MOVE)){
            this.setMove(CALL_MOVE, Intent.BUFF);
        } else if (((num <75) && (!this.lastMove(DROP_MOVE))) || (numTurns == 2) || (numTurns == 5)){
            this.setMove(DROP_MOVE, Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
        } else {
            setMove(HOP_MOVE,Intent.DEFEND);
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

    private void SpawnCollected(){
        if (numSpawns() <5) {

            int i = 0;
            boolean hasSpawned = false;
            while (!hasSpawned){
                if (this.Spawned[i] == null || this.Spawned[i].isDeadOrEscaped()) {
                    AbstractMonster creatureToSpawn;
                    int rando = AbstractDungeon.monsterRng.random(0, 9);
                    if (rando < 6) {
                        creatureToSpawn = new monsterBaldur(POSX[i], POSY[i]);
                    } else if (rando < 9) {
                        creatureToSpawn = new minionFly(POSX[i], POSY[i]);
                    } else {
                        creatureToSpawn = new monsterLittleHopper(POSX[i], POSY[i], false);
                    }
                    this.Spawned[i] = creatureToSpawn;
                    AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(creatureToSpawn, true));
                    AbstractDungeon.actionManager.addToBottom(new CollectedMinionAction(this));
                    hasSpawned = true;
                }
                i++;
            }

        } else {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Obsession(), 1, true,true));
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:eliteCollector");
        NAME = eliteCollector.monsterStrings.NAME;
        MOVES = eliteCollector.monsterStrings.MOVES;
        DIALOG = eliteCollector.monsterStrings.DIALOG;
        POSX = new float[]{-15.0F, -155.0F, -285.0F, -415.0F, -555.0F };
        POSY = new float[]{-35.0F, 18.0F, -30.0F, 23.0F, -38.0f };
    }

    public void damage(DamageInfo info)
    {
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
        useShakeAnimation(2.0F);
        for (final AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDying) {
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
            }
        }
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

    public class AnimationInfection implements Player.PlayerListener {

        private eliteCollector character;

        public AnimationInfection(eliteCollector character) {
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