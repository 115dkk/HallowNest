package Hallownest.monsters.GreenpathEnemies;

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
import com.megacrit.cardcrawl.powers.CurlUpPower;

public class monsterBaldur extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterBaldur");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte ATTACK_MOVE = 0;
    private static final byte CURL_MOVE = 1;
    private static final byte ROLL_MOVE = 2;




    //Hornet Values
    private int  Attack_DMG = AbstractDungeon.monsterHpRng.random(4, 6);
    private int  Exist_VAL = 1;

    private int  Rollout_DMG = AbstractDungeon.monsterHpRng.random(6, 7);
    private int  Rollout_BLOCK = AbstractDungeon.monsterHpRng.random(5, 6);

    private int  Curl_BLOCK = 5;


    private int  StartingCurls = AbstractDungeon.monsterHpRng.random(5, 7);;
    private int  Turn_HP;
    private boolean BallForm = false;
    private boolean Damaged = false;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 22;
    private int minHP = 18;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int chargeCount = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String AttackAnim = "Attack";
    private String CurlAnim = "Curl";
    private String HitAnim = "Hit";

    private String BallIdleAnim = "BallIdle";
    private String BallRollAnim = "BallRoll";
    private String BallHitAnim = "BallHit";

    public monsterBaldur() {
        this(0.0f, 0.0f);
    }

    public monsterBaldur(float x) {
        this(x, 0.0f);
    }

    public monsterBaldur(float x, float y) {
        super(monsterBaldur.NAME, ID, 45, 0, 0, 90.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/Baldurs/Baldur.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Attack_DMG = AbstractDungeon.monsterHpRng.random(4, 7);
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.StartingCurls = AbstractDungeon.monsterHpRng.random(6, 10);

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Rollout_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        this.Turn_HP = this.currentHealth;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CurlUpPower(this, this.StartingCurls)));

        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x) * Settings.scale;
                this.dialogY = (this.hb_y) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }

    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case ATTACK_MOVE:{
                runAnim(AttackAnim);
                CardCrawlGame.sound.playV(SoundEffects.CrawlerAttack.getKey(),1.4F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            }
            case CURL_MOVE:{
                this.BallForm = true;
                runAnim(CurlAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BaldurCurl.getKey()));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Curl_BLOCK));


                break;
            }
            case ROLL_MOVE:{
                runAnim(BallRollAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BaldurSpin.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Rollout_BLOCK));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns ++;
        if (this.numTurns == 1) {
            this.Turn_HP = this.currentHealth;
        }

        if (BallForm){
            this.setMove(ROLL_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(1)).base);
            return;
        }

        if (this.currentHealth < this.Turn_HP){
            this.setMove(CURL_MOVE, Intent.DEFEND_BUFF);
        } else {
            this.setMove(ATTACK_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);

        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterBaldur");
        NAME = monsterBaldur.monsterStrings.NAME;
        MOVES = monsterBaldur.monsterStrings.MOVES;
        DIALOG = monsterBaldur.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            if (BallForm){
                runAnim(BallHitAnim);
            } else {
                runAnim(HitAnim);
            }
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

    public void BallresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(BallIdleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterBaldur character;

        public AnimationListener(monsterBaldur character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){

            if (BallForm){
                if (!animation.name.equals(BallIdleAnim)) {
                    character.BallresetAnimation();
                }
            } else {
                if (!animation.name.equals(IdleAnim)) {
                    character.resetAnimation();
                }
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