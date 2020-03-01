package org.grammlex.v1;

import java.util.*;

/* Grammar
 *
 * Grammar is a set of rules, each composed of a variable on the left
 * of a colon and a set of terms on the right.
 *
 * Rules are compatible with a subset of the ANTLR4 grammar format.
 * Variables should be lower case and terminals uppercase.
 * Example:
 * comment_line: HASH SPACE STRING nl;
 * nl: CR | LF;
 *
 * Sequences of terms can be separated by | which means choose either.
 * The modifiers * + and ? stand for zero or many, one or many, or zero
 * or one instances of a term, respectively.
 * Example:
 * stream: documents*;
 * document: BOM? header_line value_line+ end_line;
 *
 */
public class Grammar {
    public static final String EPSILON = "epsilon";
    public static final String REPEAT_SUFFIX = "_repeat";
    public static final String REPEAT1_SUFFIX = "_repeat1";
    public static final String OPT_SUFFIX = "_opt";

    private final List<Rule> extendedRules; // rules before modifiers are expanded
    private final List<Rule> rules; // low level rules suitable for LR(1)
    private final Set<String> terminals; // terms only on the right
    private final Set<String> variables; // terms on the left
    private final Set<String> repeats; // terms with *
    private final Set<String> repeat1s; // terms with +
    private final Set<String> optionals; // terms with ?
    private String startVariable;
    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;

    public Grammar(String grammarText) {
        extendedRules = new ArrayList<>();
        rules = new ArrayList<>();
        terminals = new HashSet<>();
        variables = new HashSet<>();
        repeats = new HashSet<>();
        repeat1s = new HashSet<>();
        optionals = new HashSet<>();
        parseRules(grammarText);
        computeFirstSets();
        computeFollowSets();
    }

    protected void parseRules(String grammarText) {
        StringBuilder rule = new StringBuilder();
        boolean inBlock = false;
        boolean isFirstRule = true;
        String[] specials = new String[]{"/", "@", "}", "parser ", "options ", "tokens "};
        for (String line : grammarText.split("\n")) {
            boolean special = false;
            if (startsWithAny(line, specials)) {
                special = true;
            }
            if (line.startsWith("@")) {
                inBlock = true;
            }
            if (line.startsWith("}")) {
                inBlock = false;
            }

            if (!inBlock && !special) {
                rule.append(line);
                if (line.endsWith(";")) {
                    makeRule(rule.toString(), isFirstRule);
                    isFirstRule = false;
                    rule.setLength(0);
                }
            }
        }
        if (rule.length() != 0) {
            throw new IllegalArgumentException("ERROR: Missing semicolon? Leftover text at end of grammar.");
        }
        makeModifierRules();
        for (String variable : variables) {
            terminals.remove(variable);
        }
    }


    protected void makeRule(String rule, boolean isFirstRule) {
        rule = rule.substring(0, rule.length() - 1);
        String[] sides = rule.split(":", 2);
        String leftSide = sides[0].trim();
        variables.add(leftSide);
        if (isFirstRule) {
            startVariable = leftSide;
            rules.add(new Rule("S'", new String[]{startVariable}));
        }
        String[] choices = sides[1].trim().split("\\|");
        for (String choice : choices) {
            String[] terms = choice.trim().split("\\s+");
            extendedRules.add(new Rule(leftSide, terms.clone()));
            int termIndex = 0;
            for (String term : terms) {
                // Epsilon is not a terminal
                if (term.equals(Grammar.EPSILON))
                    continue;

                if (term.endsWith("*")) {
                    terms[termIndex] = processWildcardTerm(term);
                } else if (term.endsWith("+")) {
                    terms[termIndex] = processPlusTerm(term);
                } else if (term.endsWith("?")) {
                    terms[termIndex] = processOptionalTerm(term);
                } else {
                    terminals.add(term);
                }
                termIndex++;
            }
            rules.add(new Rule(leftSide, terms));
        }
    }

    protected String processWildcardTerm(String term) {
        term = term.substring(0, term.length() - 1);
        repeats.add(term);
        terminals.add(term);
        term = term + Grammar.REPEAT_SUFFIX;
        terminals.add(term);
        return term;
    }

