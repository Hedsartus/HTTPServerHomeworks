package Path;

import java.util.List;

public class ImpPath implements Path {
    private List<String> paths;

    public ImpPath() {
        initPath();
    }

    @Override
    public void initPath() {
        this.paths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
                "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html",
                "/events.html", "/events.js", "/default-get.html");
    }

    @Override
    public List<String> getListPath() {
        return this.paths;
    }

    @Override
    public boolean isContains(String path) {
        return this.paths.contains(path);
    }
}
