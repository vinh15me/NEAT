//Holds data in Strings for the json file
public class RuleDTO {
    public String originPath;
    public String destinationPath;
    public String regex;

    public RuleDTO(Rule rule) {
        this.originPath = rule.getOrigin().getAbsolutePath();
        this.destinationPath = rule.getDestination().getAbsolutePath();
        this.regex = rule.getPattern().pattern();
    }
}
