package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerInfection;
import Hallownest.powers.powerRich;
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
import com.megacrit.cardcrawl.powers.*;

public class monsterGluttonousHusk extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterGluttonousHusk");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterGluttonousHusk.monsterStrings.NAME;
    public static final String[] MOVES = monsterGluttonousHusk.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterGluttonousHusk.monsterStrings.DIALOG;

    private static final byte STROLL_MOVE = 0;
    private static final byte HOP_MOVE = 1;
    private static final byte POOR_MOVE = 2;





    //Values
    private int  Hop_DMG = 11;
    private int  Hop_VAL = 1;
    private int  Stroll_DMG = 7;
    private int  Stroll_HITS = 2;
    private int  Starting_Strength = 4;
    private int  GoldperStrength = 10;
    private int  DmgPerStrengthDown = 10;

    private boolean hasCried = false;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 68;
    private int minHP = 62;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String HopAnim = "Hop";
    private String StrollAnim = "Slapper";
    private String HitAnim = "Hit";



    public monsterGluttonousHusk() {
        this(0.0f, 0.0F);
    }

    public monsterGluttonousHusk(float x, float y) {
        super(monsterGluttonousHusk.NAME, ID, 130, 0, 0, 150.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/HuskFat/GluttonousHusk.scml");
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
            this.Starting_Strength+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.DmgPerStrengthDown+=5;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Stroll_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Hop_DMG)); // attack 1 damage



    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerRich(this, this.DmgPerStrengthDown,(this.Starting_Strength * this.GoldperStrength))));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.Starting_Strength)));
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 33) {
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
            case STROLL_MOVE:{
                runAnim(StrollAnim);
                CardCrawlGame.sound.playV(SoundEffects.RichHuskAttack1.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));


                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case HOP_MOVE:{
                runAnim(HopAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new FrailPower(p,Hop_VAL,true),Hop_VAL));

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));




                break;
            }
            case POOR_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.RichHuskCry.getKey(),1.5F);

                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;


        if ((!this.hasPower(powerRich.POWER_ID)) && (!hasCried) && (this.numTurns!=1)){
            this.setMove(MOVES[POOR_MOVE], POOR_MOVE, Intent.UNKNOWN);
            this.hasCried = true;
            return;
        }

        if (((num % 2 == 0) && !this.lastMove(STROLL_MOVE)) && this.numTurns!=1){
            this.setMove(STROLL_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Stroll_HITS, true);

        } else {
            this.setMove(HOP_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(1)).base);

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

        private monsterGluttonousHusk character;

        public AnimationListener(monsterGluttonousHusk character) {
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