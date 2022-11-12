package Request.query;

import Common.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImpQuery implements Query {
    private String method;
    private String path;
    private List<NameValuePair> postParameters;
    private List<NameValuePair> getParameters;

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
    public void parse(String query, Method method) {
        if (method.equals(Method.GET)) {
            try {
                URI uri = new URI(query);
                this.getParameters = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
                setPath(uri.getPath());
                System.out.println("GET: " + getParams(Method.GET));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (method.equals(Method.POST)) {
            this.postParameters = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
            System.out.println("POST: " + getParams(Method.POST));
        }
    }

    @Override
    public List<NameValuePair> getParams(Method method) {
        List<NameValuePair> list;

        if (Method.POST == method) {
            list = this.postParameters;
        } else {
            list = this.getParameters;
        }

        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<NameValuePair> getParam(Method method, String name) {
        List<NameValuePair> list;
        if (Method.POST == method) {
            list = this.postParameters;
        } else {
            list = this.getParameters;
        }

        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .filter(x -> Objects.equals(x.getName(), name)).collect(Collectors.toList());
    }
}
