import javax.swing.*;
import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main {
    static String title = "CCommenter";

    public static void main(String[] args) {
        /*
        File inputFile = getFile();

        if (inputFile == null){
            System.err.println("No input file selected");
            System.exit(1);
        }
        */

        String startComment = getFunctionComment("void main(){}");

        if(startComment == null){
            System.out.println("Cancelled");
            System.exit(0);
        }

        System.out.println(startComment);
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

    private static String getStartComment(String filename){
        ArrayList<String> startCommentList = new ArrayList<String>();
        startCommentList.add(filename);

        String prompt = "File Start Comment:\n";

        String description = JOptionPane.showInputDialog(null, prompt + formatStartComment(startCommentList) + "\nEnter file description:", title, JOptionPane.PLAIN_MESSAGE);

        if (description == null){
            return null;
        } else {
            startCommentList.add(description);
        }

        startCommentList.add("");

        String name = JOptionPane.showInputDialog(null, prompt + formatStartComment(startCommentList) + "\nEnter programmer name (and student ID):", title, JOptionPane.PLAIN_MESSAGE);

        if (name == null){
            return null;
        } else {
            startCommentList.add(name);
        }

        startCommentList.add(getDate());

        return formatStartComment(startCommentList);
    }

    private static String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(new Date());
    }

    private static String formatStartComment(ArrayList<String> scList){
        String startComment = "";

        startComment += "/*\n";
        for (String line : scList){
            startComment += " * " + line + "\n";
        }
        startComment+=" *\n";
        startComment+=" */\n";

        return startComment;
    }

    private static String getFunctionComment(String functionCode){
        ArrayList<String> functionCommentList = new ArrayList<String>();

        String prompt1 = "Function Code:\n";
        String prompt2 = "\nFunction Comment:\n";

        String description = JOptionPane.showInputDialog(null, prompt1 + functionCode + prompt2 + formatFunctionComment(functionCommentList) + "\nEnter file description:", title, JOptionPane.PLAIN_MESSAGE);

        if(description == null){
            return null;
        } else {
            functionCommentList.add(description);
        }

        functionCommentList.add("");

        String[] sections = { "Preconditions:", "Postconditions:", "Parameters:\t", "Returns:\t\t", "Exceptions:\t" };

        for (String section : sections){
            functionCommentList.add(getFunctionSectionComment(section));
        }

        return formatFunctionComment(functionCommentList);

    }

    private static String getFunctionSectionComment(String sectionTitle){
        ArrayList<String> sectionList = new ArrayList<String>();

        while (true) {
            String value = JOptionPane.showInputDialog(null, formatSection(sectionTitle, sectionList) + "\nEnter value (or leave blank for none):", title, JOptionPane.PLAIN_MESSAGE);
            if(value == null || value.equals("")){
                break;
            } else {
                sectionList.add(value);
                String description = JOptionPane.showInputDialog(null, formatSection(sectionTitle, sectionList) + "\nEnter description:", title, JOptionPane.PLAIN_MESSAGE);
                sectionList.add(description);
            }
        }

        return formatSection(sectionTitle, sectionList);
    }

    private static String formatSection(String sectionTitle, ArrayList<String> list){
        String section = "";

        if (list.size() == 0){
            return sectionTitle + "\tnone";
        }

        int maxTabs = getMaxEvenTabs(list);

        int firstExtraTabs = getExtraTabs(list.get(0), maxTabs);

        if(list.size() == 1){
            section += sectionTitle + "\t" + list.get(0);
        } else {
            section += sectionTitle + "\t" + list.get(0) + tabs(firstExtraTabs) + list.get(1);
        }

        for (int i=1; i<list.size()/2; i++){
            int valueIndex = 2*i;
            int descriptionIndex = 2*i+1;
            int extraTabs = getExtraTabs(list.get(valueIndex), maxTabs);

            section += "\n *" + tabs(4) + "\t" + list.get(valueIndex) + tabs(extraTabs) + list.get(descriptionIndex);
        }

        return section;
    }

    private static int getExtraTabs(String s, int tabs){
        return tabs - s.length()/4;
    }

    private static int getMaxEvenTabs(ArrayList<String> list){
        boolean even = true;
        int maxTabs = 1;

        for (String element : list){
            if(even){
                int thisTabs = element.length()/4;
                if(thisTabs > maxTabs){
                    maxTabs = thisTabs;
                }
            }

            even = !even;
        }

        return maxTabs;
    }

    private static String tabs(int num){
        String output = "";

        for (int i = 0; i < num; i++){
            output += "\t";
        }

        return output;
    }

    private static String formatFunctionComment(ArrayList<String> fcList){
        String startComment = "";

        startComment += "/*\n";
        for (String line : fcList){
            startComment += " * " + line + "\n";
        }
        startComment+=" *\n";
        startComment+=" */\n";

        return startComment;
    }
}
