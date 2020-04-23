package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.cards.status.IdeaInstilled;
import Hallownest.cards.status.StolenSoul;
import Hallownest.powers.*;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedProjectileEffect;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;


public class BossHollowKnight extends CustomMonster {
    public static final String ID = HallownestMod.makeID("BossHollowKnight");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte SLASHES_MOVE = 0;
    private static final byte PILLARS_MOVE = 1;
    private static final byte PARRY_MOVE = 2;
    private static final byte SPEW_MOVE = 3;
    private static final byte BOUNCE_MOVE = 4;
    private static final byte CONTRITION_MOVE = 5;
    private static final byte DESPAIR_MOVE = 6;


    //Hornet Values
    private int Slash_DMG = 7;
    private int Slash_HITS = 3;

    private int Swarm_DMG = 9;
    private int Swarm_HITS = 3;
    private int Swarm_BASE_HITS = 3;


    private int Bounce_BLOCK = 20;
    private int Bounce_CARDS = 2;

    private int Contrition_DMG = 15;
    private int Contrition_VAL = 2;
    private int Contrition_CARDS = 1;

    private int Despair_TEMP_PER = 4;
    private int Despair_STR_FOR = 1;

    private int Pillar_CARDS = 1;
    private int RegenGain = 10;



    private int Despair_Chance = -30;


    private int Pillar_Counter = 0;
    private int Pillar_LIMIT = 4;


    private boolean hasThreeFourths = false;

    private boolean hasHalfed = false;

    private boolean hasQuartered = false;

    private int statusCards;


    private boolean hasDeflated = false;
    private boolean isParrying = false;


    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 465;
    private int minHP = 450;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int clockTimer = 6;
    private int clockCounter = 3;
    private int slamTimer = 3;
    private int slamCounter = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String ParryIdleAnim = "Parry";
    private String SlashesAnim = "ThreeSlash";
    private String PillarAnim = "FlamePillars";
    private String ContritionAnim = "Contrition";
    private String DespairAnim = "Despair";
    private String SpewAnim = "Spew";
    private String BounceAnim = "Bouncing";
    private String ParryHitAnim = "ParryHit";
    private String HitAnim = "Hit";


    public BossHollowKnight() {
        this(0.0f, 0.0f);
    }

    public BossHollowKnight(final float x, final float y) {
        super(BossHollowKnight.NAME, ID, 310, -25.0f, 0, 200.0f, 375.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/HollowKnight/HollowKnight.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.95f);
        this.type = EnemyType.BOSS;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 9) {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 35;
            this.maxHP += 35;

        }
        if (AbstractDungeon.ascensionLevel >= 4) {
            this.Slash_DMG += 1;
            this.Bounce_BLOCK+=4;
            this.Swarm_DMG +=1;

        }
        if (AbstractDungeon.ascensionLevel >= 19) {
            this.Despair_STR_FOR +=1;
            this.Despair_TEMP_PER+=2;
            this.Swarm_HITS +=1;
            this.RegenGain+=2;

        }

        setHp(this.minHP, this.maxHP);




