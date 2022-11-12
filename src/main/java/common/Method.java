package common;

public enum Method {
    POST("POST"),
    GET("GET");


    final String method;

    Method(String method) {
        this.method = method;
    }
}
