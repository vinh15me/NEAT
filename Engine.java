import java.io.FileWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Engine
{
    private static final String RULES_FILE = "rules.json";
    private final Gson gson = new Gson();
    private static final Type RULE_LIST_TYPE = new TypeToken<List<RuleDTO>>() {}.getType();

    private static final LinkedList<Rule> rules = new LinkedList<>();
    private static final FileHandler fileHandler;
    public static final Logger logger = Logger.getLogger("neat");

    public boolean addRule(Rule rule) {
        rules.push(rule);
        UpdateJson();
        return true;
    }

    public boolean editRule(int number, Rule rule) // TODO: add params
    {
        if(number > 0 && number <= rules.size()+1){
            rules.set(number-1,rule);
            UpdateJson();
            return true;
        } 
        return false;
    }

    public boolean deleteRule(int number) {
        if(number > 0 && number <=rules.size()+1){
            rules.remove(number-1);
            UpdateJson();
            return true;
        }
        return false;
    }

    public void UpdateJson() {
        try (FileWriter writer = new FileWriter(RULES_FILE)) {
            gson.toJson(rules, writer);
        } catch (IOException e) {
            e.printStackTrace(); // or handle it another way
        }
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
