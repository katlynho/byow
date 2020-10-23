package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private int WIDTH = 50;
    private int HEIGHT = 40;
    private int seed;
    private DoubleMapPQ<Room> roomMap;
    //public static DoubleMapPQ<Room> fullRoomMap;
    private TETile[][] world;
    private HashSet<Double> prioritySet;
    private TETile avatar = Tileset.AVATAR;
    private char avatarChar;
    private int xAv; // x and y for avatar mvmt
    private int yAv;

    public Engine() {
        roomMap = new DoubleMapPQ<>();
        //world = new TETile[WIDTH][HEIGHT];
        prioritySet = new HashSet<>();
        //fullRoomMap = new DoubleMapPQ<>();
    }

    public int retWid() {
        return WIDTH;
    }

    public int retHeight() {
        return HEIGHT;
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        // menu screen
        makeMenu();

        String input = "";
        char prev = ' ';
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                makeHUD();
                if (world != null) {
                    ter.renderFrame(world);
                }
                continue;
            }
            char c = Character.toLowerCase(StdDraw.nextKeyTyped());
            if (c == 'r') {
                replay(input); // press r for replay of most recent save
            }
            input += String.valueOf(c); // add key input into string

            // set up of world
            if (c == 'n') {
                input = makeNewString();
                interactWithInputString(input);
            } else if (prev == ':' && c == 'q') {
                // save string to text file when quitting
                input += String.valueOf(avatarChar);
                stringToFile(input);
                System.exit(0);
            } else if (c == 'l') {
                // get string from text file then load previous world
                String oldString = null;
                try {
                    oldString = fileToString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (oldString == null) {
                    StdDraw.text(WIDTH * 0.5, -1 * HEIGHT + 1, "No World to Load");
                } else {
                    interactWithInputString(oldString);
                }
                input = oldString;
            }
            if (c == 'c') {
                chooseAvatar();
                makeMenu();
            } else {
                prev = c;

                if (c == 'a' || c == 's' || c == 'd' || c == 'w') {
                    moveAvatar(c); // movement of avatar
                }
//                ter.renderFrame(world);
            }
        }

