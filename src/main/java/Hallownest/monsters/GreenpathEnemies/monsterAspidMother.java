package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.Swarmed;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MalleablePower;

import java.util.Iterator;

public class monsterAspidMother extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterAspidMother");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte BIRTH_MOVE = 0;
    private static final byte SWARM_MOVE = 1;




    //Hornet Values
    private int  Birth_Num = 1;

    private boolean RePositioner = false;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 65;
    private int minHP = 55;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String BirthAnim = "Birth";
    private String SwarmAnim = "Swarmed";
    private String HitAnim = "Hit";


    public boolean [] livingSpwans ={false, false, false, false , false };

    private AbstractMonster[] Spawned = new AbstractMonster[5];
    public static final float[] POSX;
    public static final float[] POSY;


    public monsterAspidMother() {
        this(0.0f, 0.0f);
    }

    public monsterAspidMother(final float x, final float y) {
        super(monsterAspidMother.NAME, ID, 80, 0, 0, 200.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/AspidMother/MotherAspid.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 3;
            this.maxHP += 3;

        }
        /*if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.attdefAtt += 1;
            this.attdefDef += 1;
            this.flurryDamage += 1;
        }
        */


        setHp(this.minHP,this.maxHP);


        Player.PlayerListener listener = new MotherListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    private int numBabies() {
        int count = 0;
        Iterator var2 = AbstractDungeon.getMonsters().monsters.iterator();

        while(var2.hasNext()) {
            AbstractMonster m = (AbstractMonster)var2.next();
            if (m != this && !m.isDying) {
                ++count;
            }
        }
        return count;
    }

    private void SpawnBaby() {
        int aliveCount = 0;

        Iterator var2 = AbstractDungeon.getMonsters().monsters.iterator();

        while(var2.hasNext()) {
            AbstractMonster m = (AbstractMonster)var2.next();
            if (m != this && !m.isDying) {
                ++aliveCount;
            }
        }
        int i = 0;
        boolean hasSpawned = false;

        while ((i < 5) && !hasSpawned){
            if (this.Spawned[i] == null || this.Spawned[i].isDeadOrEscaped()) {
                minionBabyAspid minionToSpawn = new minionBabyAspid(POSX[i], POSY[i]);
                this.Spawned[i] = minionToSpawn;
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(minionToSpawn, true));
                hasSpawned = true;
            } else {
                i++;
            }
        }



    }

    @Override
    public void usePreBattleAction() {

        if (AbstractDungeon.ascensionLevel >= 17)
        {
           AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MalleablePower(this, 1)));
        }

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                CardCrawlGame.sound.playV(SoundEffects.EvGpDreamerEnter.getKey(),1.0F);
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 5.0f, 5.0f));
            }
        }
    }
    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case BIRTH_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.SFXMotherBirth.getKey(),1.4F);
                runAnim(BirthAnim);
                SpawnBaby();
                break;
            }
            case SWARM_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXMotherSwarm.getKey()));
                runAnim(SwarmAnim);
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Swarmed(),1));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        int spawn = numBabies();
        if (spawn <=4 ){
            this.setMove(BIRTH_MOVE, Intent.UNKNOWN);
            return;
        } else {
            this.setMove(SWARM_MOVE, Intent.DEBUFF);
            return;
        }
       
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterAspidMother");
        NAME = monsterAspidMother.monsterStrings.NAME;
        MOVES = monsterAspidMother.monsterStrings.MOVES;
        DIALOG = monsterAspidMother.monsterStrings.DIALOG;
        POSX = new float[]{275.0F, -200.0F, 400.0F, -325.0F, 200.0F};
        POSY = new float[]{25.0F, 125.0F, 150.0F, 25.0F , 345.0F };

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
        for (final AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDying) {
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
            }
        }
        stopAnimation();
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

    public class MotherListener implements Player.PlayerListener {

        private monsterAspidMother character;

        public MotherListener(monsterAspidMother character) {
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