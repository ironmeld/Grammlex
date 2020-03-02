package org.grammlex.v1;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class LR1Item extends Rule {

    private final Set<String> lookahead;
    private final int dotPointer;

    public LR1Item(String variable, String[] terms, int dotPointer, Set<String> lookahead){
        super(variable, terms);
        this.dotPointer = dotPointer;
        this.lookahead = lookahead;
    }

    public String getCurrent(){
        if(dotPointer == terms.length){
            return null;
        }
        return terms[dotPointer];
    }

    public int getDotPointer() {
        return dotPointer;
    }
    public Set<String> getLookahead() {
        return lookahead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR1Item lr1Item = (LR1Item) o;
        return dotPointer == lr1Item.getDotPointer() &&
                Objects.equals(lookahead, lr1Item.getLookahead()) &&
                Objects.equals(variable, lr1Item.getVar()) &&
                Arrays.equals(terms, lr1Item.getTerms());
    }

    public  boolean equalLR0(LR1Item item){
        return variable.equals(item.getVar())
                && Arrays.equals(terms,item.getTerms())
                && dotPointer == item.getDotPointer();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.dotPointer;
        hash = 31 * hash + Objects.hashCode(this.variable);
        hash = 31 * hash + Arrays.deepHashCode(this.terms);
        hash = 31 * hash + Objects.hashCode(this.lookahead);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(variable).append( ": ");
        for (int i = 0; i < terms.length; i++) {
            if (i == dotPointer) {
                str.append("• ");
            }
            str.append(terms[i]);
            if(i != terms.length - 1){
                str.append(" ");
            }
        }
        if (terms.length == dotPointer) {
            str.append(" •");
        }
        str.append(" , ").append(lookahead);
        return str.toString();
    }
}

