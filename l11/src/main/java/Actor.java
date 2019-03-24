import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Actor {

    public void run()
    {
        final ActorSystem system = ActorSystem.create("helloakka");
        final ActorRef printerActor =
                system.actorOf(MyActor.props(), "printerActor");
        printerActor.tell("test", ActorRef.noSender());
    }
}
