import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        ArrayList<ArrayList<ArrayList<String>>> functionFields = getCtagLines(ctagsFile);

        // Read in file
        int line = 1;
        ArrayList<String> functionStrings = new ArrayList<>();
        String beforeF1;

        try {
            StringBuilder beforeF1SB = new StringBuilder();

            int f1Start = getFunctionStart(0, functionFields);
            while (codeBR.ready() && line < f1Start) {
                beforeF1SB.append(codeBR.readLine()).append("\n");
                line++;
            }
            functionStrings.add(beforeF1SB.toString());

            for(int functionNum = 0; functionNum < functionFields.size(); functionNum++){
                StringBuilder functionSB = new StringBuilder();
                while (codeBR.ready() && line < Integer.parseInt(functionFields.get(functionNum).get(0).get(1)) + 1){
                    functionSB.append(codeBR.readLine()).append("\n");
                    line++;
                }
                functionStrings.add(functionSB.toString());

                StringBuilder afterFunctionSB = new StringBuilder();
                while (codeBR.ready() && line < getFunctionStart(functionNum+1, functionFields)){
                    afterFunctionSB.append(codeBR.readLine()).append("\n");
                    line++;
                }
                functionStrings.add(afterFunctionSB.toString());
            }

            codeBR.close();
        } catch (IOException e){
            System.err.println("Error while reading file");
            System.exit(1);
        }

        // Trim leading and trialing newlines from the strings
        for(int i=0; i < functionStrings.size(); i++){
            String trimmedString = trimNewlines(functionStrings.get(i));
            functionStrings.set(i, trimmedString);
        }

        // Get SC and write file
        StartComment sc = new StartComment(inputFile.getName());
        try {
            sc.promptForComment();

            codeBW.append(sc.getComment());
            codeBW.newLine();

            for (int i = 0; i < functionStrings.size(); i++) {
                if(i % 2 == 1){
                    int functionNumber = (i-1)/2;
                    FunctionComment fc = new FunctionComment(functionStrings.get(i), functionFields.get(functionNumber).get(0).get(2), functionFields.get(functionNumber).get(1));
                    fc.promptForComment();

                    codeBW.newLine();
                    codeBW.append(fc.getComment());
                    codeBW.append(functionStrings.get(i));
                    codeBW.newLine();
                } else {
                    codeBW.append(functionStrings.get(i));
                    codeBW.newLine();
                }
            }

            codeBW.close();
        } catch (IOException e){
            System.err.println("Error while writing file");
            System.exit(1);
        } catch (CancellationException e){
            System.out.println("Cancelled");
            System.exit(0);
        }

        JOptionPane.showMessageDialog(null, "Commented sucessfully", Main.title, JOptionPane.PLAIN_MESSAGE);


        //Junk
        /*
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

    private static int getFunctionStart(int n, ArrayList<ArrayList<ArrayList<String>>> functionFields){
        if(n<functionFields.size()){
            return Integer.parseInt(functionFields.get(n).get(0).get(0));
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private static String trimNewlines (String s){
         return s.replaceAll("\\A\\n+", "").replaceAll("\\n+\\z", "");
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

    private static ArrayList<ArrayList<ArrayList<String>>> getCtagLines (File ctags){
        BufferedReader ctagsBR = getReader(ctags);
        ArrayList<ArrayList<ArrayList<String>>> allFunctionBounds = new ArrayList<>();

        try {
            while(ctagsBR.ready()){
                String line = ctagsBR.readLine();
                if(line.charAt(0) != '!'){
                    String[] fields = line.split("\t");

                    ArrayList<ArrayList<String>> functionFields = new ArrayList<>();

                    String start = getFunctionField(fields, "line:");
                    String end = getFunctionField(fields, "end:");
                    String returnType = getFunctionField(fields, "typeref:typename:");
                    returnType = (returnType == null) ? "void" : returnType;

                    ArrayList<String> info = new ArrayList<>();
                    info.add(start);
                    info.add(end);
                    info.add(returnType);
                    functionFields.add(info);

                    String parameters = getFunctionField(fields, "signature:").replaceAll("\\)$", "").replaceAll("^\\(", "");

                    ArrayList<String> parametersList = new ArrayList<String>(Arrays.asList(parameters.split(",")));
                    functionFields.add(parametersList);

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
