package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.HallownestMod;
import Hallownest.actions.ApplyInfectionAction;
import Hallownest.actions.SFXVAction;
import Hallownest.powers.infoInfHusks;
import Hallownest.powers.powerInfection;
import Hallownest.util.SoundEffects;
import Hallownest.vfx.InfectedProjectileEffect;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class monsterViolentHusk extends AbstractMonster {

    public static final String ID = HallownestMod.makeID("ViolentHusk");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    //Not sure what special encounter this happens for
    //public static final String SPECIAL_ENCOUNTER_ID = InfiniteSpire.createID("Three Voidlings");


    // ****** MOVE AND STAT VALUES ********//
    private int slashdmg = 10;
    private int chargeDmg = 13;
    private int swellAmt = 2;
    private int stdSelf = 5;
    private int popDmg = 30;
    private int popPwr = 5;
    private int maxHP = 185;
    private int minHP = 175;
    // ******* END OF MOVE AND STAT VALUES *********//


    public monsterViolentHusk()
    //Defines the offset of the model loaded i think?
    {
        this(0.0F);
    }
    // the Main "Constructer" iirc that's what the big method/function that the class relies upon is called.
    public monsterViolentHusk(float xOffset)
    {
        //the stuff that gets sent up the line to AbstractMonster to do what it does
    /*here's what these refer to: (final String name, final String id, final int maxHealth, final float hb_x, final float hb_y, final float hb_w, final float hb_h, final String imgUrl,


	final float offsetX, final float offsetY,
	final boolean ignoreBlights: Not included as false by default?
	*/
        super(monsterViolentHusk.NAME, ID, 375, 0.0F, 0.0F, 175.0F, 250.0F, null, xOffset, 0.0F);


        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 25;
            this.maxHP += 25;

        }
        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.slashdmg += 2;
            this.chargeDmg+= 2;
            this.popPwr += 1;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.popPwr += 3;
        }
        //set the min and max hp bounds,
        setHp(this.minHP, this.maxHP);
        //****** DAMAGE INFO ARRAYS? **** //
        //creates a list 0,1,2 of damageinfos to pull from for later.
        this.damage.add(new DamageInfo(this, this.slashdmg)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.chargeDmg)); //attack 1 damage
        // **** END ARRAYS **** //
        loadAnimation(
                //loads the animation
                ("HallownestResources/images/monsters/KingdomsEdge/violenthusk/ViolentHusk.atlas"),
                ("HallownestResources/images/monsters/KingdomsEdge/violenthusk/ViolentHusk.json"), 0.9F);
        //starts the animation called idle i think, im unsire of the 1st variable, but i think the second is the animation name, and the 3rd is a boolean for islooping?
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        //no idea
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new infoInfHusks(this)));
    }
    //take turn is actually one of the later things to occur, it happens AFTER the action has been decided and displayed for a turn as intent. deciding the move happens in GetMove
    public void takeTurn()
    {

        //Just a handy little shortener Blank seems to use to make writing the actions easier
        AbstractPlayer p = AbstractDungeon.player;
        //very simple, it checks what you've assinged as .nextMove's value. that happens in getMove
        switch (this.nextMove)
        {   //0 Swing- att
            case 0: //Charge
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "CHARGE"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.4f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction( p, (DamageInfo)this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3f));
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this, this, this.stdSelf));
                break;
            // Defend
            case 1: //Burst
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "POP"));
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.ZomSplode1.getKey()));
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this, null, this.popDmg));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new InfectedProjectileEffect(this.hb.cX, this.hb.cY, 0.0f)));
                AbstractDungeon.actionManager.addToBottom(new ApplyInfectionAction(p, this, this.popPwr));
                //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new powerInfection(p, this, popPwr),popPwr));
                break;
            case 2: //Strike
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.ZomBlarg.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, (DamageInfo)this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3f));
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this, this, this.stdSelf));
                break;
            case 3: //Swell
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.ZomSpit1.getKey()));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.1f));
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "SWELL"));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, swellAmt), swellAmt));
                break;
        }
        //unsure here, I think it basically  uses an action to send this monsters data to the AbstractMonster.rollMove , which activates the DefaultMonster.getMove and sends a rng amount?
        //this seems to basically be the "get the intent for the next turn's move thing"
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    //this is the method that the ChangeStateAction requires within the class of any monster thant calls it.
    public void changeState(String key)
    {
        switch (key)
        {
            //for each key, it has a simple little transition between animations,
            //for this example, sets the animation to attack, and not looping, then adds the looping idle animation as next in line.
            case "ATTACK":
                this.state.setAnimation(0, "scrape", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                break;
            case "CHARGE":
                this.state.setAnimation(0, "charge", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                break;
            case "SWELL":
                this.state.setAnimation(0, "swell", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                break;
            case"POP":
                this.state.setAnimation(0, "pop", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                break;
        }
    }
    //Unsure, but I think this handles the event of Taking damage, not sure if it's needed or not.
    //basically works just like the change state attack, the oof animation plays once. then it sets the looping idle animation to play again afterwards.
    public void damage(DamageInfo info)
    {
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            this.state.setAnimation(0, "oof", false);
            this.state.addAnimation(0, "idle", true, 0.0F);
        }
        if (this.currentHealth < (this.maxHealth/2)){
            this.nextMove = 1;
            setMove(MOVES[1],(byte)1, Intent.MAGIC);
            this.intent = (Intent.MAGIC);
            this.createIntent();
        }

    }
    //This is where the monster recieves a roll between 0 and 99 (so a full 1/100 chances is easily done) the getMove method uses that number to determine probability of assigning a specific action
    //
    protected void getMove(int i)
    {
        // so for this, it's a modified probability. it's a 30% chance (any roll less than 30) but it's also gauranteed if it's the first turn of the combat
        if ((i < 65) && (this.currentHealth < (this.maxHealth/2))){ // Burst
            setMove(MOVES[1],(byte)1, Intent.MAGIC);
        } else if ((i < 35)&& (!this.lastMove((byte)3))) { // Swell
            setMove(MOVES[2],(byte)3, Intent.DEBUFF);
            //so anything over 44 will be this i think?  so 65% generic attacks.
        } else if ((i < 80) && (!this.lastMove((byte)0))){ // Charge
            setMove((byte) 0, Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
        } else { // Slash
            setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
        }
    }

    public void die() {
        this.state.setTimeScale(0.1f);
        this.useShakeAnimation(5.0f);
        super.die();
    }

    //Assigns byte values to the attack names. I can't find this directly called, maybe it's just put in the output for debugging
    public static class MoveBytes
    {
        public static final byte CHARGE = 0;
        public static final byte BURST = 1;
        public static final byte STRIKE = 2;
        public static final byte SWELL = 3;
    }





}
