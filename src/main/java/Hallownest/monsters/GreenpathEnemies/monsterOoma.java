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

public class monsterOoma extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterOoma");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte RISE_MOVE = 0;
    private static final byte PERISH_MOVE = 1;
    private static final byte BOOM_MOVE = 2;




    //Hornet Values
    private int  Rise_DMG = 8;
    private int  Rise_BLOCK = 8;
    private int  Boom_DMG = 22;


    private boolean CoreStance = false;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 42;
    private int minHP = 36;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int FullHP;


    private int chargeCount = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String RiseAnim = "Rise";
    private String PerishAnim = "Perish";
    private String CoreIdle = "CoreIdle";
    private String BoomAnim = "CoreBoom";


    public monsterOoma() {
        this(0.0f, 0.0f);
    }

    public monsterOoma(final float x, final float y) {
        super(monsterOoma.NAME, ID, 45, 0, 0, 150.0f, 475.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/FogCanyons/Ooma.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 4;
            this.maxHP += 4;

        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Rise_BLOCK +=1;
            this.Rise_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Boom_DMG+=2;
        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Rise_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Boom_DMG)); // attack 0 damage
        
    }

    @Override
    public void usePreBattleAction() {
        this.FullHP = this.currentHealth;
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 25) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x - 25.0F) * Settings.scale;
                this.dialogY = (this.hb_y + 50.0F) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 3.0f, 3.0f));
            }
        }
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case RISE_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.JellyFloat.getKey(),1.4F);
                runAnim(RiseAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.Rise_BLOCK));
                break;
            }
            case PERISH_MOVE:{
                this.CoreStance = true;
                runAnim(PerishAnim);
                CardCrawlGame.sound.playV(SoundEffects.JellyFade.getKey(),1.4F);

                break;
            }
            case BOOM_MOVE:{
                runAnim(BoomAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellyBoom.getKey(),1.4F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(PERISH_MOVE)) {
            this.setMove(BOOM_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
        } else {
            this.setMove(RISE_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterOoma");
        NAME = monsterOoma.monsterStrings.NAME;
        MOVES = monsterOoma.monsterStrings.MOVES;
        DIALOG = monsterOoma.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if (currentHealth < (FullHP/2)){
            this.setMove(PERISH_MOVE, Intent.UNKNOWN);
            this.intent = (Intent.UNKNOWN);
            this.nextMove = PERISH_MOVE;

        }
    }

    @Override
    public void die() {
        stopAnimation();
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

    public void CoreresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(CoreIdle);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterOoma character;

        public AnimationListener(monsterOoma character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (CoreStance){
                if (!animation.name.equals(IdleAnim)) {
                    character.CoreresetAnimation();
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