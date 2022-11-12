package Request;

import Common.Config;
import Common.Method;
import Path.ImpPath;
import Path.Path;
import Request.query.ImpQuery;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ImpRequest extends BaseRequest implements Request {
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
            var contentTypeHeader = extractHeader(headers, "Content-Type");

            final var contentLength = extractHeader(headers, "Content-Length");
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(100);
            factory.setRepository(new File("temp"));

            ServletFileUpload upload = new ServletFileUpload(factory);

            List<FileItem> items = upload.parseRequest(this);
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();

                if (item.isFormField()) {
                    this.query.addToParams(Method.POST, item.getFieldName(), item.getString());
                } else {
                    processUploadedFile(item);
                }
            }

            System.out.println("Параметры : " + this.query.getParams(Method.POST));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processUploadedFile(FileItem item) throws Exception {
        String fieldName = item.getFieldName();
        String fileName = item.getName();
        int i2 = fileName.lastIndexOf("\\");
        if (i2 > -1) fileName = fileName.substring(i2 + 1);
        File dirs = new File("downld");

        File uploadedFile = new File(dirs, fileName);
        item.write(uploadedFile);
        System.out.println("Файл " + fileName + " успешно загружен в папку downld!");
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

    @Override
    public String getCharacterEncoding() {
        return StandardCharsets.UTF_8.toString();
    }

    @Override
    public String getContentType() {
        return extractHeader(headers, "Content-Type").get();
    }

    @Override
    public int getContentLength() {
        final var contentLength = extractHeader(headers, "Content-Length");
        return Integer.parseInt(contentLength.get());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }
}
