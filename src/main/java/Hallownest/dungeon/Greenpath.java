package Hallownest.dungeon;

import Hallownest.events.greenpathEvents.DreamersSpecialEvent;
import Hallownest.events.greenpathEvents.TeachersArchiveEvent;
import Hallownest.monsters.GreenpathEnemies.*;
import Hallownest.scenes.GreenpathScene;
import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;

import java.util.ArrayList;

public class Greenpath extends actlikeit.dungeons.CustomDungeon {

    public static String ID = "Hallownest:Greenpath";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];
    public static final String OPTION = TEXT[2];

    public Greenpath() {
        super(NAME, ID, "images/ui/event/panel.png", false, 3, 12, 10);
        this.onEnterEvent(NeowEvent.class);
        this.setMainMusic("audio/music/Greenpath/Green Path Core.ogg");
        this.addTempMusic("HornetBGM", "audio/music/Greenpath/Hornet BG.ogg");
        this.addTempMusic("FalseBGM", "audio/music/Greenpath/FalseBGM.ogg");
        this.addTempMusic("VesselBGM", "audio/music/Greenpath/Broken Vessel BG.ogg");
        this.addTempMusic("GPEliteBGM", "audio/music/Greenpath/GP Elite BG.ogg");
        //this.addTempMusic("MantisBG", "audio/music/CityofTears/MantigBGM.ogg");
    }

    @Override
    public String getOptionText() {
        return OPTION;
    }

    public Greenpath(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Greenpath(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
    }

    @Override
    public AbstractScene DungeonScene() {
        return new GreenpathScene();
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.25F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        cardUpgradedChance = 0.0F;
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
        monsters.add(new MonsterInfo(monsterHuskGuard.ID, 2.0F));
        monsters.add(new MonsterInfo(monsterHuskWarrior.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_UUMA, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_MOSS, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_CRAWLERS_1, 1.3F));
        monsters.add(new MonsterInfo(EncounterIDs.WEAK_BALDURS, 1.5F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }
    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo(monsterAspidMother.ID, 1.4F));
        monsters.add(new MonsterInfo(monsterMossCharger.ID, 1.5F));
        monsters.add(new MonsterInfo(monsterFoolEater.ID, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.FOG_CANYON, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.MOSS_MATES, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.BALDUR_SWARM, 1.3F));
        monsters.add(new MonsterInfo(EncounterIDs.HUSK_PATROL, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.SAD_HUSKS, 1.6F));
        monsters.add(new MonsterInfo(EncounterIDs.STRONG_CRAWLERS, 1.3F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }
    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo(eliteMossKnight.ID, 1.1F));
        monsters.add(new MonsterInfo(eliteCrystalGuardian.ID, 1.0F));
        monsters.add(new MonsterInfo(eliteVengeflyKing.ID, 0.9F));


        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }
    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList();
        switch (monsterList.get(monsterList.size() - 1))
        {
            /*
            case EncounterIDs.WEAK_CRAWLERS_1:
                retVal.add(EncounterIDs.WEAK_BALDURS);
                break;
            case EncounterIDs.WEAK_BALDURS:
                retVal.add(EncounterIDs.WEAK_CRAWLERS_1);
                break;
            case EncounterIDs.STRONG_CRAWLERS:
                retVal.add(EncounterIDs.BALDUR_SWARM);
                break;
            case EncounterIDs.BALDUR_SWARM:
                retVal.add(EncounterIDs.STRONG_CRAWLERS);
                break;
                */
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
        specialOneTimeEventList.clear(); //gets rid of these global events just in case
        specialOneTimeEventList.add(DreamersSpecialEvent.ID);
        //shrineList.clear(); //gets rid of this shit too just in case
        //shrineList.add(TeachersArchiveEvent.ID);
    }
}