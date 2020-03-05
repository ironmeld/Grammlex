package org.grammlex.v1;

import org.junit.Test;

public class GrammarTest {
    @Test
    public void testGrammar() {
        Grammar grammar = new Grammar(
                "S: document*;\n" +
                "document: BOM? HEADER value+ TRAILER;\n" +
                "value: map;\n" +
                "map: ID COLON STRING NL;\n");

        assert (grammar.getFirstSets().get("document").contains("HEADER"));
        StringBuilder out = new StringBuilder();
        grammar.outputGrammar(out).append("\n");
        System.out.println(out.toString()); //NOSONAR
    }

    @Test
    public void testGrammarWithMembers() {
        Grammar grammar = new Grammar("@members {\n" +
                "\n" +
                "}\n" +
                "S: document*;\n" +
                "document: BOM? HEADER value+ TRAILER;\n" +
                "value: map;\n" +
                "map: ID COLON STRING NL;\n");
        StringBuilder out = new StringBuilder();
        grammar.outputGrammar(out);
        System.out.println(out.toString()); //NOSONAR
    }

    @Test
    public void testGrammarWithEpsilon() {
        Grammar grammar = new Grammar("S: epsilon;");
        StringBuilder out = new StringBuilder();
        grammar.outputGrammar(out);
        System.out.println(out.toString()); //NOSONAR
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGrammarMissingSemi() {
        Grammar grammar = new Grammar("S: HEADER");
        StringBuilder out = new StringBuilder();
        grammar.outputGrammar(out);
        System.out.println(out.toString()); //NOSONAR
    }

    @Test
    public void testGrammarRules() {
        Grammar grammar = new Grammar("S: HEADER;");
        Rule rule = new Rule("S", new String[] {"HEADER"});
        assert(grammar.findRuleIndex(rule) == 1);

        rule = new Rule("S", new String[] {"FOOBAR"});
        assert(grammar.findRuleIndex(rule) == -1);
    }
}
