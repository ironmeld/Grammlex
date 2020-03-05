package org.grammlex.v1;

import org.junit.Test;

public class ActionTest {
    @Test
    public void testAction() {
        Action action = new Action(Action.SHIFT, 0);
        assert(action.toString().equals("SHIFT(0)"));
        assert(action.getTypeAsString().equals("SHIFT"));

        action = new Action(Action.REDUCE, 0);
        assert(action.getTypeAsString().equals("REDUCE"));

        action = new Action(Action.ACCEPT, 0);
        assert(action.getTypeAsString().equals("ACCEPT"));

        action = new Action(9, 0);
        assert(action.getTypeAsString().equals(""));
        assert(action.toString().equals(""));

    }
}
