package Hallownest.events.greenpathEvents;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;

import java.util.ArrayList;

import static Hallownest.HallownestMod.makeEventPath;

public class HunterEvent extends AbstractImageEvent {

    public static final String ID = HallownestMod.makeID("HunterEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("HunterEventA.png");
//


    private final static int INTRO_SCREEN = 0;
    private final static int OPTION_SCREEN = 1;
    //choice ints for easier reading
    //Screen Button Options
    private final static int NORMAL_BUTTON = 0;
    private final static int ELITE_BUTTON = 1;
    private final static int BOSS_BUTTON = 2;

    private int screenNum = 0;

    public HunterEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[0]);

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case INTRO_SCREEN:
                if (Settings.AMBIANCE_ON) {
                    CardCrawlGame.sound.playV(SoundEffects.EvGpHunter.getKey(), 1.4F);
                }
                //Prep the options screen since continue is the only button, you only need to setup 1 screen regardless
                this.imageEventText.loadImage(makeEventPath("HunterEventB.png"));
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[1]);
                this.imageEventText.setDialogOption(OPTIONS[2]);
                this.imageEventText.setDialogOption(OPTIONS[3]);
                screenNum = 1;
                break;
            case OPTION_SCREEN:
                switch (buttonPressed) {
                    case NORMAL_BUTTON: //Encounter a normal enemy
                        screenNum = 3;
                        encounterNormalEnemy();
                        break;
                    case ELITE_BUTTON: // Encounter an elite enemy
                        screenNum = 3;
                        encounterEliteEnemy();
                        break;
                    case BOSS_BUTTON: // Encounter a random event
                        screenNum = 3;
                        encounterBossEnemy();
                        break;
                }
                break;
            case 2:

                break;
            default:
                this.openMap();
        }
    }

    private void encounterNormalEnemy() {
        CardCrawlGame.music.unsilenceBGM();
        final MapRoomNode cur = AbstractDungeon.currMapNode;
        final MapRoomNode node = new MapRoomNode(cur.x, cur.y);
        node.room = new MonsterRoom();
        final ArrayList<MapEdge> curEdges = cur.getEdges();
        for (final MapEdge edge : curEdges) {
            node.addEdge(edge);
        }
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.setCurrMapNode(AbstractDungeon.nextRoom = node);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.player.preBattlePrep();
        AbstractDungeon.scene.nextRoom(node.room);
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        AbstractDungeon.monsterList.remove(0);

        cur.taken = true;
    }

    private void encounterEliteEnemy() {
        CardCrawlGame.music.unsilenceBGM();
        final MapRoomNode cur2 = AbstractDungeon.currMapNode;
        final MapRoomNode node2 = new MapRoomNode(cur2.x, cur2.y);
        node2.room = new MonsterRoomElite();
        final ArrayList<MapEdge> curEdges2 = cur2.getEdges();
        for (final MapEdge edge : curEdges2) {
            node2.addEdge(edge);
        }
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.setCurrMapNode(AbstractDungeon.nextRoom = node2);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.player.preBattlePrep();
        AbstractDungeon.scene.nextRoom(node2.room);
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        AbstractDungeon.eliteMonsterList.remove(0);

        cur2.taken = true;
    }

    private void encounterBossEnemy() {
        /*
        CardCrawlGame.music.unsilenceBGM();

        final MapRoomNode cur3 = AbstractDungeon.currMapNode;
        final MapRoomNode node3 = new MapRoomNode(cur3.x, cur3.y);
        node3.room = new MonsterRoomBoss();
        final ArrayList<MapEdge> curEdges3= cur3.getEdges();
        for (final MapEdge edge : curEdges3) {
            node3.addEdge(edge);
        }
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.setCurrMapNode(AbstractDungeon.nextRoom = node3);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.player.preBattlePrep();
        AbstractDungeon.scene.nextRoom(node3.room);
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        AbstractDungeon.bossList.remove(0);
         cur3.taken = true;
        */
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        MapRoomNode node = new MapRoomNode(-1, 15);
        node.room = new MonsterRoomBoss();
        AbstractDungeon.nextRoom = node;
        CardCrawlGame.music.fadeOutTempBGM();
        AbstractDungeon.pathX.add(1);
        AbstractDungeon.pathY.add(15);
        AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
        AbstractDungeon.nextRoomTransitionStart();


    }
}
