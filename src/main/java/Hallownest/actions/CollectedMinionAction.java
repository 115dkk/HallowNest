package Hallownest.actions;

import Hallownest.monsters.GreenpathEnemies.monsterBaldur;
import Hallownest.monsters.KingdomsEdgeEnemies.monsterLittleHopper;
import Hallownest.powers.powerCollected;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.CurlUpPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class CollectedMinionAction extends AbstractGameAction {
    private AbstractPlayer p;

    public CollectedMinionAction(AbstractMonster Monster) {
        this.source = Monster;
    }

    @Override
    public void update() {
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!(m == source)) {

                if (m.moveHistory.size() <=1){
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this.source, new powerCollected(m)));
                    if (m.id != monsterLittleHopper.ID) {
                        m.maxHealth += m.maxHealth;
                        m.currentHealth += m.currentHealth;
                    }
                    if (m.id == monsterBaldur.ID){
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this.source, new CurlUpPower(m, AbstractDungeon.monsterHpRng.random(12, 15))));
                    } else {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this.source, new PlatedArmorPower(m, 4),4));
                    }
                }


            }
        }
        isDone = true;
    }
}
