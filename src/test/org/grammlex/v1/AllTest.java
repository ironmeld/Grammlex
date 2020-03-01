package org.grammlex.v1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    RuleTest.class,
    GrammarTest.class,
    ToolTest.class,
})
public class AllTest {
}
