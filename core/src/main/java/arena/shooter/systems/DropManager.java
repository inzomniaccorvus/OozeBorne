package arena.shooter.systems;

import arena.shooter.core.Constants;
import arena.shooter.core.GameAssets;
import arena.shooter.entities.Drop;
import arena.shooter.entities.Player;
import arena.shooter.util.Drawable;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;


public class DropManager implements Drawable {
    Array<Drop> drops;

    private float weaponDropTimer;
    private float powerupTimer;

    private static final float WEAPON_DROP_INTERVAL = 15f;
    private static final float POWERUP_INTERVAL = 8f;
    private static final float WEAPON_DROP_LIFETIME = 10f;
    private static final float SPAWN_PADDING = 50f;
    private static final float DROP_PICKUP_RADIUS = 20f;

    public DropManager() {
        drops = new Array<>();
    }


    private void spawnWeaponDrop() {
        float spawnX = SPAWN_PADDING + (float) (Math.random() * (Constants.SCREEN_WIDTH - SPAWN_PADDING * 2));
        float spawnY = SPAWN_PADDING + (float) (Math.random() * (Constants.SCREEN_HEIGHT - SPAWN_PADDING * 2));
        Drop.Type[] weaponTypes = {Drop.Type.SHOTGUN, Drop.Type.RAPID, Drop.Type.BURST};
        Drop.Type type = weaponTypes[(int) (Math.random() * weaponTypes.length)];
        drops.add(new Drop(spawnX, spawnY, WEAPON_DROP_LIFETIME, type));
    }

    private void spawnPowerup() {
        float spawnX = SPAWN_PADDING + (float) (Math.random() * (Constants.SCREEN_WIDTH - SPAWN_PADDING * 2));
        float spawnY = SPAWN_PADDING + (float) (Math.random() * (Constants.SCREEN_HEIGHT - SPAWN_PADDING * 2));
        Drop.Type[] powerupTypes = {Drop.Type.HEAL, Drop.Type.SPEED, Drop.Type.FIRERATE, Drop.Type.ARMORBUSTER};
        Drop.Type type = powerupTypes[(int) (Math.random() * powerupTypes.length)];
        drops.add(new Drop(spawnX, spawnY, -1f, type)); //powerup drops don't disappear
    }

    public void update(float delta, Player player, Sound pickupSound) {

        weaponDropTimer += delta;
        if (weaponDropTimer >= WEAPON_DROP_INTERVAL) {
            weaponDropTimer = 0f;
            spawnWeaponDrop();
        }

        powerupTimer += delta;
        if (powerupTimer >= POWERUP_INTERVAL) {
            powerupTimer = 0f;
            spawnPowerup();
        }

        for (int i = drops.size - 1; i >= 0; i--) {
            Drop drop = drops.get(i);
            if (drop.isWeapon()) {
                drop.lifeTime -= delta;
                if (drop.lifeTime <= 0) {
                    drops.removeIndex(i);
                    continue;
                }
            }
            float playerLeft = player.x;
            float playerRight = player.x + player.size * 2.5f;
            float playerBottom = player.y;
            float playerTop = player.y + player.size * 3.25f;
            float dropSize = 24f;

            if (drop.x + dropSize > playerLeft && drop.x - dropSize < playerRight && drop.y + dropSize > playerBottom && drop.y - dropSize < playerTop) {
                if (drop.isWeapon()) {
                    Player.WeaponType weaponType;
                    if (drop.type == Drop.Type.SHOTGUN) weaponType = Player.WeaponType.SHOTGUN;
                    else if (drop.type == Drop.Type.BURST) weaponType = Player.WeaponType.BURST;
                    else weaponType = Player.WeaponType.RAPID_FIRE;
                    player.equipWeapon(weaponType);
                } else {
                    if (drop.type == Drop.Type.HEAL) player.heal(20);
                    else if (drop.type == Drop.Type.SPEED) player.applySpeedBoost();
                    else if (drop.type == Drop.Type.FIRERATE) player.applyFirerateBoost();
                    else if (drop.type == Drop.Type.ARMORBUSTER) player.applyDamageBoost();
                }
                pickupSound.play(1f);
                drops.removeIndex(i);
            }

        }
    }

    public void draw(SpriteBatch batch, GameAssets assets) {
        for (Drop drop : drops) {
            Texture tex = assets.getDropTexture(drop.type);
            float size = 24f;
            batch.draw(tex, drop.x - size / 2, drop.y - size / 2, size * 2, size * 2);
        }
    }

    public void clear() {
        drops.clear();
        weaponDropTimer = 0f;
        powerupTimer = 0f;
    }
}
