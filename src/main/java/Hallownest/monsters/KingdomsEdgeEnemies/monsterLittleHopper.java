package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.powerVigilant;
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
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterLittleHopper extends CustomMonster {
    public static final String ID = HallownestMod.makeID("monsterLittleHopper");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;




    private int maxHP = 58;
    private int minHP = 52;


    private static final byte FATTACK_MOVE = 0;
    private static final byte BATTACK_MOVE = 1;


    private String FIdleAnim = "FIdle";
    private String BIdleAnim = "BIdle";
    private String FAttackAnim = "FAttack";
    private String BAttackAnim = "BAttack";
    private String FHitAnim = "FHit";
    private String BHitAnim = "BHit";

    private String IdleAnim = FIdleAnim;
    private String HitAnim = FHitAnim;
    private String AttackAnim = FAttackAnim;

    private int numTurns = 0;


    private int FAttack_DMG = 11;
    private int FAttack_BLOCK = 6;

    private int BAttack_DMG = 7;
    private int BAttack_BLOCK = 14;



    private int TurnLimit = 3;
    private int TurnCounter = 0;

    private boolean StartFlipped;

    private boolean isFlipped = false;


    public monsterLittleHopper() {
        this(0.0f, 0.0f, false);
    }

    public monsterLittleHopper(float x, float y, boolean flip) {
        super(NAME, ID, 22, 0.0F, 0.0F, 100.0F, 150.0F, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/LittleHopper/LittleHopper.scml");
        this.type = EnemyType.NORMAL;
        setHp(this.minHP,this.maxHP);
        this.StartFlipped = flip;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 5;
            this.maxHP += 5;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.FAttack_BLOCK +=1;
            this.FAttack_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.TurnLimit--;

        }


        Player.PlayerListener listener = new monsterLittleHopper.FLyListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);


        this.damage.add(new DamageInfo(this, this.FAttack_DMG)); // attack 0 damage

        this.damage.add(new DamageInfo(this, this.FAttack_BLOCK)); // attack 0 damage


        if (this.StartFlipped){
            Flip();
        }

    }

    @Override
    public void usePreBattleAction() {

        int Flipped = AbstractDungeon.miscRng.random(0,10);
        if (Flipped <2){
            Flip();
        }


        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x-75) * Settings.scale;
                this.dialogY = (this.hb_y+75) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0f));
            }
        }
    }

    private void Flip(){
        if (this.isFlipped){
            this.IdleAnim = this.FIdleAnim;
            this.AttackAnim = this.FAttackAnim;
            this.HitAnim = this.FHitAnim;
            this.isFlipped = false;
            this.nextMove = FATTACK_MOVE;
            this.setMove(FATTACK_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
            this.intent = (Intent.ATTACK_DEFEND);
            this.createIntent();


        } else {
            this.IdleAnim = this.BIdleAnim;
            this.AttackAnim = this.BAttackAnim;
            this.HitAnim = this.BHitAnim;
            this.isFlipped = true;



            this.nextMove = BATTACK_MOVE;
            this.setMove(BATTACK_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(1)).base);
            this.intent = (Intent.ATTACK_DEFEND);
            this.createIntent();
        }
    }



    @Override
    public void die(boolean triggerRelics) {
        super.die(false);

    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        switch(this.nextMove) {
            case FATTACK_MOVE: {
                runAnim(AttackAnim);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.FlukeAttack1.getKey()));
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.LittleJump.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, FAttack_BLOCK ));



                break;
            }

            case BATTACK_MOVE: {
                runAnim(AttackAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.LittleJump.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, FAttack_DMG ));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }
    public void damage(DamageInfo info)
    {
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (!info.owner.isDying) && !info.owner.isDead && (info.output > 0))
        {
            runAnim(HitAnim);
        }
    }

    @Override
    protected void getMove(int num) {
        this.numTurns++;
        this.TurnCounter++;
        if (this.TurnCounter >= this.TurnLimit){
            Flip();
            this.TurnCounter = 0;
        }

        if (this.isFlipped){
            this.setMove(BATTACK_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(1)).base);
        } else {
            this.setMove(FATTACK_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterLittleHopper");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
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

    public class FLyListener implements Player.PlayerListener {

        private monsterLittleHopper character;

        public FLyListener(monsterLittleHopper character) {
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

