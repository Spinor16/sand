package data_structures;

public class Particle {
    public Vector position;
    public Vector velocity;

    private final double mass;

    public int index;

    public Particle(int dimensions, double[] position, double[] velocity) {
        //mass
        mass = 1;

        //position
        this.position = new Vector(position);

        //velocity
        this.velocity = new Vector(velocity);

    }


    int dimensions() { return position.dimension; }

    public double mass() { return mass; }

}