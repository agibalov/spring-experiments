import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.crsh.text.Color
import org.crsh.text.Decoration

class hello {
    // hello
    @Usage("Say Hello")
    @Command
    def main(InvocationContext context) {
        return "Hello! Time is ${new Date()}"
    }

    // hello table
    @Usage("Demo a table")
    @Command
    def table(InvocationContext context) {
        context.provide([ x: 'rowOne', y: 'hello' ])
        context.provide([ x: 'rowTwo', y: 'world' ])
        context.provide([ x: 'rowThree', y: '!!!' ])
    }

    // hello style
    @Usage("Demo a style")
    @Command
    def style(InvocationContext context) {
        context.getWriter().println('green hello', Color.green)
        context.getWriter().println('bold hello', Decoration.bold)
        context.getWriter().println('underlined red hello on cyan', Decoration.underline, Color.red, Color.cyan)
    }
}
