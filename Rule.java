import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class Rule
{
    private Path    origin;
    private Path    destination;
    private Pattern pattern;
    private boolean delete;

    public Rule(Path origin, Path destination, Pattern pattern) { this(origin, destination, pattern, true); }
    public Rule(Path origin, Path destination, Pattern pattern, boolean delete)
    {
        this.origin = origin;
        this.destination = destination;
        this.pattern = pattern;
        this.delete = delete;
    }

    public boolean run(File file)
    {
        return false;
    }

    public Path    getOrigin()      { return origin;      }
    public Path    getDestination() { return destination; }
    public Pattern getPattern()     { return pattern;     }
}
