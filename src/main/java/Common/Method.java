package Common;

public enum Method {
    POST("POST"),
    GET("GET");


    final String method;

    Method(String method) {
        this.method = method;
    }

    public static boolean contains(String method) {
        try {
            Enum.valueOf(Method.class, method);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
