package Request.query;

import org.apache.hc.core5.http.NameValuePair;

import java.util.List;

public interface Query {
    void setMethod(String method);

    String getMethod();

    void setPath(String path);

    String getPath();

    void parseQueryParams(String params);

    void parseGetQuery(String sUrl);

    List<NameValuePair> getQueryParams();

    String getQueryParam(String name);
}
