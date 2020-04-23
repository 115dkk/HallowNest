package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.StolenSoul;
import Hallownest.cards.status.Swarmed;
import Hallownest.monsters.GreenpathEnemies.minionBlob;
import Hallownest.powers.powerInfection;
import Hallownest.powers.powerSoulMasterConsumed;
import Hallownest.powers.powerSoulMasterDrained;
import Hallownest.powers.powerSpewing;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import basemod.helpers.BaseModCardTags;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;

import java.util.Iterator;

public class BossSoulMaster extends CustomMonster {
    public static final String ID = HallownestMod.makeID("BossSoulMaster");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte BALL_MOVE = 0;
    private static final byte SLAM_MOVE = 1;
    private static final byte CLOCK_MOVE = 2;
    private static final byte DASH_MOVE = 3;
    private static final byte INFLATE_MOVE = 4;
    private static final byte DEFLATED_MOVE = 5;


    //Hornet Values
    private int Clock_DMG = 0;
    private int Clock_HP_DIV = 10;
    private int Clock_TIMES = 4;
    private int Ball_CARDS = 1;
    private int Dash_DMG = 9;
    private int Dash_BLOCK = 14;
    private int Slam_DMG = 16;
    private int Slam_VAL = 1;
    private int Slam_TIMES = 2; // not for now i dont think
    private int Consume_HEAL = 15;


    private int FullHP;
    private int HalfHP;
    private int statusCards;


    private boolean hasDeflated = false;
    private boolean isDeflated = false;


    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 345;
    private int minHP = 340;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int clockTimer = 6;
    private int clockCounter = 3;
    private int slamTimer = 3;
    private int slamCounter = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String DefIdleAnim = "DefIdle";
    private String DeflateAnim = "Deflate";
    private String SlamAnim = "Slam";
    private String ClockAnim = "Clock";
    private String DashAnim = "Dash";
    private String BallAnim = "Ball";
    private String InflateAnim = "Inflate";
    private String DefHitAnim = "DefHit";
    private String HitAnim = "Hit";


    public BossSoulMaster() {
        this(0.0f, 0.0f);
    }

