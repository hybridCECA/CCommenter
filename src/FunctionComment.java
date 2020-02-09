import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class FunctionComment {
    private String code;
    private String returnType;
    private ArrayList<String> parameters;
    private String comment;
    private String functionDescription;
    private ArrayList<ArrayList<String>> sectionLists;
    private String[] sections = { "Preconditions:", "Postconditions:", "Parameters:\t", "Returns:\t\t", "Exceptions:\t" };

    public FunctionComment(String functionCode, String returnT, ArrayList<String> p){
        code = functionCode;
        returnType = returnT;
        parameters = p;

        sectionLists = new ArrayList<>();
    }

    public String getComment() {
        return comment;
    }

    public void promptForComment() throws CancellationException {
        functionDescription = prompt("Enter function description:");

        for (String section : sections){
            promptForSectionComment(section);
        }

        comment = formatFunctionComment();

    }

    private void promptForSectionComment(String sectionTitle) throws CancellationException{
        ArrayList<String> sectionList = new ArrayList<>();
        sectionLists.add(sectionList);

        int parameterCounter = 0;
        boolean returnDone = false;

        while (true) {
            String value;

            if(sectionTitle.equals(sections[2])){
                if(parameterCounter >= parameters.size()) {
                    break;
                }
                value = parameters.get(parameterCounter);
                parameterCounter++;
            } else if (sectionTitle.equals(sections[3])) {
                if(returnDone) {
                    break;
                } else {
                    value = returnType;
                    returnDone = true;
                }
            } else {
                value = prompt("Enter " + sectionTitle.replaceAll(":", "") + " (or leave blank to continue):");

                if (value.equals("")) {
                    break;
                }
            }

            sectionList.add(value);
            sectionList.add(prompt("Enter description for " + sectionList.get(sectionList.size() -1) + ":"));
        }
    }

    private String prompt(String inputPrompt) throws CancellationException{
        return (new Prompter("Function Code:\n" + code + "\n\nFunction Comment:\n" + formatFunctionComment() + "\n\n" + inputPrompt)).prompt();
    }

    private String formatFunctionComment(){
        StringBuilder output = new StringBuilder();
        int generalMaxTabs = getMaxEvenTabsFromList(sectionLists);
        int i=0;

        output.append("/*\n");
        output.append(" * ").append(((functionDescription == null) ? "" : functionDescription)).append("\n");
        output.append(" *\n");

        for (ArrayList<String> list : sectionLists){
            output.append(formatSection(sections[i], list, generalMaxTabs)).append("\n");
            i++;
        }

        output.append(" *\n");
        output.append(" */\n");

        return output.toString();
    }

    private String formatSection(String sectionTitle, ArrayList<String> list, int maxTabs){
        StringBuilder section = new StringBuilder(" * ");

        if (list.size() == 0){
            section.append(sectionTitle).append("\tnone");
            return section.toString();
        }

        int firstExtraTabs = getExtraTabs(list.get(0), maxTabs);

        if(list.size() == 1){
            section.append(sectionTitle).append("\t").append(list.get(0));
        } else {
            section.append(sectionTitle).append("\t").append(list.get(0)).append(tabs(firstExtraTabs)).append(list.get(1));
        }

        for (int i=1; i<list.size()/2; i++){
            int valueIndex = 2*i;
            int descriptionIndex = 2*i+1;
            int extraTabs = getExtraTabs(list.get(valueIndex), maxTabs);

            if(descriptionIndex >= list.size()){
                section.append("\n *").append(tabs(4)).append("\t").append(list.get(valueIndex));
            } else {
                section.append("\n *").append(tabs(4)).append("\t").append(list.get(valueIndex)).append(tabs(extraTabs)).append(list.get(descriptionIndex));
            }
        }

        return section.toString();
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
