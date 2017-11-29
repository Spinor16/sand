package data_structures;

import calc.VectorCalculus;
import org.omg.CORBA.MARSHAL;
import org.w3c.dom.css.Rect;
import utils.IO;

import javax.swing.*;
import java.awt.*;

public class InitialConditions extends JPanel {
    private static final long serialVersionUID = 1L;  //used for JPanel
    /*
    defines how the initial situation is supposed to look like. includes:
    - where can particles in the beginning be?
    - how does the box around particles look like? shape, dimensions, ect.
    assumptions (for now):
    - particle_radius = 1 for all particles
    - walls don'normal move
    - situation looks like this:

          w
     ___________
     ppppppppppp    |
                    |
   \              / |  h
    \           /   |
     \        /     |
     l \  a / l     |
         \/         |
         0
    w: width
    h:height
    a:angle alpha
    :length of each lower wall
    0: origin, xaxis to right, yaxis down (as in Jpanel)
    p: possible particle position, with index
    rn: roughness, how many wall points per particle diameter distance? (many points->smooth) constant for all walls

     */

    //constants
    public final double pRadius = 0.01;
    public final double[] origin = {0,0};
    public double width;
    public double height;
    public double angle;
    public final double particleDistanceInitial = 0.4; // around the particle, in units of the radius
    public final int nParticles;

    private Boundary[] boundaries = new Boundary[3];
    private Boundary lowerWallL;
    private Boundary lowerWallR;
    private Boundary floor;

    //particle array
    private Particle[] particles;

    public InitialConditions(int nParticles, double width, double height, double angle) {
        /*
        width and height of box, should be larger than double the radius of the particle (r=1).
        Angle between 0 and pi
         */
        setWidth(width);
        setHeight(height);
        setAngle(angle);
        this.nParticles = nParticles;

//        nParticles = (int) (width/(2*particleDistanceInitial+2*pRadius));
        IO.print(nParticles);
        particles = new Particle[nParticles];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(new double[]{0,0}, new double[]{0,0});
        }
        makeBoundary();
        makeParticles();
    }

    public void setWidth(double width){
        this.width = width;
    }

    public void setHeight(double height){
        this.height = height;
    }

    public void setAngle(double angle){ this.angle = angle; }

    public Boundary[] getBoundaries() {
        return boundaries;
    }

    public Particle[] getParticles() {
        return particles;
    }

    private void makeBoundary(){
        int dimensions = 2;

//        upperWall = new Boundary[dimensions];
//        leftWall = new Boundary[dimensions];
//        rightWall = new Boundary[dimensions];

        double[] lWallVelocity = {0,0};
        double[] lWallPosition = {width/2.,0};
        angle = (Math.PI-angle)/2;
        double[] lWallDirection = {Math.cos(angle), Math.sin(angle)};
//        VectorCalculus.plusSE(lWallPosition,VectorCalculus.mult(0.3,lWallDirection));
        lowerWallL = new Boundary(lWallVelocity, lWallPosition, lWallDirection);

        double[] rWallVelocity = {0,0};
        double[] rWallPosition = lWallPosition;
        double[] rWallDirection = {-Math.cos(angle), Math.sin(angle)};
//        VectorCalculus.plusSE(rWallPosition,VectorCalculus.mult(0.3,rWallDirection));
        lowerWallR = new Boundary(rWallVelocity, rWallPosition, rWallDirection);

        double[] floorVelocity = {0,0};
        double[] floorPosition = {0, 0.2*height};
        double[] floorDirection = {1, 0};
//        VectorCalculus.plusSE(rWallPosition,VectorCalculus.mult(0.3,rWallDirection));
        floor = new Boundary(floorVelocity, floorPosition, floorDirection);


        boundaries[0] = lowerWallL;
        boundaries[1] = lowerWallR;
        boundaries[2] = floor;
    }

    /*
    Set the particles in one line along the whole width. Works in 2D
     */
    private void makeParticles() {
        int nParticlesInRow = (int)(width/(2*pRadius*(particleDistanceInitial+1)));
        double displUp = 0; //displace upwards if row full (based on int-division)
        for (int i = 0; i < particles.length; i++){

            displUp = (double)(i/nParticlesInRow)*(2*pRadius*(particleDistanceInitial+1));

            particles[i].position[0] = origin[0]+((i % nParticlesInRow)+1)*(2*pRadius*(particleDistanceInitial+1));
            particles[i].position[1] = origin[1]+height+displUp;

            particles[i].velocity[1] = 0;
            particles[i].setColorIndex(i);
        }
//        particles[0].velocity[0] = -1;
//        particles[1].velocity[0] = 1;
    }
}
