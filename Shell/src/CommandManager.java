import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager {

    private LinkedList<Command> HISTORY;
    private LinkedList<Command> TRASH;
    private File curDirectory;

    private CommandManager(String home) throws WrongCommand {
        HISTORY = new LinkedList<>();
        TRASH = new LinkedList<>();
        File homeDirectory = new File(home);
        if ((!homeDirectory.exists()) || (homeDirectory.isFile())) {
            throw new WrongCommand("Incorrect home directory");
        }
        curDirectory = homeDirectory;
    }

    private Command getCommand(String command) throws WrongCommand {
        List<String> parseCommand = Arrays.stream(command.split(" "))
                .filter((w) -> w.length() > 0)
                .collect(Collectors.toList());
        switch (parseCommand.get(0)) {
            case "cd":
                return new CdCommand(curDirectory, parseCommand.get(1));
            case "cp":
                return new CpCommand(curDirectory, parseCommand.get(1), parseCommand.get(2));
            case "mv":
                return new MvCommand(curDirectory, parseCommand.get(1), parseCommand.get(2));
            case "mkdir":
                return new MkdirCommand(curDirectory, parseCommand.get(1));
            case "redo":
                return new RedoCommand(curDirectory, TRASH.removeLast(), HISTORY);
            case "undo":
                return new UndoCommand(curDirectory, HISTORY.removeLast(), TRASH);
            case "history":
                return new HistoryCommand(curDirectory, HISTORY);
            case "exit":
                return new ExitCommand(curDirectory);
            default:
                throw new WrongCommand("Wrong command");
        }
    }

    private void doSession() throws SessionClosed, WrongCommand {
        Scanner scanner = new Scanner(System.in);
        System.out.println(curDirectory + "$");
        while (true) {
            String curCommandName = scanner.nextLine();
            try {
                Command curCommand = getCommand(curCommandName);
                curDirectory = curCommand.execute();
                if (!curCommand.getClass().equals(RedoCommand.class) &&
                        !curCommand.getClass().equals(UndoCommand.class) &&
                        !curCommand.getClass().equals(HistoryCommand.class) &&
                        !curCommand.getClass().equals(ExitCommand.class)) {
                    HISTORY.add(curCommand);
                }
                System.out.println(curDirectory);
            } catch (WrongCommand e) {
                System.out.println(e.getMessage());
                System.out.println(curDirectory);
            } catch (SessionClosed e) {
                System.out.println(e.getMessage());
                break;
            }
        }
    }

    public static void main(String... args) {

        CommandManager manager;
        try {
            manager = new CommandManager(System.getProperty("user.dir"));
            manager.doSession();
        } catch (WrongCommand | SessionClosed e) {
            System.out.println(e.getMessage());
        }
    }

}
