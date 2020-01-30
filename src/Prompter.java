import javax.swing.*;
import java.awt.*;
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
        jt.setTabSize(Main.tabSize);
        jt.setFont(new Font("monospaced", Font.PLAIN, 12));

        String result = JOptionPane.showInputDialog(null, jt, Main.title, JOptionPane.PLAIN_MESSAGE);

        if (result == null){
            throw new CancellationException();
        } else {
            return result;
        }
    }

    private String tabsToSpaces (String s){
        StringBuilder output = new StringBuilder();
        int charsInRow = 0;

        for (int i=0; i<s.length(); i++){
            char currentCh = s.charAt(i);
            if(currentCh == '\t'){
                output.append(" ");
                charsInRow++;
                while(charsInRow%Main.tabSize != 0){
                    output.append(" ");
                    charsInRow++;
                }
            } else if (currentCh == '\n'){
                output.append(currentCh);
                charsInRow=0;
            } else {
                output.append(currentCh);
                charsInRow++;
            }
        }

        return output.toString();
    }
}
