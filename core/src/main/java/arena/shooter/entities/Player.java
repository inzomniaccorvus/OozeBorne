package arena.shooter.entities;

import arena.shooter.core.Constants;
import arena.shooter.core.GameAssets;
import arena.shooter.systems.BulletManager;
import arena.shooter.util.Damageable;
import arena.shooter.util.Drawable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player implements Drawable, Damageable {
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

    public float stateTime;

    public enum Direction {DOWN, UP, LEFT, RIGHT}

    public Direction direction = Direction.DOWN;
    public boolean isMoving = false;

    public float velX, velY;

    public Player(float startX, float startY, float size) {
        this.x = startX;
        this.y = startY;
        this.size = size;
        this.hp = 100;
        this.currentWeapon = WeaponType.PISTOL;
        this.stateTime = 0f;
    }

    public float centerX() {
        return x + (size * 3f) / 2;
    }

    public float centerY() {
        return y + (size * 3.75f) / 2;
    }

    public void update(float delta, BulletManager bulletManager, float aimDirectionX, float aimDirectionY, Sound shootSound) {
        stateTime += delta;
        float prevX = x;
        float prevY = y;
        float movementSpeed = speedBoostTimer > 0 ? BOOSTED_SPEED : BASE_SPEED;

        isMoving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= movementSpeed * delta;
            direction = Direction.LEFT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += movementSpeed * delta;
            direction = Direction.RIGHT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += movementSpeed * delta;
            direction = Direction.UP;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= movementSpeed * delta;
            direction = Direction.DOWN;
            isMoving = true;
        }


        x = Math.max(0, Math.min(Constants.SCREEN_WIDTH - size, x));
        y = Math.max(0, Math.min(Constants.SCREEN_HEIGHT - size, y));

        velX = x - prevX;
        velY = y - prevY;

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


    public void draw(SpriteBatch batch, GameAssets assets) {
        TextureRegion frame;
        switch (direction) {
            case UP:
                frame = assets.mageUp;
                break;
            case LEFT:
                frame = assets.mageLeft;
                break;
            case RIGHT:
                frame = assets.mageRight;
                break;
            default:
                frame = assets.mageDown;
                break;
        }

        float bobSpeed = isMoving ? 4f : 2f;
        float bobAmount = isMoving ? 4f : 2f;
        float bob = (float) Math.sin(stateTime * bobSpeed) * bobAmount;
        float squash = 1f + (float) Math.sin(stateTime * bobSpeed) * 0.05f;

        if (damageFlashTimer > 0) batch.setColor(1f, 0.3f, 0.3f, 1f);

        float drawWidth = size * 3f;
        float drawHeight = size * 3.75f;
        batch.draw(frame, x, y + bob, drawWidth, drawHeight * squash);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private Bullet makeBullet(float dirX, float dirY, float posOffset) {
        Bullet b = new Bullet(centerX() + dirX * posOffset, centerY() + dirY * posOffset, dirX, dirY);
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
