import akka.actor.ActorRef;

public class BlockElementFactory {

    private ActorRef mediator;

    public BlockElementFactory(ActorRef mediator) {
        this.mediator = mediator;
    }

    public BlockElement createBlockElement(Position pos)
    {
        if (pos.getX() == pos.getY() && pos.getX() == 0) { // "first" block at 0,0 coordinates
            return new FirstBlockElement(pos, mediator);
        } else if (pos.getX() == pos.getY()) { //block on the diagonal of the matrix
            return new DiagonalBlockElement(pos, mediator);
        } else if (pos.getX() == 0) {
            return new FirstColumnBlockElement(pos, mediator);
        } else {
            return new SubdiagonalBlockElement(pos, mediator);
        }
    }

}
