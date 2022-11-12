package Response;

import Request.Request;
import Request.StatusRequest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImpResponse implements Response {
    private final Request request;
    private BufferedOutputStream outputStream;

    public ImpResponse(BufferedOutputStream outputStream, Request request) {
        this.outputStream = outputStream;
        this.request = request;
    }

    @Override
    public void setHeader(BufferedOutputStream out, int status, String statusText, String type, long lenght) throws IOException {
        out.write((
                "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                        "Content-Type: " + type + "\r\n" +
                        "Content-Length: " + lenght + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
    }

    @Override
    public void sendResponse() {
        try {
            createAnswer(this.request.getStatus());
            this.outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createBody() throws IOException {
        final var filePath = Path.of(".", "public", this.request.getQuery().getPath());
        setHeader(this.outputStream, 200, "OK", getContentType(filePath), getSizeFile(filePath));
        Files.copy(filePath, this.outputStream);
    }

    @Override
    public void createAnswer(StatusRequest statusRequest) {
        try {
            switch (statusRequest) {
                case BADREQUEST -> {
                    setHeader(this.outputStream, 400,
                            StatusRequest.BADREQUEST.toString(), "text/plain",
                            StatusRequest.BADREQUEST.toString().length());
                }
                case NOTFOUND -> {
                    setHeader(this.outputStream, 404,
                            StatusRequest.NOTFOUND.toString(), "text/plain",
                            StatusRequest.NOTFOUND.toString().length());
                }
                case OK -> {
                    createBody();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getSizeFile(Path filePath) throws IOException {
        return Files.size(filePath);
    }

    @Override
    public String getContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
}
