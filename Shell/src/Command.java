import java.io.File;

public interface Command {
    File execute() throws WrongCommand, SessionClosed;
    File cancel() throws WrongCommand;
    String name();
}