    protected String processPlusTerm(String term) {
        term = term.substring(0, term.length() - 1);
        repeat1s.add(term);
        terminals.add(term);
        term = term + Grammar.REPEAT1_SUFFIX;
        terminals.add(term);
        return term;
    }

    protected String processOptionalTerm(String term) {
        term = term.substring(0, term.length() - 1);
        optionals.add(term);
        terminals.add(term);
        term = term + Grammar.OPT_SUFFIX;
        terminals.add(term);
        return term;
    }

    protected void makeModifierRules() {
        for (String repeatTerm : repeats) {
            rules.add(new Rule(repeatTerm + Grammar.REPEAT_SUFFIX,
                    new String[]{Grammar.EPSILON}));
            rules.add(new Rule(repeatTerm + Grammar.REPEAT_SUFFIX,
                    new String[]{repeatTerm, repeatTerm + Grammar.REPEAT_SUFFIX}));
            variables.add(repeatTerm + Grammar.REPEAT_SUFFIX);
        }
        for (String repeatTerm : repeat1s) {
            rules.add(new Rule(repeatTerm + Grammar.REPEAT1_SUFFIX, new String[]{repeatTerm}));
            rules.add(new Rule(repeatTerm + Grammar.REPEAT1_SUFFIX,
                    new String[]{repeatTerm, repeatTerm + Grammar.REPEAT1_SUFFIX}));
            variables.add(repeatTerm + Grammar.REPEAT1_SUFFIX);
        }
        for (String optionalTerm : optionals) {
            rules.add(new Rule(optionalTerm + Grammar.OPT_SUFFIX, new String[]{Grammar.EPSILON}));
            rules.add(new Rule(optionalTerm + Grammar.OPT_SUFFIX, new String[]{optionalTerm}));
            variables.add(optionalTerm + Grammar.OPT_SUFFIX);
        }

    }

