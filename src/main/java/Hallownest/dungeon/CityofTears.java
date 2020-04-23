package Hallownest.dungeon;

import Hallownest.events.CityofTearsEvents.WatchersTowerEvent;
import Hallownest.events.greenpathEvents.DreamersSpecialEvent;
import Hallownest.events.greenpathEvents.TeachersArchiveEvent;
import Hallownest.monsters.GreenpathEnemies.*;
import Hallownest.monsters.CityofTearsEnemies.*;
import Hallownest.scenes.CityofTearsScene;
import Hallownest.scenes.GreenpathScene;
import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;

import java.util.ArrayList;

public class CityofTears extends CustomDungeon {

    public static String ID = "Hallownest:CityofTears";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];
    public static final String OPTION = TEXT[2];

    public CityofTears() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.setMainMusic("audio/music/CityofTears/City of Tears BG.ogg");
        this.addTempMusic("CoTEliteBGM", "audio/music/CityofTears/CoT Elite BG.ogg");
        this.addTempMusic("GrimmBGM", "audio/music/CityofTears/GrimmBG.ogg");
        this.addTempMusic("SoulBGM", "audio/music/CityofTears/SoulBG.ogg");
        this.addTempMusic("MantisBG", "audio/music/CityofTears/MantisLordsBG.ogg");


    }

    public CityofTears(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public CityofTears(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
    }

    @Override
    public String getOptionText() {
        return OPTION;
    }

    @Override
    public AbstractScene DungeonScene() {
        return new CityofTearsScene();
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.23F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.125F;
        } else {
            cardUpgradedChance = 0.25F;
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
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_MISTAKES, 1.5F));
        //monsters.add(new MonsterInfo(EncounterIDs.WEAK_COWARDS, 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_WATCH, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_MANTID, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_SEWERS, 1.0F));

        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }
    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo(monsterMisterMushroom.ID, 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.MISTAKE_SWARM, 1.3F));
        monsters.add(new MonsterInfo(EncounterIDs.HUSK_SENTRIES, 1.4F));
        monsters.add(new MonsterInfo(EncounterIDs.FLUKES, 1.3F));
        monsters.add(new MonsterInfo(monsterShrumalOgre.ID, 1.6F));
        monsters.add(new MonsterInfo(EncounterIDs.RICH_HUSKS, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.MANTIDS, 1.4F));
        monsters.add(new MonsterInfo(EncounterIDs.GRIMMKIN, 1.6F));

        // maybe 1 more in brooding mawlek if necessary

        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }
    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo(eliteSoulWarrior.ID, 1.0F));
        monsters.add(new MonsterInfo(eliteWatcherKnight.ID, 1.0F));
        monsters.add(new MonsterInfo(eliteNosk.ID, 1.0F));
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
        //shrineList.add(WatchersTowerEvent.ID);
    }
}