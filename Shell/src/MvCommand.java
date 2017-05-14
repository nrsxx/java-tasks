import java.io.File;
import java.io.IOException;

public class MvCommand implements Command {

    private File curDirectory;
    private String source;
    private CpCommand copying;

    MvCommand(File directory, String destination, String source) {
        curDirectory = directory;
        this.source = source;
        copying = new CpCommand(curDirectory, destination, source);
    }

    @Override
    public File execute() throws WrongCommand {
        try {
            copying.execute();
            new File(source).delete();
            System.out.println("Original source has been deleted.");
        } catch (WrongCommand e) {
            System.out.println(e.getMessage());
        }
        return curDirectory;
    }

    @Override
    public File cancel() throws WrongCommand {
        copying.cancel();
        File file = new File(source);
        if (file.isDirectory()) {
            file.mkdir();
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(file.getName() + " file has been recovered.");
        return curDirectory;
    }

    @Override
    public String name() {
        return "mv";
    }

}
