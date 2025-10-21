import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;

public class Engine
{
    private static final LinkedList<Rule> rules = new LinkedList<>();
    private static File logFile;

    public static boolean addRule(Rule rule)
    {
        rules.push(rule);
        return true;
    }

    public static boolean editRule(Rule rule) // add params
    {
        return false;
    }

    public static boolean deleteRule(Rule rule)
    {
        return rules.remove(rule);
    }

    public static boolean sort()
    {
        return false;
    }

    private static void log(String log)
    {

    }
}
