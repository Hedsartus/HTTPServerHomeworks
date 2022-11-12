package Common;

public final class Config {
    public final static byte[] REQUEST_LINE_DELIMITER = new byte[]{'\r', '\n'};
    public final static byte[] HEADERS_DELIMITER = new byte[]{'\r', '\n', '\r', '\n'};
    public final static int LIMIT = 4096;

    // from google guava with modifications
    public static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}
