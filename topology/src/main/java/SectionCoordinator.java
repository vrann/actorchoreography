import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.List;

public class SectionCoordinator {

    private List<Position> sectionPositions;
    private ActorSystem actorSystem;
    private final LoggingAdapter log;
    private BlockElementFactory elementFactory;

    public SectionCoordinator(ActorSystem system, List<Position> positions, BlockElementFactory elementFactory)
    {
        this.sectionPositions = positions;
        this.actorSystem = system;
        log = Logging.getLogger(actorSystem, this);
        this.elementFactory = elementFactory;
    }

    public void startActors()
    {
//        log.info("Section coordinator started allocation of actors for section of {} actors", sectionPositions.size());
//        for (Position pos: sectionPositions) {
//            log.info("Starting actor for {}", pos);
//            if (pos.getX() == pos.getY() && pos.getX() == 0) { // "first" block at 0,0 coordinates
//                ActorRef block = actorSystem.actorOf(
//                        FirstBlock.props(new Position(pos.getX(), pos.getY())), String.format("first-%d-%d-actor", pos.getX(), pos.getY()));
//            } else if (pos.getX() == pos.getY()) { //block on the diagonal of the matrix
//                ActorRef block = actorSystem.actorOf(
//                        DiagonalBlock.props(new Position(pos.getX(), pos.getY())), String.format("diagonal-%d-%d-actor", pos.getX(), pos.getY()));
//            } else if (pos.getX() == 0) {
//                ActorRef block = actorSystem.actorOf(
//                        FirstColumnBlock.props(new Position(pos.getX(), pos.getY())), String.format("firstcolumn-%d-%d-actor", pos.getX(), pos.getY()));
//            } else {
//                ActorRef block = actorSystem.actorOf(
//                        SubdiagonalBlock.props(new Position(pos.getX(), pos.getY())), String.format("subdiagonal-%d-%d-actor", pos.getX(), pos.getY()));
//            }
//        }
        for (Position pos: sectionPositions) {
            log.info("Starting actor for {}", pos);
            BlockElement element = elementFactory.createBlockElement(pos);
            ActorRef block = actorSystem.actorOf(
                        BlockActor.props(element), element.getName());
        }

    }

}
