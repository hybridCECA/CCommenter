import javax.swing.*;
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

        String prompt = "File Start Comment:\n";

        String description = JOptionPane.showInputDialog(null, prompt + formatStartComment(startCommentList) + "\nEnter file description:", Main.title, JOptionPane.PLAIN_MESSAGE);

        if (description == null){
            throw new CancellationException();
        } else {
            startCommentList.add(description);
        }

        startCommentList.add("");

        String name = JOptionPane.showInputDialog(null, prompt + formatStartComment(startCommentList) + "\nEnter programmer name (and student ID):", Main.title, JOptionPane.PLAIN_MESSAGE);

        if (name == null){
            throw new CancellationException();
        } else {
            startCommentList.add(name);
        }

        startCommentList.add(getDate());

        comment = formatStartComment(startCommentList);
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
