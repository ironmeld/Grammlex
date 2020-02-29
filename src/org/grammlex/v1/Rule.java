package org.grammlex.v1;

public class Rule {
    private final String ruleVar;
    private final String[] ruleTerms;

    public Rule(String ruleVar, String[] ruleTerms) {
        this.ruleVar = ruleVar;
        this.ruleTerms = ruleTerms;
    }

    @Override
    public String toString() {
        StringBuilder ruleStr = new StringBuilder(ruleVar);
        ruleStr.append(": ");
        boolean first = true;
        for (String term : ruleTerms) {
            if (!first) {
                ruleStr.append(" ");
            }
            ruleStr.append(term);
            first = false;
        }
        ruleStr.append(";");
        return ruleStr.toString();
    }
}
