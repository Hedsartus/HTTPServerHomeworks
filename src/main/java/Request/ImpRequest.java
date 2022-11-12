package Request;

import Common.Config;
import Common.Method;
import Path.Path;
import Path.ImpPath;
import Request.query.ImpQuery;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ImpRequest implements Request {
    private final ImpQuery query = new ImpQuery();
    private BufferedInputStream inputStream;
    private StatusRequest status = StatusRequest.OK;
    private List<String> headers;
    private final Path pathContent = new ImpPath();

    public ImpRequest(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
        initialization();
    }

    @Override
    public void initialization() {
        this.inputStream.mark(Config.LIMIT);
        final var buffer = new byte[Config.LIMIT];
        try {
            getRequestLine(buffer, this.inputStream.read(buffer));
        } catch (IOException e) {
            e.printStackTrace();
            this.status = StatusRequest.BADREQUEST;
        }
    }

    @Override
    public void setStatus(StatusRequest status) {
        this.status = status;
    }

    private void getRequestLine(byte[] buffer, int read) {
        if (this.status == StatusRequest.OK) {
            final var requestLineEnd = Config.indexOf(buffer, Config.REQUEST_LINE_DELIMITER, 0, read);
            if (requestLineEnd == -1) {
                this.status = StatusRequest.BADREQUEST;
            } else {
                try {
                    searchHeaders(buffer, requestLineEnd, read);
                    readRequestLine(buffer, requestLineEnd);
                } catch (IOException e) {
                    e.printStackTrace();
                    this.status = StatusRequest.BADREQUEST;
                }
            }
        }
    }

    private void readRequestLine(byte[] buffer, int requestLineEnd) {
        if (this.status == StatusRequest.OK) {
            final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
            if (requestLine.length != 3) {
                this.status = StatusRequest.BADREQUEST;
            } else {
                checkMethod(requestLine[0]);
                checkPath(requestLine[1]);
            }
        }
    }

    private void checkPath(String path) {
        if (this.status == StatusRequest.OK) {
            if (!path.startsWith("/")) {
                this.status = StatusRequest.BADREQUEST;
            } else {
                if (path.contains("?")) {
                    this.query.parse(path, Method.GET);
                } else {
                    this.query.setPath(path);
                }

                if (!pathContent.isContains(this.query.getPath())) {
                    this.status = StatusRequest.NOTFOUND;
                }
            }
        }
    }

    private void checkMethod(String method) {
        if (!Method.contains(method)) {
            this.status = StatusRequest.BADREQUEST;
        } else {
            this.query.setMethod(method);
            // для GET тела нет
            if (!this.query.getMethod().equals("GET")) {
                parsePost();
            }
        }
    }

    private void parsePost() {
        try {
            this.inputStream.skip(Config.REQUEST_LINE_DELIMITER.length);
            // вычитываем Content-Length, чтобы прочитать body
            final var contentTypeHeader = extractHeader(headers, "Content-Type");
            if (contentTypeHeader.isPresent() && contentTypeHeader.get().equals("application/x-www-form-urlencoded")) {
                final var contentLength = extractHeader(headers, "Content-Length");
                if (contentLength.isPresent()) {
                    final var bodyBytes = this.inputStream.readNBytes(Integer.parseInt(contentLength.get()));
                    var body = new String(bodyBytes);
                    this.query.parse(body, Method.POST);
                    System.out.println("PARAM = value: " + this.query.getParam(Method.POST, "value"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ищем заголовки
    private void searchHeaders(byte[] buffer, int requestLineEnd, int read) throws IOException {
        final var headersStart = requestLineEnd + Config.REQUEST_LINE_DELIMITER.length;
        final var headersEnd = Config.indexOf(buffer, Config.HEADERS_DELIMITER, headersStart, read);
        if (headersEnd == -1) {
            this.status = StatusRequest.BADREQUEST;
        } else {
            // отматываем на начало буфера
            this.inputStream.reset();
            // пропускаем requestLine
            this.inputStream.skip(headersStart);

            final var headersBytes = this.inputStream.readNBytes(headersEnd - headersStart);
            this.headers = Arrays.asList(new String(headersBytes).split("\r\n"));
            //System.out.println("headers: "+headers);
        }
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    @Override
    public StatusRequest getStatus() {
        return this.status;
    }

    @Override
    public List<String> getHeaders() {
        return this.headers;
    }

    @Override
    public ImpQuery getQuery() {
        return this.query;
    }
}
