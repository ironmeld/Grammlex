package org.grammlex.v1;

import org.junit.Test;
import java.util.HashSet;

public class LR1ItemTest {
    @Test
    public void testLR1Item() {
        Rule rule = new Rule("document",
                new String[] {"BOM?","HEADER","value+","TRAILER"});
	LR1Item testItem = new LR1Item(rule.getVar(), rule.getTerms(),
					0, new HashSet<>());
        assert(testItem.getVar().equals("document"));
    }
}
