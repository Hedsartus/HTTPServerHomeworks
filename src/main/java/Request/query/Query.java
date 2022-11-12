package Request.query;

import Common.Method;
import org.apache.hc.core5.http.NameValuePair;

import java.util.List;

public interface Query {
    void setMethod(String method);

    String getMethod();

    void setPath(String path);

    String getPath();

    void parse(String query, Method method);

    List<NameValuePair> getParams(Method method);

    List<NameValuePair> getParam(Method method, String name);

    void addToParams(Method method, String name, String value);
}
