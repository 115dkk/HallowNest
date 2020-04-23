package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.FlameCloakPower;
import Hallownest.powers.powerCrimsonFlame;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

public class monsterGrimmkinNovice extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterGrimmkinNovice");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterGrimmkinNovice.monsterStrings.NAME;
    public static final String[] MOVES = monsterGrimmkinNovice.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterGrimmkinNovice.monsterStrings.DIALOG;

    private static final byte FLAMEBALL_MOVE = 0;
    private static final byte FLAMEPILLAR_MOVE = 1;
    private static final byte DIVEBOMB_MOVE = 2;






    //Values
    private int  Flameball_VAL = 3;
    private int  Flamepillar_HEAL = 7;
    private int  Divebomb_DMG = 12;
    private int  Divebomb_VAL = 1;



    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 40;
    private int minHP = 36;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String FlamepillarAnim = "Flamepillar";
    private String DivebombAnim = "Divebomb";
    private String FlameballAnim = "Flameball";
    private String HitAnim = "Hit";



    public monsterGrimmkinNovice() {
        this(0.0f, 0.0F);
    }

    public monsterGrimmkinNovice(float x, float y) {
        super(monsterGrimmkinNovice.NAME, ID, 55, 0, 0, 125.0f, 325.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/Grimmkin/GrimmkinNovice.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(0.85f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Divebomb_DMG+=2;
            this.Flamepillar_HEAL+=1;

        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Flameball_VAL+=1;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Divebomb_DMG)); // attack 0 damage


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
            case FLAMEBALL_MOVE:{
                runAnim(FlameballAnim);
                CardCrawlGame.sound.playV(SoundEffects.GrimmkinFireball.getKey(),1.3F);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new powerCrimsonFlame(p, this, Flameball_VAL),(Flameball_VAL-1)));



                break;
            }
            case FLAMEPILLAR_MOVE:{
                runAnim(FlamepillarAnim);
                CardCrawlGame.sound.playV(SoundEffects.GrimmkinCast.getKey(),1.3F);

                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else{
                        AbstractDungeon.actionManager.addToBottom(new VFXAction(new InflameEffect(m), 0.1f));
                        AbstractDungeon.actionManager.addToBottom(new HealAction(m, this, Flamepillar_HEAL));
                    }
                }
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));

                break;
            }
            case DIVEBOMB_MOVE:{
                runAnim(DivebombAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new VulnerablePower(p,Divebomb_VAL, true),Divebomb_VAL));

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
        int moncount = 0;

        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.isDying) {
                continue;
            } else if (m != this) {
                moncount++;
            }
        }
        
        if (((num < 50) && moncount > 0 && !this.lastMove(FLAMEPILLAR_MOVE)) && this.numTurns != 1 ){
            this.setMove(FLAMEPILLAR_MOVE, Intent.BUFF);
            return;
        } else if ((num < 75 && moncount <1)){
            this.setMove(DIVEBOMB_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        } else {
            this.setMove(FLAMEBALL_MOVE, Intent.DEBUFF);

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

        private monsterGrimmkinNovice character;

        public AnimationListener(monsterGrimmkinNovice character) {
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