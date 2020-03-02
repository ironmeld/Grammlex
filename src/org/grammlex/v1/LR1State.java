package org.grammlex.v1;

import java.util.*;

public class LR1State {
    private final LinkedHashSet<LR1Item> items;
    private final HashMap<String, LR1State> transition;
    private final Grammar grammar;

    public LR1State(Grammar grammar, Set<LR1Item> coreItems) {
        this.grammar = grammar;
        items = new LinkedHashSet<>(coreItems);
        transition = new HashMap<>();
        closure();
    }

    private void closure() {
        boolean changed;
        do {
            changed = false;
            /* Go through every item in the state and try to find a dot before any variable
               that can be expanded into a *new* item (with the variable as the left side and
               the dot at the beginning of the right side).
            */
            for (LR1Item item : items) {
                if (closureOneItem(item)) {
                    changed = true;
                    break;
                }
            }
        } while (changed);
    }

    private boolean closureOneItem(LR1Item item) {
        boolean changed = false;
        /* dot before a variable? */
        if (item.getDotPosition() != item.getTerms().length
                && grammar.getVariables().contains(item.getNextTerm())) {
            /*
               We found an item X ->  . Y *  (with lookahead l)
               (Asterisk * means any set of terminals or none).

               We prepare to create items for every rule Y -> *.
               We do this because terminals in this rule might need to
               be matched first in order to transition to reducing Y,
               which ultimately transitions to reducing the X rule.

               Before we look at the Y rules, we prepare by
               considering all possible lookahead terminals that are valid
               *after* the Y rule is reduced in the context of the X
               rule above.

               If Y is the last variable of X -> . Y * (lookahead l), then
               when the new Y -> * items are completed, they should
               expect l as a lookahead. They are first in line now!

               If Y is not the last term of the found item,
               (in other words the item is X -> . Y Z *)
               the new items should expect the lookahead of any possible
               terminal following Y: which is FIRST(Z *).
               Additionally, if Z * is nullable, then the next
               possible terminal of the new item after reducing Y might
               be the lookahead of the original item (lookahead l).
            */
            HashSet<String> lookahead = new HashSet<>();
            if (item.getDotPosition() == item.getTerms().length - 1) {
                lookahead.addAll(item.getLookahead());
            } else {
                Set<String> firstSet = grammar.computeFirst(item.getTerms(), item.getDotPosition() + 1);
                if (firstSet.contains(Grammar.EPSILON)) {
                    firstSet.remove(Grammar.EPSILON);
                    firstSet.addAll(item.getLookahead());
                }
                lookahead.addAll(firstSet);
            }
            /*
               Now that we have all possible terminals following Y,
               then for every rule Y -> *, we create new items for that
               rule for each lookahead terminal.
            */
            Set<Rule> rules = grammar.getRulesByVar(item.getNextTerm());
            for (Rule rule : rules) {
                if (closureCreateItems(rule, lookahead)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    private boolean closureCreateItems(Rule rule, HashSet<String> lookahead) {
        boolean changed = false;
        String[] terms = rule.getTerms().clone();
        int finished = 0;
        if (terms.length == 1 && terms[0].equals(Grammar.EPSILON)) {
            finished = 1;
        }
        Set<String> newLA = new HashSet<>(lookahead);
        LR1Item newItem = new LR1Item(rule.getVar(), terms, finished, newLA);
        boolean found = false;
        for (LR1Item existingItem : items) {
            if (newItem.equalLR0(existingItem)) {
                Set<String> existLA = existingItem.getLookahead();
                if (!existLA.containsAll(newLA)) {
                    // Merging into the lookahead will change the hash code.
                    // So the item must be removed
                    items.remove(existingItem);
                    existLA.addAll(newLA);
                    items.add(existingItem);
                    changed = true;
                }
                found = true;
                break;
            }
        }
        if (!found) {
            items.add(newItem);
            changed = true;
        }
        return changed;
    }

    public Map<String, LR1State> getTransition() {
        return transition;
    }

    public Set<LR1Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (LR1Item item : items) {
            s.append(item).append("\n");
        }
        return s.toString();
    }
}
