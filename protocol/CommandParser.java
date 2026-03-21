package protocol;

public class CommandParser {

    public static String[] parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim().split("\\s+");
    }
}