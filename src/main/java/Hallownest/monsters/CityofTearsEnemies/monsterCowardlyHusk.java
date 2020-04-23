package Hallownest.monsters.CityofTearsEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.monsters.GreenpathEnemies.BossBrokenVessel;
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
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterCowardlyHusk extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterCowardlyHusk");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterCowardlyHusk.monsterStrings.NAME;
    public static final String[] MOVES = monsterCowardlyHusk.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterCowardlyHusk.monsterStrings.DIALOG;

    private static final byte FLURRY_MOVE = 0;
    private static final byte CORNERED_MOVE = 1;
    private static final byte LOOK_FOR_EXIT = 2;
    private static final byte RUN_MOVE = 3;





    //Values
    private int  Flurry_DMG = 3;
    private int  Flurry_HITS = 3;
    private int  Cornered_BLOCK = 11;
    private int  FleeSTR = 4;

    private boolean justran = false;




    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 38;
    private int minHP = 34;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String RunAnim = "Run";
    private String FlurryAnim = "Flurry";
    private String CorneredAnim = "Cornered";

    private String HitAnim = "Hit";



    public monsterCowardlyHusk() {
        this(0.0f, 0.0F);
    }

    public monsterCowardlyHusk(float x, float y) {
        super(monsterCowardlyHusk.NAME, ID, 130, 0, 0, 150.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/CityofTears/HuskDandy/HuskDandy.scml");
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
            this.Cornered_BLOCK +=2;
            //for Ascenction 3 and higher, adds a bit more damage
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.FleeSTR+=1;

        }

        setHp(this.minHP,this.maxHP);


        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Flurry_DMG)); // attack 0 damage



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
            case FLURRY_MOVE:{
                runAnim(FlurryAnim);
                CardCrawlGame.sound.playV(SoundEffects.RichHuskAttack2.getKey(),1.3F);

                for (int i = 0; i < this.Flurry_HITS; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3f));

                }


                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }

            case CORNERED_MOVE:{
                runAnim(CorneredAnim);
               AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,Cornered_BLOCK));


                //CardCrawlGame.sound.playV(SoundEffects.JellyZap.getKey(),1.4F);

                break;
            }
            case LOOK_FOR_EXIT:{
                runAnim(CorneredAnim);
                //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new FrailPower(p,Hop_VAL,true),Hop_VAL));

                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));




                break;
            }
            case RUN_MOVE:{
                this.justran = true;
                runAnim(CorneredAnim);


                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.isDying) {
                        continue;
                    } else if (m.id != this.ID){
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m,this,new StrengthPower(m,FleeSTR),FleeSTR));
                    }
                }


                AbstractDungeon.actionManager.addToBottom(new EscapeAction(this));

                //might need to make it so it's obvious that him running will buff the enemy
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        boolean isScared = false;
        int moncount = 0;

        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.isDying) {
                continue;
            } else if (m != this) {
                moncount++;
                if (m.currentHealth <= m.maxHealth / 2){
                    isScared = true;
                }
            }
        }


        if ((this.lastMove(LOOK_FOR_EXIT)) && moncount > 0){
            this.setMove(MOVES[RUN_MOVE],RUN_MOVE, Intent.ESCAPE);
            return;
        }

        if (((this.numTurns >= 4 ) || (isScared)) && (num % 2 ==0) && moncount > 0){
            this.setMove(MOVES[LOOK_FOR_EXIT],LOOK_FOR_EXIT, Intent.UNKNOWN);
            return;
        }

        if ((num <50) && !this.lastMove(FLURRY_MOVE)){
            this.setMove(FLURRY_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base, this.Flurry_HITS, true);

        } else {
            this.setMove(CORNERED_MOVE, Intent.DEFEND);

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

    public void FledAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(RunAnim);
    }
    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterCowardlyHusk character;

        public AnimationListener(monsterCowardlyHusk character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if ((animation.name.equals(CorneredAnim)) && justran){
                character.FledAnimation();
                justran = false;
            }else if (!animation.name.equals(IdleAnim)) {
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