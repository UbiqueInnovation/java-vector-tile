package no.ecc.vectortile.validation;

import java.util.List;

public final class LineToCommand implements Command {

    private final List<Delta> deltas;

    public LineToCommand(final List<Delta> deltas) {
        this.deltas = deltas;
    }

    public List<Delta> deltas() {
        return deltas;
    }

    public int count() {
        return deltas.size();
    }
}
