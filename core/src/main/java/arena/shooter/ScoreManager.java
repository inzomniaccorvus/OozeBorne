package arena.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;

public class ScoreManager {
    private IntArray scores;
    private Json json;
    private static final int MAX_SCORES = 10;
    private static final String FILE = "highscores.json";

    public ScoreManager() {
        json = new Json();
        scores = new IntArray();
    }

    public void load() {
        try {
            FileHandle file = Gdx.files.local(FILE);
            if (!file.exists()) {
                scores = new IntArray();
                return;
            }
            scores = json.fromJson(IntArray.class, file);
            if (scores == null) scores = new IntArray();
        } catch (Exception e) {
            scores = new IntArray();
        }
    }

    public void save() {

        try {
            FileHandle file = Gdx.files.local(FILE);
            file.writeString(json.toJson(scores, IntArray.class), false);
        } catch (Exception e) {
            Gdx.app.log("ScoreManager", "Failed to save scores: " + e.getMessage());
        }


    }

    public void addScore(int newScore) {
        scores.add(newScore);
        scores.sort();
        scores.reverse();
        if (scores.size > MAX_SCORES) {
            scores.truncate(MAX_SCORES);
        }
        save();
    }

    public IntArray getScores() {
        return scores;
    }

    public boolean isTopTen(int s) {
        return scores.size < MAX_SCORES || s > scores.get(scores.size - 1);
    }
}
