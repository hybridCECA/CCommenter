import java.util.List;

public class Function {
    List<String> parameters;
    private String code;
    private String after;
    private String returnType;
    private int start;
    private int end;

    public Function (){

    }


    // When setting the code, newlines are automatically trimmed
    public void setCode(String code) {
        this.code = Main.trimNewlines(code);
    }
    public String getCode() {
        return code;
    }

    public void setAfter(String after) {
        this.after = Main.trimNewlines(after);
    }
    public String getAfter() {
        return after;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    public String getReturnType() {
        return returnType;
    }

    public void setStart(int start) {
        this.start = start;
    }
    public int getStart() {
        return start;
    }

    public void setEnd(int end) {
        this.end = end;
    }
    public int getEnd() {
        return end;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public List<String> getParameters() {
        return parameters;
    }
}
