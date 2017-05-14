import java.io.File;

public class ExitCommand implements Command {

    private File curDirectory;

    ExitCommand(File directory) {
        curDirectory = directory;
    }

    @Override
    public File execute() throws SessionClosed {
        throw new SessionClosed("Session Closed");
    }

    @Override
    public File cancel() {
        return curDirectory;
        //do nothing
    }

    @Override
    public String name() {
        return "exit";
    }

}
