package ee.smit.inventory.common;

public final class QueryUtils {

    private QueryUtils() {}

    public static String escapeLikePattern(String input) {
        return input.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }
}
