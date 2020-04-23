package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.infoHiveGuardian;
import Hallownest.powers.powerHivesBlood;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveAllPowersAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterHiveGuardian extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterHiveGuardian");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterHiveGuardian.monsterStrings.NAME;
    public static final String[] MOVES = monsterHiveGuardian.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterHiveGuardian.monsterStrings.DIALOG;

    private static final byte CHARGE_MOVE = 0;
    private static final byte SMASH_MOVE = 1;
    private static final byte DIZZY_MOVE = 2;





    //Values
    private int  Plates_VAL = 5;






    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 80;
    private int maxHP = 84;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String SmashAnim = "Smash";
    private String HitAnim = "Hit";



    public monsterHiveGuardian() {
        this(0.0f, 0.0F);
    }

    public monsterHiveGuardian(float x, float y) {
        super(monsterHiveGuardian.NAME, ID, 130, 0, 0, 175.0f, 325.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/HiveGuardian/HiveGuardian.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(1.00f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 6;
            this.maxHP += 6;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Plates_VAL+=1;
        }


        /*
        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Dance_VAL+=1;
            add a healing to the smash
        }
        */

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);


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
            case CHARGE_MOVE:{

                break;
            }
            case SMASH_MOVE:{
                runAnim(SmashAnim);
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BeeBuff.getKey()));
                CardCrawlGame.sound.playV(SoundEffects.BigBeeSmash.getKey(),1.3F);

                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else {
                        AbstractDungeon.actionManager.addToBottom(new RemoveAllPowersAction(m, true));
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this,new PlatedArmorPower(m,this.Plates_VAL),this.Plates_VAL));
                        if (AbstractDungeon.ascensionLevel >= 17)
                        {
                            AbstractDungeon.actionManager.addToBottom(new HealAction(m,this, 20));
                        }

                    }
                }
                if (!this.hasPower(infoHiveGuardian.POWER_ID)){
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new infoHiveGuardian(this)));
                }







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

        if ((this.lastMove(DIZZY_MOVE)) || this.numTurns ==1){
            this.setMove(MOVES[CHARGE_MOVE],CHARGE_MOVE, Intent.UNKNOWN);
        } else if (this.lastMove(SMASH_MOVE)){
            this.setMove(DIZZY_MOVE, Intent.STUN);
        } else {
            this.setMove(MOVES[SMASH_MOVE],SMASH_MOVE,Intent.BUFF);
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

        private monsterHiveGuardian character;

        public AnimationListener(monsterHiveGuardian character) {
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