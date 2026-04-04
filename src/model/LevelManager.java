package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private List<Level> levels;
    private int currentLevelIndex;

    public LevelManager() {
        levels = new ArrayList<>();
        loadLevels();
        currentLevelIndex = 0;
    }

    private void loadLevels() {
        for (int i = 1; i <= 7; i++) {
            String path = "assets/levels/level" + i + ".txt";
            Level l = loadLevelFromFile(path);
            if (l != null) {
                levels.add(l);
            } else {
                int[][] fallbackData = {{1, 1, 1, 1, 1, 1, 1, 1}, {2, 2, 2, 2, 2, 2, 2, 2}};
                levels.add(new Level(fallbackData));
                File f = new File(path);
                System.err.println("Cảnh báo: Không tìm thấy " + f.getAbsolutePath() + ", dùng dữ liệu mặc định.");
            }
        }
    }

    private Level loadLevelFromFile(String path) {
        File file = new File(path);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<int[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.trim().split("\\s+");
                int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = values[i].equalsIgnoreCase("X") ? 9 : Integer.parseInt(values[i]);
                }
                rows.add(row);
            }
            
            if (rows.isEmpty()) return null;

            int[][] data = new int[rows.size()][];
            for (int i = 0; i < rows.size(); i++) {
                data[i] = rows.get(i);
            }
            return new Level(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCurrentLevel(int index) {
        if (index >= 0 && index < levels.size()) {
            this.currentLevelIndex = index;
        }
    }

    public int getLevelCount() {
        return levels.size();
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public Level getCurrentLevel() {
        if (levels.isEmpty()) return null;
        return levels.get(currentLevelIndex);
    }

    public void nextLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
        }
    }

    public boolean hasNextLevel() {
        return currentLevelIndex < levels.size() - 1;
    }
}
