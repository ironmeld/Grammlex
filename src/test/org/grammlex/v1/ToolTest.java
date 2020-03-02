package org.grammlex.v1;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class ToolTest {
    private static final String TEST_FILE = "../../examples/mfield.g4";
    private static final String MISSING_FILE = "../../examples/_missing.g4";

    @Test
    public void testToolOpenGrammar() throws IOException {
        Tool.main(new String[]{TEST_FILE});
    }

    @Test
    public void testToolOpenDebug() throws IOException  {
        Tool.main(new String[]{"-d", "-q", TEST_FILE});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolMissingArg() throws IOException {
        Tool.main(new String[]{});
    }

    @Test(expected = NoSuchFileException.class)
    public void testToolOpenBad() throws IOException  {
        Tool.main(new String[]{MISSING_FILE});
    }

    @Test(expected = NoSuchFileException.class)
    public void testToolNew() throws IOException  {
        //noinspection unused
        Tool tool = new Tool(); //NOSONAR
        Tool.main(new String[]{MISSING_FILE});
    }


}
