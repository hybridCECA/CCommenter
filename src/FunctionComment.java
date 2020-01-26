import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class FunctionComment {
    private String code;
    private String comment;
    private ArrayList<String> functionCommentList;
    private ArrayList<ArrayList<String>> sectionLists;
    private String[] sections = { "Preconditions:", "Postconditions:", "Parameters:\t", "Returns:\t\t", "Exceptions:\t" };

    public FunctionComment(String functionCode){
        code = functionCode;
        functionCommentList = new ArrayList<>();
        sectionLists = new ArrayList<>();
    }

    public String getComment() {
        return comment;
    }

    public void promptForComment() throws CancellationException {
        functionCommentList.add(prompt("Enter function description:"));
        functionCommentList.add("");

        for (String section : sections){
            getFunctionSectionComment(section);
            functionCommentList.add(formatSection(section, sectionLists.get(sectionLists.size()-1)));
        }

        for(int i=0; i<sections.length; i++){
            functionCommentList.remove(functionCommentList.size()-1);
        }

        int generalMaxTabs = getMaxEvenTabsFromList(sectionLists);
        for (int i=0; i<sections.length; i++){
            functionCommentList.add(formatSection(sections[i], sectionLists.get(i), generalMaxTabs));
        }

        comment = formatFunctionComment(functionCommentList);
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


    private void getFunctionSectionComment(String sectionTitle) throws CancellationException{
        ArrayList<String> sectionList = new ArrayList<>();
        sectionLists.add(sectionList);

        while (true) {
            String value;
            value = prompt("Enter " + sectionTitle.replaceAll(":", "") + " (or leave blank to continue):");

            if(value.equals("")) {
                break;
            }

            sectionList.add(value);
            sectionList.add(prompt("Enter description for " + sectionList.get(sectionList.size() -1) + ":"));
        }
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

            if(descriptionIndex >= list.size()){
                section += "\n *" + tabs(4) + "\t" + list.get(valueIndex);
            } else {
                section += "\n *" + tabs(4) + "\t" + list.get(valueIndex) + tabs(extraTabs) + list.get(descriptionIndex);
            }
        }

        return section;
    }

    private String formatSection(String sectionTitle, ArrayList<String> list){
        return formatSection(sectionTitle, list, getMaxEvenTabs(list));
    }

    private String basicSectionFormat() {
        String output = "";
        int generalMaxTabs = getMaxEvenTabsFromList(sectionLists);
        int i=0;

        for (ArrayList<String> list : sectionLists){
            output += formatSection(sections[i], list, generalMaxTabs).replace(" *", "") + "\n";
            i++;
        }
        return output;
    }

    private String prompt(String inputPrompt) throws CancellationException{
        return (new Prompter("Function Code:\n" + code + "\n\nFunction Comment:\n" + formatFunctionComment(functionCommentList) + "\n\n" + basicSectionFormat() + "\n\n" + inputPrompt)).prompt();
    }

    private String tabs(int num){
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < num; i++){
            output.append("\t");
        }

        return output.toString();
    }

    private int getExtraTabs(String s, int tabs){
        return tabs - s.length()/Main.tabSize;
    }

    private int getMaxEvenTabs(ArrayList<String> list){
        boolean even = true;
        int maxTabs = 1;

        for (String element : list){
            if(even){
                int thisTabs = element.length()/Main.tabSize + 1;
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
}
