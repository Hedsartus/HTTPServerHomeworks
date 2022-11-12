package Request;

public enum StatusRequest {
    BADREQUEST("Bad request"),
    NOTFOUND("Not found"),
    OK("OK");


    private String status;

    StatusRequest(String status) {
        this.status = status;
    }
}
