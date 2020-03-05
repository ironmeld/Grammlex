package org.grammlex.v1;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/* An LR1Item is a Rule which has been extended with a position
   which represents a parser's state when it is in the process
   of matching that rule.

   LR1Items are collected together into an LR1State which represents
   the "collective" state of the parser when it is open to matching
   multiple rules simultaneously.
 */
public class LR1Item extends Rule {

    private final Set<String> lookahead;
    private final int dotPosition;

    public LR1Item(String variable, String[] terms, int dotPosition, Set<String> lookahead){
        super(variable, terms);
        this.dotPosition = dotPosition;
        this.lookahead = lookahead;
    }

    public String getNextTerm(){
        if(dotPosition == terms.length){
            return null;
        }
        return terms[dotPosition];
    }

    public int getDotPosition() {
        return dotPosition;
    }
    public Set<String> getLookahead() {
        return lookahead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR1Item lr1Item = (LR1Item) o;
        return dotPosition == lr1Item.getDotPosition() &&
                Objects.equals(lookahead, lr1Item.getLookahead()) &&
                Objects.equals(variable, lr1Item.getVar()) &&
                Arrays.equals(terms, lr1Item.getTerms());
    }

    public  boolean equalLR0(LR1Item item){
        return variable.equals(item.getVar())
                && Arrays.equals(terms,item.getTerms())
                && dotPosition == item.getDotPosition();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.dotPosition;
        hash = 31 * hash + Objects.hashCode(this.variable);
        hash = 31 * hash + Arrays.deepHashCode(this.terms);
        hash = 31 * hash + Objects.hashCode(this.lookahead);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(variable).append( ": ");
        for (int i = 0; i < terms.length; i++) {
            if (i == dotPosition) {
                str.append("• ");
            }
            str.append(terms[i]);
            if(i != terms.length - 1){
                str.append(" ");
            }
        }
        if (terms.length == dotPosition) {
            str.append(" •");
        }
        str.append(" , ").append(lookahead);
        return str.toString();
    }
}

