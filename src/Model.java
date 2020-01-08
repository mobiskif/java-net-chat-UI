import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Model {
    public Model() {
        super();
    }

    void printFile() {
        try {
            List<String> s = Files.readAllLines(Paths.get("res/index.html"), StandardCharsets.UTF_8);
            for (String ss:s) System.out.println(ss);
        }
        catch (IOException e) {}
    }
}


