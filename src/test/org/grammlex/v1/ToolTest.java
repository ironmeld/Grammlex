package org.grammlex.v1;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class ToolTest {
    @Test
    public void testToolOpenGrammar() throws IOException {
        Tool.main(new String[]{"../../examples/mfield.g4"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolMissingArg() throws IOException {
        Tool.main(new String[]{});
    }

    @Test(expected = NoSuchFileException.class)
    public void testToolOpenBad() throws IOException  {
        Tool.main(new String[]{"../../examples/_missing.g4"});
    }

    @Test(expected = NoSuchFileException.class)
    public void testToolNew() throws IOException  {
        //noinspection unused
        Tool tool = new Tool(); //NOSONAR
        Tool.main(new String[]{"../../examples/_missing.g4"});
    }

    @Test(expected = NoSuchFileException.class)
    public void testToolDebug() throws IOException  {
        //noinspection unused
        Tool tool = new Tool(); //NOSONAR
        Tool.main(new String[]{"-d", "-q", "../../examples/_missing.g4"});
    }
}
