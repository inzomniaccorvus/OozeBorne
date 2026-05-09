package arena.shooter.ui;

import arena.shooter.core.FontManager;
import arena.shooter.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.IntArray;

public class HUD {
    private static final int SCREEN_WIDTH = 1366;
    private static final int SCREEN_HEIGHT = 768;

    private static final float BAR_X = 16f;
    private static final float BAR_Y = 16f;
    private static final float BAR_WIDTH = 220f;
    private static final float BAR_HEIGHT = 18f;
    private static final float BAR_BORDER = 2f;

    private static final Color GOLD = new Color(1f, 0.85f, 0.40f, 1f);
    private static final Color LAVENDER = new Color(0.90f, 0.85f, 1.00f, 1f);
    private static final Color BRIGHT = new Color(0.75f, 0.72f, 0.90f, 1f);
    private static final Color DARK_BG = new Color(0.04f, 0.04f, 0.08f, 0.85f);

    private final GlyphLayout glyphLayout = new GlyphLayout();

    public void drawHPBar(ShapeRenderer shapeRenderer, int playerHP) {
        float hpRatio = playerHP / 100f;

        shapeRenderer.setColor(DARK_BG);
        shapeRenderer.rect(BAR_X - BAR_BORDER, BAR_Y - BAR_BORDER, BAR_WIDTH + BAR_BORDER * 2, BAR_HEIGHT + BAR_BORDER * 2);

        shapeRenderer.setColor(0.15f, 0.05f, 0.05f, 1f);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT);

