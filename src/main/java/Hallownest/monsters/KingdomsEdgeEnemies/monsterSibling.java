package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.infoCreeper;
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
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterSibling extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterSibling");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterSibling.monsterStrings.NAME;
    public static final String[] MOVES = monsterSibling.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterSibling.monsterStrings.DIALOG;

    private static final byte VIBE_MOVE = 0;
    private static final byte ACTION_MOVE = 1;






    //Values
    private int Vibe_BLOCK = 15;

    private int  Attack_DMG = 15;


    //private int  Attack_VAL = 1;
    private int  Bull_VAL = 2;
    private int  Horn_VAL = 2;
    private int  Crown_VAL = 5;

    private int HealVAL = 15;


    private final static int Bull = 1;
    private final  static  int Horn = 2;
    private final static int Crown = 3;

    private int SiblingType;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 46;
    private int maxHP = 50;

    private int TurnCounter = 0;
    private int TurnReset = 3;
    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)


    private String HitAnim = "Hit";
    //Creeper anims



    private String BullIdle = "IdleBull";
    private String BullAttack = "BullAttack";

    private String HornIdle = "IdleHorn";
    private String HornAttack = "HornAttack";


    private String CrownIdle = "IdleCrown";
    private String CrownAttack = "CrownAttack";

    private String IdleAnim = "IdleHorn";
    private String AttackAnim = "HornAttack";





    public monsterSibling() {
        this(0.0f, 0.0F, 1);
    }

    public monsterSibling(float x, float y,int Sibling) {
        super(monsterSibling.NAME, ID, 55, 0, 0, 125.0f, 175.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/Siblings/Sibling.scml");
        //((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);
        this.type = EnemyType.NORMAL;
        this.SiblingType = Sibling;

        switch (this.SiblingType) {
            case Bull: {

                this.IdleAnim = BullIdle;
                this.AttackAnim = BullAttack;
                break;
            }
            case Horn: {
                this.IdleAnim = HornIdle;
                this.AttackAnim = HornAttack;
                break;
            }
            case Crown: {
                this.IdleAnim = CrownIdle;
                this.AttackAnim = CrownAttack;
                break;
            }
        }

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
            this.Attack_DMG+=2;
            this.Bull_VAL+=1;
            this.HealVAL+=1;

        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Crown_VAL+=2;
            this.Horn_VAL+=1;
            this.HealVAL+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Attack_DMG)); // attack 0 damage

    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RegenPower(this, HealVAL),HealVAL));
    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case VIBE_MOVE:{
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.Vibe_BLOCK));
                //CardCrawlGame.sound.playV(SoundEffects.MantisWarriorSlice.getKey(),1.3F);
                break;
            }
            case ACTION_MOVE:{
                CardCrawlGame.sound.playV(SoundEffects.SiblingAct.getKey(),1.4F);
                runAnim(this.AttackAnim);
                switch (this.SiblingType){
                    case Bull:{
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new WeakPower(p,this.Bull_VAL, true), this.Bull_VAL));

                        break;
                    }
                    case Horn:{
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new VulnerablePower(p,this.Horn_VAL, true), this.Horn_VAL));

                        break;
                    }
                    case Crown:{

                        AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                            if (m.isDying) {
                                continue;
                            } else {
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this,new StrengthPower(m,Crown_VAL),Crown_VAL));
                            }
                        }
                        break;
                    }
                }
                //runAnim(WanderAnim);


                //CardCrawlGame.sound.playV(SoundEffects.MantisWarriorSlice.getKey(),1.3F);



                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        this.TurnCounter++;

        if ((this.TurnCounter == 1) && (this.SiblingType == Bull)){
            this.setMove(ACTION_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
            return;
        } else if ((this.TurnCounter == 2) && (this.SiblingType == Horn)){
            this.setMove(ACTION_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
            return;
        } else if ((this.TurnCounter == 3) && (this.SiblingType == Crown)){
            this.setMove(ACTION_MOVE, Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
            this.TurnCounter = 0;
            return;
        } else {
            this.setMove(VIBE_MOVE, Intent.DEFEND);
        }

        if (TurnCounter >= 3){
            this.TurnCounter = 0;
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

        private monsterSibling character;

        public AnimationListener(monsterSibling character) {
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