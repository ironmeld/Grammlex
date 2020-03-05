package org.grammlex.v1;

import org.junit.Test;

public class RuleTest {
    protected static final Rule testRule = new Rule("document",
            new String[] {"BOM?","HEADER","value+","TRAILER"});
    @Test
    public void testRule() {
        assert(testRule.getVar().equals("document"));
    }

    @SuppressWarnings({"ConstantConditions", "EqualsBetweenInconvertibleTypes", "EqualsWithItself"})
    @Test
    public void testRuleEquals1() {
        assert(testRule.equals(testRule));
        assert(!testRule.equals(null));
        assert(!testRule.equals(""));
    }
}
