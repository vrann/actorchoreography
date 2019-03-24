import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.List;

public class BlockActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private BlockElement blockElement;

    public BlockActor(BlockElement block) {
        blockElement = block;
        log.info("{} block for position {} started", blockElement.getName(), blockElement.getPosition());
        for (String subscription: blockElement.getSubscriptions()) {
            mediator.tell(new DistributedPubSubMediator.Subscribe(subscription, getSelf()), getSelf());
            log.info("Block ({}) subscribed to {}", blockElement.getPosition(), subscription);
        }
    }

    static public Props props(BlockElement block) {
        return block.getProps();
    }

    public List<String> getTopicSubscriptions()
    {
        return blockElement.getSubscriptions();
    }

    ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

    @Override
    public AbstractActor.Receive createReceive() {
        return blockElement.getReceive(log, () -> {return getSelf();}, getContext().system());
    }
}
