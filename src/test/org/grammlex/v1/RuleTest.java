package org.grammlex.v1;

import org.junit.Test;

public class RuleTest {
    @Test
    public void testRule() {
        Rule rule = new Rule("document",
                new String[] {"BOM?","HEADER","value+","TRAILER"});
        assert(rule.getVar().equals("document"));
    }
}
