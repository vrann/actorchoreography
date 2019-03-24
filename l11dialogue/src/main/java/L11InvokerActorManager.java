import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.ConfigFactory;


//what actors to create?
//(1,1) get block, l11 (1,2) (1,3)
//(2,1) l21 (2,2) a22 l11 (2,3)
//(3,1) l21 (3,2) a22 l21 (3,3) a22 l11
//
//data nodes: do I implement separate actor running in parallel or should that be part of the function
//replica can be used to push on all nodes but then we will wait for all nodfes to receive a data
//I need to have identifier of the node (i.e. ip to compare to current ip and to get a data


public class L11InvokerActorManager extends AllDirectives {

    private ActorRef supervisor;

    public static void main(String[] args) throws Exception {
        // boot up server using the route as defined below
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        //In order to access all directives we need an instance where the routes are define.
        L11InvokerActorManager app = new L11InvokerActorManager();
        ActorSystem l11actorSystem  = ActorSystem.create("l11-actor-system", ConfigFactory.load("app2.conf"));


        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute(l11actorSystem).flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("0.0.0.0", 8080), materializer);


    }

    private Route createRoute(ActorSystem l11actorSystem) {
                // Create top level supervisor
        supervisor = l11actorSystem.actorOf(L11InvokerActor.props(), "l11-invoker-actor");
        supervisor.tell("Test", ActorRef.noSender());

        return concat(
                path("hello", () ->
//                                get(() ->
//                                        complete("<h1>Say hello to akka-http</h1>"))
                        get(() -> {
                            supervisor.tell("Test", ActorRef.noSender());
                            return complete(StatusCodes.ACCEPTED, "bid placed");
                        })
                ));
    }
}

