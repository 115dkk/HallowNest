package Hallownest.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.CurlUpPower;

public class empowerLouseAction extends AbstractGameAction {
    private AbstractPlayer p;

    public empowerLouseAction(AbstractMonster Monster) {
        this.source = Monster;
    }

    @Override
    public void update() {
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!(m == source)) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this.source, new CurlUpPower(m, AbstractDungeon.monsterHpRng.random(4, 7))));
            }
        }
        isDone = true;
    }
}
