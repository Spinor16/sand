package data_structures;

import exceptions.TimeException;

/**
 * Contain information about one collision event.
 *
 * normal = time at which event happens.
 * i = index of first particle involved.
 * j = index of second particle involved.
 *
 * Condition: i < j.
 */
public class CollisionEvent implements Comparable<CollisionEvent> {
    private double t;
    private int i, j;

    /**
     * Constructor
     * @pre i<j
     * @param normal time at which the collision occurs
     * @param i index of first colliding particle
     * @param j index of second colliding particle
     */
    public CollisionEvent(double t, int i, int j){
        this.reset(t, i, j);
    }

    public double t() { return t; }
    public int i() { return i; }
    public int j() { return j; }

    /**
     * Reset attributes.
     * @pre i<j
     * @param normal time at which the collision occurs
     * @param i index of first colliding particle
     * @param j index of second colliding particle
     */
    public void reset(double t, int i, int j){
        assert t > 0;
        assert i < j;

        this.t = t;
        this.i = i;
        this.j = j;
    }

    /**
     * Compares this to other.
     * < 0 if this < other
     * = 0 if this = other
     * > 0 if this > other
     */
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


