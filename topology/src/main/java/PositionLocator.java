import java.util.ArrayList;
import java.util.List;

public class PositionLocator {

    private Position currentPosition;

    public PositionLocator(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<Position> getL21Neighbours()
    {
        return new ArrayList<>();
    }

    public String getDataLocation(Position position, String type) {
        return "topology1/data";
    }
}
