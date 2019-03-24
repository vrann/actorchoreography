//accepts diagonal-ready
//gets coordinates
//starts with the parameters of own location
//compares own location to l11 coordinates
//if it's him starts execution
// reads datablock and verifies that it's latest (by node from the message)
//calculates block and writes data
//sends message l11ready


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class L11InvokerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(L11InvokerActor.class, () -> new L11InvokerActor());
    }

    //private ActorSelection L11 = getContext().actorSelection("akka.tcp://l11-actor-system@127.0.0.1:5150/user/l11-actor");
    ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    log.info("Receiver {}", mediator.getClass());
                    //L11.tell("L11.data.ready", getSelf());
                    mediator.tell(new DistributedPubSubMediator.Publish("content", "L11.data.ready"), getSelf());
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
