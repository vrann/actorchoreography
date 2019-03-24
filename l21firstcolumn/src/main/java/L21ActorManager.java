import java.io.IOException;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import com.typesafe.config.ConfigFactory;

public class L21ActorManager {
    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("l11-actor-system", ConfigFactory.load("app1.conf"));
        Runtime.getRuntime().addShutdownHook(new ProcessorHook(system));
        ActorRef supervisor = system.actorOf(L21Actor.props(), "l21-actor");
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

