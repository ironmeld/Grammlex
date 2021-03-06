package org.grammlex.v1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Tool {
    public static final String CMD_SHOW = "show";
    public static final String TYPE_GRAMMAR = "grammar";
    public static final String TYPE_RULES = "rules";
    public static final String TYPE_FIRST_SETS = "firstSets";
    public static final String TYPE_FOLLOW_SETS = "followSets";
    public static final String TYPE_STATES = "states";
    public static final String TYPE_CREATE_STATES = "createStates";
    public static final String TYPE_ACTION_TABLE = "actionTable";
    public static final String TYPE_GOTO_TABLE = "gotoTable";

    protected static final Set<String> contentTypes = new HashSet<>(Arrays.asList(
            TYPE_GRAMMAR, TYPE_RULES,
            TYPE_FIRST_SETS, TYPE_FOLLOW_SETS,
            TYPE_STATES, TYPE_CREATE_STATES,
            TYPE_ACTION_TABLE, TYPE_GOTO_TABLE
            ));

    public static void main(String[] args) throws IOException {
        StringBuilder out = new StringBuilder();
        //noinspection unused
        int throwaway = handleCommands(out, args); //NOSONAR
        System.out.print(out); //NOSONAR
    }

    public static int handleCommands(StringBuilder out, String[] args) throws IOException {
        return handleCommands(out, args, 0);
    }

    public static int handleCommands(StringBuilder out, String[] args, int currentArg) throws IOException {
        Map<String, StringBuilder> cachedContent = new HashMap<>();
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing grammar file argument");
        }
        String grammarText = readFile(Paths.get(args[currentArg++]));
        Grammar grammar = new Grammar(grammarText);
        LR1Builder builder = new LR1Builder(grammar);

        if (currentArg >= args.length) {
            currentArg = handleShowCommand(grammar, builder, cachedContent, args, currentArg, out);
        }
        while (currentArg < args.length) {
            if (args[currentArg].equals(CMD_SHOW)) {
                currentArg++;
                currentArg = handleShowCommand(grammar, builder, cachedContent, args, currentArg, out);
            } else {
                throw new IllegalArgumentException("Unknown command: " + args[currentArg]);
            }
            currentArg++;
        }
        return currentArg;
    }

    public static int handleShowCommand(Grammar grammar, LR1Builder builder,
                                        Map<String, StringBuilder> cachedContent,
                                        String[] args, int currentArg, StringBuilder out) {
        String contentTypes;
        if (currentArg >= args.length) {
            contentTypes = "grammar,createStates,states";
        } else {
            contentTypes = args[currentArg];
            currentArg++;
        }
        for (String contentType : contentTypes.split(",")) {
            updateCachedContent(grammar, builder, cachedContent, contentType);
            out.append(cachedContent.get(contentType));
        }
        return currentArg;
    }

    public static void updateCachedContent(Grammar grammar, LR1Builder builder,
                                    Map<String, StringBuilder> cachedContent,
                                    String contentType) {
        if (!contentTypes.contains(contentType)) {
            throw new IllegalArgumentException(
                    "ERROR: grammlex: show: Unknown content type: " + contentType);
        }
        if (!cachedContent.containsKey(contentType)) {
            StringBuilder newContent;
            switch (contentType) {
                case TYPE_GRAMMAR:
                    newContent = new StringBuilder();
                    grammar.outputGrammar(newContent);
                    cachedContent.put(contentType, newContent);
                    break;

                case TYPE_RULES:
                    newContent = new StringBuilder();
                    grammar.outputRules(newContent);
                    cachedContent.put(contentType, newContent);
                    break;

                case TYPE_FIRST_SETS:
                    newContent = new StringBuilder();
                    grammar.outputFirstSets(newContent);
                    cachedContent.put(contentType, newContent);
                    break;

                case TYPE_FOLLOW_SETS:
                    newContent = new StringBuilder();
                    grammar.outputFollowSets(newContent);
                    cachedContent.put(contentType, newContent);
                    break;

                case TYPE_STATES:
                case TYPE_CREATE_STATES:
                case TYPE_ACTION_TABLE:
                case TYPE_GOTO_TABLE:
                    newContent = new StringBuilder();
                    if (!builder.createStatesForCLR1(newContent)) {
                        throw new IllegalArgumentException("Grammar is not LR(1)!");
                    }
                    cachedContent.put(TYPE_CREATE_STATES, newContent);

                    newContent = builder.outputStates(new StringBuilder());
                    cachedContent.put(TYPE_STATES, newContent);

                    newContent = builder.outputActionTable(new StringBuilder());
                    cachedContent.put(TYPE_ACTION_TABLE, newContent);

                    cachedContent.put(TYPE_GOTO_TABLE,
                            builder.outputGotoTable(new StringBuilder()));
                    break;

                default:
                    break;
            }
        }
    }

    public static String readFile(Path path) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
