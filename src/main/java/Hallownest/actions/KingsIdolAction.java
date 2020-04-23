package Hallownest.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import java.util.ArrayList;
import java.util.Iterator;

public class KingsIdolAction extends AbstractGameAction {
    public static int numPlaced;
    private boolean retrieveCard = false;

    public KingsIdolAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.isDone = true;
        } else if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(this.generateCardChoices(), CardRewardScreen.TEXT[1], true);
            this.tickDuration();
        } else {
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard codexCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                    codexCard.current_x = -1000.0F * Settings.scale;
                    AbstractDungeon.effectList.add(new ShowCardAndAddToDrawPileEffect(codexCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, true));
                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                }

                this.retrieveCard = true;
            }

            this.tickDuration();
        }
    }

    private ArrayList<AbstractCard> generateCardChoices() {
        ArrayList derp = new ArrayList();

        while(derp.size() != 20) {
            boolean dupe = false;
            AbstractCard tmp = AbstractDungeon.returnTrulyRandomColorlessCardInCombat();
            Iterator var4 = derp.iterator();

            while(var4.hasNext()) {
                AbstractCard c = (AbstractCard)var4.next();
                if (c.cardID.equals(tmp.cardID)) {
                    dupe = true;
                    break;
                }
            }

            if (!dupe) {
                derp.add(tmp.makeCopy());
            }
        }

        return derp;
    }
}
