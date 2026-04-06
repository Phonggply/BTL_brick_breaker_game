package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.PowerUp;

public class PowerUpController {

    private List<PowerUp> powerUps;
    private Random random;

    public PowerUpController() {
        powerUps = new ArrayList<>();
        random = new Random();
    }

    public void spawnPowerUp(int x, int y) {
        if (random.nextFloat() < 0.15) {
            int r = random.nextInt(3);
            PowerUp.Type type = PowerUp.Type.EXPAND;
            
            if(r == 0) type = PowerUp.Type.EXPAND;
            if(r == 1) type = PowerUp.Type.MULTIBALL;
            if(r == 2) type = PowerUp.Type.SHIELD;

            powerUps.add(new PowerUp(x, y, type));
        }
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public void update(int height) {
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp p = powerUps.get(i);
            p.move();
            if (p.getY() > height) {
                powerUps.remove(i--);
            }
        }
    }
}
