package Hallownest.vfx;

import Hallownest.HallownestMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class AshFallingEffects extends AbstractGameEffect {
    private Texture img;
    private static Texture ash1 = ImageMaster.loadImage(HallownestMod.makeEffectPath("ASH1.png"));
    private static Texture ash2 = ImageMaster.loadImage(HallownestMod.makeEffectPath("ASH2.png"));
    private static Texture ash3 = ImageMaster.loadImage(HallownestMod.makeEffectPath("ASH3.png"));
    private static Texture ash4 = ImageMaster.loadImage(HallownestMod.makeEffectPath("ASH4.png"));
    private float x;
    private float y;
    private float vX;
    private float vY;
    private float vX2;
    private float vY2;
    private float aV;


    public AshFallingEffects() {
        this.duration = MathUtils.random(3.0F, 12.0F);
        this.startingDuration = this.duration;
        int roll = MathUtils.random(3);
        if (roll == 0) {
            this.img = ash1;
        } else if (roll == 1) {
            this.img = ash2;
        } else if (roll == 2) {
            this.img = ash3;
        } else if (roll == 3) {
            this.img = ash4;
        } else {
            this.img = ash2;
        }

        this.x = MathUtils.random(0.0F, (float)Settings.WIDTH) - (float)this.img.getWidth() / 2.0F;
        this.y = MathUtils.random(0.0F, (float)Settings.HEIGHT) - (float)this.img.getHeight() / 2.0F;
        this.vX = MathUtils.random(-20.0F, 20.0F) * Settings.scale * this.scale;
        this.vY = MathUtils.random(-60.0F, 60.0F) * Settings.scale * this.scale;
        this.vX2 = MathUtils.random(-20.0F, 20.0F) * Settings.scale * this.scale;
        this.vY2 = MathUtils.random(-60.0F, 60.0F) * Settings.scale * this.scale;
        this.aV = MathUtils.random(-50.0F, 50.0F);
        float tmp = MathUtils.random(0.2F, 0.4F);
        this.color = new Color();
        this.color.r = tmp + MathUtils.random(0.0F, 0.2F);
        this.color.g = tmp;
        this.color.b = tmp + MathUtils.random(0.0F, 0.2F);
        this.renderBehind = MathUtils.randomBoolean(0.8F);
        this.scale = MathUtils.random(12.0F, 20.0F) * Settings.scale;
    }

    public void update() {
        this.x += this.vX * Gdx.graphics.getDeltaTime();
        this.y += this.vY * Gdx.graphics.getDeltaTime();
        this.vX += this.vX2 * Gdx.graphics.getDeltaTime();
        this.vY += this.vY2 * Gdx.graphics.getDeltaTime();
        this.rotation += this.aV * Gdx.graphics.getDeltaTime();
        if (this.startingDuration - this.duration < 1.5F) {
            this.color.a = Interpolation.fade.apply(0.0F, 0.3F, (this.startingDuration - this.duration) / 1.5F);
        } else if (this.duration < 1.5F) {
            this.color.a = Interpolation.fade.apply(0.3F, 0.0F, 1.0F - this.duration / 1.5F);
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(img, this.x, this.y, (float)img.getWidth() / 2.0F, (float)img.getHeight() / 2.0F, (float)img.getWidth(), (float)img.getHeight(), this.scale, this.scale, this.rotation, 0, 0, 30, 29, false, false);

    }

    public void dispose() {
    }
}
