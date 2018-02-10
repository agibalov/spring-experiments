package me.loki2302

import com.codahale.metrics.ConsoleReporter
import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.util.concurrent.TimeUnit

@Component
class Tester {
    @Autowired
    private Facade facade

    @Value('${app.find-random-user-repetitions}')
    private long findRandomUserRepetitions

    @Value('${app.find-random-post-repetitions}')
    private long findRandomPostRepetitions

    void run() {
        def metrics = new MetricRegistry()
        def reporter = ConsoleReporter.forRegistry(metrics).build()
        reporter.start(1, TimeUnit.SECONDS)

        def findRandomUserTimer = metrics.timer('find random user')
        findRandomUserRepetitions.times {
            def randomUserId = facade.findRandomUser().id

            def timingContext = findRandomUserTimer.time()
            facade.findUser(randomUserId)
            timingContext.stop()
        }

        def findRandomPostTimer = metrics.timer('find random post')
        findRandomPostRepetitions.times {
            def randomPostId = facade.findRandomPost().id

            def timingContext = findRandomPostTimer.time()
            facade.findPost(randomPostId)
            timingContext.stop()
        }
    }
}
