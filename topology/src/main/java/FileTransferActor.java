import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.SourceRef;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.StreamRefs;
import akka.util.ByteString;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

class FileTransferActor extends AbstractActor {

    private static ActorSystem system;

    static public Props props(ActorMaterializer materializer, ActorSystem system, int sectionId) {
        return Props.create(FileTransferActor.class, () -> new FileTransferActor(materializer, system, sectionId));
    }

    private ActorMaterializer materializer;
    private LoggingAdapter log;
    ActorRef mediator;
    private int sectionId;

    public FileTransferActor(ActorMaterializer materializer, ActorSystem system, int sectionId) {
        this.materializer = materializer;
        this.system = system;
        log  = Logging.getLogger(system, system);
        mediator = DistributedPubSub.get(system).mediator();
        this.sectionId = sectionId;
        mediator.tell(new DistributedPubSubMediator.Subscribe(String.format("request-file-transfer-%d", sectionId), getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RequestFileTransfer.class, this::handleRequestLogs).build();
    }

    private void handleRequestLogs(RequestFileTransfer requestFileTransfer) {
        StringBuilder pathBuilder = (new StringBuilder())
                .append(System.getProperty("user.home"))
                .append("/.actorchoreography/")
                .append(requestFileTransfer.fileName);
        final Path file = Paths.get(pathBuilder.toString());
        Source<ByteString, CompletionStage<IOResult>> fileSource = FileIO.fromPath(file);
        CompletionStage<SourceRef<ByteString>> fileRef = fileSource.runWith(StreamRefs.sourceRef(), materializer);
        Patterns.pipe(fileRef.thenApply(ref -> new FileTransfer(requestFileTransfer.fileName, ref)), context().dispatcher())
                .to(sender());
    }
}