package BTL_brick_breaker_game.src.model;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private List<Level> levels;
    private int currentLevelIndex;

    public LevelManager() {

        levels = new ArrayList<>();

        levels.add(new Level());
        levels.add(new Level());
        levels.add(new Level());

        currentLevelIndex = 0;
    }

    public Level getCurrentLevel(){
        return levels.get(currentLevelIndex);
    }

    public void nextLevel(){

        if(currentLevelIndex < levels.size() - 1){
            currentLevelIndex++;
        }
    }

    public boolean hasNextLevel(){
        return currentLevelIndex < levels.size() - 1;
    }

}