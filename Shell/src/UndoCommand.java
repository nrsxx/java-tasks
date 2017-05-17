import java.io.File;
import java.util.LinkedList;

public class UndoCommand implements Command {

    private File curDirectory;
    private Command prevCommand;
    private LinkedList<Command> trash;

    UndoCommand(File directory, Command command, LinkedList<Command> commands) {
        curDirectory = directory;
        prevCommand = command;
        trash = commands;
    }

    @Override
    public File execute() throws WrongCommand {
        trash.add(prevCommand);
        return prevCommand.cancel();
    }

    @Override
    public File cancel() {
        //do nothing
        return curDirectory;
    }

    @Override
    public String name() {
        return "undo";
    }

}
