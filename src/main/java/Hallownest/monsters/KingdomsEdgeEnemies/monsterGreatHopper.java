package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
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
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterGreatHopper extends CustomMonster {
    public static final String ID = HallownestMod.makeID("monsterGreatHopper");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;




    private int maxHP = 80;
    private int minHP = 74;


    private static final byte FATTACK_MOVE = 0;
    private static final byte BATTACK_MOVE = 1;


    private String FIdleAnim = "FIdle";
    private String BIdleAnim = "BIdle";
    private String FAttackAnim = "FAttack";
    private String BAttackAnim = "BAttack";
    private String FHitAnim = "FHit";
    private String BHitAnim = "BHit";

    private String IdleAnim = BIdleAnim;
    private String HitAnim = BHitAnim;
    private String AttackAnim = BAttackAnim;

    private int numTurns = 0;


    private int BAttack_DMG = 10;
    private int BAttack_BLOCK = 18;

    private int BackSTR = 2;
    private int FrontPlate = 4;

    private int FAttack_DMG = 12;
    private int FAttack_BLOCK = 9;



    private int TurnLimit = 3;
    private int TurnCounter = 0;

    private boolean isFlipped = false;


    public monsterGreatHopper() {
        this(0.0f, 0.0f, false);
    }

    public monsterGreatHopper(float x, float y, boolean flip) {
        super(NAME, ID, 22, 0.0F, 0.0F, 150.0F, 250.0F, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/GreatHopper/GreatHopper.scml");
        this.type = EnemyType.NORMAL;
        setHp(this.minHP,this.maxHP);



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
            this.BAttack_BLOCK +=1;
            this.BAttack_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.TurnLimit--;

        }


        Player.PlayerListener listener = new monsterGreatHopper.FLyListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);


        this.damage.add(new DamageInfo(this, this.BAttack_DMG)); // attack 0 damage


        if (flip){
            Flip();
        }

    }

    @Override
    public void usePreBattleAction() {


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
            this.IdleAnim = this.BIdleAnim;
            this.AttackAnim = this.BAttackAnim;
            this.HitAnim = this.BHitAnim;
            this.isFlipped = false;
            this.nextMove = FATTACK_MOVE;
            this.setMove(FATTACK_MOVE, Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
            this.intent = (Intent.ATTACK_BUFF);
            this.createIntent();
        } else {
            this.IdleAnim = this.FIdleAnim;
            this.AttackAnim = this.FAttackAnim;
            this.HitAnim = this.FHitAnim;
            this.isFlipped = true;
            this.nextMove = BATTACK_MOVE;
            this.setMove(BATTACK_MOVE, Intent.DEFEND_BUFF);
            this.intent = (Intent.DEFEND_BUFF);
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
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.GreatLand.getKey()));

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.FrontPlate),this.FrontPlate));



                break;
            }

            case BATTACK_MOVE: {
                runAnim(AttackAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.GreatLand.getKey()));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, BAttack_DMG ));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.BackSTR),this.BackSTR));

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
            this.setMove(FATTACK_MOVE, Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
        } else {
            this.setMove(BATTACK_MOVE, Intent.DEFEND_BUFF);

        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterGreatHopper");
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

        private monsterGreatHopper character;

        public FLyListener(monsterGreatHopper character) {
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

