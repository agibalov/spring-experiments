package me.loki2302

import me.loki2302.charlatan.RandomEventGeneratorBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DataGenerator {
    @Autowired
    private Facade facade

    @Value('${app.random-events}')
    private long randomEvents

    void generateData() {
        def reg = new RandomEventGeneratorBuilder<ActivityEvent>()
                .withEvent(new CreateUserActivityEvent(), 1)
                .withEvent(new CreatePostActivityEvent(), 10)
                .withEvent(new CreateCommentActivityEvent(), 100)
                .build()

        for(int i = 0; i < randomEvents; ++i) {
            def event = reg.makeEvent()
            event.execute(facade)

            if((i + 1) % 100 == 0) {
                println "${i + 1} of $randomEvents..."
            }
        }
    }

    // WTF: no idea why, but Spring doesn't like it, when this interface is here
    /*private static interface ActivityEvent {
        void execute(Facade facade)
    }*/

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

            def content = UUID.randomUUID().toString()
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
