import java.util.LinkedList;

public class Process {

    private ProcessConfiguration configuration;
    private ActorFactory actorFactory;
    private LinkedList<Actor> actors = new LinkedList<>();

    Process(
            ProcessConfiguration configuration,
            ActorFactory actorFactory
    ) {
        this.configuration = configuration;
        this.actorFactory = actorFactory;
        Runtime.getRuntime().availableProcessors();
    }

    public void run() {
        for (ActorConfiguration a: configuration.getActors()) {
            actors.add(actorFactory.create(a));
        }

        while (true) {
            for (Actor a: actors) {
                a.run(); //actively listening to messages in the queue?
            }
        }

    }
}
