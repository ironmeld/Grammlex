package org.grammlex.v1;

import java.util.*;

public class LR1Builder {
    private final Grammar grammar;
    private final ArrayList<LR1State> states;
    protected final List<HashMap<String, Action>> actionTable= new ArrayList<>();
    protected final List<HashMap<String, Integer>> gotoTable = new ArrayList<>();

    public LR1Builder(Grammar grammar) {
        states = new ArrayList<>();
        this.grammar = grammar;
    }

    /* Create the canonical LR(1) table. "Canonical" means the original,
    standard LR(1), not any variation like LALR, SLR, or any other
    from the "LR family".
    https://en.wikipedia.org/wiki/Canonical_LR_parser
     */
    protected boolean createStatesForCLR1(StringBuilder out) {
        Rule startRule = grammar.getRules().get(0);
        Set<String> startLookahead = new HashSet<>();
        startLookahead.add("$");

        LR1Item firstItem = new LR1Item(startRule.getVar(), startRule.getTerms(), 0, startLookahead);

        out.append("\nCreating initial state from item: ").append(firstItem).append("\n");
        Set<LR1Item> start = new HashSet<>();
        start.add(firstItem);
        LR1State startState = new LR1State(grammar, start);
        states.add(startState);
        out.append("    state after closure:\n");
        for (LR1Item item : startState.getItems()) {
            out.append("        item: ").append(item).append("\n");
        }
        out.append("\n");

        /* go through all states to process */
        for (int stateNum = 0; stateNum < states.size(); stateNum++) {
            createStatesFromState(states.get(stateNum), stateNum, out);
        }

        createGotoTable();
        return createActionTable();
    }

    public void createStatesFromState(LR1State state, int stateNum, StringBuilder out) {
        out.append("Processing transitions for state ").append(stateNum).append("\n");
        Set<String> stringWithDot = new HashSet<>();

        /* go through all items in state looking for next terms */
        for (LR1Item item : state.getItems()) {
            if (item.getNextTerm() != null) {
                stringWithDot.add(item.getNextTerm());
            }
        }
        if (stringWithDot.isEmpty()) {
            out.append("   No new states. All terms are reduces.\n");
        }
        List<String> stringWithDotList = new ArrayList<>(stringWithDot);
        Collections.sort(stringWithDotList);
        /* for each unique next term, build transition states */
        for (String term : stringWithDotList) {
            createNextStateForTerm(state, stateNum, term, out);
        }
        out.append("\n");
    }

    public void createNextStateForTerm(LR1State state, int stateNum, String term,
                                       StringBuilder out) {
        out.append("   Process transition from state ").append(stateNum)
                    .append(" for term ").append(term).append("\n");

        Set<LR1Item> nextStateItems = createNextStateItems(state, term);
        out.append("        New state before closure:\n");
        for (LR1Item item : nextStateItems) {
            out.append("            item: ").append(item).append("\n");
        }

        LR1State nextState = new LR1State(grammar, nextStateItems);
        out.append("        New state after closure:\n");
        for (LR1Item item : nextState.getItems()) {
            out.append("            item: ").append(item).append("\n");
        }

        int foundStateNum = checkExistingState(state, term, nextState);
        if (foundStateNum != -1) {
            out.append("        Created transition from ").append(stateNum)
                    .append(" with ").append(term)
                    .append(" to existing state ").append(foundStateNum)
                    .append("\n");
        } else {
            states.add(nextState);
            state.getTransition().put(term, nextState);
            out.append("        Created transition from ").append(stateNum)
                    .append(" with ").append(term)
                    .append(" to new state ").append(states.size() - 1)
                    .append("\n");
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
            if (originalItem.getNextTerm() != null && originalItem.getNextTerm().equals(term)) {
                nextStateItems.add(new LR1Item(originalItem.getVar(),
                        originalItem.getTerms(),
                        originalItem.getDotPosition() + 1,
                        new HashSet<>(originalItem.getLookahead())));
            }
        }
        return nextStateItems;
    }

    protected int checkExistingState(LR1State originalState, String term, LR1State nextState) {
        int stateNum = 0;
        for (LR1State existingState : states) {
            if (existingState.getItems().containsAll(nextState.getItems())
                    && nextState.getItems().containsAll(existingState.getItems())) {
                originalState.getTransition().put(term, existingState);
                return stateNum;
            }
            stateNum++;
        }
        return -1;
    }

