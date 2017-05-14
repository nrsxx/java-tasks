import java.io.File;
import java.util.LinkedList;

public class RedoCommand implements Command {

    private File curDirectory;
    private Command prevCommand;
    private LinkedList<Command> history;

    RedoCommand(File directory, Command command, LinkedList<Command> commands) {
        curDirectory = directory;
        prevCommand = command;
        history = commands;
    }

    @Override
    public File execute() throws WrongCommand, SessionClosed {
        history.add(prevCommand);
        return prevCommand.execute();
    }

    @Override
    public File cancel() {
        //do nothing
        return curDirectory;
    }

    @Override
    public String name() {
        return "redo";
    }

}
