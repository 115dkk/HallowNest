package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerInfection;
import Hallownest.powers.powerPotentInfection;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedProjectileEffect;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterPrimalAspid extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterPrimalAspid");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterPrimalAspid.monsterStrings.NAME;
    public static final String[] MOVES = monsterPrimalAspid.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterPrimalAspid.monsterStrings.DIALOG;

    private static final byte SPEW_MOVE = 0;






    //Values
    private int  Spew_DMG = 7;
    private int  Spew_Hits = 3;
    private int  Spit_INF = 2;





    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 135;
    private int minHP = 130;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SpitAnim = "Spit";
    private String HitAnim = "Hit";



    public monsterPrimalAspid() {
        this(0.0f, 0.0F);
    }

    public monsterPrimalAspid(float x, float y) {
        super(monsterPrimalAspid.NAME, ID, 130, 0, 0, 100.0f, 175.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/PrimalAspid/PrimalAspid.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 4;
            this.maxHP += 4;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Spew_DMG+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Spit_INF+=1;
        }
        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Spew_DMG)); // attack 0 damage
    }
    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this, new powerPotentInfection(this,this.Spit_INF),this.Spit_INF));
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 15) {
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
            case SPEW_MOVE:{
                runAnim(SpitAnim);
                CardCrawlGame.sound.playV(SoundEffects.SpitSound.getKey(),1.3F);
                for (int i = 0; i < this.Spew_Hits; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedProjectileEffect(this.hb.cX, this.hb.cY, 0.0f)));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.POISON));

                }
                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        this.setMove(SPEW_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Spew_Hits, true);

        if (numTurns == 7){
            this.Spew_Hits++;
        }
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            runAnim(HitAnim);

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


    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterPrimalAspid character;

        public AnimationListener(monsterPrimalAspid character) {
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