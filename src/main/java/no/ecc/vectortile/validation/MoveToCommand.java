package no.ecc.vectortile.validation;

import java.util.List;

public class MoveToCommand implements Command {
    private final List<Delta> deltas;

    public MoveToCommand(final List<Delta> deltas) {
        this.deltas = deltas;
    }

    public List<Delta> deltas() {
        return deltas;
    }

    public int count() {
        return deltas.size();
    }
}
