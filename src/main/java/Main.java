import Request.StatusRequest;
import Response.ImpResponse;
import Response.Response;
import server.Server;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        // добавление handler'ов (обработчиков)
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            request.setStatus(StatusRequest.OK);
            request.getQuery().setPath("index.html");
            Response response = new ImpResponse(responseStream, request);
            response.sendResponse();
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            request.setStatus(StatusRequest.OK);
            request.getQuery().setPath("index.html");
            Response response = new ImpResponse(responseStream, request);
            response.sendResponse();
        });

        server.listen(9999);
    }
}
