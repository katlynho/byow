package byow.lab12;

import byow.Core.Engine;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
//import byow.Core.Engine;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class BoringWorldDemo {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        /*for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int seed = 88;
        Random n = new Random(seed); // for # of rooms
        int num = (n.nextInt() * 33) % 100;
        if (num < 0) {
            num = num * -1;
        }
        if (num < 10) {
            num = num * 2;
        }
        while (num > 40) {
            num = num / 2;
        }

        //Random n = new Random(seed); // for # of rooms
        Random dim = new Random(seed); // dimension of room (width/height)

        // random spot in world for lower left corner of rooms
        Random Llxy = new Random(seed);

        for (int ns = 0; ns < num; ns++) {
            // check if room works
            int x = Llxy.nextInt(WIDTH - 8) + 1;
            int y = Llxy.nextInt(HEIGHT - 8) + 1;
            int w = dim.nextInt(5) + 2;
            int h = dim.nextInt(5) + 2;

            boolean checkRoom = true;

            if (w == 1 || h == 1) {
                checkRoom = false; // hallway
            }
            if (x + w + 1 > WIDTH - 1 || y + h + 1 > HEIGHT - 1 ) {
                checkRoom = false; // out of bounds
            }
            for (int i = x - 1; i < x + w + 1; i += 1) {
                for (int j = y - 1; j < y + h + 1; j += 1) {
                    if (world[i][j] != Tileset.NOTHING) {
                        checkRoom = false;
                        break;
                    }
                }
            }

            if (!checkRoom) {
                ns -= 1;
                continue;
            }
            // make the room
            for (int i = x - 1; i < x + w + 1; i += 1) {
                for (int j = y - 1; j < y + h + 1; j += 1) {
                    if (i == x - 1 || j == y - 1 || j == y + h || i == x + w) {
                        world[i][j] = Tileset.WALL;
                    } else {
                        world[i][j] = Tileset.FLOOR;
                    }
                }
            }
        } */

        // fills in a block 14 tiles wide by 4 tiles tall
        /*for (int x = 20; x < 35; x += 1) {
            for (int y = 5; y < 10; y += 1) {
                world[x][y] = Tileset.WALL;
            }
        } */

        /*for (int i = 0; i < 30; i += 1) { //x val = i, y val = j
            for (int j = 0; j < 20; j += 1) { //should give a 10x20 block, very wide
                world[i][j] = Tileset.WALL;
            }
        }*/
        for (int k = 0; k < 5; k ++) {
            world[k][0] = Tileset.WALL;
        }
        for (int s = 0; s < 8; s++) {
            world[0][s] = Tileset.WALL;
        }
        int x = 1;
        int y = 1;
        int w = 7;
        int h = 2;
        for (int i = x - 1; i < x + w + 1; i += 1) {
            for (int j = y - 1; j < y + h + 1; j += 1) {
                world[i][j] = Tileset.WALL;
            }
        }

        // draws the world to the screen

        ter.renderFrame(world);

    }


}