    public BossSoulMaster(final float x, final float y) {
        super(BossSoulMaster.NAME, ID, 310, 0, 0, 200.0f, 375.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/SoulMaster/SoulMaster.scml");
        this.type = EnemyType.BOSS;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 9) {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 25;
            this.maxHP += 25;

        }
        if (AbstractDungeon.ascensionLevel >= 4) {
            this.Slam_DMG += 3;
            this.Dash_DMG+=1;
            this.Dash_BLOCK +=2;

        }
        if (AbstractDungeon.ascensionLevel >= 19) {
            this.Consume_HEAL +=5;
            this.Clock_HP_DIV-=1;

        }

        setHp(this.minHP, this.maxHP);




        this.damage.add(new DamageInfo(this, this.Clock_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Dash_DMG)); // attack 1 damage
        this.damage.add(new DamageInfo(this, this.Slam_DMG)); //attack 2 damagee


        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation) this.animation).myPlayer.addListener(listener);
    }


    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("SoulBGM");
        this.HalfHP = this.maxHealth / 2;
        this.Clock_DMG = AbstractDungeon.player.maxHealth / Clock_HP_DIV;
        this.damage.set(0, new DamageInfo(this, (this.Clock_DMG)));
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)) {
            int nailchance = AbstractDungeon.miscRng.random(0, 99);
            if (nailchance < 33) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x) * Settings.scale;
                this.dialogY = (this.hb_y) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0f));
            }
        }
        //mask = FOX_MASK;
    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {


            case BALL_MOVE: {
                //Whenever the player has more thna 10 Infection, the next turn the Broken Vessel will absorb it all and heal for 2x that amount.
                //It's kinda a self check for letting the boss stack up too much infection unlimited. So this is way better.

                runAnim(BallAnim);
                CardCrawlGame.sound.playV(SoundEffects.BossSoulBall.getKey(), 1.5F);
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new FireballEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.1f));
                randomStatus(AbstractDungeon.monsterRng.random(1, 4));
                randomStatus(AbstractDungeon.monsterRng.random(1, 4));
                int chance = AbstractDungeon.monsterRng.random(0, 99);
                if (chance < 50) {
                    randomStatus(AbstractDungeon.monsterRng.random(1, 4));
                }

                //NEW SFX

                break;
            }
            case SLAM_MOVE: { // dones
                runAnim(SlamAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new VulnerablePower(p, Slam_VAL, true), Slam_VAL));
                break;
            }
            case CLOCK_MOVE: {
                runAnim(ClockAnim);
                for (int i = 0; i < this.Clock_TIMES; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
                }
                break;
            }
            case DASH_MOVE: {
                runAnim(DashAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, Dash_BLOCK));

                //Maybe have a Infection Effect?
                break;
            }
            case INFLATE_MOVE: {

                runAnim(InflateAnim);
                int healval = getPlayerStatusCards().size() * Consume_HEAL;
                CardCrawlGame.sound.playV(SoundEffects.BossSoulConsume.getKey(), 1.2F);
                RemovePlayerStatusCards();
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this,  powerSoulMasterDrained.POWER_ID));
                AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, (healval)));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerSoulMasterConsumed(this)));
                this.isDeflated = false;
                break;
            }
            case DEFLATED_MOVE: {

                //nada
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        AbstractPlayer p = AbstractDungeon.player;
        this.numTurns++;
        this.clockCounter++;
        if (this.hasDeflated){
            slamCounter++;
        }
        if (lastMove(DEFLATED_MOVE)){
            this.setMove(MOVES[INFLATE_MOVE], INFLATE_MOVE, Intent.UNKNOWN);
            return;
        }

        if (this.clockCounter >= this.clockTimer){
            this.setMove(CLOCK_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Clock_TIMES, true);
            this.clockCounter = 0;
            return;
        }

        if (slamCounter >= slamTimer){
            this.setMove(SLAM_MOVE, Intent.ATTACK_DEBUFF, (this.damage.get(2)).base);
            this.slamCounter = 0;
            return;
        }



        if ((num < 15) && (!this.lastMove(SLAM_MOVE)) && !this.lastMoveBefore(SLAM_MOVE)){
            this.setMove(SLAM_MOVE, Intent.ATTACK_DEBUFF, (this.damage.get(2)).base);
            return;
        } else if ((num < 50) && (!this.lastMove(BALL_MOVE))){
            this.setMove(BALL_MOVE, Intent.STRONG_DEBUFF);
            return;
        } else if (!this.lastMoveBefore(DASH_MOVE)){
            this.setMove(DASH_MOVE, Intent.ATTACK_DEFEND,(this.damage.get(1)).base);
        } else {
            this.setMove(BALL_MOVE, Intent.STRONG_DEBUFF);
            return;
        }
    }

    public void damage(DamageInfo info) {
        super.damage(info);

        if ((this.hasDeflated == false) && (this.currentHealth < this.HalfHP)) {
            this.transform();
        } else if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0)) {
            if (!isDeflated) {
                runAnim(HitAnim);
            } else {
                runAnim(DefHitAnim);
            }
        }
    }

    private void randomStatus (int whichone){
        switch (whichone) {
            case 1: {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Burn(), Ball_CARDS));
                break;
            }
            case 2: {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new VoidCard(), Ball_CARDS));
                break;
            }
            case 3: {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new StolenSoul(), Ball_CARDS));
                break;
            }
            case 4: {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new StolenSoul(), Ball_CARDS));
                break;
            }
        }


    }

    private void transform ()
    {
        this.hasDeflated = true;
        this.isDeflated = true;
        CardCrawlGame.sound.playV(SoundEffects.BossSoulDeflate.getKey(),1.6F);
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerSoulMasterDrained(this)));
        runAnim(DeflateAnim);
        this.nextMove = DEFLATED_MOVE;
        this.setMove(DEFLATED_MOVE, Intent.STUN);
        this.intent = (Intent.STUN);

    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:BossSoulMaster");
        NAME = BossSoulMaster.monsterStrings.NAME;
        MOVES = BossSoulMaster.monsterStrings.MOVES;
        DIALOG = BossSoulMaster.monsterStrings.DIALOG;
    }

    @Override
    public void die() {
        stopAnimation();
        useShakeAnimation(5.0F);
        //runAnim("Defeat");
        super.die();
        for (final AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDying) {
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
            }
        }
        this.onBossVictoryLogic();
    }

    private CardGroup getPlayerStatusCards()
    {

        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if ((c.cardID == VoidCard.ID) || (c.cardID == Burn.ID)|| (c.cardID == StolenSoul.ID)) {
                ret.group.add(c);

            }
        }

        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if ((c.cardID == VoidCard.ID) || (c.cardID == Burn.ID)|| (c.cardID == StolenSoul.ID)) {
                ret.group.add(c);
            }
        }

        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if ((c.cardID == VoidCard.ID) || (c.cardID == Burn.ID)|| (c.cardID == StolenSoul.ID)) {
                ret.group.add(c);
            }
        }
        return ret;
    }

    private void RemovePlayerStatusCards()
    {
        AbstractCreature p = AbstractDungeon.player;

        //CardGroup forRemoval = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if ((c.cardID == VoidCard.ID) || (c.cardID == Burn.ID) ||(c.cardID == StolenSoul.ID)) {
                //AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(c,AbstractDungeon.player.hand, true));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new FlyingOrbEffect(p.hb.cX , p.hb.cY), 0.0F));
            }
        }


        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if ((c.cardID == VoidCard.ID) || (c.cardID == Burn.ID) ||(c.cardID == StolenSoul.ID)) {
                //AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(c,AbstractDungeon.player.discardPile, true));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new FlyingOrbEffect(p.hb.cX , p.hb.cY), 0.0F));
            }
        }

        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if ((c.cardID == VoidCard.ID) || (c.cardID == Burn.ID) ||(c.cardID == StolenSoul.ID)) {
                //AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(c,AbstractDungeon.player.drawPile, true));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new FlyingOrbEffect(p.hb.cX , p.hb.cY), 0.0F));
            }
        }
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(IdleAnim);
    }

    public void AltresetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(DefIdleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private BossSoulMaster character;

        public AnimationListener(BossSoulMaster character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (!isDeflated){
                if (!animation.name.equals(IdleAnim)){
                    character.resetAnimation();
                }
            } else {
                if (!animation.name.equals(DefIdleAnim)){
                    character.AltresetAnimation();
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