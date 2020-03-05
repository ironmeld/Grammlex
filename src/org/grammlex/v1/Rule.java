package org.grammlex.v1;

import java.util.Arrays;
import java.util.Objects;

public class Rule {
    protected final String variable;
    protected final String[] terms;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + Objects.hashCode(this.variable);
        hash = hash * 31 + hash + Arrays.deepHashCode(this.terms);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rule other = (Rule) obj;
        if (!Objects.equals(this.variable, other.variable)) {
            return false;
        }
        return Arrays.deepEquals(this.terms, other.terms);
    }

}
