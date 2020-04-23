package Hallownest.util;

import Hallownest.powers.powerInfection;
import HollowMod.characters.TheBugKnight;
import HollowMod.powers.InfectionPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BugKnightInfectionCrossover {

    public static AbstractPower OGInfection (int InfVal){
        AbstractPower SwitchedPower;
        if (AbstractDungeon.player instanceof TheBugKnight){
            SwitchedPower = new InfectionPower(AbstractDungeon.player, InfVal);
        } else {
            SwitchedPower = new powerInfection(AbstractDungeon.player, InfVal);
        }
        return (SwitchedPower);
    }

}