//        if (avatar reached goal) {
//            StdDraw.textRight(WIDTH / 2, HEIGHT / 2, "Congrats! You won!");
//        }
    }

    private void makeMenu() {
        int width = 550;
        int height = 700;

        int midWidth = width / 2;
        int midHeight = height / 2;

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.LIGHT_GRAY);
        StdDraw.enableDoubleBuffering();

        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(midWidth, height - 150, "CS61B: THE GAME");

        Font smallFont = new Font("Monaco", Font.PLAIN, 20);
        StdDraw.setFont(smallFont);
        StdDraw.text(midWidth, midHeight, "New World (N)");
        StdDraw.text(midWidth, midHeight - 50, "Load Game (L)");
        StdDraw.text(midWidth, midHeight - 100, "Choose Avatar (C)");
        StdDraw.text(midWidth, midHeight - 175, "Quit (:Q)");
        StdDraw.show();
    }

    private void chooseAvatar() {
        int width = 550;
        int height = 700;

        int midWidth = width / 2;
        int midHeight = height / 2;

        StdDraw.clear(Color.LIGHT_GRAY);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(midWidth, height - 30, "Choose your avatar");
        StdDraw.text(midWidth, midHeight + 100, "Avatar @ (V)");
        StdDraw.text(midWidth, midHeight + 50, "Mountain ▲ (M)");
        StdDraw.text(midWidth, midHeight, "Flower ❀ (F)");
        StdDraw.text(midWidth, midHeight - 50, "Tree ♠ (T)");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                avatarChar = StdDraw.nextKeyTyped();
                if (avatarChar == 'v') {
                    avatar = Tileset.AVATAR;
                } else if (avatarChar == 'm') {
                    avatar = Tileset.MOUNTAIN;
                } else if (avatarChar == 'f') {
                    avatar = Tileset.FLOWER;
                } else if (avatarChar == 't') {
                    avatar = Tileset.TREE;
                }
                break;
            }
        }
        StdDraw.setFont(new Font("Monaco", Font.ITALIC, 20));
        StdDraw.text(midWidth, midHeight - 125,
                "You chose " + avatar.description().toUpperCase() + " " + avatar.character());
        StdDraw.show();
        StdDraw.pause(3000);
    }

    private String makeNewString() {
        int width = 550;
        int height = 700;

        int midWidth = width / 2;
        int midHeight = height / 2;

        String input = "";
        char c = 'n';
        while (c != 's') {
            StdDraw.clear(Color.LIGHT_GRAY);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
            StdDraw.text(midWidth, midHeight + 100, "Enter seed integer");
            StdDraw.text(midWidth, midHeight + 75, "Press 's' to save");
            StdDraw.text(midWidth, midHeight, input);
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                input += String.valueOf(c);
            }
            StdDraw.show();
        }
        return "n" + input;
    }

    // puts string into text file
    private void stringToFile(String input) {
        try {
            File file = new File("C:\\Users\\katlyn\\cs61b\\sp19-proj3-s792-s1492\\"
                    + "proj3\\byow\\Core\\oldInputString.txt");
            FileWriter saveString = new FileWriter(file);
            saveString.write(input);
            saveString.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    /** returns string in the text file
     * @source
     * @return previous input string
     */
    private String fileToString() throws FileNotFoundException {
        File file = new File("C:\\Users\\katlyn\\cs61b\\sp19-proj3-s792-s1492\\"
                + "proj3\\byow\\Core\\oldInputString.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // randomly place avatar in world
    private void avatarStart() {
        Random r = new Random(seed);
        while (true) {
            int xStart = r.nextInt(WIDTH);
            int yStart = r.nextInt(HEIGHT);
            if (world[xStart][yStart].equals(Tileset.FLOOR)) {
                world[xStart][yStart] = avatar;
                xAv = xStart;
                yAv = yStart;
                break;
            }
        }
//        ter.renderFrame(world);
    }

    private boolean isWall(int x, int y) {
        if (world[x][y].equals(Tileset.WALL)) {
            return true;
        }
        return false;
    }

    private void moveAvatar(char c) {
        if (c == 'a') { // move left
            if (!isWall(xAv - 1, yAv)) {
                world[xAv - 1][yAv] = avatar;
                world[xAv][yAv] = Tileset.FLOOR;
                xAv -= 1;
            }
        } else if (c == 'd') { // move right
            if (!isWall(xAv + 1, yAv)) {
                world[xAv + 1][yAv] = avatar;
                world[xAv][yAv] = Tileset.FLOOR;
                xAv += 1;
            }
        } else if (c == 'w') { // move up
            if (!isWall(xAv, yAv + 1)) {
                world[xAv][yAv + 1] = avatar;
                world[xAv][yAv] = Tileset.FLOOR;
                yAv += 1;
            }
        } else if (c == 's') { // move down
            if (!isWall(xAv, yAv - 1)) {
                world[xAv][yAv - 1] = avatar;
                world[xAv][yAv] = Tileset.FLOOR;
                yAv -= 1;
            }
        }
//        ter.renderFrame(world);
    }

    // replay most recent save
    private void replay(String recent) {
        world[xAv][yAv] = Tileset.FLOOR;
        avatarStart();
        for (char c : recent.toLowerCase().toCharArray()) {
            moveAvatar(c);
            StdDraw.pause(700);
        }
    }

    private void makeHUD() {
        StdDraw.setFont(new Font("monaco", Font.PLAIN, 15));
        StdDraw.setPenColor(Color.white);
        StdDraw.textLeft(4, 2, "Press 'r' to replay moves");
        mousePos();
        StdDraw.line(0, 3, WIDTH, 3);
        StdDraw.show();
    }

    private void mousePos() {
        StdDraw.enableDoubleBuffering();

        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX < WIDTH && mouseX > 0 && mouseY < HEIGHT && mouseY > 0) {
            TETile currPos = world[mouseX][mouseY];
            if (currPos.equals(Tileset.FLOOR)) {
                StdDraw.textRight(WIDTH - 1, 2, "Tile Type: FLOOR");
            } else if (currPos.equals(Tileset.WALL)) {
                StdDraw.textRight(WIDTH - 1, 2, "Tile Type: WALL");
            } else if (currPos.equals(avatar)) {
                StdDraw.textRight(WIDTH - 1, 2, "Tile Type: AVATAR");
            } else {
                StdDraw.textRight(WIDTH - 1, 2, "Tile Type: NOTHING");
            }
        }
        StdDraw.show();
        StdDraw.pause(200);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        String oldString = "";

        if (input.toLowerCase().toCharArray()[0] == 'l') {
            try {
                oldString = fileToString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (oldString == null) {
                StdDraw.text(WIDTH * 0.5, -1 * HEIGHT + 1, "No World to Load");
            }
        }
        String newInput = "";
        input = oldString + input;
        char[] chars = input.toLowerCase().toCharArray();

        seed = 0;
        int i = 0;

        for (Character c : chars) {
            if (c == 'l') {
                continue;
            }
            newInput += String.valueOf(c);
            if (c == 's') {
                break;
            }
            if (Character.isDigit(c)) {
                seed = seed * 10 + Character.getNumericValue(c);
            }
            i++;
        }

//        ter.initialize(WIDTH, HEIGHT);

        world = createWorld(new TETile[WIDTH][HEIGHT]);
        createRooms(seed);
        Room rm1 = roomMap.removeSmallest();
        Room rm2 = roomMap.removeSmallest();
        createHallways(rm1, rm2);
        while (roomMap.size() > 0) {
            rm1 = rm2;
            rm2 = roomMap.removeSmallest();
            createHallways(rm1, rm2);
        }

        avatarStart();
        char prev = ' ';
        for (int k = i + 1; k < chars.length; k++) {
            char c = chars[k];
            if (c == 'a' || c == 's' || c == 'd' || c == 'w') {
                moveAvatar(c);
                newInput += String.valueOf(c);
            }
            if (prev == ':' && c == 'q') {
                stringToFile(newInput);
            }
            if (c == 'f') {
                avatar = Tileset.FLOWER;
            } else if (c == 'm') {
                avatar = Tileset.MOUNTAIN;
            } else if (c == 't') {
                avatar = Tileset.TREE;
            }
            prev = c;
        }
//        ter.renderFrame(world);

        return world;
    }

    private TETile[][] createWorld(TETile[][] wld) {
        /* initialize world with all tiles as NOTHING */
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                wld[x][y] = Tileset.NOTHING;
            }
        }
        return wld;
    }

    private void createRooms(int rmSeed) {
        Random n = new Random(rmSeed); // for # of rooms

        int num = (n.nextInt() * 33) % 100;
        int maxRooms = 20;
        int minRooms = 13;
        if (num < 0) {
            num = num * -1;
        }
        if (num == 0) {
            num = minRooms;
        }
        if (num < minRooms) {
            num = num * 2;
        }
        while (num > maxRooms) {
            num = num / 2;
        }
        while (num < minRooms) {
            num++;
        }
        //num = 20;


        Random dim = new Random(seed); // dimension of room (width/height)
        //Random Llxy = new Random(seed);

        Random xyLL = new Random(seed);

        for (int j = 0; j < num; j++) {
            // check if room works
            int x = xyLL.nextInt(WIDTH - 9) + 1;
            int y = xyLL.nextInt(HEIGHT - 10) + 5;
            int w = dim.nextInt(4) + 3;
            int h = dim.nextInt(4) + 3;

            if (!checkRoom(x, y, w, h)) {
                j -= 1;
                continue;
            }
            // make the room
            createSingleRoom(x, y, w, h);
            Room rm = new Room(x, y, w, h, num);
            double temp = x;
            if (prioritySet.contains(temp)) {
                while (prioritySet.contains(temp)) {
                    temp += 0.1;
                }
            }
            prioritySet.add(temp);
            roomMap.add(rm, temp);
            // world[x][y] = Tileset.FLOOR;
            //roomMap.add(rm, x); // for rooms closest to each other by their x
        }
        //fullRoomMap = roomMap;

    }

    /**
     * checks whether placement of room is valid within world
     *
     * @param x position of x value of lower left tile
     * @param y pos of y value of lower left tile
     * @param w width of room
     * @param h height of room
     * @return
     */
    private boolean checkRoom(int x, int y, int w, int h) {
        if (w == 1 || h == 1) {
            return false; // hallway
        }
        if (x + w + 1 > WIDTH - 1 || y + h + 1 > HEIGHT - 1) {
            return false; // out of bounds
        }
        for (int i = x - 1; i < x + w + 1; i += 1) {
            for (int j = y - 1; j < y + h + 1; j += 1) {
                if (world[i][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    private void createSingleRoom(int x, int y, int w, int h) {
        for (int i = x - 1; i < x + w + 1; i += 1) {
            for (int j = y - 1; j < y + h + 1; j += 1) {
                if (i == x - 1 || j == y - 1 || j == y + h || i == x + w) {
                    world[i][j] = Tileset.WALL;
                } else {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }

    }

    private void createHallways(Room rm1, Room rm2) {
        if (!sharedX(rm1, rm2) && !sharedY(rm1, rm2)) {
            // if no straight hallways -> make corner hallways
            makeCornerHalls(rm1, rm2);
        }
    }


    private boolean sharedX(Room rm1, Room rm2) {
        ArrayList<Integer> sharedXList = new ArrayList();
        int sharedX = -1;
        // check for shared x-value
        for (int x1 = (int) rm1.getLL().getX(); x1 <= rm1.getLR().getX(); x1++) {
            for (int x2 = (int) rm2.getLL().getX(); x2 <= rm2.getLR().getX(); x2++) {
                if (x1 == x2) {
                    sharedX = x1;
                    sharedXList.add(sharedX);
                    //break;
                }
            }
        }
        if (sharedX >= 0) {
            if (rm1.getCentPos().getY() > rm2.getCentPos().getY()) {
                //check to see if path is clear
                //for (int a = (int) rm1.LL.getY() - 1; a)
                //room 1 is above room 2
                //goes down
                int ny = 0;
                sharedX = sharedXList.get(0);
                for (int y = (int) rm1.getLL().getY() - 1; y > rm2.getUL().getY(); y--) {
                    if (world[sharedX][y] == Tileset.FLOOR) {
                        continue;
                    }
                    //if (world[sharedX][y] == Tileset.WALL && world[sharedX + 1][y] != Tileset.NOTHING) {
                    //  world[sharedX - 1][y] = Tileset.WALL;
                    //}
                    if (world[sharedX][y] != Tileset.WALL && world[sharedX - 1][y] == Tileset.NOTHING && world[sharedX + 1][y] != Tileset.WALL) {
                        world[sharedX - 1][y] = Tileset.WALL;
                    }

                    world[sharedX][y] = Tileset.FLOOR;

                    world[sharedX + 1][y] = Tileset.WALL;
                    if (world[sharedX - 1][y] == Tileset.NOTHING && world[sharedX - 2][y] == Tileset.WALL) {
                        world[sharedX - 1][y] = Tileset.FLOOR;
                    }
                    if (world[sharedX - 1][y] == Tileset.NOTHING && world[sharedX - 2][y] != Tileset.WALL) {
                        world[sharedX - 1][y] = Tileset.WALL;
                    }
                    if (world[sharedX + 1][y] == Tileset.NOTHING && world[sharedX + 2][y] == Tileset.WALL) {
                        world[sharedX + 1][y] = Tileset.FLOOR;
                    }
                    if (world[sharedX + 1][y] == Tileset.NOTHING && world[sharedX + 2][y] != Tileset.WALL) {
                        world[sharedX + 1][y] = Tileset.WALL;
                    }
                    ny = y;
                }
                if (world[sharedX][ny] == Tileset.WALL) {
                    world[sharedX][ny] = Tileset.FLOOR;
                }
            } else {
                // room 1 is below room 2
                //goes up
                sharedX = sharedXList.get(sharedXList.size() - 1);
                for (int y = (int) rm1.getUL().getY() + 1; y < rm2.getLL().getY(); y++) {

                    if (world[sharedX][y] == Tileset.FLOOR) {
                        continue;
                    }
                    if (world[sharedX][y] != Tileset.WALL && world[sharedX + 1][y] == Tileset.NOTHING) {
                        world[sharedX + 1][y] = Tileset.WALL;
                    }
                    //if (world[sharedX][y] != Tileset.WALL && world[sharedX - 1][y] == Tileset.NOTHING) {
                    world[sharedX - 1][y] = Tileset.WALL;
                    //}
                    world[sharedX][y] = Tileset.FLOOR;
                    if (world[sharedX - 1][y] == Tileset.NOTHING && world[sharedX - 2][y] == Tileset.WALL) {
                        world[sharedX - 1][y] = Tileset.FLOOR;
                    }
                    if (world[sharedX - 1][y] == Tileset.NOTHING && world[sharedX - 2][y] != Tileset.WALL) {
                        world[sharedX - 1][y] = Tileset.WALL;
                    }
                    if (world[sharedX + 1][y] == Tileset.NOTHING && world[sharedX + 2][y] == Tileset.WALL) {
                        world[sharedX + 1][y] = Tileset.FLOOR;
                    }
                    if (world[sharedX + 1][y] == Tileset.NOTHING && world[sharedX + 2][y] != Tileset.WALL) {
                        world[sharedX + 1][y] = Tileset.WALL;
                    }

                }
            }
            return true;
        }
        return false;
    }

    private boolean sharedY(Room rm1, Room rm2) {
        int sharedY = -1;
        // check if shared y-value
        for (int y1 = (int) rm1.getLL().getY(); y1 <= rm1.getUL().getY(); y1++) {
            for (int y2 = (int) rm2.getLL().getY(); y2 <= rm2.getUL().getY(); y2++) {
                if (y1 == y2) {
                    sharedY = y1;
                    break;
                }
            }
        }
        if (sharedY >= 0) {
            for (int x = (int) rm1.getLR().getX() + 1; x < rm2.getLL().getX(); x++) {
                world[x][sharedY] = Tileset.FLOOR;
                world[x][sharedY - 1] = Tileset.WALL;
                world[x][sharedY + 1] = Tileset.WALL;
            }
            return true;
        }
        return false;
    }

    private void makeCornerHalls(Room rm1, Room rm2) {
        int y1 = (int) rm1.getCentPos().getY();
        int x;
        boolean stop = false;
        for (x = (int) rm1.getLR().getX() + 1; x < rm2.getLL().getX() - 1; x++) {
            if (world[x][y1] == Tileset.FLOOR) {
                stop = true;
                break;
            }

            world[x][y1] = Tileset.FLOOR;
            world[x][y1 - 1] = Tileset.WALL;
            world[x][y1 + 1] = Tileset.WALL;
        }


        if (!stop) {
            int floorSide = 0;
            if (world[x][y1] == Tileset.WALL) {
                if (world[x - 1][y1] == Tileset.FLOOR) {
                    floorSide = -1; // floor wall
                } else if (world[x + 1][y1] == Tileset.FLOOR) {
                    floorSide = 1; // wall floor
                }
            }
            // room 1 is above room 2
            if (y1 > rm2.getUL().getY()) {
                world[x][y1] = Tileset.FLOOR;
                world[x + 1][y1 + 1] = Tileset.WALL;
                world[x][y1 + 1] = Tileset.WALL;
                world[x + 1][y1] = Tileset.WALL;
                for (int y2 = y1 - 1; y2 > rm2.getUL().getY() - 1; y2--) {
                    if (world[x][y2] == Tileset.FLOOR) {
                        continue;
                    }
                    world[x][y2] = Tileset.FLOOR;
                    //if (floorSide != -1) {
                    world[x - 1][y2] = Tileset.WALL;
                    //}
                    world[x + 1][y2] = Tileset.WALL;
                    if (y2 == rm2.getUL().getY()) {
                        world[x + 1][y2] = Tileset.FLOOR;
                        world[x - 1][y2 - 1] = Tileset.WALL;
                    }
                }

            } else {
                // room 1 is below room 2
                world[x][y1] = Tileset.FLOOR;
                world[x][y1 - 1] = Tileset.WALL;
                world[x + 1][y1 - 1] = Tileset.WALL;
                world[x + 1][y1] = Tileset.WALL;
                for (int y2 = y1 + 1; y2 < rm2.getLL().getY() + 1; y2++) {
                    if (world[x][y2] == Tileset.FLOOR) {
                        continue;
                    }
                    world[x][y2] = Tileset.FLOOR;
                    //if (floorSide != -1) {
                    world[x - 1][y2] = Tileset.WALL;
                    //}
                    //if (floorSide != 1) {
                    world[x + 1][y2] = Tileset.WALL;
                    //}
                    if (y2 == rm2.getLL().getY()) {
                        world[x + 1][y2] = Tileset.FLOOR;
                        world[x - 1][y2 + 1] = Tileset.WALL;
                    }
                }

            }
        }
    }

}
