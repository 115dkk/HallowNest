package Hallownest.actions;

import Hallownest.HallownestMod;
import Hallownest.powers.powerInfection;

import Hallownest.util.BugKnightInfectionCrossover;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplyInfectionAction extends AbstractGameAction {

    public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());

    AbstractPower SwitchedPower;
    private int infVal;
    AbstractCreature target;
    AbstractCreature source;


    public ApplyInfectionAction(AbstractCreature target, AbstractCreature source, int inf){
        this.infVal = inf;
        this.target = target;
        this.source = source;
        if (Loader.isModLoaded("hollowmod")){
                //logger.info("Definitely got the Char Checked");
            SwitchedPower = BugKnightInfectionCrossover.OGInfection(infVal);
        } else {
            SwitchedPower = new powerInfection(AbstractDungeon.player, infVal);
        }

    }

    @Override
    public void update(){
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, source, SwitchedPower, infVal));
        isDone = true;
    }



}
