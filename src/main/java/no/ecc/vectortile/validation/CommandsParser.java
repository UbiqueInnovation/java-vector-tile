package no.ecc.vectortile.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandsParser {

    private CommandsParser() {}

    public static List<Command> parse(List<Integer> commands) {
        List<Command> decodedCommands = new ArrayList<>();

        Iterator<Integer> iterator = commands.iterator();
        while (iterator.hasNext()) {
            int commandInteger = iterator.next();
            int commandId = commandInteger & 0x7;
            int count = commandInteger >> 3;

            switch (commandId) {
                case 1:
                    decodedCommands.add(new MoveToCommand(parseDeltas(iterator, count)));
                    break;
                case 2:
                    decodedCommands.add(new LineToCommand(parseDeltas(iterator, count)));
                    break;
                case 7:
                    decodedCommands.add(new ClosePathCommand());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command ID " + commandId);
            }
        }
        return decodedCommands;
    }

    private static List<Delta> parseDeltas(final Iterator<Integer> iterator, final int count) {
        final List<Delta> deltas = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            int dx = decodeParameterInteger(iterator.next());
            int dy = decodeParameterInteger(iterator.next());
            deltas.add(new Delta(dx, dy));
        }
        return deltas;
    }

    private static int decodeParameterInteger(final int parameterInteger) {
        return ((parameterInteger >> 1) ^ (-(parameterInteger & 1)));
    }
}
