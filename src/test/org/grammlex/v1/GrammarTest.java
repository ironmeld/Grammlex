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
        System.out.println(grammar.dumpGrammar()); //NOSONAR
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
        System.out.println(grammar.dumpGrammar()); //NOSONAR
    }

    @Test
    public void testGrammarWithEpsilon() {
        Grammar grammar = new Grammar("S: epsilon;");
        System.out.println(grammar.dumpGrammar()); //NOSONAR
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGrammarMissingSemi() {
        Grammar grammar = new Grammar("S: HEADER");
        System.out.println(grammar.dumpGrammar()); //NOSONAR
    }
}
