import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.*;

public class Rule
{
    private static final Logger log = Engine.logger;

    private File    origin;
    private File    destination;
    private Pattern pattern;

    public Rule(File origin, File destination, Pattern pattern)
    {
        this.origin      = origin;
        this.destination = destination;
        this.pattern     = pattern;
    }

    public void print()
    {
        System.out.println("Origin: " + origin.getName());
    }

    public boolean run()
    {
        if (!origin.exists() || !origin.isDirectory())
        {
            log.severe("Origin directory does not exist: " + origin.getName());
            return false;
        }
        if (!destination.exists() || !destination.isDirectory())
        {
            log.severe("Destination directory does not exist: " + destination.getName());
            return false;
        }

        for (File currFile : Objects.requireNonNull(origin.listFiles()))
        {
            if (pattern.matcher(currFile.getName()).matches())
            {
                log.info("Moved " + currFile.getName() + " to " + destination.getName() + " with code " + (move(currFile) ? 1 : 0));
            }
        }

        return true; //TODO:this alway reutrned false: FIXED
    }

    private boolean move(@NotNull File file)
    {
        try
        {
            Files.move(file.toPath(), Paths.get(destination.getAbsolutePath(), file.getName()));
            System.out.println("Move Completed");
            return true;
        } catch (IOException e)
        {
            log.severe(e.getMessage());
            return false;
        }
    }

    public File    getOrigin()      { return origin;      }
    public File    getDestination() { return destination; }
    public Pattern getPattern()     { return pattern;     }

    public void setOrigin(File path)           { origin      = path;       }
    public void setDestination(File path)      { destination = path;       }
    public void setPattern(Pattern newPattern) { pattern     = newPattern; }
}
