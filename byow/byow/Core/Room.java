package byow.Core;

public class Room {
    //public int roomNum;
    private Point centerPos;
    private Point LL; // on the wall of the room
    private Point UL;
    private Point LR;
    private Point UR;
    //public boolean isConnected;

    public Room(int x, int y, int w, int h, int rm) {
        //roomNum = rm;
        LL = new Point(x, y);
        UL = new Point(x, y + h - 1);
        LR = new Point(x + w - 1, y);
        UR = new Point(x + w - 1, y + w - 1);
        double centY = (LL.getY() + UL.getY()) / 2;
        double centX = (LL.getX() + LR.getX()) / 2;
        centerPos = new Point(centX, centY);
        //isConnected = false;
    }

    public Point getCentPos() {
        return centerPos;
    }

    public Point getLL() {
        return LL;
    }

    public Point getUL() {
        return UL;
    }

    public Point getLR() {
        return LR;
    }

    public Point getUR() {
        return UR;
    }


}