    public List<LR1State> getStates() {
        return states;
    }

    protected int findStateIndex(LR1State state) {
        for (int stateNum = 0; stateNum < states.size(); stateNum++) {
            if (states.get(stateNum).equals(state)) {
                return stateNum;
            }
        }
        return -1;
    }

    public StringBuilder outputStates(StringBuilder out) {
        int stateNum = 0;
        for (LR1State state : states) {
            out.append("State #").append(stateNum).append(":\n");
            out.append(state.toString());
            out.append("\n");
            stateNum++;
        }
        return out;
    }

    protected void createGotoTable() {
        for (int stateNum = 0; stateNum < states.size(); stateNum++) {
            gotoTable.add(new HashMap<>());
        }
        for (int stateNum = 0; stateNum < states.size(); stateNum++) {
            for (String term : states.get(stateNum).getTransition().keySet()) {
                if (grammar.getVariables().contains(term)) {
                    gotoTable.get(stateNum).put(term,
                            findStateIndex(states.get(stateNum).getTransition().get(term)));
                }
            }
        }
    }

    protected boolean createActionTable() {
        for (int stateNum = 0; stateNum < states.size(); stateNum++) {
            actionTable.add(stateNum, new HashMap<>());
        }

        populateShiftStates();
        return populateReduceStates();
    }

    protected void populateShiftStates() {
        for (int stateNum = 0; stateNum < states.size(); stateNum++) {
            for (String term : states.get(stateNum).getTransition().keySet()) {
                if (grammar.getTerminals().contains(term)) {
                    actionTable.get(stateNum).put(term, new Action(Action.SHIFT,
                            findStateIndex(states.get(stateNum).getTransition().get(term))));
                }
            }
        }
    }

    protected boolean populateReduceStates() {
        for (int stateNum = 0; stateNum < states.size(); stateNum++) {
            for (LR1Item item : states.get(stateNum).getItems()) {
                if (!populateReduceForStateItem(stateNum, item)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean populateReduceForStateItem(int stateNum, LR1Item item) {
        if (item.getDotPosition() == item.getTerms().length) {
            if (item.getVar().equals("S'")) {
                actionTable.get(stateNum).put("$", new Action(Action.ACCEPT, 0));
            } else {
                Rule rule = new Rule(item.getVar(), item.getTerms());
                int index = grammar.findRuleIndex(rule);
                Action action = new Action(Action.REDUCE, index);
                for (String str : item.getLookahead()) {
                    if (actionTable.get(stateNum).get(str) != null) {
                        System.err.println("Action table has a REDUCE-" + //NOSONAR
                                actionTable.get(stateNum).get(str).getTypeAsString() +
                                " conflict in state " + stateNum);
                        return false;
                    } else {
                        actionTable.get(stateNum).put(str, action);
                    }
                }
            }
        }
        return true;
    }

    public StringBuilder outputActionTable(StringBuilder out) {
        HashSet<String> terminals = new HashSet<>(grammar.getTerminals());
        terminals.add("$");

        out.append("Action Table:\n");
        for (int stateNum = 0; stateNum < actionTable.size(); stateNum++) {
            statePrefix(out, stateNum);
            for (String terminal : terminals) {
                if (actionTable.get(stateNum).get(terminal) != null) {
                    out.append(actionTable.get(stateNum).get(terminal))
                            .append(" on ").append(terminal).append(", ");
                }
            }
            out.append("\n");
        }
        return out;
    }

    public StringBuilder outputGotoTable(StringBuilder out) {
        out.append("Goto Table:\n");
        for (int stateNum = 0; stateNum < gotoTable.size(); stateNum++) {
            statePrefix(out, stateNum);
            for (String variable : grammar.getVariables()) {
                if (gotoTable.get(stateNum).get(variable) != null) {
                    out.append("State ").append(gotoTable.get(stateNum).get(variable))
                            .append(" on ").append(variable).append(", ");
                }
            }
            out.append("\n");
        }
        return out;
    }

    protected static void statePrefix(StringBuilder out, int stateNum) {
        out.append("State: ").append(stateNum).append(": ");
    }
}
