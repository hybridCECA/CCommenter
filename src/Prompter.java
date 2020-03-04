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

    public boolean ask() throws CancellationException {
        int result = JOptionPane.showConfirmDialog(null, formatText(), Main.title, JOptionPane.YES_NO_CANCEL_OPTION);

        if (result == JOptionPane.CANCEL_OPTION){
            throw new CancellationException();
        } else {
            return result == JOptionPane.YES_OPTION;
        }
    }

    private JScrollPane formatText(){
        JTextArea jt = new JTextArea(text, getRows(), getCols());
        jt.setEditable(false);
        jt.setOpaque(false);
        jt.setTabSize(Main.tabSize);
        jt.setFont(new Font("monospaced", Font.PLAIN, 12));

        JScrollPane sp =  new JScrollPane(jt);
        sp.setBorder(BorderFactory.createEmptyBorder());

        return sp;
    }

    private int getCols(){
       int maxCols = 0;
       int current = 0;
       for(char c : text.toCharArray()){
           if(c == '\n'){
               current=0;
           } else if (c == '\t'){
               current+=Main.tabSize;
           } else {
               current++;
           }
           maxCols=Math.max(maxCols, current);
       }

       return Math.min(Main.cols_max, maxCols);
    }

    private int getRows(){
        int rows = 1;

        for(char c : text.toCharArray()){
            if(c == '\n'){
                rows++;
            }
        }

        return Math.min(Main.rows_max, rows);
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
