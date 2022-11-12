package Request;

import Request.query.ImpQuery;

import java.util.List;

public interface Request {
    void initialization();

    void setStatus(StatusRequest status);

    StatusRequest getStatus();

    List<String> getHeaders();

    ImpQuery getQuery();
}
