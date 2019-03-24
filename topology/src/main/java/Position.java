import com.typesafe.config.ConfigObject;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(String x, String y) {
        this.x = Integer.valueOf(x);
        this.y = Integer.valueOf(y);
    }


    public int getX() {
        return x;
    }

    public int getY()
    {
        return y;
    }

    @Override
    public String toString() {
        return (new StringBuilder()).append(Integer.toString(x)).append(':').append(Integer.toString(y)).toString();
    }
}
