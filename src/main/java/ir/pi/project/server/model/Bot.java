package ir.pi.project.server.model;

public class Bot {
    String jarURL;
    int id;

    public Bot(String jarURL, int id) {
        this.jarURL = jarURL;
        this.id = id;
    }

    public String getJarURL() {
        return jarURL;
    }

    public void setJarURL(String jarURL) {
        this.jarURL = jarURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
