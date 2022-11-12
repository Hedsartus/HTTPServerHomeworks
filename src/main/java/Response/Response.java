package Response;

import Request.StatusRequest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public interface Response {
    void setHeader(BufferedOutputStream out, int status, String statusText, String type, long lenght) throws IOException;

    void sendResponse();

    void createBody() throws IOException;

    void createAnswer(StatusRequest statusRequest);

    long getSizeFile(Path filePath) throws IOException;

    String getContentType(Path filePath) throws IOException;
}
