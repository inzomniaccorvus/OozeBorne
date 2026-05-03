package arena.shooter.entities;

import arena.shooter.systems.BulletManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player {
    public float x;
    public float y;
    public float size;
    public int hp;
    public float invincibilityTimer;
    public float damageFlashTimer;
    public float speedBoostTimer;
    public float firerateBoostTimer;
    public float damageBoostTimer;
    public float fireTimer;
    public float weaponTimer;
    public WeaponType currentWeapon;

    private static final float BASE_SPEED = 200f;
    private static final float BOOSTED_SPEED = 350f;
    private static final float WEAPON_DURATION = 10f;
    private static final float INVINCIBILITY_DURATION = 0.5f;
    private static final float DAMAGE_FLASH_DURATION = 0.2f;

    public enum WeaponType {
        PISTOL, SHOTGUN, RAPID_FIRE, BURST
    }

    public Player(float startX, float startY, float size) {
        this.x = startX;
        this.y = startY;
        this.size = size;
        this.hp = 100;
        this.currentWeapon = WeaponType.PISTOL;
    }

    public float centerX() {
        return x + size / 2;
    }

    public float centerY() {
        return y + size / 2;
    }

    public void update(float delta, BulletManager bulletManager, float aimDirectionX, float aimDirectionY, Sound shootSound) {
        float movementSpeed = speedBoostTimer > 0 ? BOOSTED_SPEED : BASE_SPEED;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) y += movementSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= movementSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= movementSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) x += movementSpeed * delta;

        x = Math.max(0, Math.min(1366 - size, x));
        y = Math.max(0, Math.min(768 - size, y));

        fireTimer -= delta;
        invincibilityTimer -= delta;
        damageFlashTimer -= delta;
        speedBoostTimer -= delta;
        firerateBoostTimer -= delta;
        damageBoostTimer -= delta;

        if (currentWeapon != WeaponType.PISTOL) {
            weaponTimer -= delta;
            if (weaponTimer <= 0) {
                currentWeapon = WeaponType.PISTOL;
            }
        }

        handleShooting(bulletManager, aimDirectionX, aimDirectionY, shootSound);
    }

    private void handleShooting(BulletManager bulletManager, float aimDirectionX, float aimDirectionY, Sound shootSound) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) || fireTimer > 0) return;
        switch (currentWeapon) {
            case PISTOL:
                bulletManager.addBullet(makeBullet(aimDirectionX, aimDirectionY, 0f));
                shootSound.play(0.4f);
                fireTimer = firerateBoostTimer > 0 ? 0.1f : 0.3f;
                break;

            case SHOTGUN:
                float baseAngle = (float) Math.atan2(aimDirectionY, aimDirectionX);
                float[] spreadOffsets = new float[]{-0.35f, -0.175f, 0f, 0.175f, 0.35f};
                for (float offset : spreadOffsets) {
                    float spreadAngle = baseAngle + offset;
                    bulletManager.addBullet(makeBullet((float) Math.cos(spreadAngle), (float) Math.sin(spreadAngle), 0f));
                }
                shootSound.play(0.4f);
                fireTimer = firerateBoostTimer > 0 ? 0.3f : 0.6f;
                break;

            case RAPID_FIRE:
                bulletManager.addBullet(makeBullet(aimDirectionX, aimDirectionY, 0f));
                shootSound.play(0.4f);
                fireTimer = firerateBoostTimer > 0 ? 0.05f : 0.1f;
                break;

            case BURST:
                for (int i = 0; i < 3; i++) {
                    bulletManager.addBullet(makeBullet(aimDirectionX, aimDirectionY, i * 8f));
                }
                shootSound.play(0.4f);
                fireTimer = firerateBoostTimer > 0 ? 0.2f : 0.4f;
                break;
        }
    }

    public void takeDamage(int amount) {
        if (invincibilityTimer > 0) return;
        hp -= amount;
        invincibilityTimer = INVINCIBILITY_DURATION;
        damageFlashTimer = DAMAGE_FLASH_DURATION;
    }

    public void applySpeedBoost() {
        speedBoostTimer = 5f;
    }

    public void applyFirerateBoost() {
        firerateBoostTimer = 5f;
    }

    public void heal(int amount) {
        hp = Math.min(100, hp + amount);
    }

    public void applyDamageBoost() {
        damageBoostTimer = 5f;
    }

    public void equipWeapon(WeaponType weaponType) {
        currentWeapon = weaponType;
        weaponTimer = WEAPON_DURATION;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void draw(ShapeRenderer shape) {
        shape.setColor(damageFlashTimer > 0 ? Color.RED : Color.CYAN);
        shape.rect(x, y, size, size);
    }

    private Bullet makeBullet(float dirX, float dirY, float posOffset) {
        Bullet b = new Bullet(
            centerX() + dirX * posOffset,
            centerY() + dirY * posOffset,
            dirX, dirY);
        b.damage = damageBoostTimer > 0 ? 2 : 1;
        return b;
    }

    public void reset(float startX, float startY) {
        x = startX;
        y = startY;
        hp = 100;
        invincibilityTimer = 0f;
        damageFlashTimer = 0f;
        speedBoostTimer = 0f;
        firerateBoostTimer = 0f;
        damageBoostTimer = 0f;
        fireTimer = 0f;
        weaponTimer = 0f;
        currentWeapon = WeaponType.PISTOL;
    }

}
