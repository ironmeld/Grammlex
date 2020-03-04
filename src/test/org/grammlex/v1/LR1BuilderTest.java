package org.grammlex.v1;

import org.junit.Test;

public class LR1BuilderTest {
    @Test
    public void testLR1Builder() {
        Grammar grammar = new Grammar(
                "S: document*;\n" +
                "document: BOM? HEADER value+ TRAILER;\n" +
                "value: map;\n" +
                "map: ID COLON STRING NL;\n");

        StringBuilder out = new StringBuilder();
        LR1Builder builder = new LR1Builder(grammar);
        builder.createStatesForCLR1(out); //NOSONAR

        int stateNum = 0;
        for (LR1State state : builder.getStates()) {
            out.append("State #").append(stateNum).append(":\n");
            out.append(state.toString());
            stateNum++;
        }
        System.out.print(out.toString()); //NOSONAR
    }
}
