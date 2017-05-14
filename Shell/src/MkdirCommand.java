import java.io.File;

public class MkdirCommand implements Command {

    private File curDirectory;
    private String nameOfNewDirectory;
    private boolean alreadyExist;

    MkdirCommand(File directory, String name) {
        curDirectory = directory;
        nameOfNewDirectory = name;
        alreadyExist = false;
    }

    @Override
    public File execute() throws WrongCommand {
        File file = new File(curDirectory.toString() + File.separator + nameOfNewDirectory);
        alreadyExist = !file.mkdir();
        if (alreadyExist) {
            System.out.println("Directory already exists. Nothing to be done.");
        }
        return curDirectory;
    }

    @Override
    public File cancel() {
        if (!alreadyExist) {
            File file = new File(curDirectory.toString() + File.separator + nameOfNewDirectory);
            file.delete();
        }
        return curDirectory;
    }

    @Override
    public String name() {
        return "mkdir";
    }

}
