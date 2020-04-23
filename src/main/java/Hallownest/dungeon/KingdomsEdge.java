package Hallownest.dungeon;

import Hallownest.events.KingdomsEdgeEvents.BeastsDenEvent;
import Hallownest.events.greenpathEvents.DreamersSpecialEvent;
import Hallownest.events.greenpathEvents.TeachersArchiveEvent;
import Hallownest.monsters.GreenpathEnemies.*;
import Hallownest.monsters.KingdomsEdgeEnemies.*;
import Hallownest.scenes.GreenpathScene;
import Hallownest.scenes.KingdomsEdgeScene;
import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;

import java.util.ArrayList;

public class KingdomsEdge extends CustomDungeon {

    public static String ID = "Hallownest:KingdomsEdge";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];
    public static final String OPTION = TEXT[2];

    public KingdomsEdge() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.setMainMusic("audio/music/KingdomsEdge/Kingdoms Edge BG.ogg");
        this.addTempMusic("HollowKnightBGM", "audio/music/KingdomsEdge/Hollow Knight BG.ogg");
        this.addTempMusic("GPZoteBGM", "audio/music/KingdomsEdge/Grey Prince BG.ogg");
        this.addTempMusic("RadianceBGM", "audio/music/KingdomsEdge/Radiance BG.ogg");
        this.addTempMusic("KEEliteBGM", "audio/music/KingdomsEdge/KE Elite BG.ogg");


    }

    public KingdomsEdge(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public KingdomsEdge(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
    }

    @Override
    public String getOptionText() {
        return OPTION;
    }

    @Override
    public AbstractScene DungeonScene() {
        return new KingdomsEdgeScene();
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.25F;
        } else {
            cardUpgradedChance = 0.5F;
        }
    }

    @Override
    protected void generateMonsters() {
        generateWeakEnemies(weakpreset);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
       // monsters.add(new MonsterInfo(monsterHuskWarrior.ID, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.FOOLS_ONE, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.SIBLINGS, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_HOPPERS, 1.5F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }
    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();

        monsters.add(new MonsterInfo(EncounterIDs.FOOLS_TWO, 1.5F));
        monsters.add(new MonsterInfo(monsterPrimalAspid.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.STRONG_BEES, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.STRONG_CORPSES, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.STRONG_MOULDS, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.STRONG_HOPPERS, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.DREAM_WARRIORS, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.INFECTED_HUSKS, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }
    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo(eliteStalkingDevout.ID, 1.0F));
        monsters.add(new MonsterInfo(eliteHiveKnight.ID, 1.0F));
        monsters.add(new MonsterInfo(eliteCollector.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }
    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList();
        switch (monsterList.get(monsterList.size() - 1))
        {
        }

        return retVal;
    }

    @Override
    protected void initializeShrineList() {

        //No shrines
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in HallownestMod.receivePostInitialize()
        //specialOneTimeEventList.clear(); //gets rid of these global events just in case
        //specialOneTimeEventList.add(DreamersSpecialEvent.ID);
        //shrineList.clear(); //gets rid of this shit too just in case
        //shrineList.add(BeastsDenEvent.ID);

    }
}