import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class FunctionComment {
    private String code;
    private String comment;

    public FunctionComment(String functionCode){
        code = functionCode;
    }

    public String getComment() {
        return comment;
    }

    public void promptForComment() throws CancellationException {
        ArrayList<String> functionCommentList = new ArrayList<>();

        String prompt1 = "Function Code:\n";
        String prompt2 = "\nFunction Comment:\n";

        String description = JOptionPane.showInputDialog(null, prompt1 + code + prompt2 + formatFunctionComment(functionCommentList) + "\nEnter function description:", Main.title, JOptionPane.PLAIN_MESSAGE);

        if(description == null){
            throw new CancellationException();
        } else {
            functionCommentList.add(description);
        }

        functionCommentList.add("");

        String[] sections = { "Preconditions:", "Postconditions:", "Parameters:\t", "Returns:\t\t", "Exceptions:\t" };

        ArrayList<ArrayList<String>> sectionLists = new ArrayList<>();

        for (String section : sections){
            sectionLists.add(getFunctionSectionComment(section));
        }

        int generalMaxTabs = getMaxEvenTabsFromList(sectionLists);

        for (int i=0; i<sections.length; i++){
            functionCommentList.add(formatSection(sections[i], sectionLists.get(i), generalMaxTabs));
        }

        comment = formatFunctionComment(functionCommentList);

    }

    private ArrayList<String> getFunctionSectionComment(String sectionTitle){
        ArrayList<String> sectionList = new ArrayList<>();

        while (true) {
            String value = JOptionPane.showInputDialog(null, formatSection(sectionTitle, sectionList, getMaxEvenTabs(sectionList)) + "\nEnter value (or leave blank for none):", Main.title, JOptionPane.PLAIN_MESSAGE);
            if(value == null || value.equals("")){
                break;
            } else {
                sectionList.add(value);
                String description = JOptionPane.showInputDialog(null, formatSection(sectionTitle, sectionList, getMaxEvenTabs(sectionList)) + "\nEnter description:", Main.title, JOptionPane.PLAIN_MESSAGE);
                sectionList.add(description);
            }
        }

        return sectionList;
    }

    private String formatSection(String sectionTitle, ArrayList<String> list, int maxTabs){
        String section = "";

        if (list.size() == 0){
            return sectionTitle + "\tnone";
        }

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

    private int getExtraTabs(String s, int tabs){
        return tabs - s.length()/4;
    }

    private int getMaxEvenTabs(ArrayList<String> list){
        boolean even = true;
        int maxTabs = 1;

        for (String element : list){
            if(even){
                int thisTabs = element.length()/4+1;
                if(thisTabs > maxTabs){
                    maxTabs = thisTabs;
                }
            }

            even = !even;
        }

        return maxTabs;
    }

    private int getMaxEvenTabsFromList(ArrayList<ArrayList<String>> listOfLists){
        int max = 1;


        for (ArrayList<String> list : listOfLists){
            int listMax = getMaxEvenTabs(list);
            if(listMax > max){
                max = listMax;
            }
        }

        return max;
    }

    private String tabs(int num){
        String output = "";

        for (int i = 0; i < num; i++){
            output += "\t";
        }

        return output;
    }

    private String formatFunctionComment(ArrayList<String> fcList){
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
