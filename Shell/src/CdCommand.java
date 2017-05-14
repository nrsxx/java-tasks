import java.io.File;
import java.util.Arrays;

public class CdCommand implements Command {

    private File curDirectory;
    private String path;

    CdCommand(File directory, String path) {
        curDirectory = directory;
        this.path = path;
    }

    @Override
    public File execute() throws WrongCommand {
        if (path.equals("..")) {
            return new File(curDirectory.getParent());
        } else {
            for (String name : Arrays.asList(curDirectory.list())) {
                if (path.equals(name) && new File(curDirectory + File.separator + name).isDirectory()) {
                    return new File(curDirectory.toString() + File.separator + name);
                }
            }
            throw new WrongCommand("No directory named " + path);
        }
    }

    @Override
    public File cancel() {
        return curDirectory;
    }

    @Override
    public String name() {
        return "cd";
    }

}
