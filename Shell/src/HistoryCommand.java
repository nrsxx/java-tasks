import java.io.File;
import java.util.LinkedList;

public class HistoryCommand implements Command {

    private File curDirectory;
    private LinkedList<? extends Command> history;

    HistoryCommand(File curDirectory, LinkedList<? extends Command> history) {
        this.curDirectory = curDirectory;
        this.history = history;
    }

    @Override
    public File execute() {
        System.out.println("HISTORY OF COMMANDS:");
        int counter = 0;
        for (Command command : history) {
            ++counter;
            System.out.println(counter + " " + command.name());
        }
        return curDirectory;
    }

    @Override
    public File cancel() {
        return curDirectory;
        //do nothing
    }

    @Override
    public String name() {
        return "undo";
    }

}
