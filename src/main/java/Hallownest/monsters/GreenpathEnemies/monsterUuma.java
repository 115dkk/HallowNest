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
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class monsterUuma extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterUuma");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte ZAP_MOVE = 0;
    private static final byte TOWARDS_MOVE = 1;
    private static final byte AWAY_MOVE = 2;




    //Hornet Values
    private int  Zap_DMG = 4;
    private int  Zap_CARDS = 2;

    private int  Towards_DMG = 6;
    private int  Away_BLOCK = 8;

    private int  StartingPlates = 3;


    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 29;
    private int minHP = 23;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String ZapAnim = "Zap";
    private String TowardAnim = "Toward";
    private String AwayAnim = "Away";



    public monsterUuma() {
        this(0.0f, 0.0F);
    }

    public monsterUuma(float x, float y) {
        super(monsterUuma.NAME, ID, 26, 0, 0, 100.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/FogCanyons/Uuma.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 5;
            this.maxHP += 5;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Towards_DMG+=2;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.StartingPlates+=2;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Zap_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Towards_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.StartingPlates)));
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 25) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x) * Settings.scale;
                this.dialogY = (this.hb_y) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0F));
            }
        }


    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case ZAP_MOVE:{
                runAnim(ZapAnim);
                CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.LIGHTNING));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Dazed(),this.Zap_CARDS));

                break;
            }
            case TOWARDS_MOVE:{
                runAnim(TowardAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                break;
            }
            case AWAY_MOVE:{
                runAnim(AwayAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Away_BLOCK));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if ((num < 40) && (!lastTwoMoves(ZAP_MOVE))){
            this.setMove(ZAP_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);

        } else if ((num < 75)&&(!lastMove(TOWARDS_MOVE))){
            this.setMove(TOWARDS_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);

        } else {
            this.setMove(AWAY_MOVE, Intent.DEFEND);
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterUuma");
        NAME = monsterUuma.monsterStrings.NAME;
        MOVES = monsterUuma.monsterStrings.MOVES;
        DIALOG = monsterUuma.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {

        super.damage(info);
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

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterUuma character;

        public AnimationListener(monsterUuma character) {
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