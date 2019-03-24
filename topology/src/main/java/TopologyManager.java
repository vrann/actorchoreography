import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import com.typesafe.config.*;
import static akka.http.javadsl.server.Directives.path;
import static akka.http.javadsl.server.Directives.get;
import static akka.http.javadsl.server.Directives.complete;
import static akka.http.javadsl.server.Directives.concat;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;

import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.ConfigFactory;



import akka.actor.AbstractActor;

import akka.actor.Props;




class LogsOffer implements Serializable {
    final SourceRef<ByteString> sourceRef;

    public LogsOffer(SourceRef<ByteString> sourceRef) {
        this.sourceRef = sourceRef;
    }
}

class DataSource extends AbstractActor {

    private static ActorSystem system;

    static public Props props(ActorMaterializer materializer, ActorSystem system, int sectionId) {
        return Props.create(DataSource.class, () -> new DataSource(materializer, system, sectionId));
    }

    private ActorMaterializer materializer;
    private LoggingAdapter log;
    ActorRef mediator;
    private int sectionId;

    public DataSource(ActorMaterializer materializer, ActorSystem system, int sectionId) {
        this.materializer = materializer;
        this.system = system;
        log  = Logging.getLogger(system, system);
        mediator = DistributedPubSub.get(system).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe((new StringBuilder()).append("logs-").append(sectionId).toString(), getSelf()), getSelf());
        this.sectionId = sectionId;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RequestLogs.class, this::handleRequestLogs).build();
    }

    private void handleRequestLogs(RequestLogs requestLogs) {
        Source<String, NotUsed> logs = streamLogs(requestLogs.streamId);
//        Sink<ByteString, CompletionStage<Done>> printlnSink =
//                Sink.<ByteString> foreach(chunk -> System.out.println(chunk.utf8String()));

        StringBuilder pathBuilder = (new StringBuilder())
                .append(System.getProperty("user.home"))
                .append("/.actorchoreography/H.data");
        final Path file = Paths.get(pathBuilder.toString());
        log.info(file.toString());
        //CompletionStage<IOResult> ioResult =
        Source<ByteString, CompletionStage<IOResult>> fileSource = FileIO.fromPath(file);
//                        .to(sender())
//                        .run(materializer);

        CompletionStage<SourceRef<ByteString>> fileRef = fileSource.runWith(StreamRefs.sourceRef(), materializer);
//        Patterns.pipe(fileRef, context().dispatcher())
//                .to(sender());


        //CompletionStage<SourceRef<String>> logsRef = logs.runWith(StreamRefs.sourceRef(), materializer);

        log.info("Accepted request");
        log.info("Logs Ref {}", requestLogs.streamId);
        Patterns.pipe(fileRef.thenApply(ref -> new LogsOffer(ref)), context().dispatcher())
                .to(sender());
        log.info("Logs request sent to reciever");
    }

    private Source<String, NotUsed> streamLogs(long streamId) {
        return Source.repeat("[INFO] some interesting logs here (for id: " + streamId + ")");
    }
}

class RequestLogs implements Serializable {
    public final long streamId;

    public RequestLogs(long streamId) {
        this.streamId = streamId;
    }
}

public class TopologyManager {

    private static ActorSystem system;
    static private LoggingAdapter log;

    public static void main(String[] args) throws IOException {
        system = ActorSystem.create("l11-actor-system", ConfigFactory.load("app1.conf"));
        Runtime.getRuntime().addShutdownHook(new ProcessorHook(system));
        log = Logging.getLogger(system, system);


        StringBuilder pathBuilder = (new StringBuilder())
            .append(System.getProperty("user.home"))
            .append("/.actorchoreography/node.conf");

        Config config = ConfigFactory.parseFile(new File(pathBuilder.toString()),
                ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF));

        List<? extends ConfigObject> possitionsArray = config.getObjectList("actors.matrix-blocks");
        List<Position> positions = new ArrayList<>();
        int sectionId = config.getInt("actors.section");

