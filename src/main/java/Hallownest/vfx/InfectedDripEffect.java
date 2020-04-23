package Hallownest.vfx;


import com.megacrit.cardcrawl.vfx.*;
import com.badlogic.gdx.math.*;
import com.megacrit.cardcrawl.core.*;
import com.badlogic.gdx.graphics.*;
import com.megacrit.cardcrawl.helpers.*;
import com.badlogic.gdx.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.badlogic.gdx.graphics.g2d.*;

public class InfectedDripEffect extends AbstractGameEffect
{
    private float x;
    private float y;
    private static Texture[] imgs = null;
    private int frame = 0;
    private float animTimer =0.1f;
    private static final int W = 64;

    public InfectedDripEffect(){
        this(AbstractDungeon.player.hb_x, AbstractDungeon.player.hb_y );
    }

    public InfectedDripEffect(final float x, final float y) {
        this.x = x;
        this.y = y;
        if (InfectedDripEffect.imgs == null) {
            (InfectedDripEffect.imgs = new Texture[6])[0] = ImageMaster.loadImage("images/vfx/water_drop/drop1.png");
            InfectedDripEffect.imgs[1] = ImageMaster.loadImage("images/vfx/water_drop/drop2.png");
            InfectedDripEffect.imgs[2] = ImageMaster.loadImage("images/vfx/water_drop/drop3.png");
            InfectedDripEffect.imgs[3] = ImageMaster.loadImage("images/vfx/water_drop/drop4.png");
            InfectedDripEffect.imgs[4] = ImageMaster.loadImage("images/vfx/water_drop/drop5.png");
            InfectedDripEffect.imgs[5] = ImageMaster.loadImage("images/vfx/water_drop/drop6.png");
        }
        this.frame = 0;
        this.scale = MathUtils.random(2.5f, 3.0f) * Settings.scale;
        this.rotation = 0.0f;
        this.scale *= Settings.scale;
        this.color = new Color(1.0f, 0.635f, 0f, 0.95f);
    }

    public void dispose() {
        this.isDone = true;
    }

    public void update() {
        this.color.a = MathHelper.fadeLerpSnap(this.color.a, 1.0f);
        this.animTimer -= Gdx.graphics.getDeltaTime();
        if (this.animTimer < 0.0f) {
            this.animTimer += 0.1f;
            ++this.frame;
            if (this.frame == 3) {

                for (int i = 0; i < 3; ++i) {
                    AbstractDungeon.effectsQueue.add(new InfectedParticleEffect(this.x, this.y, new Color(1.0f, 0.635f, 0f, 0.95f)));
                }

            }
            if (this.frame > 5) {
                this.frame = 5;
                this.isDone = true;
            }
        }
    }

    public void render(final SpriteBatch sb) {
        sb.setColor(this.color);
        switch (this.frame) {
            case 0: {
                sb.draw(InfectedDripEffect.imgs[0], this.x - 32.0f, this.y - 32.0f + 40.0f * Settings.scale, 32.0f, 32.0f, 64.0f, 64.0f, this.scale, this.scale, this.rotation, 0, 0, 64, 64, false, false);
                break;
            }
            case 1: {
                sb.draw(InfectedDripEffect.imgs[1], this.x - 32.0f, this.y - 32.0f + 20.0f * Settings.scale, 32.0f, 32.0f, 64.0f, 64.0f, this.scale, this.scale, this.rotation, 0, 0, 64, 64, false, false);
                break;
            }
            case 2: {
                sb.draw(InfectedDripEffect.imgs[2], this.x - 32.0f, this.y - 32.0f + 10.0f * Settings.scale, 32.0f, 32.0f, 64.0f, 64.0f, this.scale, this.scale, this.rotation, 0, 0, 64, 64, false, false);
                break;
            }
            case 3: {
                sb.draw(InfectedDripEffect.imgs[3], this.x - 32.0f, this.y - 32.0f, 32.0f, 32.0f, 64.0f, 64.0f, this.scale, this.scale, this.rotation, 0, 0, 64, 64, false, false);
                break;
            }
            case 4: {
                sb.draw(InfectedDripEffect.imgs[4], this.x - 32.0f, this.y - 32.0f, 32.0f, 32.0f, 64.0f, 64.0f, this.scale, this.scale, this.rotation, 0, 0, 64, 64, false, false);
                break;
            }
            case 5: {
                sb.draw(InfectedDripEffect.imgs[5], this.x - 32.0f, this.y - 32.0f, 32.0f, 32.0f, 64.0f, 64.0f, this.scale, this.scale, this.rotation, 0, 0, 64, 64, false, false);
                break;
            }
        }
    }

}
