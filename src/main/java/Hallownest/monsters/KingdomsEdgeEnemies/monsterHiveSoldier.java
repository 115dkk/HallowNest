package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerHivesBlood;
import Hallownest.powers.powerRich;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterHiveSoldier extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterHiveSoldier");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterHiveSoldier.monsterStrings.NAME;
    public static final String[] MOVES = monsterHiveSoldier.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterHiveSoldier.monsterStrings.DIALOG;

    private static final byte RECOIL_MOVE = 0;
    private static final byte DANCE_MOVE = 1;
    private static final byte SPIN_MOVE = 2;
    private static final byte DIZZY_MOVE = 3;





    //Values
    private int  Spin_DMG = 6;
    private int  Spin_HITS = 3;
    private int  Recoil_BLOCK = 20;
    private int  Dance_VAL = 2;






    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 58;
    private int maxHP = 62;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String DrillAnim = "Drill";
    private String DanceAnim = "Screech";
    private String RecoilAnim = "Recoil";
    private String HitAnim = "Hit";



    public monsterHiveSoldier() {
        this(0.0f, 0.0F);
    }

    public monsterHiveSoldier(float x, float y) {
        super(monsterHiveSoldier.NAME, ID, 130, 0, 0, 150.0f, 276.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/HiveWarrior/HiveWarrior.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(1.00f);
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
            this.Spin_DMG+=1;
            this.Recoil_BLOCK+=2;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Dance_VAL+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Spin_DMG)); // attack 0 damage



    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerHivesBlood(this)));
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
            case RECOIL_MOVE:{
                runAnim(RecoilAnim);
                //CardCrawlGame.sound.playV(SoundEffects.RichHuskAttack1.getKey(),1.3F);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, Recoil_BLOCK));


                break;
            }
            case DANCE_MOVE:{
                runAnim(DanceAnim);
                CardCrawlGame.sound.playV(SoundEffects.BeeRoar.getKey(),1.3F);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BeeBuff.getKey()));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this,this, new StrengthPower(this,this.Dance_VAL),this.Dance_VAL));





                break;
            }
            case SPIN_MOVE:{
                runAnim(DrillAnim);
                CardCrawlGame.sound.playV(SoundEffects.BeeDrill.getKey(),1.3F);
                //CardCrawlGame.sound.playV(SoundEffects.RichHuskCry.getKey(),1.5F);

                for (int i = 0; i < this.Spin_HITS; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }

                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }
            case DIZZY_MOVE:{
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

        if (this.lastMove(SPIN_MOVE)){
            this.setMove(MOVES[DIZZY_MOVE],DIZZY_MOVE,Intent.STUN);
            return;
        }

        if (((num < 33 ) && (!this.lastMoveBefore(SPIN_MOVE))) || ((numTurns == 1) && (num < 55))){
            this.setMove(SPIN_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Spin_HITS, true);
        } else if ((num < 60) && !this.lastMove(DANCE_MOVE)){
            this.setMove(DANCE_MOVE, Intent.BUFF);
        } else if ((num <95) && (!this.lastTwoMoves(RECOIL_MOVE))) {
            this.setMove(RECOIL_MOVE, Intent.DEFEND);
        } else {
            this.setMove(SPIN_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Spin_HITS, true);
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

        private monsterHiveSoldier character;

        public AnimationListener(monsterHiveSoldier character) {
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