package Models;

/**
 * Created by Ксения Горская on 23.01.2017.
 */
public class Point {
    private double x;
    private double y;

    public Point() {
        x = 0;
        y = 0;
    }
    public Point(int _x,int _y){
        x=_x;
        y=_y;
    }
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(String x, String y) throws Exception {
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}