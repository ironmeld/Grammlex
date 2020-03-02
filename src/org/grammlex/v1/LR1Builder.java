package org.grammlex.v1;

import java.util.*;

public class LR1Builder {
    private final Grammar grammar;
    private final ArrayList<LR1State> states;

    public LR1Builder(Grammar grammar) {
        states = new ArrayList<>();
        this.grammar = grammar;
    }

    protected void createStatesForCLR1(int debugLevel, StringBuilder debugOut) {
        StringBuilder out = Objects.requireNonNullElseGet(debugOut, StringBuilder::new);
        Rule startRule = grammar.getRules().get(0);
        Set<String> startLookahead = new HashSet<>();
        startLookahead.add("$");

        LR1Item firstItem = new LR1Item(startRule.getVar(), startRule.getTerms(), 0, startLookahead);

        if (debugLevel > 0) {
            out.append("\nCreating initial state from item: ").append(firstItem).append("\n");
        }
        Set<LR1Item> start = new HashSet<>();
        start.add(firstItem);
        LR1State startState = new LR1State(grammar, start);

        if (debugLevel > 0) {
            out.append("    state after closure:\n");
            for (LR1Item item : startState.getItems()) {
                out.append("        item: ").append(item).append("\n");
            }
            out.append("\n");
        }

        states.add(startState);

        /* go through all states to process */
        for (int i = 0; i < states.size(); i++) {
            createStatesFromState(states.get(i), i, debugLevel, out);
        }
    }

    public void createStatesFromState(LR1State state, int stateNum, int debugLevel, StringBuilder debugOut) {
        StringBuilder out = Objects.requireNonNullElseGet(debugOut, StringBuilder::new);
        if (debugLevel > 0) {
            out.append("Processing transitions for state ").append(stateNum).append("\n");
        }
        Set<String> stringWithDot = new HashSet<>();

        /* go through all items in state looking for next terms */
        for (LR1Item item : state.getItems()) {
            if (item.getCurrent() != null) {
                stringWithDot.add(item.getCurrent());
            }
        }
        if (debugLevel > 0 && stringWithDot.isEmpty()) {
            out.append("   No new states. All terms are reduces.\n");
        }
        List<String> stringWithDotList = new ArrayList<>(stringWithDot);
        Collections.sort(stringWithDotList);
        /* for each unique next term, build transition states */
        for (String term : stringWithDotList) {
            createNextStateForTerm(state, stateNum, term, debugLevel, out);
        }
        if (debugLevel > 0) out.append("\n");
    }

    public void createNextStateForTerm(LR1State state, int stateNum, String term,
                                  int debugLevel, StringBuilder out) {
        if (debugLevel > 0) {
            out.append("   Process transition from state ").append(stateNum)
                    .append(" for term ").append(term).append("\n");
        }

        Set<LR1Item> nextStateItems = createNextStateItems(state, term);
        if (debugLevel > 0) {
            out.append("        New state before closure:\n");
            for (LR1Item item : nextStateItems) {
                out.append("            item: ").append(item).append("\n");
            }
        }

        LR1State nextState = new LR1State(grammar, nextStateItems);
        if (debugLevel > 0) {
            out.append("        New state after closure:\n");
            for (LR1Item item : nextState.getItems()) {
                out.append("            item: ").append(item).append("\n");
            }
        }

        if (!checkExistingState(state, term, nextState)) {
            states.add(nextState);
            state.getTransition().put(term, nextState);
            if (debugLevel > 0) {
                out.append("        Created transition from ").append(stateNum)
                        .append(" with ").append(term)
                        .append(" to new state ").append((states.size() - 1))
                        .append("\n");
            }
        }
    }

    protected Set<LR1Item> createNextStateItems(LR1State originalState, String term) {
        Set<LR1Item> nextStateItems = new HashSet<>();
        /* We are creating a new state from an existing state with the specified
         * term as the transition to the new state.
         *
         * For every item in the original state, if the item has the term as its
         * next term, then we add that item, moving its dot past the term.
         */
        for (LR1Item originalItem : originalState.getItems()) {
            if (originalItem.getCurrent() != null && originalItem.getCurrent().equals(term)) {
                nextStateItems.add(new LR1Item(originalItem.getVar(),
                        originalItem.getTerms(),
                        originalItem.getDotPointer() + 1,
                        new HashSet<>(originalItem.getLookahead())));
            }
        }
        return nextStateItems;
    }

    protected boolean checkExistingState(LR1State originalState, String term, LR1State nextState) {
        for (LR1State existingState : states) {
            if (existingState.getItems().containsAll(nextState.getItems())
                    && nextState.getItems().containsAll(existingState.getItems())) {
                originalState.getTransition().put(term, existingState);
                return true;
            }
        }
        return false;
    }

    public List<LR1State> getStates() {
        return states;
    }
}