        for (ConfigObject position: possitionsArray) {
            positions.add(new Position(position.get("x").render(), position.get("y").render()));
        }

        log.info("Number of actors in section: {}", positions.size());

        ActorRef mediator = DistributedPubSub.get(system).mediator();
        SectionCoordinator sc = new SectionCoordinator(system, positions, new BlockElementFactory(mediator));
        sc.startActors();

        // IncomingConnection and ServerBinding imported from Tcp
//        final Source<Tcp.IncomingConnection, CompletionStage<Tcp.ServerBinding>> connections =
//                Tcp.get(system).bind("0.0.0.0", 8888);



//        final Materializer materializer = ActorMaterializer.create(system);
//
//        ActorRef sourceActor = system.actorOf(Props.create(DataSource.class), "dataSource");
//
//        //sourceActor.tell(new RequestLogs(1337), getTestActor());
//        //LogsOffer offer = expectMsgClass(LogsOffer.class);
//
//        offer.sourceRef.getSource().runWith(Sink.foreach(log -> System.out.println(log)), mat);

//        Tcp.WriteFile()
//        connections.runForeach(
//                connection -> {
//                    System.out.println("New connection from: " + connection.remoteAddress());
//
//                    final Path file = Paths.get("example.csv");
//                    Sink<ByteString, CompletionStage<Done>> printlnSink =
//                            Sink.<ByteString> foreach(chunk -> System.out.println(chunk.utf8String()));
//
//                    CompletionStage<IOResult> ioResult =
//                            FileIO.fromPath(file)
//                                    .to(printlnSink)
//                                    .run(materializer);
//                    FileIO.toPath()
//
//                    final Flow<ByteString, ByteString, NotUsed> echo =
//                            Flow.of(ByteString.class)
//                                    .via(
//                                            Framing.delimiter(
//                                                    ByteString.fromString("\n"), 256, FramingTruncation.DISALLOW))
//                                    .map(ByteString::utf8String)
//                                    .map(s -> s + "!!!\n")
//                                    .map(ByteString::fromString);
//
//                    connection.handleWith(echo, materializer);
//                },
//                materializer);

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        TopologyManager app = new TopologyManager();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute(system, materializer, sectionId).flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("0.0.0.0", 2000), materializer);

    }



    private Route createRoute(ActorSystem system, ActorMaterializer materializer, int sectionId) {
        // Create top level supervisor
//        supervisor = l11actorSystem.actorOf(L11InvokerActor.props(), "l11-invoker-actor");
//        supervisor.tell("Test", ActorRef.noSender());
        ActorRef sourceActor = system.actorOf(DataSource.props(materializer, system, sectionId), "dataSource");
        ActorRef mediator = DistributedPubSub.get(system).mediator();

        return concat(
                path("hello", () ->
//                                get(() ->
//                                        complete("<h1>Say hello to akka-http</h1>"))
                                get(() -> {

//                                    log.info("Logs request sent");
//                                    //sourceActor.tell(new RequestLogs(1337), ActorRef.noSender());
//                                    mediator.tell(new DistributedPubSubMediator.Publish("logs-1", new RequestLogs(1337)), getSelf());
//                                    log.info("Logs request received");
                                    mediator.tell(new DistributedPubSubMediator.Publish("logs", new RequestLogs(1337)), ActorRef.noSender());
                                    //LogsOffer offer = new LogsOffer();

                                    //offer.sourceRef.getSource().runWith(Sink.foreach(log -> System.out.println(log)), materializer);

                                    //supervisor.tell("Test", ActorRef.noSender());
                                    return complete(StatusCodes.ACCEPTED, "bid placed");
                                })
                ));
    }
}

class ProcessorHook extends Thread {

    ActorSystem system;

    public ProcessorHook(ActorSystem system) {
        this.system = system;
    }

    @Override
    public void run(){
        System.out.println("Shutting down actor system");
        system.terminate();
        System.out.println("Actor system terminated");

    }
}

