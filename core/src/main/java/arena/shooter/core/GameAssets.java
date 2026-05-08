package arena.shooter.core;

import arena.shooter.entities.Drop;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameAssets {
    public Texture map1, map2, map3;

    public TextureRegion mageDown, mageUp, mageLeft, mageRight;
    public Texture mageTexture;

    public Texture basicTexture, fastTexture, tankTexture;
    public Texture shooterTexture, splitterTexture, bossTexture;
    public Animation<TextureRegion> basicAnim, fastAnim, tankAnim;
    public Animation<TextureRegion> shooterAnim, splitterAnim, bossAnim;

    public Texture fireballTexture, goopTexture;
    public TextureRegion fireballTextureRegion;

    public Texture dropShotgun, dropRapid, dropBurst;
    public Texture dropHeal, dropSpeed, dropFirerate, dropArmorbuster;

    private static final float FRAME_DURATION = 0.2f;

    private static final int MAGE_FRAME_W = 300;
    private static final int MAGE_FRAME_H = 298;
    private static final int SLIME_FRAME_W = 516;
    private static final int SLIME_FRAME_H = 512;

    public void load() {
        map1 = new Texture("map1.png");
        map2 = new Texture("map2.png");
        map3 = new Texture("map3.png");

        mageTexture = new Texture("mage.png");
        mageDown = new TextureRegion(mageTexture, 0, 0, 516, 512);
        mageUp = new TextureRegion(mageTexture, 516, 0, 516, 512);
        mageRight = new TextureRegion(mageTexture, 1032, 0, 516, 512);
        mageLeft = new TextureRegion(mageTexture, 1548, 0, 516, 512);

        basicTexture = new Texture("slime_basic.png");
        fastTexture = new Texture("slime_fast.png");
        tankTexture = new Texture("slime_tank.png");
        shooterTexture = new Texture("slime_shooter.png");
        splitterTexture = new Texture("slime_splitter.png");
        bossTexture = new Texture("slime_boss.png");

        basicAnim = makeAnim(basicTexture, SLIME_FRAME_W, SLIME_FRAME_H);
        fastAnim = makeAnim(fastTexture, SLIME_FRAME_W, SLIME_FRAME_H);
        tankAnim = makeAnim(tankTexture, SLIME_FRAME_W, SLIME_FRAME_H);
        shooterAnim = makeAnim(shooterTexture, SLIME_FRAME_W, SLIME_FRAME_H);
        splitterAnim = makeAnim(splitterTexture, SLIME_FRAME_W, SLIME_FRAME_H);
        bossAnim = makeAnim(bossTexture, SLIME_FRAME_W, SLIME_FRAME_H);

        fireballTexture = new Texture("fireball.png");
        fireballTextureRegion = new TextureRegion(fireballTexture);
        goopTexture = new Texture("goop.png");

        dropShotgun = new Texture("drop_shotgun.png");
        dropRapid = new Texture("drop_rapidfire.png");
        dropBurst = new Texture("drop_burst.png");
        dropHeal = new Texture("drop_heal.png");
        dropSpeed = new Texture("drop_speed.png");
        dropFirerate = new Texture("drop_firerate.png");
        dropArmorbuster = new Texture("drop_armorbuster.png");
    }

    private Animation<TextureRegion> makeAnim(Texture texture, int frameW, int frameH) {
        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            frames[i] = new TextureRegion(texture, i * frameW, 0, frameW, frameH);
        }
        return makeAnimFromFrames(frames);
    }

    private Animation<TextureRegion> makeAnimFromFrames(TextureRegion[] frames) {
        Animation<TextureRegion> anim = new Animation<>(FRAME_DURATION, frames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    public Texture getDropTexture(Drop.Type type) {
        switch (type) {
            case SHOTGUN:
                return dropShotgun;
            case RAPID:
                return dropRapid;
            case BURST:
                return dropBurst;
            case HEAL:
                return dropHeal;
            case SPEED:
                return dropSpeed;
            case FIRERATE:
                return dropFirerate;
            case ARMORBUSTER:
                return dropArmorbuster;
            default:
                return dropHeal;
        }
    }

    public Texture getMapTexture(int wave) {
        if (wave < 3) return map1;
        else if (wave < 7) return map2;
        else return map3;
    }

    public void dispose() {
        map1.dispose();
        map2.dispose();
        map3.dispose();
        mageTexture.dispose();
        basicTexture.dispose();
        fastTexture.dispose();
        tankTexture.dispose();
        shooterTexture.dispose();
        splitterTexture.dispose();
        bossTexture.dispose();
        fireballTexture.dispose();
        goopTexture.dispose();
        dropShotgun.dispose();
        dropRapid.dispose();
        dropBurst.dispose();
        dropHeal.dispose();
        dropSpeed.dispose();
        dropFirerate.dispose();
        dropArmorbuster.dispose();
    }
}
