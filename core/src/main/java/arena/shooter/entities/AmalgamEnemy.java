package arena.shooter.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class AmalgamEnemy extends Enemy {

    public int phase;
    public boolean isMini;

    public boolean shouldSplit;
    public int lastSummonHP;
    public boolean canSummon;


    public boolean dashing;
    public float dashTimer;
    private float dashInterval = 4f;
    public float dashDuration;
    public float speedMultiplier = 4f;

    public float shootTimer;
    public float shootInterval = 2f;
    public float spiralAngle;
    public float spiralAngleTick = (float) Math.toRadians(13f);
    public Array<Bullet> bossBullets;

    public boolean reflecting;
    public float reflectDuration = 3f;
    public float reflectTimer;
    public int hitCount;
    private float hitWindow = 1.5f;
    private int hitThreshold = 5;
    public float hitWindowTimer;
    private float reflectCooldown = 8f;
    public float reflectCooldownTimer;

    public AmalgamEnemy(float x, float y, float speed) {
        super(x, y, 60f, speed, 150, Color.LIGHT_GRAY, 200);
        this.isMini = false;
        reflectTimer = 0f;
        reflectCooldownTimer = reflectCooldown;
        hitCount = 0;
        hitWindowTimer = 0f;
        reflecting = false;
        dashing = false;
        phase = 1;
        shouldSplit = false;
        lastSummonHP = 150;
        canSummon = false;
        dashTimer = 0f;
        shootTimer = 0f;
        spiralAngle = 0f;
        dashDuration = 0f;
        bossBullets = new Array<>();
    }

    public AmalgamEnemy(float x, float y, float speed, boolean isMini) {
        super(x, y, 35f, speed, 30, Color.LIGHT_GRAY, 50);
        this.isMini = isMini;
        reflectTimer = 0f;
        reflectCooldownTimer = reflectCooldown;
        hitCount = 0;
        hitWindowTimer = 0f;
        reflecting = false;
        dashing = false;
        phase = 1;
        shouldSplit = false;
        canSummon = false;
        lastSummonHP = 30;
        dashTimer = 0f;
        dashInterval = 6f;
        shootTimer = 0f;
        shootInterval = 3f;
        spiralAngle = 0f;
        dashDuration = 0f;
        reflectCooldown = 12f;
        bossBullets = new Array<>();
    }

    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount);
        hitCount += amount;
        if (hitCount >= hitThreshold && reflectCooldownTimer >= reflectCooldown) {
            hitCount = 0;
            hitWindowTimer = 0;
            reflecting = true;
            reflectTimer = 0f;
            reflectCooldownTimer = 0f;
        }
    }

    @Override
    public void update(float delta, float targetX, float targetY) {
        super.update(delta, targetX, targetY);
        if (hp <= 75 && phase == 1) {
            phase = 2;
            shootInterval = 1.5f;
            dashInterval = 2.5f;
            speedMultiplier = 3f;
            reflectCooldown = 5f;
        }
        hitWindowTimer += delta;
        dashTimer += delta;
        shootTimer += delta;

        if (hitWindowTimer >= hitWindow) {
            hitWindowTimer = 0;
            hitCount = 0;
        }

        if (dashTimer >= dashInterval) {
            speed *= speedMultiplier;
            dashDuration = 0.6f;
            dashTimer = 0f;
        }

        if (dashDuration > 0f) {
            dashDuration -= delta;
            if (dashDuration <= 0f) {
                speed /= speedMultiplier;
            }
        }

        if (shootTimer >= shootInterval) {
            shootTimer = 0f;
            spiralAngle += spiralAngleTick;
            int roll = (int) (Math.random() * 2);
            if (roll == 0) {
                targetedBurst(targetX, targetY);
            } else {
                if ((phase == 1)) {
                    spiral();
                } else {
                    radial();
                }
            }

        }

        if (reflecting) {
            reflectTimer += delta;
            if (reflectTimer >= reflectDuration) {
                reflecting = false;
            }
        } else {
            reflectCooldownTimer += delta;
        }

        if (!isMini && lastSummonHP - hp >= 30) {
            lastSummonHP = hp;
            canSummon = true;
        }

        if (!isMini && hp <= 25) {
            shouldSplit = true;
        }


    }

    public void updateBullets(float delta) {
        for (int i = bossBullets.size - 1; i >= 0; i--) {
            Bullet bullet = bossBullets.get(i);
            bullet.update(delta);
            if (bullet.isOffScreen(1366,768)) {
                bossBullets.removeIndex(i);
            }
        }
    }

    public void drawBullets(ShapeRenderer shape) {
        shape.setColor(Color.RED);
        for (Bullet bullet : bossBullets) {
            shape.circle(bullet.x, bullet.y, bullet.size);
        }
    }

    public void targetedBurst(float targetX, float targetY) {
        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        dx /= distance;
        dy /= distance;
        float bulletCount = 8;
        float offset = 10f;
        for (int i = 0; i < bulletCount; i++) {
            Bullet bullet = new Bullet(x + i * offset * dx, y + i * offset * dy, dx, dy);
            bossBullets.add(bullet);
        }
    }

    public void radial() {
        float bulletCount = 8;
        float baseAngle = 0;
        for (int i = 0; i < bulletCount; i++) {
            float offset = (float) ((2 * Math.PI / bulletCount) * i);
            float spreadAngle = baseAngle + offset;
            Bullet bullet = new Bullet(x, y, (float) Math.cos(spreadAngle), (float) Math.sin(spreadAngle));
            bossBullets.add(bullet);
        }
    }

    public void spiral() {
        float bulletCount = 8;
        float baseAngle = spiralAngle;
        for (int i = 0; i < bulletCount; i++) {
            float offset = (float) ((2 * Math.PI / bulletCount) * i);
            float spreadAngle = baseAngle + offset;
            Bullet bullet = new Bullet(x, y, (float) Math.cos(spreadAngle), (float) Math.sin(spreadAngle));
            bossBullets.add(bullet);
        }
    }
}

