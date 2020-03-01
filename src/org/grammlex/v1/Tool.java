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
        String grammarText = readFile(Paths.get(args[0]));
        Grammar grammar = new Grammar(grammarText);
        System.out.println(grammar.dumpGrammar()); //NOSONAR
    }

    public static String readFile(Path path) throws IOException {
        byte[] encoded;
        encoded = Files.readAllBytes(path);
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
