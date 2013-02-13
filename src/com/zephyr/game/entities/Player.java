package com.zephyr.game.entities;

import com.zephyr.game.Game;
import com.zephyr.game.InputHandler;
import com.zephyr.game.gfx.Colors;
import com.zephyr.game.gfx.Font;
import com.zephyr.game.gfx.Screen;
import com.zephyr.game.level.Level;
import com.zephyr.game.net.packets.Packet02Move;

public class Player extends Mob {

    private InputHandler input;
    private int color = Colors.get(-1, 111, 054, 543);
    private int scale = 1;
    protected boolean isSwimming = false;
    private int tickCount = 0;
    private String username;

    public Player(Level level, int x, int y, InputHandler input, String username) {
        super(level, "Player", x, y, 1);
        this.input = input;
        this.username = username;
    }

    public void tick() {
    	
        int xa = 0;
        int ya = 0;
        if (input != null) {
            if (input.up.isPressed()) {
                ya--;
            }
            if (input.down.isPressed()) {
                ya++;
            }
            if (input.left.isPressed()) {
                xa--;
            }
            if (input.right.isPressed()) {
                xa++;
            }
        }
        if (xa != 0 || ya != 0) {
            move(xa, ya);
            isMoving = true;
            
            Packet02Move packet = new Packet02Move(this.username, this.x, this.y, this.numSteps,this.isMoving,this.movingDir);
            packet.writeData(Game.game.socketClient);
            
        } else {
            isMoving = false;
        }
        if (level.getTile(this.x >> 3, this.y >> 3).getId() == 3 ||level.getTile(this.x >> 3, this.y >> 3).getId() == 12 ) {
            isSwimming = true;
        }
        if (isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3 && isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 12  ) {
            isSwimming = false;
        }
        tickCount++;
    }

    public void render(Screen screen) {
        int xTile = 0;
        int yTile = 28;
        int walkingSpeed = 4;
        int flipTop = (numSteps >> walkingSpeed) & 1;
        int flipBottom = (numSteps >> walkingSpeed) & 1;

        if (movingDir == 1) {
            xTile += 2;
        } else if (movingDir > 1) {
            xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;
            flipTop = (movingDir - 1) % 2;
        }

        int modifier = 8 * scale;
        int xOffset = x - modifier / 2;
        int yOffset = y - modifier / 2 - 4;
        if (isSwimming) {
            int waterColour = 0;
            yOffset += 4;
            if(level.getTile(this.x >> 3, this.y >> 3).getId() == 12){
            	//lava
            	  if (tickCount % 60 < 15) {
  	                waterColour = Colors.get(-1, -1, 530, -1);
  	            } else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
  	                yOffset -= 1;
  	                waterColour = Colors.get(-1, 500, 530, -1);
  	            } else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
  	                waterColour = Colors.get(-1, 500, -1, 530);
  	            } else {
  	                yOffset -= 1;
  	                waterColour = Colors.get(-1, 500, 530, -1);
  	            }
            	  screen.render(xOffset, yOffset + 3, 0 + 27 * 32, waterColour, 0x00, 1);
                  screen.render(xOffset + 8, yOffset + 3, 0 + 27 * 32, waterColour, 0x01, 1);
            }else{
		         if (tickCount % 60 < 15) {
		                waterColour = Colors.get(-1, -1, 225, -1);
		            } else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
		                yOffset -= 1;
		                waterColour = Colors.get(-1, 225, 115, -1);
		            } else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
		                waterColour = Colors.get(-1, 115, -1, 225);
		            } else {
		                yOffset -= 1;
		                waterColour = Colors.get(-1, 225, 115, -1);
		            }
		        screen.render(xOffset, yOffset + 3, 0 + 27 * 32, waterColour, 0x00, 1);
		        screen.render(xOffset + 8, yOffset + 3, 0 + 27 * 32, waterColour, 0x01, 1);
        }
        }
        screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, color, flipTop, scale);
        screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, color, flipTop,
                scale);

        if (!isSwimming) {
            screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, color,
                    flipBottom, scale);
            screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile + 1)
                    * 32, color, flipBottom, scale);
        }
        if (username != null) {
            Font.render(username, screen, xOffset - ((username.length() - 1) / 2 * 8), yOffset - 10,
                    Colors.get(-1, -1, -1, 555), 1);
        }
        
        switch(level.getTile(this.x >> 3, this.y >> 3).getId()){
        case 9://snow
        	//color = Colors.get(-1, 111, 054, 555);
        	break;
        case 10: //ice
        	//color = Colors.get(-1, 111, 054, 555);
        	break;
        case 12://lava
        	color = Colors.get(-1, 111, 054, 511);
        	break;
        default:
        	color = Colors.get(-1, 111, 054, 543);
        	break;
        }
        
    }

    public boolean hasCollided(int xa, int ya) {
        int xMin = 0;
        int xMax = 7;
        int yMin = 3;
        int yMax = 7;
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya, x, yMin)) {
                return true;
            }
        }
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya, x, yMax)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya, xMin, y)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya, xMax, y)) {
                return true;
            }
        }
        return false;
    }

    public String getUsername() {
        return this.username;
    }
    

}
