import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.LoggingAdapter;

import java.util.List;

public interface BlockElement {

    public String getName();

    public Props getProps();

    public List<String> getSubscriptions();

    public AbstractActor.Receive getReceive(LoggingAdapter log, ActorSelfReference selfReference, ActorSystem system);

    public Position getPosition();
}
