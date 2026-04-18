package arena.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HUD {
    private static final float HP_BAR_X = 10f;
    private static final float HP_BAR_Y = 20f;
    private static final float HP_BAR_WIDTH = 200f;
    private static final float HP_BAR_HEIGHT = 20f;
    private static final float HUD_LEFT_MARGIN = 10f;

    public void drawHPBar(ShapeRenderer shape, int playerHP) {
        shape.setColor(0.3f, 0f, 0f, 1f);
        shape.rect(HP_BAR_X, HP_BAR_Y, HP_BAR_WIDTH, HP_BAR_HEIGHT);

        float hpRatio = playerHP / 100f;
        shape.setColor(1f - hpRatio, hpRatio, 0f, 1f);
        shape.rect(HP_BAR_X, HP_BAR_Y, HP_BAR_WIDTH * hpRatio, HP_BAR_HEIGHT);
    }

    public void drawGameInfo(SpriteBatch batch, BitmapFont font, Player player, int score, float survivalTime) {
        int screenHeight = Gdx.graphics.getHeight();

        font.setColor(Color.WHITE);
        font.draw(batch, player.hp + "/100", HP_BAR_X + HP_BAR_WIDTH + 10, HP_BAR_Y + HP_BAR_HEIGHT);
        font.draw(batch, "Score: " + score, HUD_LEFT_MARGIN, screenHeight - 40);
        font.draw(batch, "Time: " + (int) survivalTime + "s", HUD_LEFT_MARGIN, screenHeight - 70);
        font.draw(batch, "Weapon: " + player.currentWeapon, HUD_LEFT_MARGIN, screenHeight - 100);

        float nextY = screenHeight - 130;
        if (player.speedBoostTimer > 0) {
            font.draw(batch, "Speed Boost: " + (int) player.speedBoostTimer + "s", HUD_LEFT_MARGIN, nextY);
            nextY -= 30;
        }
        if (player.firerateBoostTimer > 0) {
            font.draw(batch, "Firerate Boost: " + (int) player.firerateBoostTimer + "s", HUD_LEFT_MARGIN, nextY);
            nextY -= 30;
        }
        if (player.damageBoostTimer> 0) {
            font.draw(batch, "Armor Buster: " + (int) player.damageBoostTimer+ "s", HUD_LEFT_MARGIN, nextY);
            nextY -= 30;
        }
        if (player.currentWeapon != Player.WeaponType.PISTOL) {
            font.draw(batch, "Weapon time: " + (int) player.weaponTimer + "s", HUD_LEFT_MARGIN, nextY);
        }
    }

    public void drawMainMenu(SpriteBatch batch, BitmapFont font) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        font.setColor(Color.WHITE);
        font.draw(batch, "ARENA SHOOTER", screenWidth / 2f - 90, screenHeight / 2f + 40);
        font.draw(batch, "Press ENTER to Start", screenWidth / 2f - 100, screenHeight / 2f - 20);
    }

    public void drawGameOver(SpriteBatch batch, BitmapFont font, int score, float survivalTime) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        font.setColor(Color.WHITE);
        font.draw(batch, "GAME OVER", screenWidth / 2f - 60, screenHeight / 2f + 40);
        font.draw(batch, "Score: " + score, screenWidth / 2f - 40, screenHeight / 2f);
        font.draw(batch, "Time: " + (int) survivalTime + "s", screenWidth / 2f - 40, screenHeight / 2f - 40);
        font.draw(batch, "Press R to Restart", screenWidth / 2f - 80, screenHeight / 2f - 80);
    }
}
