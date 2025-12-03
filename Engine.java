import java.io.IOException;

import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Engine
{
    private static final LinkedList<Rule> rules = new LinkedList<>();
    private static final FileHandler fileHandler;
    public static final Logger logger = Logger.getLogger("neat");

    public static boolean addRule(Rule rule)
    {
        rules.push(rule);
        return true;
    }

    public static boolean editRule(int number, Rule rule) // TODO: add params
    {
        if(number > 0 && number <= rules.size()+1){
            rules.set(number-1,rule);
            return true;
        } 
        return false;
    }

    public static boolean deleteRule(int number) {
        if(number > 0 && number <=rules.size()+1){
            rules.remove(number-1);
            return true;
        }
        return false;
    }

    public static LinkedList<Rule> getRules()
    {
        return rules;
    }

    public static boolean sort()
    {
        for (Rule rule : rules){
            System.out.println();
            System.out.println("origin: " + rule.getOrigin());
            System.out.println("destination: " + rule.getDestination());
            System.out.println("pattern: " + rule.getPattern());
            if (!rule.run()) {
                System.out.println("Rule failed");
                return false;
            }
        }
        System.out.println("sort: true");
        return true;
    }

    // Set up logger
    static
    {
        try
        {
            fileHandler = new FileHandler("neat.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