        shapeRenderer.setColor(1f - hpRatio, hpRatio * 0.8f, 0.1f, 1f);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH * hpRatio, BAR_HEIGHT);

        shapeRenderer.setColor(GOLD);
        shapeRenderer.rect(BAR_X - BAR_BORDER, BAR_Y + BAR_HEIGHT, BAR_WIDTH + BAR_BORDER * 2, BAR_BORDER);
        shapeRenderer.rect(BAR_X - BAR_BORDER, BAR_Y - BAR_BORDER, BAR_WIDTH + BAR_BORDER * 2, BAR_BORDER);
        shapeRenderer.rect(BAR_X - BAR_BORDER, BAR_Y - BAR_BORDER, BAR_BORDER, BAR_HEIGHT + BAR_BORDER * 2);
        shapeRenderer.rect(BAR_X + BAR_WIDTH, BAR_Y - BAR_BORDER, BAR_BORDER, BAR_HEIGHT + BAR_BORDER * 2);
    }

    public void drawGameInfo(SpriteBatch batch, FontManager fonts, Player player, int score, float survivalTime) {
        fonts.hudFont.setColor(LAVENDER);
        fonts.hudFont.draw(batch, player.hp + " / 100", BAR_X + BAR_WIDTH + 10, BAR_Y + BAR_HEIGHT + 2);
        fonts.hudFont.draw(batch, "Score: " + score, BAR_X, SCREEN_HEIGHT - 16f);
        fonts.hudFont.draw(batch, "Time:  " + (int) survivalTime + "s", BAR_X, SCREEN_HEIGHT - 38f);

        fonts.hudFont.setColor(GOLD);
        fonts.hudFont.draw(batch, weaponLabel(player.currentWeapon), BAR_X, SCREEN_HEIGHT - 62f);

        float nextY = SCREEN_HEIGHT - 88f;
        if (player.speedBoostTimer > 0) {
            fonts.hudSmall.setColor(0.4f, 1f, 0.5f, 1f);
            fonts.hudSmall.draw(batch, "Haste  " + (int) player.speedBoostTimer + "s", BAR_X, nextY);
            nextY -= 20f;
        }
        if (player.firerateBoostTimer > 0) {
            fonts.hudSmall.setColor(1f, 0.85f, 0.3f, 1f);
            fonts.hudSmall.draw(batch, "Rapid  " + (int) player.firerateBoostTimer + "s", BAR_X, nextY);
            nextY -= 20f;
        }
        if (player.damageBoostTimer > 0) {
            fonts.hudSmall.setColor(1f, 0.4f, 0.4f, 1f);
            fonts.hudSmall.draw(batch, "Buster " + (int) player.damageBoostTimer + "s", BAR_X, nextY);
            nextY -= 20f;
        }
        if (player.currentWeapon != Player.WeaponType.PISTOL) {
            fonts.hudSmall.setColor(BRIGHT);
            fonts.hudSmall.draw(batch, "Expires " + (int) player.weaponTimer + "s", BAR_X, nextY);
        }
        fonts.hudSmall.setColor(Color.WHITE);
    }

    public void drawWaveIntro(SpriteBatch batch, FontManager fonts, int wave, float alpha) {
        fonts.waveFont.setColor(0.90f, 0.85f, 1f, alpha);
        drawCentered(batch, fonts.waveFont, "WAVE " + toRoman(wave + 1), SCREEN_HEIGHT / 2f + 60);

        fonts.bodyItalic.setColor(0.90f, 0.85f, 1.00f, alpha);
        drawCentered(batch, fonts.bodyItalic, FLAVOR_TEXTS[Math.min(wave, FLAVOR_TEXTS.length - 1)], SCREEN_HEIGHT / 2f - 40);
    }

    public void drawMainMenu(SpriteBatch batch, FontManager fonts, IntArray scores) {
        drawCentered(batch, fonts.titleFont, "OOZEBORNE", SCREEN_HEIGHT - 120f);

        fonts.bodyItalic.setColor(0.75f, 0.70f, 0.95f, 1f);
        drawCentered(batch, fonts.bodyItalic, "A Mage's Inquiry into the Slime Incursion", SCREEN_HEIGHT - 230f);

        fonts.bodyFont.setColor(1f, 1f, 1f, 1f);
        drawCentered(batch, fonts.bodyFont, "Press  ENTER  to Begin", SCREEN_HEIGHT - 310f);

        fonts.hudSmall.setColor(BRIGHT);
        drawCentered(batch, fonts.hudSmall, "WASD - Move     Mouse - Aim     LMB - Fire", SCREEN_HEIGHT - 270f);

        fonts.headerFont.setColor(GOLD);
        drawCentered(batch, fonts.headerFont, "High Scores", SCREEN_HEIGHT / 2f - 180f);

        float scoreY = SCREEN_HEIGHT / 2f - 220f;
        if (scores == null || scores.size == 0) {
            fonts.hudSmall.setColor(GOLD);
            drawCentered(batch, fonts.hudSmall, "No records yet.", scoreY);
        } else {
            for (int i = 0; i < Math.min(scores.size, 5); i++) {
                fonts.hudFont.setColor(i == 0 ? GOLD : LAVENDER);
                drawCentered(batch, fonts.hudFont, (i + 1) + ".   " + scores.get(i), scoreY - i * 24f);
            }
        }
    }

    public void drawGameOver(SpriteBatch batch, FontManager fonts, int score, float survivalTime) {
        drawEndScreen(batch, fonts, "The Mage Has Fallen", "The ooze reclaims the halls.", score, survivalTime);
    }

    public void drawGameWin(SpriteBatch batch, FontManager fonts, int score, float survivalTime) {
        drawEndScreen(batch, fonts, "The Incursion is Contained", "Order restored. For now.", score, survivalTime);
    }

    public void drawPause(SpriteBatch batch, FontManager fonts) {
        fonts.headerFont.setColor(LAVENDER);
        drawCentered(batch, fonts.headerFont, "Paused", SCREEN_HEIGHT / 2f + 20);
        fonts.hudSmall.setColor(BRIGHT);
        drawCentered(batch, fonts.hudSmall, "Press ESC to resume", SCREEN_HEIGHT / 2f - 20);
    }

    private void drawEndScreen(SpriteBatch batch, FontManager fonts, String title, String subtitle, int score, float survivalTime) {
        fonts.headerFont.setColor(LAVENDER);
        drawCentered(batch, fonts.headerFont, title, SCREEN_HEIGHT / 2f + 80);

        fonts.bodyItalic.setColor(BRIGHT);
        drawCentered(batch, fonts.bodyItalic, subtitle, SCREEN_HEIGHT / 2f + 44);

        fonts.hudFont.setColor(GOLD);
        drawCentered(batch, fonts.hudFont, "Score:  " + score, SCREEN_HEIGHT / 2f);
        drawCentered(batch, fonts.hudFont, "Time:   " + (int) survivalTime + "s", SCREEN_HEIGHT / 2f - 28f);

        fonts.bodyFont.setColor(LAVENDER);
        drawCentered(batch, fonts.bodyFont, "Press  R  to try again", SCREEN_HEIGHT / 2f - 72f);
    }

    private void drawCentered(SpriteBatch batch, BitmapFont font, String text, float y) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, (SCREEN_WIDTH - glyphLayout.width) / 2f, y);
    }

    private String weaponLabel(Player.WeaponType weaponType) {
        switch (weaponType) {
            case SHOTGUN:
                return "Weapon: Shotgun";
            case RAPID_FIRE:
                return "Weapon: Rapid Fire";
            case BURST:
                return "Weapon: Burst";
            default:
                return "Weapon: Pistol";
        }
    }

    private String toRoman(int number) {
        String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return (number >= 1 && number <= 10) ? romanNumerals[number - 1] : String.valueOf(number);
    }

    private static final String[] FLAVOR_TEXTS = {"The sewers stir. Something vile has crawled into the light.", "They multiply. The alchemical reports were not exaggerated.", "Thicker now. And one bears a hide like tempered iron.", "The marksmen emerge. They've learned to keep their distance.", "A coordinated assault. This is no mere infestation.", "The walls themselves seem to weep slime. Hold your ground.", "They split when struck. Every wound births a new enemy.", "The ooze has grown sentient. I can feel it watching.", "One presence looms behind the horde. Ancient. Patient. Wrong.", "It awakens."};
}
