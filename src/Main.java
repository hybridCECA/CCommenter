import javax.swing.*;
import java.io.File;
import java.util.concurrent.CancellationException;

public class Main {
    static String title = "CCommenter";
    static int tabSize = 4;

    public static void main(String[] args) {
        /*
        File inputFile = getFile();

        if (inputFile == null){
            System.err.println("No input file selected");
            System.exit(1);
        }

        StartComment sc = new StartComment(inputFile.getName());
        try {
            sc.promptForComment();
        } catch (CancellationException e){
            System.out.println("Cancelled");
            System.exit(0);
        }
        System.out.println(sc.getComment());

        */
        FunctionComment fc = new FunctionComment("main() { }");
        try {
            fc.promptForComment();
        } catch (CancellationException e){
            System.out.println("Cancelled");
            System.exit(0);
        }
        System.out.println(fc.getComment());
    }

    private static File getFile (){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
}