        this.damage.add(new DamageInfo(this, this.Slash_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Swarm_DMG)); // attack 1 damage
        this.damage.add(new DamageInfo(this, this.Contrition_DMG)); //attack 2 damagee


        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation) this.animation).myPlayer.addListener(listener);
    }


    @Override
    public void usePreBattleAction() {
        //Setup music, room, any VO or SFX and apply any natural powers.
        AbstractDungeon.getCurrRoom().playBgmInstantly("HollowKnightBGM");
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
    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;


        switch (this.nextMove) {


            case SLASHES_MOVE: {
                //Whenever the player has more thna 10 Infection, the next turn the Broken Vessel will absorb it all and heal for 2x that amount.
                //It's kinda a self check for letting the boss stack up too much infection unlimited. So this is way better.
                runAnim(SlashesAnim);
                for (int i = 0; i < this.Slash_HITS; i++)
                {
                    AbstractDungeon.actionManager.addToBottom(new SFXAction(SoundEffects.GenSword.getKey()));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, (DamageInfo)this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }

                break;
            }
            case PILLARS_MOVE: { // dones
                runAnim(PillarAnim);
                CardCrawlGame.sound.playV(SoundEffects.HKFire.getKey(), 1.2F);
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new IdeaInstilled(), 1));
                break;
            }
            case PARRY_MOVE: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerHKParry(this)));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RegenerateMonsterPower(this, this.RegenGain),this.RegenGain));

                break;
            }
            case SPEW_MOVE: {
                this.isParrying = false;
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, powerHKParry.POWER_ID));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerBlobSwarm(this)));
                runAnim(SpewAnim);
                for (int i = 0; i < this.Swarm_HITS; i++)
                {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedProjectileEffect(this.hb.cX, this.hb.cY, 0.1f)));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.1f));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, (DamageInfo)this.damage.get(1), AbstractGameAction.AttackEffect.POISON));
                }
                this.Swarm_HITS = this.Swarm_BASE_HITS;

                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, powerBlobSwarm.POWER_ID));

                //Maybe have a Infection Effect?
                break;
            }
            case BOUNCE_MOVE: {
                runAnim(BounceAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXAction(SoundEffects.HKBounce.getKey()));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, Bounce_BLOCK));
                AbstractDungeon.actionManager.addToBottom(new SFXAction(SoundEffects.HKBounce.getKey()));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Dazed(), this.Bounce_CARDS));
                break;
            }
            case CONTRITION_MOVE: {
                runAnim(ContritionAnim);
                CardCrawlGame.sound.playV(SoundEffects.HKStab.getKey(), 1.2F);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new VulnerablePower(p, this.Contrition_VAL, true),this.Contrition_VAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new VulnerablePower(this , this.Contrition_VAL, true), this.Contrition_VAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(this, (DamageInfo)this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Wound(), Contrition_CARDS));
                //nada
                break;
            }
            case DESPAIR_MOVE: {
                runAnim(DespairAnim);
                CardCrawlGame.sound.playV(SoundEffects.HKScream.getKey(), 1.2F);
                int HPval = getStatusCardsinExhaust().size() * Despair_TEMP_PER;
                int STRval = getStatusCardsinExhaust().size() / Despair_STR_FOR;
                AbstractDungeon.actionManager.addToBottom(new AddTemporaryHPAction(this, this, HPval));
                if (STRval >0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, STRval), STRval));
                }//nada
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        AbstractPlayer p = AbstractDungeon.player;
        this.numTurns++;
        this.Pillar_Counter++;
        Despair_Chance +=10;


        if (this.hasDeflated){
            slamCounter++;
        }
        if (lastMove(PARRY_MOVE)){
            this.setMove(MOVES[SPEW_MOVE], SPEW_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(1)).base, this.Swarm_HITS, true);
            return;
        }

        if (Pillar_Counter >= Pillar_LIMIT){
            this.setMove(PILLARS_MOVE, Intent.STRONG_DEBUFF);
            this.Pillar_Counter = 0;
            return;
        }
        
        if (num < Despair_Chance){
            this.setMove(DESPAIR_MOVE, Intent.BUFF);
            this.Despair_Chance = -15;
            return;
        }

        if ((this.hasHalfed) && num > 69){
            this.setMove(CONTRITION_MOVE, Intent.MAGIC);
        } else if (((this.hasThreeFourths)&&(num<15)) || ((this.hasHalfed)&&(num<30)) || ((this.hasQuartered)&&(num <45))){
            this.setMove(BOUNCE_MOVE, Intent.DEFEND_DEBUFF);
        } else {
            this.setMove(SLASHES_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Slash_HITS, true);
        }
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0)) {
            if (!isParrying) {
                runAnim(HitAnim);


            } else {
                runAnim(ParryHitAnim);
                if (this.hasPower(powerHKParry.POWER_ID)) {
                    this.Swarm_HITS++;
                    this.setMove(MOVES[SPEW_MOVE], SPEW_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(1)).base, this.Swarm_HITS, true);
                    this.createIntent();
                }
            }
        }

        if ((currentHealth - info.output <= ((this.maxHP/4)*3)) && (!this.hasThreeFourths)){
            ParrySwitch();
            this.hasThreeFourths = true;
        }

        if ((currentHealth - info.output <= ((this.maxHP/4)*2)) && (!this.hasHalfed)){
            ParrySwitch();
            this.hasHalfed = true;
        }

        if ((currentHealth - info.output <= (this.maxHP/4)) && (!this.hasQuartered)){
            ParrySwitch();
            this.hasQuartered = true;
        }
    }

    private void ParrySwitch ()
    {
        this.isParrying = true;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new powerPlatedThorns(this, 5), 5));
        this.nextMove = PARRY_MOVE;
        this.setMove(PARRY_MOVE, Intent.UNKNOWN);
        this.intent = (Intent.UNKNOWN);
        this.createIntent();

    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:BossHollowKnight");
        NAME = BossHollowKnight.monsterStrings.NAME;
        MOVES = BossHollowKnight.monsterStrings.MOVES;
        DIALOG = BossHollowKnight.monsterStrings.DIALOG;
    }

    @Override
    public void die() {
        stopAnimation();
        useShakeAnimation(5.0F);
        //runAnim("Defeat");
        super.die();
        this.onBossVictoryLogic();
        this.onFinalBossVictoryLogic();
    }

    private CardGroup getStatusCardsinExhaust()
    {

        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
            if ((c.type == AbstractCard.CardType.STATUS)){
                ret.group.add(c);
            }
        }
        return ret;
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
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(ParryIdleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private BossHollowKnight character;

        public AnimationListener(BossHollowKnight character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (!isParrying){
                if (!animation.name.equals(IdleAnim)){
                    character.resetAnimation();
                }
            } else {
                if (!animation.name.equals(ParryIdleAnim)){
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