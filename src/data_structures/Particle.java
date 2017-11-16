package data_structures;

import utils.Drawing;

import java.awt.*;

public class Particle {
    public double[] position;
    public double[] velocity;

    public final double mass;
    public final double radius;
    public int index;
    private double speedFactor;


    public Particle(double[] position, double[] velocity) {
        //mass
        this.mass = 1;

        this.radius = 1;

        //position
        this.position = position;

        //velocity
        this.velocity = velocity;

    }


    int dimensions() { return position.length; }

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
                position[0],
                position[1],
                size/scale,
                size/scale,
                scale,
                true
        );
        g.fillRect(scaledValues.x, scaledValues.y, scaledValues.width, scaledValues.height);
    }
}