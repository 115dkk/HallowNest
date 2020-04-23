package Hallownest.actions;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SFXVAction extends AbstractGameAction
{
    private String key;
    private float volVar;

    public SFXVAction(final String key) {
        this.volVar = 1.1f;
        this.key = key;
        this.actionType = ActionType.WAIT;
    }

    public SFXVAction(final String key, final float volVar) {
        this.volVar = 0.0f;
        this.key = key;
        this.volVar = volVar;
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        CardCrawlGame.sound.playV(this.key, this.volVar);
        this.isDone = true;
    }
}
