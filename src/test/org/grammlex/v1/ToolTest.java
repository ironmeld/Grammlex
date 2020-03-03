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
    public void testToolShowDetails() throws IOException  {
        Tool.main(new String[]{TEST_FILE, "show", "-d", "rules"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolShowBad() throws IOException  {
        Tool.main(new String[]{TEST_FILE, "show"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolShowBadType() throws IOException  {
        Tool.main(new String[]{TEST_FILE, "show", "foo"});
    }

    @Test
    public void testToolShowFirstFollow() throws IOException  {
        Tool.main(new String[]{TEST_FILE, "show", "-q", "firstSets,followSets"});
    }

    @Test
    public void testToolShowReport() throws IOException  {
        Tool.main(new String[]{TEST_FILE, "show", "-d", "grammar,createStates,states"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolOpenBadArg() throws IOException  {
        Tool.main(new String[]{TEST_FILE, "-d", "show", "rules"});
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
