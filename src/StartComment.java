import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CancellationException;

public class StartComment {
    private String comment;
    private String filename;

    public StartComment(String fn){
        filename = fn;
    }

    public String getComment(){
        return comment;
    }

    public void promptForComment() throws CancellationException {
        ArrayList<String> startCommentList = new ArrayList<>();

        startCommentList.add(filename);
        startCommentList.add(prompt(startCommentList, "Enter file description:"));
        startCommentList.add("");
        startCommentList.add(prompt(startCommentList, "Enter programmer name (and optionally student ID):"));
        startCommentList.add(getDate());

        comment = formatStartComment(startCommentList);
    }

    private String prompt(ArrayList<String> startCommentList, String inputPrompt) throws CancellationException {
        return (new Prompter("File Start Comment:\n" + formatStartComment(startCommentList) + "\n" + inputPrompt)).prompt();
    }

    private String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(new Date());
    }

    private String formatStartComment(ArrayList<String> scList){
        String startComment = "";

        startComment += "/*\n";
        for (String line : scList){
            startComment += " * " + line + "\n";
        }
        startComment+=" *\n";
        startComment+=" */\n";

        return startComment;
    }
}
