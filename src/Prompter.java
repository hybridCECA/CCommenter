import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CancellationException;

public class Prompter {
    String text;

    public Prompter (String p){
        text = p;
    }

    public String prompt() throws CancellationException {
        String result = JOptionPane.showInputDialog(null, formatText(), Main.title, JOptionPane.PLAIN_MESSAGE);

        if (result == null){
            throw new CancellationException();
        } else {
            return result;
        }
    }

    public boolean ask() {
        int result = JOptionPane.showConfirmDialog(null, formatText(), Main.title, JOptionPane.YES_NO_CANCEL_OPTION);

        if (result == JOptionPane.CANCEL_OPTION){
            throw new CancellationException();
        } else {
            return result == JOptionPane.YES_OPTION;
        }
    }

    private JTextArea formatText(){
        JTextArea jt = new JTextArea(text);
        jt.setEditable(false);
        jt.setOpaque(false);
        jt.setTabSize(Main.tabSize);
        jt.setFont(new Font("monospaced", Font.PLAIN, 12));

        return jt;
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
