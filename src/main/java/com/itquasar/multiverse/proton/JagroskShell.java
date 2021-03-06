package com.itquasar.multiverse.proton;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.SimpleMaskingCallback;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.logging.LogManager;

public class JagroskShell implements Runnable {

    public static final Character ATOM = '⚛';
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JagroskShell.class);

    // MUST BE THE FIRST BLOCK OF CODE IN THE MAIN CLASS!!!!
    static {
        try {
            LogManager.getLogManager().reset();
            SLF4JBridgeHandler.install();

            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            ContextInitializer ci = new ContextInitializer(loggerContext);
            URL url = ci.findURLOfDefaultConfigurationFile(true);
            loggerContext.reset();
            ci.configureByResource(url);
        } catch (JoranException e) {
            e.printStackTrace();
        }

    }

    private final Console console;

    public JagroskShell(URL configUrl, CommandManager commandManager, PrettyPrinterManager prettyPrinterManager, LineReader lineReader) {
        this.console = new Console(configUrl, commandManager, prettyPrinterManager, lineReader, new SimpleMaskingCallback(ATOM));
    }

    public static void main(String[] args) throws IOException {
        // FIXME
        URL configUrl = JagroskShell.class.getResource("/jagrosk-shell.groovy");

        Terminal terminal = TerminalBuilder.builder()
                // FIXME
                .name("JaGroSK")
                .system(true)
                //.signalHandler(Terminal.SignalHandler.SIG_IGN)
                //.jna(false) // FIXME make configurable
                //.jansi(false) // FIXME make configurable
                //.exec(false) // FIXME make configurable
                .build();

        CommandManager commandManager = new CommandManager();

        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new StringsCompleter(commandManager.getComandNames()))
                // FIXME
                .variable(LineReader.HISTORY_FILE, "/tmp/" + terminal.getName().replace(" ", "_") + ".history")
                .build();
        PrettyPrinterManager prettyPrinterManager = new PrettyPrinterManager();
        new JagroskShell(configUrl, commandManager, prettyPrinterManager, lineReader).run();
    }

    @Override
    public void run() {
        console.run();
    }
}
