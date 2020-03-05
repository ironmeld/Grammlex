package org.grammlex.v1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    RuleTest.class,
    GrammarTest.class,
    LR1ItemTest.class,
    LR1StateTest.class,
    LR1BuilderTest.class,
    ActionTest.class,
    ToolTest.class,
})
public class AllTest {
}
