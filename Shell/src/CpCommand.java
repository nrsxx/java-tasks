import java.io.File;
import java.io.IOException;

public class CpCommand implements Command {

    private File curDirectory;
    private String destination;
    private String source;
    private boolean alreadyExist;

    CpCommand(File directory, String destination, String source) {
        curDirectory = directory;
        this.destination = destination;
        this.source = source;
        alreadyExist = false;
    }

    @Override
    public File execute() throws WrongCommand {
        File folder = new File(destination);
        //folder is absolute path
        if (!folder.exists()) {
            throw new WrongCommand("Directory does not exist");
        }
        if (folder.isFile()) {
            throw new WrongCommand("Destination is not a directory");
        }

        //source is absolute path
        File file = new File(source);
        try {
            if (file.isDirectory() && file.list().length > 0) {
                throw new WrongCommand("Folder is a non-empty directory.");
            }
        } catch (NullPointerException e) {
            //do nothing
        }

        File newFile = new File(destination + File.separator + file.getName());
        if (file.isFile()) {
            try {
                if (newFile.createNewFile()) {
                    System.out.println("File " + file.getName() + " copied");
                } else {
                    System.out.println("File already exist");
                    alreadyExist = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.isDirectory()) {
            if (newFile.mkdir()) {
                System.out.println("Directory " + file.getName() + " copied");
            } else {
                System.out.println("Directory already exist");
                alreadyExist = true;
            }
        }
        return curDirectory;
    }

    @Override
    public File cancel() throws WrongCommand {
        File newFile = new File(destination + File.separator + new File(source).getName());
        if (!alreadyExist) {
            newFile.delete();
            System.out.println("File " + newFile.getName() + " has been deleted.");
        }
        return curDirectory;
    }

    @Override
    public String name() {
        return "cp";
    }

}
