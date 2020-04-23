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
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;

public class eliteVengeflyKing extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("eliteVengeflyKing");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte SWOOP_MOVE = 0;
    private static final byte BUZZ_MOVE = 1;
    private static final byte SCREECH_MOVE = 2;



    //Hornet Values
    private int  Swoop_DMG = 8;
    private int  Buzz_DMG = 5;
    private int  Buzz_DEBUFF = 1;
    private int  Artifact_VAL = 2;


    private int riposteTrigger;
    private int RIPOSTE_RETAL;
    private int demonStrength;
    private int lionDamage;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 90;
    private int minHP = 80;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SwoopAnim = "Dive";
    private String ScreechAnim = "Screech";
    private String BuzzAnim = "Buzz";
    private String HitAnim = "Hit";



    public static final float[] POSX;
    public static final float[] POSY;
    private int SpawnLimit = 4;
    private AbstractMonster[] Spawned = new AbstractMonster[4];



    public eliteVengeflyKing() {
        this(0.0f, 0.0f);
    }

    public eliteVengeflyKing(final float x, final float y) {
        super(eliteVengeflyKing.NAME, ID, 80, 0, 0, 250.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/VengeflyKing/VengeflyKing.scml");
        this.type = EnemyType.ELITE;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 8) // Elites are tougher at Asc 8
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 5;
            this.maxHP += 5;
        }
        if (AbstractDungeon.ascensionLevel >=3) //Elites are deadlier at 3
        {
            //increases the power of his multihit and debufff for higher ascensions
            this.Swoop_DMG+= 2;
            this.Buzz_DMG += 2;
        }
        if (AbstractDungeon.ascensionLevel >= 18) //18 says elites have harder move sets so do something fancy
        {
            //??
            this.Buzz_DEBUFF +=1;
        }


        setHp(this.minHP,this.maxHP);


        this.damage.add(new DamageInfo(this, this.Swoop_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Buzz_DMG)); // attack 1 damage


        Player.PlayerListener listener = new KingListender(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    private int numFlys() {
        int count = 0;
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != null && m != this && !m.isDying) {
                ++count;
            }
        }
        return count;
    }

    private void SpawnFlys() {
        int i = 0;
        boolean hasSpawned = false;
        boolean hasSwarmed = false;

        while ((!hasSwarmed) && !hasSpawned){
            if (i > SpawnLimit ) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Swarmed(),1));
                hasSwarmed = true;
            } else if (this.Spawned[i] == null || this.Spawned[i].isDeadOrEscaped()) {
                minionFly minionToSpawn = new minionFly(POSX[i], POSY[i]);
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
        AbstractDungeon.getCurrRoom().playBgmInstantly("GPEliteBGM");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, Artifact_VAL ), Artifact_VAL));

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x-25) * Settings.scale;
                this.dialogY = (this.hb_y+25) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0f));
            }
        }
    }
    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {


            case SWOOP_MOVE:{
                runAnim(SwoopAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingSwoop.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY,true));
                break;
            }
            case BUZZ_MOVE:{
                runAnim(BuzzAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.SFXVFKingBuzz.getKey(),1.3F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new WeakPower(p, Buzz_DEBUFF, true),Buzz_DEBUFF));
                break;
            }
            case SCREECH_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new IntenseZoomEffect(this.hb.cX, this.hb.cY, true), 0.6f, true));
                runAnim(ScreechAnim);
                CardCrawlGame.sound.playV(SoundEffects.SFXVFKingScreech.getKey(),1.4F);
                SpawnFlys();
                SpawnFlys();
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        if ((numTurns == 1) ||(lastMove(SCREECH_MOVE))){
            this.setMove(SWOOP_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            return;
        }
        if (lastMove(SWOOP_MOVE)){
            this.setMove(BUZZ_MOVE, Intent.ATTACK_DEBUFF, (this.damage.get(1)).base);
            return;
        }
        if (lastMove(BUZZ_MOVE)){
            this.setMove(SCREECH_MOVE, Intent.UNKNOWN);
            return;
        }
       
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:eliteVengeflyKing");
        NAME = eliteVengeflyKing.monsterStrings.NAME;
        MOVES = eliteVengeflyKing.monsterStrings.MOVES;
        DIALOG = eliteVengeflyKing.monsterStrings.DIALOG;
        POSX = new float[]{290.0F, -275.0F, 375.0F, -375.0F};
        POSY = new float[]{75.0F, 125.0F, 125.0F, 75.0F};
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
        //runAnim("Defeat");
        super.die();
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

    public class KingListender implements Player.PlayerListener {

        private eliteVengeflyKing character;

        public KingListender(eliteVengeflyKing character) {
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