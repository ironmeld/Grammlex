package org.grammlex.v1;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class LR1StateTest {
    @Test
    public void testLR1State() {
        Grammar grammar = new Grammar(
                "S: document*;\n" +
                "document: BOM? HEADER value+ TRAILER;\n" +
                "value: map;\n" +
                "map: ID COLON STRING NL;\n");
        Rule firstRule = grammar.getRules().get(0);

        LR1Item testItem = new LR1Item(firstRule.getVar(), firstRule.getTerms(),
                            0, new HashSet<>());
        Set<LR1Item> coreItems = new HashSet<>();
        coreItems.add(testItem);

        LR1State state = new LR1State(grammar, coreItems);
        for (LR1Item item : state.getItems()) {
            System.out.println("Item: " + item); //NOSONAR
        }
    }
}
