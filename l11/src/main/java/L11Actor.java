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
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class L11Actor  extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(L11Actor.class, () -> new L11Actor());
    }

    ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

    public L11Actor() {

        // subscribe to the topic named "content"
        mediator.tell(new DistributedPubSubMediator.Subscribe("content", getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if (s.equals("L11.data.ready")) {
                        log.info("Read L11 {}", s);
                        log.info("Calculate L11 {}", s);
                        log.info("Send L11 {}", s);

                        //what topic should it be
                        //should topic have the cell address so that actor can filter out what cells to listen to
                        //or there is any other mechanism in actors to selectively process messages


                        mediator.tell(new DistributedPubSubMediator.Publish("L11.ready", "L11.ready"), getSelf());
                    }
                    log.info("Received String message: {}", s);
                })
                .match(DistributedPubSubMediator.SubscribeAck.class, msg -> log.info("subscribed    "))
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
