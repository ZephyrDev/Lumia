package com.zephyr.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.zephyr.game.entities.Player;
import com.zephyr.game.entities.PlayerMP;
import com.zephyr.game.gfx.Colors;
import com.zephyr.game.gfx.Font;
import com.zephyr.game.gfx.Screen;
import com.zephyr.game.gfx.SpriteSheet;
import com.zephyr.game.level.Level;
import com.zephyr.game.net.GameClient;
import com.zephyr.game.net.GameServer;
import com.zephyr.game.net.packets.Packet00Login;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 4;
	public static final String NAME = "Lumia";
	public static Game game;
	public static String version = "Playable 1.1";
	public int levelNum = 0; //0 = block test | 1 = water test

	public JFrame frame;

	public boolean running = false;
	public int tickCount = 0;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
			.getData();
	private int[] colors = new int[6 * 6 * 6];

	private Screen screen;
	public InputHandler input;
	public WindowHandler windowHandler;
	public Level level;
	public Player player;

	public GameClient socketClient;
	public GameServer socketServer;

	public Game() {
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		frame = new JFrame(NAME);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void init() {
		game = this;
		int index = 0;
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					int rr = (r * 255 / 5);
					int gg = (g * 255 / 5);
					int bb = (b * 255 / 5);

					colors[index++] = rr << 16 | gg << 8 | bb;
				}
			}
		}
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		windowHandler = new WindowHandler(this);
		switch(levelNum){
		case 0:
			level = new Level("/levels/small_test_level.png");
			player = new PlayerMP(level, 15, 12, input,
					JOptionPane.showInputDialog(this, "Please enter a username"),
					null, -1);
			break;
		case 1:
			level = new Level("/levels/water_test_level.png");
			player = new PlayerMP(level, 100, 100, input,
					JOptionPane.showInputDialog(this, "Please enter a username"),
					null, -1);
			break;
			
		}
		
		
		level.addEntity(player);
		Packet00Login loginPacket = new Packet00Login(player.getUsername(), player.x, player.y);
		if (socketServer != null) {
			socketServer.addConnection((PlayerMP) player, loginPacket);
		}
		loginPacket.writeData(socketClient);
		
		//if (JOptionPane.showConfirmDialog(this, "Activate Dill Mode?") == 0) {
		//	InputHandler.dillMode = true;
		//}
	}
    

    public synchronized void start() {
    	requestFocus();
        running = true;
        new Thread(this).start();

        if (JOptionPane.showConfirmDialog(this, "Do you want to run the server") == 0) {
            socketServer = new GameServer(this);
            socketServer.start();
        }

        socketClient = new GameClient(this, "127.0.0.1");
        socketClient.start();
        
        
    }

    public synchronized void stop() {
        running = false;
    }

    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / 60D;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        init();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = true;

            while (delta >= 1) {
                ticks++;
                tick();
                delta -= 1;
                shouldRender = true;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender) {
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                frame.setTitle(NAME+" "+version+" | | " + ticks + " ups, " + frames + " fps");
                frames = 0;
                ticks = 0;
            }
        }
    }

    public void tick() {
        tickCount++;
        level.tick();
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        int xOffset = player.x - (screen.width / 2);
        int yOffset = player.y - (screen.height / 2);

        level.renderTiles(screen, xOffset, yOffset);
        //Below player text
        switch(levelNum){
        case 0: 
        	Font.render("Tile Test  Map", screen, 0, 32, Colors.get(-1, -1, -1, 000), 1);
        	break;
        case 1:
        
        	break;
        }
        level.renderEntities(screen);
        //Above player text
        switch(levelNum){
        case 0: 
        	
        	break;
        case 1:
        
        	break;
        }
        
        for (int y = 0; y < screen.height; y++) {
            for (int x = 0; x < screen.width; x++) {
                int colorCode = screen.pixels[x + y * screen.width];
                if (colorCode < 255)
                    pixels[x + y * WIDTH] = colors[colorCode];
            }
        }
        
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        new Game().start();
    }

    public static long fact(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * fact(n - 1);
        }
    }
}
