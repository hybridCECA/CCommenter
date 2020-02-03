import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class Main {
    static String title = "CCommenter";
    static int tabSize = 4;

    public static void main(String[] args) {
        File inputFile = getFile();

        if (inputFile == null){
            System.err.println("No input file selected");
            System.exit(1);
        }

        BufferedReader codeBR = getReader(inputFile);

        if(codeBR == null){
            System.err.println("Error opening input file");
            System.exit(1);
        }

        /*
        BufferedWriter codeBW = getWriter(inputFile);

        if(codeBW == null){
            System.err.println("Error opening output file");
            System.exit(1);
        }
        */

        File ctags = new File("D:\\Programming\\IdeaProjects\\CCommenter\\ctags.exe");
        File ctagsFile = runCtags(ctags, inputFile);

        if(ctagsFile == null){
            System.err.println("Error running ctags");
            System.exit(1);
        }

        getCtagLines(ctagsFile);

        StartComment sc = new StartComment(inputFile.getName());
        try {
            sc.promptForComment();
        } catch (CancellationException e){
            System.out.println("Cancelled");
            System.exit(0);
        }
        System.out.println(sc.getComment());

        /*
        try {
            codeBW.append(sc.getComment());
            while (codeBR.ready()) {
                codeBW.append(codeBR.readLine());
                codeBW.newLine();
            }
            codeBW.close();
        } catch (IOException e){
            System.err.println("Error while reading file");
            System.exit(1);
        }

        FunctionComment fc = new FunctionComment("main() { }");
        try {
            fc.promptForComment();
        } catch (CancellationException e){
            System.out.println("Cancelled");
            System.exit(0);
        }
        System.out.println(fc.getComment());
        */
    }

    private static BufferedReader getReader(File inputFile){
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            return br;
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    private static BufferedWriter getWriter(File inputFile){
        String fullPath = inputFile.getAbsolutePath();
        String outputFilename = FilenameUtils.removeExtension(fullPath) + "_commented." + FilenameUtils.getExtension(fullPath);
        File outputFile = new File(outputFilename);

        try {
            if (!outputFile.exists()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
                return bw;
            } else {
                System.err.println("Output file already exists");
                return null;
            }
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
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

    private static File runCtags(File exe, File inputFile) {
        try {
            String ctagFilename = inputFile.getAbsolutePath() + ".ctags";
            Process p = Runtime.getRuntime().exec("\"" + exe.getAbsolutePath() + "\" --fields=+ne-fkst -u -o \"" + ctagFilename + "\" \"" + inputFile.getAbsolutePath() + "\"");
            p.waitFor();

            File ctagFile = new File (ctagFilename);
            if(ctagFile.exists()) {
                return ctagFile;
            } else {
                return null;
            }
        } catch (InterruptedException | IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<ArrayList<Integer>> getCtagLines (File ctags){
        BufferedReader ctagsBR = getReader(ctags);
        ArrayList<ArrayList<Integer>> allFunctionBounds = new ArrayList<>();

        try {
            while(ctagsBR.ready()){
                String line = ctagsBR.readLine();
                if(line.charAt(0) != '!'){
                    String[] fields = line.split("\t");

                    int start = Integer.parseInt(fields[fields.length-2].replaceAll("[\\D]", ""));
                    int end = Integer.parseInt(fields[fields.length-1].replaceAll("[\\D]", ""));

                    ArrayList<Integer> functionBounds = new ArrayList<Integer>();
                    functionBounds.add(start);
                    functionBounds.add(end);

                    allFunctionBounds.add(functionBounds);
                }
            }

            return allFunctionBounds;
        } catch (IOException e){
            System.err.println("Error reading ctags file");
            return null;
        }
    }
}
