package arena.shooter.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontManager {

    public BitmapFont titleFont;    // Cinzel Decorative 72 — gold, for "OOZEBORNE"
    public BitmapFont headerFont;   // Cinzel 36 — lavender, for screen headers
    public BitmapFont waveFont;     // Cinzel 52 — lavender, for wave number
    public BitmapFont bodyFont;     // IM Fell English 22 — lavender, for flavor text
    public BitmapFont bodyItalic;   // IM Fell English Italic 20 — dim, for subtitles
    public BitmapFont hudFont;      // Cinzel 18 — white, for HUD labels
    public BitmapFont hudSmall;     // Cinzel 14 — dim, for HUD secondary info

    private static final Color GOLD     = new Color(1f,   0.85f, 0.40f, 1f);  // brighter gold
    private static final Color LAVENDER = new Color(0.90f, 0.85f, 1.00f, 1f); // much brighter
    private static final Color DIM      = new Color(0.65f, 0.62f, 0.80f, 1f); // was nearly invisible
    private static final Color WHITE    = new Color(1f,    1f,    1f,    1f);  // unchanged

    public void load() {
        FreeTypeFontGenerator cinzel = new FreeTypeFontGenerator(Gdx.files.internal("CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator cinzelReg = new FreeTypeFontGenerator(Gdx.files.internal("Cinzel-Regular.ttf"));
        FreeTypeFontGenerator imFell = new FreeTypeFontGenerator(Gdx.files.internal("IMFellEnglish-Regular.ttf"));
        FreeTypeFontGenerator imFellIt = new FreeTypeFontGenerator(Gdx.files.internal("IMFellEnglish-Italic.ttf"));

        titleFont = gen(cinzel, 72, GOLD, true, 2, new Color(0f, 0f, 0f, 0.8f));
        headerFont = gen(cinzelReg, 36, LAVENDER, true, 1, new Color(0f, 0f, 0f, 0.6f));
        waveFont = gen(cinzelReg, 52, LAVENDER, true, 1, new Color(0f, 0f, 0f, 0.6f));
        hudFont = gen(cinzelReg, 18, WHITE, false, 0, null);
        hudSmall = gen(cinzelReg, 14, DIM, false, 0, null);
        bodyFont = gen(imFell, 22, LAVENDER, false, 0, null);
        bodyItalic = gen(imFellIt, 20, DIM, false, 0, null);

        cinzel.dispose();
        cinzelReg.dispose();
        imFell.dispose();
        imFellIt.dispose();
    }

    private BitmapFont gen(FreeTypeFontGenerator generator, int size, Color color,
                           boolean shadow, int borderWidth, Color borderColor) {
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        parameter.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
        parameter.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
        if (shadow) {
            parameter.shadowOffsetX = 2;
            parameter.shadowOffsetY = 2;
            parameter.shadowColor = new Color(0f, 0f, 0f, 0.7f);
        }
        if (borderWidth > 0 && borderColor != null) {
            parameter.borderWidth = borderWidth;
            parameter.borderColor = borderColor;
        }
        return generator.generateFont(parameter);
    }

    public void dispose() {
        titleFont.dispose();
        headerFont.dispose();
        waveFont.dispose();
        bodyFont.dispose();
        bodyItalic.dispose();
        hudFont.dispose();
        hudSmall.dispose();
    }
}
