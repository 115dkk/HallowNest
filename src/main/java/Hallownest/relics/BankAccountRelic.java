package Hallownest.relics;

import Hallownest.HallownestMod;
import Hallownest.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

import static Hallownest.HallownestMod.makeRelicOutlinePath;
import static Hallownest.HallownestMod.makeRelicPath;

public class BankAccountRelic extends CustomRelic {

    public static final String ID = HallownestMod.makeID("BankAccountRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("relicBankAccountKey.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("relicBankAccountKey.png"));

    public int GoldLost;
    public BankAccountRelic(){
        this(0);
    }

    public BankAccountRelic(int goldgiven) {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.GoldLost = goldgiven;
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        if (room instanceof ShopRoom) {
            this.flash();
            AbstractDungeon.player.gainGold(25);
        }
    }

    public int getGoldLost(){
        return GoldLost;
    }
    @Override
    public String getUpdatedDescription() {
            return DESCRIPTIONS[0];
    }
}
