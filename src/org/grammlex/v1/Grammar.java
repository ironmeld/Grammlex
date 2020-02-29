package org.grammlex.v1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    private final HashSet<String> terminals; // terms only on the right
    private final HashSet<String> variables; // terms on the left
    private final HashSet<String> repeats; // terms with *
    private final HashSet<String> repeat1s; // terms with +
    private final HashSet<String> optionals; // terms with ?
    private String startVariable;

    public Grammar(String grammarText) {
        extendedRules = new ArrayList<>();
        rules = new ArrayList<>();
        terminals = new HashSet<>();
        variables = new HashSet<>();
        repeats = new HashSet<>();
        repeat1s = new HashSet<>();
        optionals = new HashSet<>();
        parseRules(grammarText);
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
                    rule.setLength(0);
                }
            }
            isFirstRule = false;
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
                dumpTerminals();
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

    protected static boolean startsWithAny(String str, String[] prefixes) {
        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
