package me.loki2302

import com.google.common.base.Stopwatch
import me.loki2302.charlatan.RandomEventGeneratorBuilder
import org.springframework.boot.SpringApplication

import java.util.concurrent.TimeUnit

class App {
    static void main(String[] args) {
        def app = new SpringApplication(Config)
        app.additionalProfiles = ['app']
        def context = app.run(args)
        try {
            def facade = context.getBean(Facade)
            generateFakeData(facade)

            def stopwatch = Stopwatch.createUnstarted()
            final repetitions = 10000
            repetitions.times {
                def randomUserId = facade.findRandomUser().id

                stopwatch.start()
                facade.findUser(randomUserId)
                stopwatch.stop()
            }

            def elapsed = stopwatch.elapsed(TimeUnit.SECONDS) / repetitions
            println "$elapsed per request ($repetitions requests)"
        } finally {
            context.close()
        }
    }

    private static void generateFakeData(Facade facade) {
        def reg = new RandomEventGeneratorBuilder<ActivityEvent>()
            .withEvent(new CreateUserActivityEvent(), 1)
            .withEvent(new CreatePostActivityEvent(), 10)
            .withEvent(new CreateCommentActivityEvent(), 100)
            .build()

        for(int i = 0; i < 1000; ++i) {
            def event = reg.makeEvent()
            event.execute(facade)
        }
    }

    private static interface ActivityEvent {
        void execute(Facade facade)
    }

    private static class CreateUserActivityEvent implements ActivityEvent {
        @Override
        void execute(Facade facade) {
            def name = UUID.randomUUID().toString()
            facade.makeUser(name)
        }
    }

    private static class CreatePostActivityEvent implements ActivityEvent {
        @Override
        void execute(Facade facade) {
            def user = facade.findRandomUser()
            if(user == null) {
                def name = UUID.randomUUID().toString()
                user = facade.makeUser(name)
            }

            String content = UUID.randomUUID().toString()
            facade.makePost(user, content)
        }
    }

    private static class CreateCommentActivityEvent implements ActivityEvent {
        @Override
        void execute(Facade facade) {
            def user = facade.findRandomUser()
            if(user == null) {
                def name = UUID.randomUUID().toString()
                user = facade.makeUser(name)
            }

            def post = facade.findRandomPost()
            if(post == null) {
                def content = UUID.randomUUID().toString()
                post = facade.makePost(user, content)
            }

            def content = UUID.randomUUID().toString()
            facade.makeComment(user, post, content)
        }
    }
}
