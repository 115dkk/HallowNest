package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.attackAdjustingAction;
import Hallownest.cards.status.Swarmed;
import Hallownest.powers.powerReadjusting;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterMisterMushroom extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMisterMushroom");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte READJUST_MOVE = 0;
    private static final byte TURN_MOVE = 1;
    private static final byte NOD_MOVE = 2;
    private static final byte SECRET_MOVE = 3;





    //Values
    private int  StrGain = 2;
    private int  BlkGain = 7;
    private int  HpGain = 6;

    private int  Nod_DMG = 6;
    private int  Turn_VAL = 2;

    private boolean CardAdded = false;

    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 125;
    private int minHP = 120;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String TurnAnim = "Turn";
    private String NodAnim = "Nod";
    private String HitAnim = "Hit";



    public monsterMisterMushroom() {
        this(0.0f, 0.0F);
    }

    public monsterMisterMushroom(float x, float y) {
        super(monsterMisterMushroom.NAME, ID, 26, 0, 0, 125.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/MisterMushroom/MisterMushroom.scml");
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
            this.BlkGain+=1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.StrGain+=1;
            this.HpGain+=3;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Nod_DMG)); // attack 0 damage


    }

    @Override
    public void usePreBattleAction() {
        //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerReadjusting(this)));
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
            case READJUST_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerReadjusting(this, this.BlkGain, this.StrGain, this.HpGain)));
                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case TURN_MOVE:{
                runAnim(TurnAnim);
                CardCrawlGame.sound.playV(SoundEffects.MrMushTurn.getKey(),1.3F);

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new WeakPower(p, this.Turn_VAL, true)));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new FrailPower(p, this.Turn_VAL, true)));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new VulnerablePower(p, this.Turn_VAL, true)));


                break;
            }
            case NOD_MOVE:{
                runAnim(NodAnim);
                CardCrawlGame.sound.playV(SoundEffects.MrMushNod.getKey(),1.3F);

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                break;
            }

            case SECRET_MOVE:{
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(new attackAdjustingAction()));
                // turn 7, give player the "adjusting Card" (as long as it's not in their deck)
                //Adjusting card deals 8(11) damage if targeting enemy, and 8(11) block if targeting self.

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;

        if (this.numTurns == 8){
            this.setMove(SECRET_MOVE, Intent.MAGIC);
            return;
        }

        if (!this.hasPower(powerReadjusting.POWER_ID)){
            this.setMove(READJUST_MOVE, Intent.UNKNOWN);
            return;
        }

        if ((num < 35) || (lastMove(TURN_MOVE))){
            this.setMove(NOD_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else {
            this.setMove(TURN_MOVE, Intent.STRONG_DEBUFF);
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterMisterMushroom");
        NAME = monsterMisterMushroom.monsterStrings.NAME;
        MOVES = monsterMisterMushroom.monsterStrings.MOVES;
        DIALOG = monsterMisterMushroom.monsterStrings.DIALOG;
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

        private monsterMisterMushroom character;

        public AnimationListener(monsterMisterMushroom character) {
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