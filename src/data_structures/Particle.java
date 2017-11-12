package data_structures;

import utils.Drawing;
import utils.IO;
import java.awt.*;

public class Particle {
    public Vector position;
    public Vector velocity;

    public final double mass;
    public final double radius;
    public int index;
    
    private double[] position;
    private double[] velocity;
    private double speedFactor;


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

    
    public Particle(double[] position, double[] velocity) {
        this.position = position.clone();
        this.velocity = velocity.clone();
        mass = 1;
        speedFactor = 20;
    }

    public Particle(double[] position) {
        this(position, new double[position.length]);
    }

    public double[] position() {
        return position.clone();
    }
    public double[] velocity() { return velocity.clone(); }
    public double mass() { return mass; }

    public double position(int i) { return position[i]; }
    public double velocity(int i) { return velocity[i]; }

    public void addPosition(int i, double position) {
        this.position[i] += position;
    }

    public void addVelocity(double[] velocity) {
        for (int i = 0; i < velocity.length; i++) {
            this.velocity[i] += velocity[i];
        }
    }

    /**
     * Apply a force over a time interval dt.
     * @param force
     * @param dt
     */
    public void applyForce(double[] force, double dt) {
        for (int i = 0; i < force.length; i++) {
            this.velocity[i] += force[i] * dt;
        }
    }

    /**
     * Optimized dist function for distance squared.
     * @param pos Position to which to calculate distance.
     * @return distance squared
     */
    public double dist2(double[] pos){
        double dist2 = 0;
        for (int i = 0; i < position.length; i++){
            dist2 += Math.pow((pos[i] - position[i]) , 2);
        }
        return dist2;
    }

    /**
     * General distance function for distance to the power of n.
     * @param pos Position to which to calculate distance.
     * @param power Power to which to raise the distance.
     * @return distance raised to the power of "power"
     */
    public double dist(double[] pos, int power) {
        return Math.pow(dist2(pos), (double)(power)/2);
    }

    /**
     * Distance vector from this to pos.
     * @param pos Vector describing position of pos.
     */
    public void distanceVector(double[] pos, double[] distanceVector){
        for(int i = 0; i < pos.length;i++) {
            distanceVector[i] = pos[i] - this.position[i];
        }
    }

    public String toString() {
        return IO.toString(position);
    }

    void paint2D(Graphics g, double scale, int size) {
        Rectangle scaledValues = Drawing.transform2D(
                position(0),
                position(1),
                size/scale,
                size/scale,
                scale,
                true
        );
        g.fillRect(scaledValues.x, scaledValues.y, scaledValues.width, scaledValues.height);
    }
}