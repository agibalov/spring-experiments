package me.loki2302

import org.springframework.boot.SpringApplication

class App {
    static void main(String[] args) {
        def app = new SpringApplication(Config)
        app.additionalProfiles = ['app']
        def context = app.run(args)
        try {
            def dataGenerator = context.getBean(DataGenerator)
            dataGenerator.generateData()

            def tester = context.getBean(Tester)
            tester.run()
        } finally {
            context.close()
        }
    }
}
