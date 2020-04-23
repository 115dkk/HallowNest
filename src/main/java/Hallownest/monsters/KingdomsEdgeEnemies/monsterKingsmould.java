package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerHivesBlood;
import Hallownest.powers.powerThreatened;
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
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class monsterKingsmould extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterKingsmould");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterKingsmould.monsterStrings.NAME;
    public static final String[] MOVES = monsterKingsmould.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterKingsmould.monsterStrings.DIALOG;

    private static final byte THROW_MOVE = 0;
    private static final byte SWING_MOVE = 1;
    private static final byte STAND_MOVE = 2;






    //Values
    private int  Swing_DMG = 8;
    private int  Swing_HITS = 2;
    private int  Throw_DMG = 15;
    private int  Throw_VAL = 1;

    private int  Stand_VAL = 1;
    



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 74;
    private int maxHP = 78;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String StandAnim = "StandGround";
    private String SwingAnim = "TwoSwing";
    private String ThrowAnim = "Throw";
    private String HitAnim = "Hit";



    public monsterKingsmould() {
        this(0.0f, 0.0F);
    }

    public monsterKingsmould(float x, float y) {
        super(monsterKingsmould.NAME, ID, 130, 0, 0, 150.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/Kingsmould/Kingsmould.scml");
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
            this.Swing_DMG+=1;
            this.Throw_DMG+=2;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Throw_VAL+=1;
            this.Stand_VAL+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Throw_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Swing_DMG)); // attack 0 damage



    }

    @Override
    public void usePreBattleAction() {
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
            case THROW_MOVE:{
                runAnim(ThrowAnim);
                CardCrawlGame.sound.playV(SoundEffects.MantisYouthScythe.getKey(),1.3F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new powerThreatened(p, this, this.Throw_VAL, true),this.Throw_VAL));


                break;
            }
            case SWING_MOVE:{
                runAnim(SwingAnim);


                for (int i = 0; i < this.Swing_HITS; ++i) {
                    CardCrawlGame.sound.playV(SoundEffects.GenSword.getKey(),1.3F);
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                }

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BeeBuff.getKey()));






                break;
            }
            case STAND_MOVE:{
                runAnim(StandAnim);

                CardCrawlGame.sound.playV(SoundEffects.GenericShield.getKey(),1.3F);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new VulnerablePower(p, this.Throw_VAL, true),this.Throw_VAL));

                //CardCrawlGame.sound.playV(SoundEffects.RichHuskCry.getKey(),1.5F);



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
        if ((this.lastMoveBefore(STAND_MOVE)) || ((numTurns == 1))){
            this.setMove(THROW_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        } else if (this.lastMove(THROW_MOVE)){
            this.setMove(SWING_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base, this.Swing_HITS, true);
        } else {
            this.setMove(STAND_MOVE, Intent.DEBUFF);
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

        private monsterKingsmould character;

        public AnimationListener(monsterKingsmould character) {
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