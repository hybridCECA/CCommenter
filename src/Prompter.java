import javax.swing.*;
import java.util.concurrent.CancellationException;

public class Prompter {
    String text;

    public Prompter (String p){
        text = p;
    }

    public String prompt() throws CancellationException {
        JTextArea jt = new JTextArea(text);
        jt.setEditable(false);
        jt.setOpaque(false);
        jt.setTabSize(3);

        String result = JOptionPane.showInputDialog(null, jt, Main.title, JOptionPane.PLAIN_MESSAGE);

        if (result == null){
            throw new CancellationException();
        } else {
            return result;
        }
    }
}
