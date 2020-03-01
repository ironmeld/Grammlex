package org.grammlex.v1;

public class Rule {
    private final String variable;
    private final String[] terms;

    public Rule(String variable, String[] terms) {
        this.variable = variable;
        this.terms = terms;
    }

    @Override
    public String toString() {
        StringBuilder ruleStr = new StringBuilder(variable);
        ruleStr.append(": ");
        boolean first = true;
        for (String term : terms) {
            if (!first) {
                ruleStr.append(" ");
            }
            ruleStr.append(term);
            first = false;
        }
        ruleStr.append(";");
        return ruleStr.toString();
    }

    public String getVar() {
        return variable;
    }

    public String[] getTerms() {
        return terms;
    }
}
