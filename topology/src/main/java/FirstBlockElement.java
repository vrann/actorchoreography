import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.LoggingAdapter;
import akka.http.impl.util.JavaMapping;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class FirstBlockElement implements BlockElement {

    private Position position;
    private ActorRef mediator;

    public FirstBlockElement(Position position, ActorRef mediator) {
        this.position = position;
        this.mediator = mediator;
    }

    public String getName()
    {
        return String.format("first-%d-%d-actor", position.getX(), position.getY());
    }

    @Override
    public Props getProps() {
        return Props.create(BlockActor.class, () -> new BlockActor(this));
    }

    public List<String> getSubscriptions()
    {
        return Arrays.asList(new String[]{"A11.first.ready", "logs"});
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public AbstractActor.Receive getReceive(LoggingAdapter log, ActorSelfReference selfReference, ActorSystem system)
    {
        ReceiveBuilder builder = new ReceiveBuilder();
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        return builder.match(String.class, s -> {
            if (s.equals("L11.ready")) {
                log.info("Read L11 computed {}", s);
                log.info("Calculate L21 {}", s);
                log.info("Send L21 {}", s);

                //what topic should it be
                //should topic have the cell address so that actor can filter out what cells to listen to
                //or there is any other mechanism in actors to selectively process messages


                //mediator.tell(new DistributedPubSubMediator.Publish("L11.ready", "L11.ready"), getSelf());
            } else if (s.equals("logs")) {
                log.info("Logs request sent");
                //sourceActor.tell(new RequestLogs(1337), ActorRef.noSender());
                mediator.tell(new DistributedPubSubMediator.Publish("logs-1", new RequestLogs(1337)), selfReference.getSelfInstance());
                log.info("Logs request received");
            }
            log.info("Received String message: {}", s);
        })
        .match(LogsOffer.class, msg -> {
            StringBuilder pathBuilder = (new StringBuilder())
                    .append(System.getProperty("user.home"))
                    .append("/.actorchoreography/H-received.data");
            final Path file = Paths.get(pathBuilder.toString());
            Sink<ByteString, CompletionStage<IOResult>> fileSink = FileIO.toPath(file);
            msg.sourceRef.getSource().runWith(fileSink, materializer);
            log.info("Transfer completed");
            //msg.sourceRef.getSource().runWith(Sink.foreach(logitem -> System.out.println(logitem)), materializer);
        })
        .match(RequestLogs.class, msg -> {
            log.info("Logs request sent");
            //sourceActor.tell(new RequestLogs(1337), ActorRef.noSender());
            mediator.tell(new DistributedPubSubMediator.Publish("logs-1", new RequestLogs(1337)), selfReference.getSelfInstance());
            log.info("Logs request received");
        })
        .match(DistributedPubSubMediator.SubscribeAck.class, msg -> log.info("subscribed    "))
        .matchAny(o -> log.info("received unknown message {}", o.getClass()))
        .build();
    }
}
