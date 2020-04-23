package Hallownest.scenes;

import Hallownest.monsters.GreenpathEnemies.BossBrokenVessel;
import Hallownest.monsters.GreenpathEnemies.BossFalseKnight;
import Hallownest.monsters.GreenpathEnemies.BossHornet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.scene.ShinySparkleEffect;
import com.megacrit.cardcrawl.vfx.scene.WobblyCircleEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class GreenpathScene extends AbstractScene {

    private static Texture topBar;
    private TextureAtlas.AtlasRegion bg;
    private TextureAtlas.AtlasRegion fg;
    private TextureAtlas.AtlasRegion ceil;
    private TextureAtlas.AtlasRegion fgGlow;
    private TextureAtlas.AtlasRegion floor;
    private TextureAtlas.AtlasRegion mg1;
    private Texture campfirebg;
    private Texture campfire;
    private Texture fire;
    private ArrayList<AbstractGameEffect> circles;

    public GreenpathScene() {
        super("HallownestResources/images/scene/Greenpath/atlas.atlas");
        //topBar = ImageMaster.loadImage("HallownestResources/images/scene/GreenpathBar.png");

        this.bg = this.atlas.findRegion("mod/TanukiForest");
        this.circles = new ArrayList<AbstractGameEffect>();
        //this.fg = this.atlas.findRegion("mod/fg");
        //this.ceil = this.atlas.findRegion("mod/ceiling");
        //this.fgGlow = this.atlas.findRegion("mod/fgGlow");
        //this.floor = this.atlas.findRegion("mod/floor");
        //this.mg1 = this.atlas.findRegion("mod/mg1");

        this.ambianceName = "AMBIANCE_CITY";
        this.fadeInAmbiance();
    }

    @Override
    public void update() {
        super.update();

    }



    @Override
    public void randomizeScene() {
    }

    @Override
    public void nextRoom(AbstractRoom room) {
        super.nextRoom(room);
        this.randomizeScene();
        if (room instanceof MonsterRoomBoss) {
            CardCrawlGame.music.silenceBGM();
        }
        if (room.monsters != null) {
            for (AbstractMonster mo : room.monsters.monsters) {
                if (mo instanceof BossHornet) {
                    this.bg = this.atlas.findRegion("mod/HakureiShrine");
                } else if (mo instanceof BossBrokenVessel) {
                    this.bg = this.atlas.findRegion("mod/LostKinArena");
                } else if (mo instanceof BossFalseKnight) {
                    this.bg = this.atlas.findRegion("mod/FalseKnightArena");
                } else {
                    this.bg = this.atlas.findRegion("mod/TanukiForest");
                }
            }
        } else if (room instanceof ShopRoom) {
            this.bg = this.atlas.findRegion("mod/Shop");
        } else {
            this.bg = this.atlas.findRegion("mod/TanukiForest");
        }
        this.fadeInAmbiance();
    }

    @Override
    public void renderCombatRoomBg(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        this.renderAtlasRegionIf(sb, bg, true);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        //this.renderAtlasRegionIf(sb, this.floor, true);
        // this.renderAtlasRegionIf(sb, this.ceil, true);
        //this.renderAtlasRegionIf(sb, this.mg1, true);
    }

    @Override
    public void renderCombatRoomFg(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.setColor(Color.WHITE.cpy());
        // this.renderAtlasRegionIf(sb, this.fg, true);
        // sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);
        // this.renderAtlasRegionIf(sb, this.fgGlow, true);
        // sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void renderCampfireRoom(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireBg, true);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);
        sb.setColor(new Color(1.0f, 1.0f, 1.0f, MathUtils.cosDeg(System.currentTimeMillis() / 3L % 360L) / 10.0f + 0.8f));
        this.renderQuadrupleSize(sb, this.campfireGlow, !CampfireUI.hidden);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireKindling, true);
    }
}