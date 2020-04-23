package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerGrandChallenge;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterMantisWarrior extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterMantisWarrior");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterMantisWarrior.monsterStrings.NAME;
    public static final String[] MOVES = monsterMantisWarrior.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterMantisWarrior.monsterStrings.DIALOG;

    private static final byte SIDE_MOVE = 0;
    private static final byte UP_MOVE = 1;
    private static final byte BOW_MOVE = 2;
    private static final byte DANCE_MOVE = 3;






    //Values
    private int  Side_DMG = 11;
    private int  Side_BLOCK = 12;
    private int  Up_DMG = 16;
    private int  Dance_BUFF = 4;
    private int  Bow_DEBUFF = 3;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 64;
    private int minHP = 60;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String UpAnim = "Upslice";
    private String SideAnim = "Sideslice";
    private String BowAnim = "Bow";
    private String DanceAnim = "Dance";
    private String HitAnim = "Hit";



    public monsterMantisWarrior() {
        this(0.0f, 0.0F);
    }

    public monsterMantisWarrior(float x, float y) {
        super(monsterMantisWarrior.NAME, ID, 55, 0, 0, 125.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/MantisWarrior/MantisWarrior.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.90f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 3;
            this.maxHP += 3;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Side_DMG+=1;
            this.Up_DMG+=2;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Dance_BUFF+=2;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Side_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Up_DMG)); // attack 1 damage



    }

    @Override
    public void usePreBattleAction() {
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
            case SIDE_MOVE:{
                runAnim(SideAnim);
                CardCrawlGame.sound.playV(SoundEffects.MantisWarriorSlice.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,Side_BLOCK));
                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case UP_MOVE:{
                runAnim(UpAnim);
                CardCrawlGame.sound.playV(SoundEffects.MantisWarriorSlice.getKey(),1.3F);

                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                break;
            }
            case BOW_MOVE:{
                runAnim(BowAnim);

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new powerGrandChallenge(p,this,Bow_DEBUFF),Bow_DEBUFF));

                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }
            case DANCE_MOVE:{
                runAnim(DanceAnim);

                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else{
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this,new PlatedArmorPower(m,Dance_BUFF),Dance_BUFF));
                    }
                }
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

        if (this.numTurns == 1 ){
            this.setMove(SIDE_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
            return;
        }

        if ((this.lastMove(SIDE_MOVE)) || (this.lastMove(UP_MOVE))){
            if (num % 2 == 0){
                this.setMove(BOW_MOVE, Intent.DEBUFF);
            } else {
                this.setMove(MOVES[DANCE_MOVE],DANCE_MOVE, Intent.BUFF);
            }
        } else {
            if (num % 2 == 0){
                this.setMove(SIDE_MOVE, Intent.ATTACK_DEFEND, ((DamageInfo) this.damage.get(0)).base);
            } else {
                this.setMove(SIDE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
            }
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

        private monsterMantisWarrior character;

        public AnimationListener(monsterMantisWarrior character) {
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