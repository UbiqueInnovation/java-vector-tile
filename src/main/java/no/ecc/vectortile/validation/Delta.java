package no.ecc.vectortile.validation;

public class Delta implements Command {
    private final int dx;
    private final int dy;

    public Delta(final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }
}
