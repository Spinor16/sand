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
    private double speedFactor;


    public Particle(double[] position, double[] velocity) {
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

    /**
    public double[] position() {
        return position.clone();
    }
    public double[] velocity() { return velocity.clone(); }

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
    **/
    /**
     * Apply a force over a time interval dt.
     * @param force
     * @param dt
     */
    /**
    public void applyForce(double[] force, double dt) {
        for (int i = 0; i < force.length; i++) {
            this.velocity[i] += force[i] * dt;
        }
    }
    **/

    void paint2D(Graphics g, double scale, int size) {
        Rectangle scaledValues = Drawing.transform2D(
                position.get(0),
                position.get(1),
                size/scale,
                size/scale,
                scale,
                true
        );
        g.fillRect(scaledValues.x, scaledValues.y, scaledValues.width, scaledValues.height);
    }
}