package no.ecc.vectortile.validation;

import java.util.Iterator;
import java.util.List;
import vector_tile.VectorTile.Tile.GeomType;

public class CommandsValidator {

    private CommandsValidator() {}

    public static void validate(final List<Integer> commands, final GeomType geomType) {
        final List<Command> decodedCommands = CommandsParser.parse(commands);

        switch (geomType) {
            case POINT:
                validatePointCommands(decodedCommands);
                break;
            case LINESTRING:
                validateLineStringCommands(decodedCommands);
                break;
            case POLYGON:
                validatePolygonCommands(decodedCommands);
                break;
            case UNKNOWN:
                throw new ValidationException("Unknown geometry type");
        }
    }

    private static void validatePointCommands(final List<Command> commands) {
//      The POINT geometry type encodes a point or multipoint geometry. The geometry
//      command sequence for a point geometry MUST consist of a single MoveTo command with a
//      command count greater than 0.
//
//      If the MoveTo command for a POINT geometry has a command count of 1, then the geometry
//      MUST be interpreted as a single point; otherwise the geometry MUST be interpreted as a
//      multipoint geometry, wherein each pair of ParameterIntegers encodes a single point.

        if (commands.size() != 1) {
            throw new ValidationException(
                "The geometry command sequence for a point geometry MUST consist of a *single* MoveTo command. Got "
                    + commands.size() + "commands");
        }

        final MoveToCommand moveToCommand = getNextCommand(commands.iterator(), MoveToCommand.class);
        if (moveToCommand.count() <= 0) {
            throw new ValidationException(
                "The geometry command sequence for a point geometry MUST consist of a single MoveTo command with a *command count greater than 0.*");
        }
    }

    private static void validateLineStringCommands(final List<Command> commands) {
//      The geometry command sequence for a linestring geometry MUST consist of
//      one or more repetitions of the following sequence:
//        - A MoveTo command with a command count of 1
//        - A LineTo command with a command count greater than 0

        if (commands.isEmpty()) {
            throw new ValidationException("One or more line strings are required");
        }

        final Iterator<Command> iterator = commands.iterator();
        while (iterator.hasNext()) {
            final MoveToCommand moveToCommand = getNextCommand(iterator, MoveToCommand.class);
            if (moveToCommand.count() != 1) {
                throw new ValidationException("The MoveTo command must have a count of 1");
            }

            final LineToCommand lineToCommand = getNextCommand(iterator, LineToCommand.class);
            if (lineToCommand.count() <= 0) {
                throw new ValidationException(
                    "The LineTo command must have a count greater than 0");
            }
        }
    }

    private static void validatePolygonCommands(final List<Command> commands) {
//      The POLYGON geometry type encodes a polygon or multipolygon geometry, each polygon
//      consisting of exactly one exterior ring that contains zero or more interior rings. The
//      geometry command sequence for a polygon consists of one or more repetitions of the following
//      sequence:
//       - An ExteriorRing
//       - Zero or more InteriorRings

        if (commands.isEmpty()) {
            throw new ValidationException("One or more polygons are required");
        }

//      Each ExteriorRing and InteriorRing MUST consist of the following sequence:
//       - A MoveTo command with a command count of 1
//       - A LineTo command with a command count greater than 1
//       - A ClosePath command

        final Iterator<Command> iterator = commands.iterator();
        while (iterator.hasNext()) {
            final MoveToCommand moveToCommand = getNextCommand(iterator, MoveToCommand.class);
            if (moveToCommand.count() != 1) {
                throw new ValidationException("The MoveTo command must have a count of 1");
            }

            final LineToCommand lineToCommand = getNextCommand(iterator, LineToCommand.class);
            if (lineToCommand.count() <= 1) {
                throw new ValidationException(
                    "The LineTo command must have a count greater than 1");
            }

            getNextCommand(iterator, ClosePathCommand.class);
        }
    }

    private static <C extends Command> C getNextCommand(Iterator<Command> iterator, Class<C> cls) {
        if (!iterator.hasNext()) {
            throw new ValidationException("Unexpected end. Expected command of type " + cls.getSimpleName());
        }
        final Command command = iterator.next();
        try {
            //noinspection unchecked
            return (C) command;
        } catch (ClassCastException e) {
            throw new ValidationException(
                "Expected command of type " + cls.getSimpleName() + " but got " + command.getClass()
                    .getSimpleName());
        }
    }

}
