package BTL_brick_breaker_game.src.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import BTL_brick_breaker_game.src.model.PowerUp;
import BTL_brick_breaker_game.src.model.PowerUp.PowerUpType;
import BTL_brick_breaker_game.src.model.Paddle;
public class PowerUpController {
    private List<PowerUp> powerUps;
    private Random random;

    public PowerUpController(){
        powerUps = new ArrayList<>();
        random = new Random();
    }

    public List<PowerUp> getPowerUps(){
        return powerUps;
    }
    public void spawnPowerUp(int x, int y){

        if(random.nextDouble() < 0.2){ 

            PowerUpType type = PowerUpType.EXPAND;

            int r = random.nextInt(3);

            if(r == 0) type = PowerUpType.EXPAND;
            if(r == 1) type = PowerUpType.MULTI_BALL;
            if(r == 2) type = PowerUpType.SHIELD;

            powerUps.add(new PowerUp(x, y, type));
        }
    }
    public void update(){
        for(PowerUp p : powerUps){
            p.update();
        }
    }
    public void checkCollision(Paddle paddle){

        for(PowerUp p : powerUps){

            if(p.isActive() && p.getBounds().intersects(paddle.getBounds())){

                applyEffect(p, paddle);

                p.deactivate();
            }
        }
    }
    private void applyEffect(PowerUp p, Paddle paddle){

        /*switch(p.getType()){

            case EXPAND:
                paddle.expand();
                break;

            case MULTI_BALL:
                paddle.spawnMultiBall();
                break;

            case SHIELD:
                paddle.activateShield();
                break;
        }*/
    }
}
