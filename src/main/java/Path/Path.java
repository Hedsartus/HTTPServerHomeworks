package Path;

import java.util.List;

public interface Path {
    void initPath();

    List<String> getListPath();

    boolean isContains(String path);
}
