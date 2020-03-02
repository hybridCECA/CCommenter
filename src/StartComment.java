import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;

public class StartComment {
    private String comment;
    private String filename;
    private String name;

    public StartComment(String fn, String n){
        filename = fn;
        name = n;
    }

    public String getComment(){
        return comment;
    }

    public void promptForComment() throws CancellationException {
        List<String> startCommentList = new ArrayList<>();

        startCommentList.add(filename);

        String description = prompt(startCommentList, "Enter file description:");
        if(!description.isEmpty()) {
            startCommentList.add(description);
        }

        startCommentList.add("");

        name = (name == null) ? prompt(startCommentList, "Enter programmer name (and optionally student ID):") : name;
        if(!name.isEmpty()) {
            startCommentList.add(name);
        }

        startCommentList.add(getDate());

        comment = formatStartComment(startCommentList);
    }

    private String prompt(List<String> startCommentList, String inputPrompt) throws CancellationException {
        return (new Prompter("File Start Comment:\n" + formatStartComment(startCommentList) + "\n" + inputPrompt)).prompt();
    }

    private String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(new Date());
    }

    private String formatStartComment(List<String> scList){
        StringBuilder startComment = new StringBuilder();

        startComment.append("/*\n");
        for (String line : scList){
            startComment.append(" * ").append(line).append("\n");
        }
        startComment.append(" *\n");
        startComment.append(" */\n");

        return startComment.toString();
    }
}
