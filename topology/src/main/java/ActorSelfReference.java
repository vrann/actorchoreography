import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public interface ActorSelfReference {
    public ActorRef getSelfInstance();
}
