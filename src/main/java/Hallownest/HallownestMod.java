package Hallownest;

import Hallownest.RazIntent.CustomIntent;
import Hallownest.RazIntent.DeathIntent;
import Hallownest.cards.*;
import Hallownest.cards.status.*;
import Hallownest.dungeon.CityofTears;
import Hallownest.dungeon.EncounterIDs;
import Hallownest.dungeon.Greenpath;
import Hallownest.dungeon.KingdomsEdge;
import Hallownest.events.CityofTearsEvents.*;
import Hallownest.events.KingdomsEdgeEvents.*;
import Hallownest.events.greenpathEvents.*;
import Hallownest.monsters.CityofTearsEnemies.*;
import Hallownest.monsters.GreenpathEnemies.*;
import Hallownest.monsters.KingdomsEdgeEnemies.*;
import Hallownest.relics.*;
import Hallownest.util.IDCheckDontTouchPls;
import Hallownest.util.SoundEffects;
import Hallownest.util.TextureLoader;
import Hallownest.variables.DefaultCustomVariable;
import Hallownest.variables.DefaultSecondMagicNumber;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SpireInitializer
public class HallownestMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        AddAudioSubscriber {

    public static final Logger logger = LogManager.getLogger(HallownestMod.class.getName());
    private static String modID;

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Hallownest";
    private static final String AUTHOR = "VenIM";
    private static final String DESCRIPTION = "3 Alternate Acts, Greenpath, City of Tears and Kingdoms Edge, Inspired by Hollow Knight.";
    
    // =============== INPUT TEXTURE LOCATION =================
    
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "HallownestResources/images/Badge.png";

      //public static final Color URBAN_LEGEND = CardHelper.getColor(0, 0, 0);

    // Card backgrounds - The actual rectangular card.


    // =============== MAKE IMAGE PATHS =================
    
    public static String makeCardPath(String resourcePath) {
        return getModID() + "Resources/images/cards/" + resourcePath;
    }
    
    public static String makeRelicPath(String resourcePath) {
        return getModID() + "Resources/images/relics/" + resourcePath;
    }
    
    public static String makeRelicOutlinePath(String resourcePath) {
        return getModID() + "Resources/images/relics/outline/" + resourcePath;
    }
    
    public static String makePowerPath(String resourcePath) {
        return getModID() + "Resources/images/powers/" + resourcePath;
    }
    
    public static String makeEventPath(String resourcePath) {
        return getModID() + "Resources/images/events/" + resourcePath;
    }

    public static String makeAudioPath(String resourcePath) {
        return getModID() + "Resources/sounds/" + resourcePath;
    }

    public static String makeEffectPath(String resourcePath) {
        return getModID() + "Resources/images/effects/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return getModID() + "Resources/images/ui/" + resourcePath;
    }
    
    // =============== /MAKE IMAGE PATHS/ =================
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================
    
    public HallownestMod() {
        logger.info("Subscribe to BaseMod hooks");
      
        setModID("Hallownest");
        
        logger.info("Done subscribing");

        /*
        BaseMod.addColor(Enums.URBAN_LEGEND, URBAN_LEGEND, URBAN_LEGEND, URBAN_LEGEND,
                URBAN_LEGEND, URBAN_LEGEND, URBAN_LEGEND, URBAN_LEGEND,
                ATTACK_BLACK, SKILL_BLACK, POWER_BLACK, ENERGY_ORB_BLACK,
                ATTACK_BLACK_PORTRAIT, SKILL_BLACK_PORTRAIT, POWER_BLACK_PORTRAIT,
                ENERGY_ORB_BLACK_PORTRAIT, CARD_ENERGY_ORB);
                */
    }
    
    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = HallownestMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO
    
    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH
    
    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NNOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = HallownestMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = HallownestMod.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO
    
    // ====== YOU CAN EDIT AGAIN ======
    
    
    @SuppressWarnings("unused")
    public static void initialize() {
        HallownestMod hallownestMod = new HallownestMod();
        BaseMod.subscribe(hallownestMod);
    }
    
    
    // =============== POST-INITIALIZE =================
    
    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");
        
        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        //NEW ACTS

        (new Greenpath()).addAct(Exordium.ID);
        (new CityofTears()).addAct(TheCity.ID);
        (new KingdomsEdge()).addAct(TheBeyond.ID);


        //GREENPATH SHIT