    /* Compute the FIRST set for each variable.
     * https://en.wikipedia.org/wiki/LL_parser#Constructing_an_LL(1)_parsing_table
     */
    protected void computeFirstSets() {
        firstSets = new HashMap<>();
        for (String s : variables) {
            Set<String> temp = new HashSet<>();
            firstSets.put(s, temp);
        }

        /* computing the first set of a variable requires the first
         * set of other variables, which is chicken or egg first?,
         * so we start just computing with empty first sets and
         * progressively fill in those sets and loop until we
         * can no longer add any more elements.
         */
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String variable : variables) {
                Set<String> firstSet = new HashSet<>();
                // for every rule A -> terms*, add first(terms*)
                for (Rule rule : rules) {
                    if (rule.getRuleVar().equals(variable)) {
                        firstSet.addAll(computeFirst(rule.getRuleTerms(), 0));
                    }
                }
                // as long as a first set expanded, keep looping
                if (!firstSets.get(variable).containsAll(firstSet)) {
                    changed = true;
                    firstSets.get(variable).addAll(firstSet);
                }
            }
        }
        firstSets.put("S'", firstSets.get(startVariable));
    }

    /* Compute the first set for a list of terms starting at index.
     * Note that this implementation includes epsilon in the first
     * set as an indication that the sequence is nullable - it can all
     * reduce to an empty string. However, in this case the first set
     * does not consist only of terminals which can be confusing because
     * epsilon is not actually processed as an input when parsing.
     * In other words, epsilon is not a terminal.
     * Some implementations exclude epsilon from the first set and
     * separately indicate whether the term or terms are nullable.
     */
    protected Set<String> computeFirst(String[] terms, int index) {
        Set<String> first = new HashSet<>();

        // if the first term is a terminal or epsilon that is the first set.
        // Stop here. Nothing after epsilon and Anything after that terminal is not first.
        if (terminals.contains(terms[index]) || terms[index].equals(Grammar.EPSILON)) {
            first.add(terms[index]);
            return first;
        }

        // if the first term is a variable, add its first set and proceed
        if (variables.contains(terms[index])) {
            first.addAll(firstSets.get(terms[index]));
        }

        // if the variable was nullable (reduces to epsilon) and there
        // are more terms, then compute first for remaining terms.
        // Otherwise return with what we got from the variable.
        if (first.contains(Grammar.EPSILON) && (index != terms.length - 1)) {
            first.remove(Grammar.EPSILON);
            first.addAll(computeFirst(terms, index + 1));
        }
        return first;
    }

    /* Compute the FOLLOW set for each variable.
     * https://en.wikipedia.org/wiki/LL_parser#Constructing_an_LL(1)_parsing_table
     */
    protected void computeFollowSets() {
        followSets = new HashMap<>();
        for (String s : variables) {
            Set<String> temp = new HashSet<>();
            followSets.put(s, temp);
        }
        Set<String> start = new HashSet<>();
        start.add("$");
        followSets.put("S'", start);

        boolean changed = true;
        while (changed) {
            changed = false;
            for (String variable : variables) {
                // Go through every rule looking for this variable.
                // If the rule contains the variable, use the rule to figure
                // out what terminals can follow the variable.
                for (Rule rule : rules) {
                    if (computeFollowSet(variable, rule)) {
                        changed = true;
                    }
                }
            }
        }
    }

    protected boolean computeFollowSet(String variable, Rule rule) {
        boolean changed = false;
        for (int i = 0; i < rule.getRuleTerms().length; i++) {
            if (rule.getRuleTerms()[i].equals(variable)) {
                Set<String> addToFollow;
                if (i == rule.getRuleTerms().length - 1) {
                    // A -> uB
                    // The variable we care about (B) is at the end of this rule
                    // so we can presume that whatever follows A can follow B.
                    // So add follow(A) to follow(B)
                    // (We are using the variable first for that purpose)
                    addToFollow = followSets.get(rule.getRuleVar());
                } else {
                    // A -> uBv
                    // there is something after B in this rule so
                    // include first(v) in the follow set of B.
                    addToFollow = computeFirst(rule.getRuleTerms(), i + 1);
                    // Moreover, if the remaining terms of the rule (v) can
                    // reduce to empty string (epsilon) then whatever can
                    // follow A can follow B, so add follow(A) to follow(B)
                    // as well.
                    if (addToFollow.contains(Grammar.EPSILON)) {
                        addToFollow.remove(Grammar.EPSILON);
                        addToFollow.addAll(followSets.get(rule.getRuleVar()));
                    }
                }
                if (!followSets.get(variable).containsAll(addToFollow)) {
                    changed = true;
                    followSets.get(variable).addAll(addToFollow);
                }
            }
        }
        return changed;
    }

    public String dumpGrammar() {
        return "Extended Rules:\n" +
                dumpExtendedRules() +
                "\nRules:\n" +
                dumpRules() +
                "\n" +
                "Start Variable: " +
                startVariable + "\n" +
                "\nVariables:\n" +
                dumpVariables() +
                "\nTerminals:\n" +
                dumpTerminals() +
                "\nFirst Sets:\n" +
                dumpFirstSets() +
                "\nFollow Sets:\n" +
                dumpFollowSets();
    }

    public String dumpExtendedRules() {
        StringBuilder dump = new StringBuilder();
        for (Rule eRule : extendedRules) {
            dump.append(eRule).append("\n");
        }
        return dump.toString();
    }

    public String dumpRules() {
        StringBuilder dump = new StringBuilder();
        for (int i = 0; i < rules.size(); i++) {
            dump.append(i).append(": ");
            dump.append(rules.get(i)).append("\n");
        }
        return dump.toString();
    }

    public String dumpVariables() {
        StringBuilder dump = new StringBuilder();
        for (String variable : variables) {
            dump.append(variable).append("\n");
        }
        return dump.toString();
    }

    public String dumpTerminals() {
        StringBuilder dump = new StringBuilder();
        for (String terminal : terminals) {
            dump.append(terminal).append("\n");
        }
        return dump.toString();
    }

    public String dumpFirstSets() {
        StringBuilder dump = new StringBuilder();
        firstSets.forEach((variable, firstSet)
                -> dump.append(variable).append(": ").append(firstSet).append("\n"));
        return dump.toString();
    }

    public String dumpFollowSets() {
        StringBuilder dump = new StringBuilder();
        followSets.forEach((variable, followSet)
                -> dump.append(variable).append(": ").append(followSet).append("\n"));
        return dump.toString();
    }

    protected static boolean startsWithAny(String str, String[] prefixes) {
        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Set<String>> getFirstSets() {
        return firstSets;
    }
}
