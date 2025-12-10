import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    private static final Gson gson = new Gson();
    private static final Type RULE_LIST_TYPE = new TypeToken<List<RuleDTO>>(){}.getType();

    private static final LinkedList<Rule> rules = new LinkedList<>();
    private static final FileHandler fileHandler;
    public static final Logger logger = Logger.getLogger("neat");

    public static boolean addRule(Rule rule) {
        rules.push(rule);
        UpdateJson();
        return true;
    }

    public static boolean editRule(int number, Rule rule) 
    {
        if(number > 0 && number <= rules.size()+1){
            rules.set(number-1,rule);
            UpdateJson();
            return true;
        } 
        return false;
    }

    public static boolean deleteRule(int number) {
        if(number > 0 && number <=rules.size()+1){
            rules.remove(number-1);
            UpdateJson();
            return true;
        }
        return false;
    }

    public static void UpdateJson() {
        //Gson gson = new Gson();
        List<RuleDTO> dtoList = new ArrayList<>();
        for (Rule rule : rules) {
            dtoList.add(new RuleDTO(rule));
        }

        try (FileWriter writer = new FileWriter(RULES_FILE)) {
            gson.toJson(dtoList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadRules(){
        //Gson gson = new Gson();
        List<RuleDTO> dtoList;
        try(FileReader reader = new FileReader(RULES_FILE)){
            dtoList = gson.fromJson(reader, RULE_LIST_TYPE);
            rules.clear();
            if(dtoList != null){
                for(RuleDTO rdto : dtoList){
                    rules.push(new Rule(rdto));
                }
            }
        }
        catch (IOException e){
            Engine.logger.warning("Could not load rules.json, starting with empty set.");
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

        loadRules();
    }
}
