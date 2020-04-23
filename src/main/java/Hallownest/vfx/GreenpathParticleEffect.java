package Hallownest.vfx;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class GreenpathParticleEffect extends AbstractGameEffect {
    private AtlasRegion img;
    private float x;
    private float y;
    private float vX;
    private float vY2;
    private float startingDuration;

    public GreenpathParticleEffect() {
        this.setImg();
        this.renderBehind = true;
        this.startingDuration = 15.0F;
        this.duration = this.startingDuration;
        this.x = MathUtils.random(0.0F, (float)Settings.WIDTH) - (float)this.img.packedWidth / 2.0F;
        this.y = (float)(-this.img.packedHeight) / 2.0F - 100.0F * Settings.scale;
        this.vX = MathUtils.random(-120.0F, 120.0F) * Settings.scale;
        this.vY2 = MathUtils.random(5.0F, 30.0F);
        this.vY2 *= this.vY2;
        this.vY2 *= Settings.scale;
        this.color = new Color(0.4f, 0.5F, 0.1f, 0.0F);
        if (this.vX > 0.0F) {
            this.rotation = MathUtils.random(0.0F, -15.0F);
        } else {
            this.rotation = MathUtils.random(0.0F, 15.0F);
        }

        this.scale = MathUtils.random(0.15F, 0.4F) * Settings.scale;
    }

    public void update() {
        this.x += this.vX * Gdx.graphics.getDeltaTime();
        this.duration -= Gdx.graphics.getDeltaTime();
        this.y += this.vY2 * Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        } else {
            this.color.a = Interpolation.pow2Out.apply(0.0F, 0.7F, this.duration);
        }

    }

    private void setImg() {
            this.img = ImageMaster.GLOW_SPARK_2;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        //sb.draw(this.img, this.x, this.y, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale * MathUtils.random(0.8F, 1.2F), this.scale * MathUtils.random(0.8F, 1.2F), this.rotation);
        sb.draw(this.img, this.x, this.y, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale * MathUtils.random(0.8F, 1.2F), this.scale * MathUtils.random(0.8F, 1.2F), this.rotation);
    }

    public void dispose() {
    }
}
