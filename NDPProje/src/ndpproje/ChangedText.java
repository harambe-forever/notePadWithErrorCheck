package ndpproje;

public class ChangedText {

    public static final int REMOVE_OPERATION = 0;
    public static final int ADD_OPERATION = 1;
    public static final int SWITCH_OPERATION = 2;

    private final int position; // index of the letter or word
    private final String text; // the text which is altered in a way
    private final int operation;

    public ChangedText(int position, String text, int operation) {
        this.position = position;
        this.text = text;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return position + " Text: " + text + " Operation: " + operation;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public int getOperation() {
        return operation;
    }
}