package data_structures;

public class Particle {
    public Vector position;
    public Vector velocity;

    public final double mass;
    public final double radius;
    public int index;

    public Particle(int dimensions, double[] position, double[] velocity) {
        //mass
        this.mass = 1;

        this.radius = 1;

        //position
        this.position = new Vector(position);

        //velocity
        this.velocity = new Vector(velocity);

    }


    int dimensions() { return position.dimension; }

    public double mass() { return mass; }

}