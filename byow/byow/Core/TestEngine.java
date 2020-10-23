package byow.Core;

import byow.TileEngine.TETile;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;


public class TestEngine {
    @Test
    public void testEngine() {
        Engine eng = new Engine();
        //int d = 0;
        boolean c = true;
        ArrayList<Room> d = new ArrayList<>();
        for (int k = 1; k < 2; k++) {
            TETile[][] a = eng.interactWithInputString("n" + k + "s");
            TETile[][] b = eng.interactWithInputString("n" + k + "s");
            //DoubleMapPQ<Room> kk = eng.returnMap();
            for (int i = 0; i < eng.retWid(); i++) {
                for (int j = 0; j < eng.retHeight(); j++) {
                    if (a[i][j] != b[i][j]) {
                        c = false;
                        //d.add(new Point);
                        break;
                    }
                }
            }
        }
        assertTrue(c);
        ArrayList<Integer> p = new ArrayList<>();
        p.add(-1);
        //assertTrue(d == p);

    }
}

