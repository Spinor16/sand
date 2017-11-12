import utils.IO;

public class CollisionEvent implements Comparable<CollisionEvent> {
    private double t;
    private int i, j;
    /**
     * Constructor
     * @param t time at which the collision occurs
     * @param i index of first colliding particle
     * @param j index of second colliding particle
     */
    public CollisionEvent(double t, int i, int j) {
        this.t = t;
        this.i = i;
        this.j = j;
    }

    public double t() { return t; }
    public int i() { return i; }
    public int j() { return j; }

    @Override
    public int compareTo(CollisionEvent other) {
        if (this.equals(other)) {
            return 0;
        }
        else {
            return this.t > other.t ?  1 : -1;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        else {
            CollisionEvent other = (CollisionEvent) obj;
            return ((Double)this.t).equals(other.t);
        }
    }

    public String toString() {
        return ("{" + t + ", " + i + ", " + j + "}");
    }
}


