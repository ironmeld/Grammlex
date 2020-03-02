package org.grammlex.v1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Tool {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing grammar file argument");
        }

        int currentArg = 0;
        int debug = 1;
        for (String arg : args) {
            if (arg.equals("-d")) {
                debug++;
            } else {
                if (arg.equals("-q")) {
                    debug--;
                } else {
                    break;
                }
            }
            currentArg++;
        }

        String grammarText = readFile(Paths.get(args[currentArg]));
        Grammar grammar = new Grammar(grammarText);
        StringBuilder out = new StringBuilder();
        out.append(grammar.dumpGrammar());
        LR1Builder builder = new LR1Builder(grammar);
        out.append("\n");
        builder.createStatesForCLR1(debug, out); //NOSONAR
        int stateNum = 0;
        for (LR1State state : builder.getStates()) {
            out.append("State #").append(stateNum).append(":\n");
            out.append(state.toString());
            out.append("\n");
            stateNum++;
        }
        System.out.print(out); //NOSONAR
    }

    public static String readFile(Path path) throws IOException {
        byte[] encoded;
        encoded = Files.readAllBytes(path);
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
