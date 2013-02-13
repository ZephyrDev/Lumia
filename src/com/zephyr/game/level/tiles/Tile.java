package com.zephyr.game.level.tiles;

import com.zephyr.game.gfx.Colors;
import com.zephyr.game.gfx.Screen;
import com.zephyr.game.level.Level;

public abstract class Tile {

    public static final Tile[] tiles = new Tile[256];
    public static final Tile VOID = new BasicSolidTile(0, 0, 0, Colors.get(000, -1, -1, -1), 0xFF000000);
    public static final Tile STONE = new BasicSolidTile(1, 1, 0, Colors.get(-1, 333, -1, -1), 0xFF555555);
    public static final Tile GRASS = new BasicTile(2, 2, 0, Colors.get(-1, 131, 141, -1), 0xFF00FF00);
    public static final Tile WATER = new AnimatedTile(3, new int[][] { { 0, 5 }, { 1, 5 }, { 2, 5 }, { 1, 5 } },Colors.get(-1, 004, 115, -1), 0xFF0000FF, 1000);
    public static final Tile DIRT = new BasicTile(4, 2, 0, Colors.get(-1, 321, 432, 432), 0xFF80552b);
    public static final Tile RED_FLOWER = new BasicTile(5, 3, 0, Colors.get(141, 131, 500, 555), 0xFFff0000);
    public static final Tile YELLOW_FLOWER = new BasicTile(6, 3, 0, Colors.get(141, 131, 550, 555), 0xFFfcff00);
    public static final Tile GRASS_ROCK = new BasicSolidTile(7, 4, 0, Colors.get(111, 131, 333, 141), 0xFF004c00);
    public static final Tile DIRT_ROCK = new BasicSolidTile(8, 4, 0, Colors.get(111, 321, 333, 432), 0xFF462301);
    public static final Tile SNOW = new BasicTile(9, 2, 0, Colors.get(-1, 555, 054, -1), 0xFFffffff);
    public static final Tile ICE = new BasicTile(10, 2, 0, Colors.get(-1, 054, 555, -1), 0xFF00ffb4);
    public static final Tile SAND = new BasicTile(11, 2, 0, Colors.get(-1, 552, 333, -1), 0xFFe0c95e);
    public static final Tile LAVA = new AnimatedTile(12, new int[][] { { 0, 6 }, { 1, 6 }, { 2, 6 }, { 1, 6 } },Colors.get(-1, 500, 530, -1), 0xFFff7e00, 1000);
    
    protected byte id;
    protected boolean solid;
    protected boolean emitter;
    private int levelColour;

    public Tile(int id, boolean isSolid, boolean isEmitter, int levelColour) {
        this.id = (byte) id;
        if (tiles[id] != null)
            throw new RuntimeException("Duplicate tile id on " + id);
        this.solid = isSolid;
        this.emitter = isEmitter;
        this.levelColour = levelColour;
        tiles[id] = this;
    }

    public byte getId() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isEmitter() {
        return emitter;
    }

    public int getLevelColour() {
        return levelColour;
    }

    public abstract void tick();

    public abstract void render(Screen screen, Level level, int x, int y);
}
