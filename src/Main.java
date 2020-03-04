import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static String title = "CCommenter";
    static int tabSize = 8;
    private static String outputFilename;

    // Config file variables
    public static int rows_max = 50;
    public static int cols_max = 250;
    private static String ctags_location = "ctags.exe";
    private static String default_location = null;
    private static String default_name = null;

    public static void main(String[] args) {
        // Read config file
        setConfigFile();

        // Get input file
        File inputFile = getFile(default_location);

        // Get reader for input
        BufferedReader codeBR = getReader(inputFile);

        // Get writer and output file
        BufferedWriter codeBW = getWriter(inputFile);

        // Run ctags
        File ctags = new File(ctags_location);
        File ctagsFile = runCtags(ctags, inputFile);

        // Parse ctags lines and delete ctags file
        List<Function> functionsList = getCtagLines(ctagsFile);
        ctagsFile.delete();

        // Read in file
        int line = 1;
        String beforeF1 = "";

        try {
            // Get string before first function
            StringBuilder beforeF1SB = new StringBuilder();
            int f1Start = getFunctionStart(0, functionsList);
            while (codeBR.ready() && line < f1Start) {
                beforeF1SB.append(codeBR.readLine()).append("\n");
                line++;
            }
            beforeF1 = trimNewlines(beforeF1SB.toString());

            for (int functionNum = 0; functionNum < functionsList.size(); functionNum++) {
                // Get function code
                StringBuilder functionSB = new StringBuilder();
                while (codeBR.ready() && line < functionsList.get(functionNum).getEnd() + 1) {
                    functionSB.append(codeBR.readLine()).append("\n");
                    line++;
                }
                functionsList.get(functionNum).setCode(functionSB.toString());

                // Get string after function
                StringBuilder afterFunctionSB = new StringBuilder();
                int nextStart = getFunctionStart(functionNum + 1, functionsList);
                while (codeBR.ready() && line < nextStart) {
                    afterFunctionSB.append(codeBR.readLine()).append("\n");
                    line++;
                }
                functionsList.get(functionNum).setAfter(afterFunctionSB.toString());
            }

            codeBR.close();
        } catch (IOException e) {
            System.err.println("Error while reading file");
            System.exit(1);
        }

        // Get SC and write file
        StartComment sc = new StartComment(inputFile.getName(), default_name);
        try {
            Prompter p1 = new Prompter("Do you want to insert a start of file comment?");
            if (p1.ask()) {
                sc.promptForComment();

                codeBW.append(sc.getComment());
                codeBW.append("\n");
            }

            codeBW.append(beforeF1);
            codeBW.append("\n\n");

            for (Function f : functionsList) {
                String trimmedCode = reduceLinesTo(f.getCode(), rows_max-12);

                Prompter p2 = new Prompter("Function Code:\n" + trimmedCode + "\n\nDo you want insert a comment for this function?");
                if (p2.ask()) {
                    FunctionComment fc = new FunctionComment(trimmedCode, f.getReturnType(), f.getParameters());
                    fc.promptForComment();

                    codeBW.append(fc.getComment());
                }

                codeBW.append(f.getCode());
                codeBW.append("\n");

                if(!f.getAfter().equals("")){
                    codeBW.append("\n").append(f.getAfter()).append("\n");
                }
                codeBW.append("\n");
            }

            codeBW.close();
        } catch (IOException e) {
            System.err.println("Error while writing file");
            System.exit(1);
        } catch (CancellationException e) {
            System.out.println("Cancelled");
            System.exit(0);
        }

        JOptionPane.showMessageDialog(null, "Commented file sucessfully saved under " + outputFilename, Main.title, JOptionPane.PLAIN_MESSAGE);
    }

    private static void setConfigFile(){
        File configFile = new File("ccommenter_config.txt");
        BufferedReader configBR = getReader(configFile);
        try {
            while (configBR.ready()) {
                String line = configBR.readLine();
                List<String> fields = Arrays.asList(line.split("\t"));
                String field1 = getStringField(fields, 0);
                String field2 = getStringField(fields, 1);

                if(field1 != null && field2 != null) {
                    if (field1.equals("ctags_location")) {
                        ctags_location = field2;
                    } else if (field1.equals("default_location")) {
                        default_location = field2;
                    } else if (field1.equals("default_name")) {
                        default_name = field2;
                    }

                    try {
                        int tmpInt = Integer.parseInt(field2);
                        if (field1.equals("rows_max")) {
                            rows_max = tmpInt;
                        } else if (field1.equals("cols_max")) {
                            cols_max = tmpInt;
                        }
                    } catch (NumberFormatException e){

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static int getFunctionStart(int n, List<Function> fList) {
        return (fList.size() <= n) ? Integer.MAX_VALUE : fList.get(n).getStart();
    }

    private static BufferedReader getReader(File inputFile) {
        try {
            return new BufferedReader(new FileReader(inputFile));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file");
            System.exit(1);

            return null;
        }
    }

    private static BufferedWriter getWriter(File inputFile) {
        try {
            return new BufferedWriter(new FileWriter(getOutputFile(inputFile)));
        } catch (IOException e) {
            System.err.println("Error opening output file");
            System.exit(1);

            return null;
        }
    }

    private static File getOutputFile(File inputFile) {
        String fullPath = inputFile.getAbsolutePath();
        String oFilename = FilenameUtils.removeExtension(fullPath) + "_commented." + FilenameUtils.getExtension(fullPath);
        File outputFile = new File(oFilename);

        int n = 1;
        while (outputFile.exists()) {
            oFilename = FilenameUtils.removeExtension(fullPath) + "_commented_" + n + "." + FilenameUtils.getExtension(fullPath);
            outputFile = new File(oFilename);
            n++;
        }

        outputFilename = oFilename;
        return outputFile;
    }

    private static File getFile(String default_location) {
        JFileChooser fileChooser = new JFileChooser();
        if (default_location == null) {
            default_location = System.getProperty("user.home");
        }
        fileChooser.setCurrentDirectory(new File(default_location));
        fileChooser.setDialogTitle("Open Source Code");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            System.err.println("No input file selected");
            System.exit(1);

            return null;
        }
    }

    private static File runCtags(File exe, File inputFile) {
        try {
            String ctagFilename = inputFile.getAbsolutePath() + ".ctags";
            String command = "\"" + exe.getAbsolutePath() + "\" --fields=+Stne -u -o \"" + ctagFilename + "\" \"" + inputFile.getAbsolutePath() + "\"";
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();

            File ctagFile = new File(ctagFilename);
            if (ctagFile.exists()) {
                return ctagFile;
            }
        } catch (InterruptedException | IOException e) {

        }

        // Is ran if ctags file doesn't exist, Interruped, or IOException
        System.err.println("Error running ctags");
        System.exit(1);

        return null;
    }

    private static List<Function> getCtagLines(File ctags) {
        BufferedReader ctagsBR = getReader(ctags);
        List<Function> functionsList = new ArrayList<>();

        try {
            while (ctagsBR.ready()) {
                String line = ctagsBR.readLine();
                if (line.charAt(0) != '!') {
                    String[] fields = line.split("\t");

                    Function f = new Function();

                    int start;
                    try {
                        start = Integer.parseInt(getFunctionField(fields, "line:"));
                    } catch (NumberFormatException e) {
                        start = -1;
                    }

                    int end;
                    try {
                        end = Integer.parseInt(getFunctionField(fields, "end:"));
                    } catch (NumberFormatException e) {
                        end = start;
                    }

                    String returnType = getFunctionField(fields, "typeref:typename:");
                    returnType = (returnType == null) ? "void" : returnType;

                    String parameters = getFunctionField(fields, "signature:");

                    if (parameters != null && start > 0 && end > 0) { // Add only if has signature and end and start
                        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(parameters);
                        if (m.find()) {
                            parameters = m.group(1);
                        } else {
                            parameters = "void";
                        }
                        List<String> parametersList = Arrays.asList(parameters.split(","));

                        f.setStart(start);
                        f.setEnd(end);
                        f.setReturnType(returnType);
                        f.setParameters(parametersList);

                        functionsList.add(f);
                    }
                }
            }

            ctagsBR.close();
            return functionsList;

        } catch (IOException e) {
            System.err.println("Error reading ctags file");
            return null;
        }
    }

    private static String getFunctionField(String[] list, String query) {
        for (String s : list) {
            if (s.startsWith(query)) {
                return s.replace(query, "");
            }
        }
        return null;
    }

    public static String trimNewlines(String s) {
        return s.replaceAll("\\A\\n+", "").replaceAll("\\n+\\z", "");
    }

    private static String getStringField(List<String> list, int n) {
        try {
            return list.get(n);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private static String reduceLinesTo(String s, int linesNum){
        int line = 1;
        int charNum = 0;

        for(char c : s.toCharArray()){
            if(line >= linesNum -1){
                return s.substring(0, charNum) + "...";
            }
            if(c == '\n'){
                line++;
            }
            charNum++;
        }

        return s;
    }
}