        BaseMod.addMonster(BossHornet.ID, (BaseMod.GetMonster)BossHornet::new);
        BaseMod.addMonster(BossBrokenVessel.ID, (BaseMod.GetMonster) BossBrokenVessel::new);
        BaseMod.addMonster(BossFalseKnight.ID, (BaseMod.GetMonster) BossFalseKnight::new);
        BaseMod.addMonster(eliteVengeflyKing.ID, () -> new MonsterGroup(new AbstractMonster[]{
                new eliteVengeflyKing(100.0F, 0.0F),
                new minionFly(-175.0F, 75.0F),
        }));
        BaseMod.addMonster(eliteCrystalGuardian.ID, (BaseMod.GetMonster)eliteCrystalGuardian::new);
        BaseMod.addMonster(eliteMossKnight.ID, () -> new eliteMossKnight(0.0F));
        BaseMod.addMonster(monsterAspidMother.ID, () -> new monsterAspidMother(50.0F, 50.0F));
        BaseMod.addMonster(monsterWanderingHusk.ID, (BaseMod.GetMonster)monsterWanderingHusk::new);
        BaseMod.addMonster(monsterHornedHusk.ID, (BaseMod.GetMonster)monsterHornedHusk::new);
        BaseMod.addMonster(monsterHuskWarrior.ID, () -> new monsterHuskWarrior(0.0F));
        BaseMod.addMonster(monsterHuskGuard.ID, (BaseMod.GetMonster)monsterHuskGuard::new);
        BaseMod.addMonster(monsterCrawlid.ID, (BaseMod.GetMonster)monsterCrawlid::new);
        BaseMod.addMonster(monsterTikTik.ID, (BaseMod.GetMonster)monsterTikTik::new);
        BaseMod.addMonster(monsterBaldur.ID, (BaseMod.GetMonster)monsterBaldur::new);
        BaseMod.addMonster(monsterOoma.ID, (BaseMod.GetMonster)monsterUuma::new);
        BaseMod.addMonster(monsterUuma.ID, (BaseMod.GetMonster)monsterOoma::new);
        BaseMod.addMonster(monsterFoolEater.ID, (BaseMod.GetMonster)monsterFoolEater::new);
        BaseMod.addMonster(monsterMosskin.ID, (BaseMod.GetMonster)monsterMosskin::new);
        BaseMod.addMonster(monsterMossCreep.ID, (BaseMod.GetMonster)monsterMossCreep::new);
        BaseMod.addMonster(monsterMossCharger.ID, () -> new monsterMossCharger(-50.0F));
        BaseMod.addMonster(minionBlob.ID, (BaseMod.GetMonster)minionBlob::new);
        BaseMod.addMonster(minionBabyAspid.ID, (BaseMod.GetMonster)minionBabyAspid::new);
        BaseMod.addMonster(minionFly.ID, (BaseMod.GetMonster)minionFly::new);
        BaseMod.addMonster(EncounterIDs.FOG_CANYON, "1_Ooma_and_2_Uuma", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterUuma(-350.0F, 85.0F),
                        new monsterOoma(-50.0F, 0.0F),
                        new monsterUuma(150.0F, 100.0F),
                }));
        BaseMod.addMonster(EncounterIDs.WEAK_UUMA, "2_Uuma", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterUuma(-175.0F, 125.0F),
                        new monsterUuma(50.0F, 50.0F),
                }));
        BaseMod.addMonster(EncounterIDs.HUSK_PATROL, "Guard_and_Warrior", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterHuskGuard(-150.0F, 0.0F),
                        new monsterHuskWarrior(150.0F),
                }));

        BaseMod.addMonster(EncounterIDs.SAD_HUSKS, "Wandering_and_Horned", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterWanderingHusk(-150.0F, 0.0F, false),
                        new monsterHornedHusk(150.0F, 0.0f, false),
                }));

        BaseMod.addMonster(EncounterIDs.WEAK_MOSS, "2_MossCreeps", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterMossCreep(-175.0F),
                        new monsterMossCreep(75.0F, 15.0f),
                }));
        BaseMod.addMonster(EncounterIDs.MOSS_MATES, "MossCreep_and_Mosskin", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterMossCreep(-150.0F, -10.0f),
                        new monsterMosskin(100.0F),
                }));
        BaseMod.addMonster(EncounterIDs.STRONG_CRAWLERS, "2_Tiktiks_1_Baldur_1_Crawlid", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterBaldur(-200.0F, 15.0f),
                        new monsterTikTik(-50.0F, -10.0f),
                        new monsterCrawlid(100.0F),
                        new monsterTikTik(250.0F, 5.0f),
                }));
        BaseMod.addMonster(EncounterIDs.WEAK_CRAWLERS_1, "2_Tiktiks_and_1_Crawlid", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterTikTik(-250.0F, 5.0f),
                        new monsterCrawlid(-60.0F , -25.0f),
                        new monsterTikTik(100.0F),
                }));

        BaseMod.addMonster(EncounterIDs.WEAK_BALDURS, "2_Baldur", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterBaldur(-85.0F, -25.0f),
                        new monsterBaldur(100.0F, 0.0f),
                }));

        BaseMod.addMonster(EncounterIDs.BALDUR_SWARM, "1_Baldur_1_Crawlid_1_Warrior", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterBaldur(-175.0F, -15.0f),
                        new monsterHuskWarrior(0.0F),
                        new monsterCrawlid(175.0F, -5.0f),
                }));



        //news
        //end
        BaseMod.addBoss(Greenpath.ID, BossHornet.ID, "HallownestResources/images/monsters/Greenpath/Hornet/BossIcon.png", "HallownestResources/images/monsters/Greenpath/Hornet/BossIconOutline.png");
        BaseMod.addBoss(Greenpath.ID, BossBrokenVessel.ID, "HallownestResources/images/monsters/Greenpath/BrokenVessel/BossIcon.png", "HallownestResources/images/monsters/Greenpath/BrokenVessel/BossIconOutline.png");
        BaseMod.addBoss(Greenpath.ID, BossFalseKnight.ID, "HallownestResources/images/monsters/Greenpath/falseknight/BossIcon.png", "HallownestResources/images/monsters/Greenpath/falseknight/BossIconOutline.png");




        // =============== CITY OF TEARS ENEMIES AND ENCOUNTERS =================

        BaseMod.addMonster(BossMantisLord.ID, () -> new MonsterGroup(new AbstractMonster[]{
                new BossMantisLord( -375.0F, -10.0F, false),
                new BossMantisLord(-125.0F, 0.0F, true),
                new BossMantisLord( 125.0F, -10.0F, false)
        }));
        BaseMod.addMonster(BossSoulMaster.ID, () -> new BossSoulMaster(-50.0F, 75.0f));
        BaseMod.addMonster(BossGrimm.ID, () -> new BossGrimm(-25.0F, 0.0F));


        BaseMod.addMonster(monsterMisterMushroom.ID, () -> new monsterMisterMushroom(0.0F, 0.0F));
        BaseMod.addMonster(monsterShrumalOgre.ID, () -> new monsterShrumalOgre(-50.0F, -10.0F));
        BaseMod.addMonster(EncounterIDs.HUSK_SENTRIES, "2_Husk_Sentries", () -> new MonsterGroup(
                new AbstractMonster[] {
                    new monsterHuskSentry(-250.0F),
                    new monsterHuskSentry(50.0F)
        }));
        BaseMod.addMonster(EncounterIDs.RICH_HUSKS, "Glutton_and_Coward", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterGluttonousHusk(-200.0F, 0.0F),
                        new monsterCowardlyHusk(45.0f,-10.0F)
                }));
        BaseMod.addMonster(EncounterIDs.MANTIDS, "Warrior_and_Youth", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterMantisYouth(-200.0F, 10.0F),
                        new monsterMantisWarrior(100.0f,-10.0F)
                }));
        BaseMod.addMonster(EncounterIDs.GRIMMKIN, "Master_and_Novice", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterGrimmkinMaster(-250.0F, 60.0F),
                        new monsterGrimmkinNovice(75.0f,90.0f)
                }));
        BaseMod.addMonster(EncounterIDs.FLUKES, "2_Flukemon", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterFlukemon(-250.0F, 5.0F),
                        new monsterFlukemon(100.0f,-5.0F)
                }));
        BaseMod.addMonster(EncounterIDs.MISTAKE_SWARM, "3_Mistakes", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterMistake(-355.0F, 0.0f),
                        new monsterMistake(-185.0F, -25.0f),
                        new monsterMistake(35.0F, 10.0f),

                }));
        /// WEAK ENCOUNTERS
        BaseMod.addMonster(EncounterIDs.WEAK_SEWERS, "Flukemon_and_Top", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterFlukemon(-250.0F, 5.0F),
                        new monsterFluketop(100.0f,75.0f)
                }));
        BaseMod.addMonster(EncounterIDs.WEAK_WATCH, "Guard_and_Sentry", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterHuskSentry(-200.0F),
                        new monsterHuskGuard(100.0F, 0.0F),
                }));
        BaseMod.addMonster(EncounterIDs.WEAK_MANTID, "3_Youths", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterMantisYouth(-300.0F, 20.0F),
                        new monsterMantisYouth(-100.0F, 9.0F),
                        new monsterMantisYouth(150.0F, 11.0F),
                }));
        BaseMod.addMonster(EncounterIDs.WEAK_MISTAKES, "2_Mistakes", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterMistake(-255.0F, 0.0f),
                        new monsterMistake(185.0F, -15.0f)
                }));
        BaseMod.addMonster(EncounterIDs.WEAK_COWARDS, "3_Cowards", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterCowardlyHusk(-255.0F, 0.0f),
                        new monsterCowardlyHusk(-25.0F, 10.0f),
                        new monsterCowardlyHusk(185.0F, -15.0f)
                }));



        BaseMod.addMonster(eliteNosk.ID, () -> new eliteNosk(0.0F));
        BaseMod.addMonster(eliteSoulWarrior.ID, () -> new eliteSoulWarrior(0.0f,0.0F));
        BaseMod.addMonster(eliteWatcherKnight.ID, () -> new MonsterGroup(new AbstractMonster[]{
                new eliteWatcherKnight(-300.0F, -25.0F),
                new eliteWatcherKnight(125.0F, 0.0F),
        }));




        BaseMod.addBoss(CityofTears.ID, BossMantisLord.ID, "HallownestResources/images/monsters/CityofTears/mantislords/BossIcon.png", "HallownestResources/images/monsters/CityofTears/mantislords/BossIconOutline.png");
        BaseMod.addBoss(CityofTears.ID, BossGrimm.ID, "HallownestResources/images/monsters/CityofTears/Grimm/BossIcon.png", "HallownestResources/images/monsters/CityofTears/Grimm/BossIconOutline.png");
        BaseMod.addBoss(CityofTears.ID, BossSoulMaster.ID, "HallownestResources/images/monsters/CityofTears/SoulMaster/BossIcon.png", "HallownestResources/images/monsters/CityofTears/SoulMaster/BossIconOutline.png");

        // =============== KINGDOM'S EDGE ENEMIES AND ENCOUNTERS =================


        //Generics
        BaseMod.addMonster(BossHollowKnight.ID, () -> new BossHollowKnight(0.0F, 0.0F));
        BaseMod.addMonster(BossRadiance.ID, () -> new BossRadiance());

        BaseMod.addMonster(BossGreyPrinceZote.ID, () -> new BossGreyPrinceZote(150.0F, 0.0F));


        BaseMod.addMonster(monsterGreatHopper.ID, () -> new monsterGreatHopper(0.0f, 0.0f, false));
        BaseMod.addMonster(monsterLittleHopper.ID, () -> new monsterLittleHopper(0.0f, 0.0f, false));

        //BOSSES

        BaseMod.addBoss(KingdomsEdge.ID, BossRadiance.ID, "HallownestResources/images/monsters/KingdomsEdge/radiance/BossIcon.png", "HallownestResources/images/monsters/KingdomsEdge/radiance/BossIconOutline.png");
        BaseMod.addBoss(KingdomsEdge.ID, BossHollowKnight.ID, "HallownestResources/images/monsters/KingdomsEdge/HollowKnight/BossIcon.png", "HallownestResources/images/monsters/KingdomsEdge/HollowKnight/BossIconOutline.png");
        BaseMod.addBoss(KingdomsEdge.ID, BossGreyPrinceZote.ID, "HallownestResources/images/monsters/KingdomsEdge/GreyPrince/BossIcon.png", "HallownestResources/images/monsters/KingdomsEdge/GreyPrince/BossIconOutline.png");



        BaseMod.addMonster(minionWingedZoteling.ID, (BaseMod.GetMonster)minionWingedZoteling::new);
        BaseMod.addMonster(minionHoppingZoteling.ID, (BaseMod.GetMonster)minionHoppingZoteling::new);
        BaseMod.addMonster(minionVolatileZoteling.ID, (BaseMod.GetMonster)minionVolatileZoteling::new);


        // ELITES
        BaseMod.addMonster(eliteStalkingDevout.ID, () -> new MonsterGroup(new AbstractMonster[]{
                new monsterLittleWeaver(-500.0F, -10.0F),
                new monsterLittleWeaver(-250.0F, 10.0F),
                new eliteStalkingDevout(100.0F),

        }));
        BaseMod.addMonster(eliteHiveKnight.ID, () -> new eliteHiveKnight(0.0f,0.0F));
        BaseMod.addMonster(eliteCollector.ID, () -> new eliteCollector(200.0f,7.0F));


        //STRONG ENEMIES
        BaseMod.addMonster(EventZote.ID, () -> new EventZote(50.0F));

        BaseMod.addMonster(monsterPrimalAspid.ID, () -> new monsterPrimalAspid(-45.0f,100.0F));

        BaseMod.addMonster(EncounterIDs.INFECTED_HUSKS, "Slobbering_and_Violent", () -> new MonsterGroup(
                new AbstractMonster[] {
                    new monsterViolentHusk(-250.0F),
                    new monsterSlobberingHusk(50.0F)

        }));
        BaseMod.addMonster(EncounterIDs.STRONG_HOPPERS, "2_Great", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterGreatHopper(-245.0F, 0.0f, true),
                        new monsterGreatHopper(185.0F, -15.0f, false)
                }));
        BaseMod.addMonster(EncounterIDs.DREAM_WARRIORS, "Hallownest:Xero_Hu_Marmu", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterXero(-385.0F, 25.0F),
                        new monsterElderHu(-90.0F, 45.0f),
                        new monsterMarmu(160.0F, 30.0F)
                }));


        BaseMod.addMonster(EncounterIDs.STRONG_CORPSES, "2_Wandering_and_1_Horned", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterWanderingHusk(-225.0F, -5.0F, true),
                        new monsterHornedHusk(-20.0F, 7.0f, true),
                        new monsterWanderingHusk(150.0F, 0.0F, true)
                }));

        BaseMod.addMonster(EncounterIDs.STRONG_MOULDS, "2_Wing_and_1_Kings", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterWingmould(-295.0F, 125.0F),
                        new monsterKingsmould(-25.0F, -10.0f),
                        new monsterWingmould(180.0F, 145.0F)
                }));


        BaseMod.addMonster(EncounterIDs.STRONG_BEES, "2_Soldiers_and_1_Guardian", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterHiveSoldier(-375.0F, 115.0F),
                        new monsterHiveSoldier(-125.0F, 85.0f),
                        new monsterHiveGuardian(150.0F, 125.0F)
                }));


        BaseMod.addMonster(EncounterIDs.FOOLS_TWO, "Shield_Sturdy", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterShieldedFool(-275.0F, 0.0F),
                        new monsterSturdyFool(50.0F, -10.0F)
                }));


        BaseMod.addMonster(EncounterIDs.FOOLS_FULL, "Shield_Sturdy_Heavy", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterShieldedFool(-395.0F, 0.0F),
                        new monsterSturdyFool(-165.0F, -10.0F),
                        new monsterHeavyFool(80.0F, 0.0F)
                }));

        BaseMod.addMonster(EncounterIDs.ASPID_NEST, "2_Primals", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterPrimalAspid(-255.0F, 60.0F),
                        new monsterPrimalAspid(-25.0F, 95.0f)
                }));
        //WEAK ENEMIES

        BaseMod.addMonster(EncounterIDs.WEAK_HOPPERS, "2_Littles", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterLittleHopper(-345.0F, -20.0f, true),
                        new monsterLittleHopper(-165.0F, 0.0f, false),
                        new monsterLittleHopper(85.0F, -10.0f,true)
                }));
        BaseMod.addMonster(EncounterIDs.SIBLINGS, "3_Siblings", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterSibling(-285.0F, 45.0F, 1),
                        new monsterSibling(-75.0F, 85.0f, 2),
                        new monsterSibling(100.0F, 20.0F, 3)
                }));

        BaseMod.addMonster(EncounterIDs.FOOLS_ONE, "Shield_Heavy", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new monsterShieldedFool(-275.0F, 0.0F),
                        new monsterHeavyFool(50.0F, -10.0F)
                }));






        //The fuck is this?
        CustomIntent.add(new DeathIntent());
        
        // =============== EVENTS =================
        //unique events for using shrines or one time only stuff

        BaseMod.addEvent(TeachersArchiveEvent.ID, TeachersArchiveEvent.class,Greenpath.ID);
        BaseMod.addEvent(WatchersTowerEvent.ID, WatchersTowerEvent.class,CityofTears.ID);
        BaseMod.addEvent(BeastsDenEvent.ID, BeastsDenEvent.class, KingdomsEdge.ID);

        BaseMod.addEvent(DreamersSpecialEvent.ID, DreamersSpecialEvent.class, Greenpath.ID);


        // --------------GREENPATH EVENTS------------
        //Unique Events

        //
        BaseMod.addEvent(ElderbugEvent.ID, ElderbugEvent.class, Greenpath.ID);
        BaseMod.addEvent(HunterEvent.ID, HunterEvent.class, Greenpath.ID);
        BaseMod.addEvent(StagwaysEvent1.ID, StagwaysEvent1.class, Greenpath.ID);
        BaseMod.addEvent(GhostsofGreenpath.ID, GhostsofGreenpath.class, Greenpath.ID);
        BaseMod.addEvent(ZoteEvent1.ID, ZoteEvent1.class, Greenpath.ID);
        BaseMod.addEvent(GrimmEvent.ID, GrimmEvent.class, Greenpath.ID);
        BaseMod.addEvent(LakeofUnnEvent.ID, LakeofUnnEvent.class, Greenpath.ID);
        BaseMod.addEvent(NailmasterSheoEvent.ID, NailmasterSheoEvent.class, Greenpath.ID);
        BaseMod.addEvent(CorniferEvent1.ID, CorniferEvent1.class, Greenpath.ID);
        BaseMod.addEvent(WhiteLadyEvent.ID, WhiteLadyEvent.class, Greenpath.ID);
        BaseMod.addEvent(BankEvent1.ID, BankEvent1.class, Greenpath.ID);
        BaseMod.addEvent(SalubrasShopEvent.ID, SalubrasShopEvent.class, Greenpath.ID);
        BaseMod.addEvent(IseldasKeepingEvent.ID, IseldasKeepingEvent.class, Greenpath.ID);




        // --------------CITY OF TEARS EVENTS------------
        BaseMod.addEvent(CorniferEvent2.ID, CorniferEvent2.class, CityofTears.ID);
        BaseMod.addEvent(StagwaysEvent2.ID, StagwaysEvent2.class, CityofTears.ID);
        BaseMod.addEvent(NailmasterMatoEvent.ID, NailmasterMatoEvent.class, CityofTears.ID);
        BaseMod.addEvent(RelicSeekerLemmEvent.ID, RelicSeekerLemmEvent.class, CityofTears.ID);
        BaseMod.addEvent(MadameDivineEvent.ID, MadameDivineEvent.class, CityofTears.ID);
        BaseMod.addEvent(TheSaunaEvent.ID, TheSaunaEvent.class, CityofTears.ID);
        BaseMod.addEvent(FungalCoreEvent.ID, FungalCoreEvent.class, CityofTears.ID);
        BaseMod.addEvent(MarissasSongEvent.ID, MarissasSongEvent.class, CityofTears.ID);
        BaseMod.addEvent(NailsmithEvent.ID, NailsmithEvent.class, CityofTears.ID);
        BaseMod.addEvent(HollowKnightMemorial.ID, HollowKnightMemorial.class, CityofTears.ID);
        BaseMod.addEvent(ZoteEvent2.ID, ZoteEvent2.class, CityofTears.ID);
        BaseMod.addEvent(FallenFuryEvent.ID, FallenFuryEvent.class, CityofTears.ID);
        BaseMod.addEvent(EmilitiaEvent.ID, EmilitiaEvent.class, CityofTears.ID);













        // --------------KINGDOM'S EDGE EVENTS------------
        BaseMod.addEvent(NailmasterOroEvent.ID, NailmasterOroEvent.class, KingdomsEdge.ID);
        BaseMod.addEvent(CorniferEvent3.ID, CorniferEvent3.class, KingdomsEdge.ID);
        BaseMod.addEvent(StagwaysEvent3.ID, StagwaysEvent3.class, KingdomsEdge.ID);
        BaseMod.addEvent(KEWhitePalaceEvent.ID, KEWhitePalaceEvent.class, KingdomsEdge.ID);
        BaseMod.addEvent(KEColosseumEvent.ID, KEColosseumEvent.class, KingdomsEdge.ID);
        BaseMod.addEvent(KEAspidNestEvent.ID, KEAspidNestEvent.class, KingdomsEdge.ID);
        BaseMod.addEvent(ZoteEvent3.ID, ZoteEvent3.class, KingdomsEdge.ID);
        BaseMod.addEvent(KELighthouseEvent.ID, KELighthouseEvent.class, KingdomsEdge.ID);
        BaseMod.addEvent(KEFountainEvent.ID, KEFountainEvent.class, KingdomsEdge.ID);



        // =============== /EVENTS/ =================

        logger.info("Done loading badge Image and mod options");
    }


    
    // =============== / POST-INITIALIZE/ =================



    // =============== / NEW SOUNDS, TRY TO GO EASY HERE BUD LIKE 2 PER THING, 3 FOR BOSSES OR ELITES MAYBE/ =================
    @Override
    public void receiveAddAudio() {
        //Event Sounds
        addAudio(SoundEffects.EvGpBanker);
        addAudio(SoundEffects.EvGpCornifer);
        addAudio(SoundEffects.EvGpCorniferHum);
        addAudio(SoundEffects.EvGpDreamerAmbience);
        addAudio(SoundEffects.EvGpDreamerEnter);
        addAudio(SoundEffects.EvGpDreamerSeer);
        addAudio(SoundEffects.EvGpElder);
        addAudio(SoundEffects.EvGpGrimm);
        addAudio(SoundEffects.EvGpGrimm2);
        addAudio(SoundEffects.EvGpHunter);
        addAudio(SoundEffects.EvGpIselda);
        addAudio(SoundEffects.EvGpQueen1);
        addAudio(SoundEffects.EvGpQueen2);
        addAudio(SoundEffects.EvGpQuirrel);
        addAudio(SoundEffects.EvGpQuirrel2);
        addAudio(SoundEffects.EvGpShaman);
        addAudio(SoundEffects.EvGpSheo);
        addAudio(SoundEffects.EvGpStag1);
        addAudio(SoundEffects.EvGpStag2);
        addAudio(SoundEffects.EvGpStag3);
        addAudio(SoundEffects.EvGpStagRumble);
        addAudio(SoundEffects.EvGpZote1);
        addAudio(SoundEffects.EvGpZote2);
        addAudio(SoundEffects.EvGpZote3);
        addAudio(SoundEffects.EvGpSalubraGreet);
        addAudio(SoundEffects.EvGpSalubraKiss);

        addAudio(SoundEffects.DreamNailSound);

        addAudio(SoundEffects.SentryBuzz);
        addAudio(SoundEffects.SentryBrava);
        //Enemy Sounds

        addAudio(SoundEffects.VOHornetDash);
        addAudio(SoundEffects.VOHornetDeath);
        addAudio(SoundEffects.VOHornetGoss);
        addAudio(SoundEffects.VOHornetIntro);
        addAudio(SoundEffects.VOHornetNeedle);
        addAudio(SoundEffects.VOHornetParry);
        addAudio(SoundEffects.VOHornetSpikes);
        addAudio(SoundEffects.SFXHornetCatch);
        addAudio(SoundEffects.SFXHornetDash);
        addAudio(SoundEffects.SFXHornetNeedle);
        addAudio(SoundEffects.SFXHornetPing);
        addAudio(SoundEffects.SFXHornetSpikes);

        addAudio(SoundEffects.VOBrkSeal);
        addAudio(SoundEffects.SFXBlobPop);
        addAudio(SoundEffects.SFXBrkCascade);
        addAudio(SoundEffects.SFXBrkDash);
        addAudio(SoundEffects.SFXBrkSlam);

        addAudio(SoundEffects.SFXFlyAttack);
        addAudio(SoundEffects.SFXFlyPoon);
        addAudio(SoundEffects.SFXVFKingBuzz);
        addAudio(SoundEffects.SFXVFKingScreech);
        addAudio(SoundEffects.SFXVFKingSwoop);

        addAudio(SoundEffects.SFXCrystalBlast);
        addAudio(SoundEffects.SFXCrystalRage);

        addAudio(SoundEffects.SFXBabyDestroy);
        addAudio(SoundEffects.SFXBabyProtect);
        addAudio(SoundEffects.SFXMotherBirth);
        addAudio(SoundEffects.SFXMotherSwarm);

        addAudio(SoundEffects.SFXGuardCharge);
        addAudio(SoundEffects.SFXGuardLand);
        addAudio(SoundEffects.SFXGuardSmash);
        addAudio(SoundEffects.VOGuardHello);

        addAudio(SoundEffects.JellyZap);
        addAudio(SoundEffects.JellySmall);
        addAudio(SoundEffects.JellyFloat);
        addAudio(SoundEffects.JellyFade);
        addAudio(SoundEffects.JellyBoom);

        addAudio(SoundEffects.MossPrep);
        addAudio(SoundEffects.MossPoof);
        addAudio(SoundEffects.MossHide);
        addAudio(SoundEffects.MossFury);
        addAudio(SoundEffects.MossEek);

        addAudio(SoundEffects.ChargeAttack);
        addAudio(SoundEffects.ChargerRecover);
        addAudio(SoundEffects.ChargeShift);

        addAudio(SoundEffects.BaldurCurl);
        addAudio(SoundEffects.BaldurSpin);
        addAudio(SoundEffects.CrawlerAct);
        addAudio(SoundEffects.CrawlerAttack);

        // City of Tear Enemies
        addAudio(SoundEffects.NosAtt);
        addAudio(SoundEffects.NosLong);
        addAudio(SoundEffects.NosRev1);
        addAudio(SoundEffects.NosRev2);
        addAudio(SoundEffects.NosShort);

        addAudio(SoundEffects.BossMantisDash);
        addAudio(SoundEffects.BossMantisDrop);
        addAudio(SoundEffects.BossMantisWind);
        addAudio(SoundEffects.BossMantisDie);

        addAudio(SoundEffects.BossSoulBall);
        addAudio(SoundEffects.BossSoulConsume);
        addAudio(SoundEffects.BossSoulDeflate);

        addAudio(SoundEffects.GrimmCape);
        addAudio(SoundEffects.GrimmCast);
        addAudio(SoundEffects.GrimmDie);
        addAudio(SoundEffects.GrimmFire);
        addAudio(SoundEffects.GrimmSpikes);
        addAudio(SoundEffects.GrimmCall);

        addAudio(SoundEffects.FlukeAttack1);
        addAudio(SoundEffects.FlukeAttack2);
        addAudio(SoundEffects.GrimmkinCast);
        addAudio(SoundEffects.GrimmkinFireball);
        addAudio(SoundEffects.MantisWarriorSlice);
        addAudio(SoundEffects.MantisYouthScythe);
        addAudio(SoundEffects.MawlekSpit);
        addAudio(SoundEffects.MistakeSound);
        addAudio(SoundEffects.MistakeSound2);
        addAudio(SoundEffects.MrMushNod);
        addAudio(SoundEffects.MrMushTurn);
        addAudio(SoundEffects.OgreThrash);
        addAudio(SoundEffects.RichHuskAttack1);
        addAudio(SoundEffects.RichHuskAttack2);
        addAudio(SoundEffects.RichHuskCry);
        addAudio(SoundEffects.SpitSound);

        //CoTEvents

        addAudio(SoundEffects.EventBankerHit);
        addAudio(SoundEffects.EventBankerTalk);
        addAudio(SoundEffects.EventBankerTalk2);
        addAudio(SoundEffects.EventDivineTalk);
        addAudio(SoundEffects.EventDungTalk1);
        addAudio(SoundEffects.EventDungTalk2);
        addAudio(SoundEffects.EventHornetTalk);
        addAudio(SoundEffects.EventLemm);
        addAudio(SoundEffects.EventLurien);
        addAudio(SoundEffects.EventMarissaSong);
        addAudio(SoundEffects.EventNailsmithTalk);

        //KingdomsEdgeEnemies
        addAudio(SoundEffects.GrimmFire);
        addAudio(SoundEffects.GrimmSpikes);
        addAudio(SoundEffects.GrimmCall);
        addAudio(SoundEffects.GrimmFire);
        addAudio(SoundEffects.GrimmSpikes);
        addAudio(SoundEffects.GrimmCall);




        addAudio(SoundEffects.ColHeavyCharge);
        addAudio(SoundEffects.ColHeavyJump);
        addAudio(SoundEffects.CollectorHop);
        addAudio(SoundEffects.CollectorRoar);
        addAudio(SoundEffects.CollectorSteal);
        addAudio(SoundEffects.ColShieldAttack);
        addAudio(SoundEffects.CollectorSummon);
        addAudio(SoundEffects.ColSturdySlash);
        addAudio(SoundEffects.ColSturdyThrow);


        addAudio(SoundEffects.HiveKnightAttack);
        addAudio(SoundEffects.HiveKnightAttack2);
        addAudio(SoundEffects.HKBounce);
        addAudio(SoundEffects.HKFire);
        addAudio(SoundEffects.HKScream);
        addAudio(SoundEffects.HKStab);
        addAudio(SoundEffects.XeroOne);
        addAudio(SoundEffects.XeroTwo);
        addAudio(SoundEffects.CorpseAttack);
        addAudio(SoundEffects.CorpseEmerge);
        addAudio(SoundEffects.ZoteAttack);
        addAudio(SoundEffects.ZoteBalloonPop);
        addAudio(SoundEffects.ZoteBombSummon);
        addAudio(SoundEffects.ZoteHop);
        addAudio(SoundEffects.Zoteling01);
        addAudio(SoundEffects.Zoteling02);
        addAudio(SoundEffects.Zoteling03);
        addAudio(SoundEffects.ZoteShadow);
        addAudio(SoundEffects.MarmuBall);
        addAudio(SoundEffects.HuRings);
        addAudio(SoundEffects.HuskAttack);
        addAudio(SoundEffects.BeeDrill);
        addAudio(SoundEffects.BeeRoar);
        addAudio(SoundEffects.BigBeeSmash);


        addAudio(SoundEffects.GreatLand);
        addAudio(SoundEffects.LittleJump);
        addAudio(SoundEffects.SiblingAct);
        addAudio(SoundEffects.SwarmSound);
        addAudio(SoundEffects.GenSword);
        addAudio(SoundEffects.GenericShield);

        addAudio(SoundEffects.WeaverScream);
        addAudio(SoundEffects.DevoutOpen);
        addAudio(SoundEffects.DevoutSlash);




        //BaseMod.addAudio("Hallownest:Train", makeEffectPath("TrainSFX.ogg"));
        //BaseMod.addAudio("Hallownest:ghost", makeEffectPath("ghostbreath.ogg"));
        //BaseMod.addAudio("Hallownest:pest", makeEffectPath("pestilence.ogg"));
       // BaseMod.addAudio("Hallownest:magic", makeEffectPath("magic.ogg"));










    }

    private void addAudio(Pair<String, String> audioData)
    {
        BaseMod.addAudio(audioData.getKey(), audioData.getValue());
    }


    // ================ ADD RELICS ===================
    
    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");

        BaseMod.addRelic(new StagBellRelic1(), RelicType.SHARED);

        BaseMod.addRelic(new StagBellStandRelic(), RelicType.SHARED);
        //BaseMod.addRelic(new UnbreakableCardRelic(), RelicType.SHARED);


        BaseMod.addRelic(new StagEggRelic(), RelicType.SHARED);
        BaseMod.addRelic(new BankAccountRelic(), RelicType.SHARED);
        BaseMod.addRelic(new SheosBrushRelic(), RelicType.SHARED);
        BaseMod.addRelic(new DreamNailRelic(), RelicType.SHARED);
        BaseMod.addRelic(new TeacherSealRelic(), RelicType.SHARED);
        BaseMod.addRelic(new WatcherSealRelic(), RelicType.SHARED);
        BaseMod.addRelic(new BeastSealRelic(), RelicType.SHARED);
        BaseMod.addRelic(new GodTamersBeastRelic(), RelicType.SHARED);
        BaseMod.addRelic(new BlackEggRelic(), RelicType.SHARED);

        BaseMod.addRelic(new SalubrasBlessingRelic(), RelicType.SHARED);
        BaseMod.addRelic(new HeavyBlowRelic(), RelicType.SHARED);
        BaseMod.addRelic(new NailmastersGloryRelic(), RelicType.SHARED);
        BaseMod.addRelic(new UnbreakableCardRelic(), RelicType.SHARED);
        BaseMod.addRelic(new KingsIdolRelic(), RelicType.SHARED);
        BaseMod.addRelic(new SoulVesselRelic(), RelicType.SHARED);




        logger.info("Done adding relics!");
    }
    
    // ================ /ADD RELICS/ ===================
    
    
    // ================ ADD CARDS ===================
    
    @Override
    public void receiveEditCards() {
        logger.info("Adding variables");
        pathCheck();
        logger.info("Added variables");

        BaseMod.addDynamicVariable(new DefaultCustomVariable());
        BaseMod.addDynamicVariable(new DefaultSecondMagicNumber());

        //Curses
        BaseMod.addCard(new curseDreamersLament());
        //Status Cards
        BaseMod.addCard(new Swarmed());
        BaseMod.addCard(new Obsession());
        BaseMod.addCard(new IdeaInstilled());


        BaseMod.addCard(new StolenSoul());
        //Unique Cards

        BaseMod.addCard(new skillGreenpathMap());
        BaseMod.addCard(new skillCityofTearsMap());
        BaseMod.addCard(new skillKingdomsEdgeMap());
        BaseMod.addCard(new skillLightsAllure());
        BaseMod.addCard(new skillHoundingGlaives());
        BaseMod.addCard(new skillLeafShield());
        BaseMod.addCard(new skillSlugHide());

        BaseMod.addCard(new attackFallenFury());
        BaseMod.addCard(new attackAdjustingAction());
        BaseMod.addCard(new attackMasterCycloneSlash());
        BaseMod.addCard(new attackMasterDashSlash());
        BaseMod.addCard(new attackPureNailEdge());
        BaseMod.addCard(new pwrSporeShroom());






    }
    
    // ================ /ADD CARDS/ ===================
    
    
    // ================ LOAD THE TEXT ===================

    private static String makeLocPath(Settings.GameLanguage language, String filename)
    {
        String ret = "localization/";
        switch (language) {
            case ZHS:
                ret += "zhs/";
                break;
            default:
                ret += "eng/";
                break;
        }
        return getModID() + "Resources/" + (ret + filename + ".json");
    }

    private void loadLocFiles(Settings.GameLanguage language)
    {
        BaseMod.loadCustomStringsFile(CardStrings.class, makeLocPath(language, "HallownestMod-Card-Strings"));
        BaseMod.loadCustomStringsFile(EventStrings.class, makeLocPath(language, "HallownestMod-Event-Strings"));
        BaseMod.loadCustomStringsFile(MonsterStrings.class, makeLocPath(language, "HallownestMod-Monster-Strings"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, makeLocPath(language, "HallownestMod-Relic-Strings"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, makeLocPath(language, "HallownestMod-Power-Strings"));
        BaseMod.loadCustomStringsFile(UIStrings.class, makeLocPath(language, "HallownestMod-ui"));
    }

    @Override
    public void receiveEditStrings()
    {
        loadLocFiles(Settings.GameLanguage.ENG);
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocFiles(Settings.language);
        }
    }
    
    // ================ /LOAD THE TEXT/ ===================
    
    // ================ LOAD THE KEYWORDS ===================

    private void loadLocKeywords(Settings.GameLanguage language)
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(makeLocPath(language, "HallownestMod-Keyword-Strings")).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditKeywords()
    {
        loadLocKeywords(Settings.GameLanguage.ENG);
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocKeywords(Settings.language);
        }
    }
    
    // ================ /LOAD THE KEYWORDS/ ===================    
    
    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }
}
