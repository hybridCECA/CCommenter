import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CancellationException;

public class Main {
    static String title = "CCommenter";
    static int tabSize = 4;

    public static void main(String[] args) {
        // Get input file
        File inputFile = getFile();
        if (inputFile == null){
            System.err.println("No input file selected");
            System.exit(1);
        }

        // Get reader for input
        BufferedReader codeBR = getReader(inputFile);
        if(codeBR == null){
            System.err.println("Error opening input file");
            System.exit(1);
        }

        // Get writer and output file
        BufferedWriter codeBW = getWriter(inputFile);
        if(codeBW == null){
            System.err.println("Error opening output file");
            System.exit(1);
        }

        // Run ctags
        File ctags = new File("D:\\Programming\\IdeaProjects\\CCommenter\\ctags.exe");
        File ctagsFile = runCtags(ctags, inputFile);
        if(ctagsFile == null){
            System.err.println("Error running ctags");
            System.exit(1);
        }

        // Parse ctags lines
        ArrayList<ArrayList<String>> functionFields = getCtagLines(ctagsFile);


        // Read in file, trimming newlines


        // Get SC and write file







        //Junk
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
            Process p = Runtime.getRuntime().exec("\"" + exe.getAbsolutePath() + "\" --fields=+Stne -u -o \"" + ctagFilename + "\" \"" + inputFile.getAbsolutePath() + "\"");
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

    private static ArrayList<ArrayList<String>> getCtagLines (File ctags){
        BufferedReader ctagsBR = getReader(ctags);
        ArrayList<ArrayList<String>> allFunctionBounds = new ArrayList<>();

        try {
            while(ctagsBR.ready()){
                String line = ctagsBR.readLine();
                if(line.charAt(0) != '!'){
                    String[] fields = line.split("\t");

                    ArrayList<String> functionFields = new ArrayList<>();

                    String start = getFunctionField(fields, "line:");
                    String end = getFunctionField(fields, "end:");
                    String returnType = getFunctionField(fields, "typeref:typename:");
                    String parameters = getFunctionField(fields, "signature:").replaceAll("\\)$", "").replaceAll("^\\(", "");

                    functionFields.add(start);
                    functionFields.add(end);
                    functionFields.add(returnType);
                    functionFields.addAll(Arrays.asList(parameters.split(",")));

                    allFunctionBounds.add(functionFields);
                }
            }

            return allFunctionBounds;

        } catch (IOException e){
            System.err.println("Error reading ctags file");
            return null;
        }
    }

    private static String getFunctionField(String[] list, String query){
        for (String s : list){
            if(s.contains(query)){
                return s.replace(query, "");
            }
        }
        return null;
    }
}
