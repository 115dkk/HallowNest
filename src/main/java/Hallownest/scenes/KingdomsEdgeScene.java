package Hallownest.scenes;

import Hallownest.monsters.GreenpathEnemies.BossBrokenVessel;
import Hallownest.monsters.GreenpathEnemies.BossFalseKnight;
import Hallownest.monsters.GreenpathEnemies.BossHornet;
import Hallownest.monsters.KingdomsEdgeEnemies.BossGreyPrinceZote;
import Hallownest.monsters.KingdomsEdgeEnemies.BossHollowKnight;
import Hallownest.monsters.KingdomsEdgeEnemies.BossRadiance;
import Hallownest.vfx.AshFallingEffects;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.DeathScreenFloatyEffect;

import java.util.ArrayList;

public class KingdomsEdgeScene extends AbstractScene {

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
    private ArrayList<AshFallingEffects> particles;

    public KingdomsEdgeScene() {
        super("HallownestResources/images/scene/KingdomsEdge/atlas.atlas");
        //topBar = ImageMaster.loadImage("HallownestResources/images/scene/GreenpathBar.png");

        this.bg = this.atlas.findRegion("mod/KingdomsEdgeBack");
        this.particles = new ArrayList<>();
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
        //updateParticles();
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
                if (mo instanceof BossRadiance) {
                    this.bg = this.atlas.findRegion("mod/RadianceArena");
                } else if (mo instanceof BossHollowKnight) {
                    this.bg = this.atlas.findRegion("mod/HollowKnightArena");
                } else if (mo instanceof BossGreyPrinceZote) {
                    this.bg = this.atlas.findRegion("mod/GreyPrinceArena");
                } else {
                    this.bg = this.atlas.findRegion("mod/KingdomsEdgeBack");
                }
            }
        } else if (room instanceof ShopRoom) {
            this.bg = this.atlas.findRegion("mod/Shop");
        } else {
            this.bg = this.atlas.findRegion("mod/KingdomsEdgeBack");
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
        /*
        if (!this.isCamp) {
            sb.setBlendFunction(770, 1);
            for (final AbstractGameEffect e : this.particles) {
                e.render(sb);
            }
            sb.setBlendFunction(770, 771);
        }
        */
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