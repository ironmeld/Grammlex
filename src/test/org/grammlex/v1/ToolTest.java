package org.grammlex.v1;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class ToolTest {
    private static final String TEST_FILE = "../../examples/mfield.g4";
    private static final String MISSING_FILE = "../../examples/_missing.g4";
    private static final String RULE_1 = "1: S: document_repeat;";

    @Test
    public void testToolMainOpenGrammar() throws IOException {
        Tool.main(new String[]{TEST_FILE});
    }

    @Test
    public void testToolOpenGrammar() throws IOException {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out, new String[]{TEST_FILE});
        assert(out.toString().contains("State #16"));
        assert(!out.toString().contains("State #17"));
    }

    @Test
    public void testToolShow() throws IOException  {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out, new String[]{TEST_FILE, "show"});
        assert(out.toString().contains("State #16"));
        assert(!out.toString().contains("State #17")); }

    @Test
    public void testToolShowDetails() throws IOException {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out, new String[]{TEST_FILE, "show", "-d", "rules"});
        assert (out.toString().contains(RULE_1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolShowBadType() throws IOException  {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out, new String[]{TEST_FILE, "show", "foo"});
    }

    @Test
    public void testToolShowFirstFollow() throws IOException  {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out,
                new String[] {TEST_FILE, "show", "-q", "firstSets,followSets"});
        assert(out.toString().contains("S: [epsilon, BOM, HEADER]"));
        assert(!out.toString().contains(RULE_1));
    }

    @Test
    public void testToolShowReport() throws IOException  {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out,
                new String[]{TEST_FILE,
                        "show", "-d", "grammar,createStates,states"});
        assert(out.toString().contains(RULE_1));
        assert(out.toString().contains("S: [epsilon, BOM, HEADER]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolOpenBadArg() throws IOException  {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out,
                new String[]{TEST_FILE, "-d", "show", "rules"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolMissingArg() throws IOException {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out, new String[]{});
    }

    @Test(expected = NoSuchFileException.class)
    public void testToolOpenBad() throws IOException  {
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out, new String[]{MISSING_FILE});
    }

    @Test(expected = NoSuchFileException.class)
    public void testToolNew() throws IOException  {
        //noinspection unused
        Tool tool = new Tool(); //NOSONAR
        StringBuilder out = new StringBuilder();
        Tool.handleCommands(out, new String[]{MISSING_FILE});
    }
}
