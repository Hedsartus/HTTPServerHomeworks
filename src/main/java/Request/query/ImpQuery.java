package Request.query;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ImpQuery implements Query {
    private String method;
    private String path;
    private List<NameValuePair> parameters;

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void parseQueryParams(String params) {
        this.parameters = URLEncodedUtils.parse(params, StandardCharsets.UTF_8);
    }

    @Override
    public void parseGetQuery(String sUrl) {
        try {
            URI uri = new URI(sUrl);
            this.parameters = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
            setPath(uri.getPath());
            System.out.println("GET: " + getQueryParams());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<NameValuePair> getQueryParams() {
        return this.parameters;
    }

    @Override
    public String getQueryParam(String name) {
        for (NameValuePair kv : this.parameters) {
            if (name.equals(kv.getName())) {
                return kv.getValue();
            }
        }
        return null;
    }
}
