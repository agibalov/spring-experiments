package commands

import me.loki2302.AppProperties
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory

class hello {
    @Usage("Say Hello")
    @Command
    def main(InvocationContext context) {
        def beanFactory = (BeanFactory)context.attributes["spring.beanfactory"]
        def appProperties = beanFactory.getBean(AppProperties.class)
        return String.format("HELLO! message is '%s'", appProperties.getMessage())
    }
}
