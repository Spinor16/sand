package data_structures;

import org.w3c.dom.css.Rect;

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
    - walls don't move
    - situation looks like this:

          w
    0___________
    |ppppppppppp|
    |           |
    |           |  h
    |           |
     \        /
     l \  a / l
         \/
    w: width
    h:height
    a:angle alpha
    :length of each lower wall
    0: origin, xaxis to right, yaxis down (as in Jpanel)
    p: possible particle position, with index
    rn: roughness, how many wall points per particle diameter distance? (many points->smooth) constant for all walls

     */

    //constants
    public final double pRadius = 10.;
    public final double[] origin = {0,0};
    public final double width;
    public final double height;
    public final double angle;
    public final double roughness;
    public final double particleDistanceInitial = 0.01; // around the particle, in units of the radius
    int nParticles;

    //boundary array
    public Boundary[] boundaryPoints;
    public Boundary[] upperWall;
    public Boundary[] leftWall;
    public Boundary[] rightWall;
    public Boundary[] lowerWallL;
    public Boundary[] lowerWallR;

    //particle array
    Particle[] particles;

    public static void main(String[] args) {
        InitialConditions initialConditions = new InitialConditions(900, 450, Math.PI/2., 100);
        initialConditions.makeBoundary();
        initialConditions.makeParticles();
        initialConditions.run();
    }

    public void run(){
        JFrame top = new JFrame("InitialConditions");
        top.setBounds(0, 0, 900, 900);
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.getContentPane().add(this);
        top.setVisible(true);

        repaint();

    }

    public InitialConditions(double width, double height, double angle, double roughness) {
        /*
        width and height of box, should be larger than double the radius of the particle (r=1).
        Angle between 0 and pi
        Roughness larger than 1, else particles slip through wall. probably good if around 100.
         */
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.roughness = roughness;

        nParticles = (int) (width/(2*particleDistanceInitial+pRadius));
        particles = new Particle[nParticles];

//        makeBoundary();
//        makeParticles();
    }

    private void makeBoundary(){
        int nUpperWall = (int)(width/pRadius*roughness);
        int nSideWall = (int)(height/pRadius*roughness);
        double lengthLower = width/2./Math.sin(angle/2);
        int nLowerPartWall = (int)(lengthLower/pRadius*roughness);

        upperWall = new Boundary[nUpperWall];
        leftWall = new Boundary[nSideWall];
        rightWall = new Boundary[nSideWall];
        lowerWallL = new Boundary[nLowerPartWall];
        lowerWallR = new Boundary[nLowerPartWall];
        boundaryPoints = new Boundary[nUpperWall+2*nSideWall+2*nLowerPartWall];
        /*
        pointwise construction of the boarders as shown above
        clockwise
        */
        for (int i = 0; i < nUpperWall; i++) {
            upperWall[i].position[0] = origin[0]+roughness*pRadius*i;
            upperWall[i].position[1] = origin[1];
        }
        for (int i = 0; i < nSideWall; i++) {
            leftWall[i].position[0] = origin[0];
            leftWall[i].position[1] = origin[1]+height - roughness*pRadius*i;

            rightWall[i].position[0] = origin[0]+width;
            rightWall[i].position[1] = origin[1] + roughness*pRadius*i;
        }

        for (int i = 0; i < nLowerPartWall; i++) {
            lowerWallR[i].position[0] = origin[0]+width - roughness*pRadius*Math.cos(angle/2);
            lowerWallR[i].position[1] = origin[1]+height + roughness*pRadius*Math.sin(angle/2);

            lowerWallL[i].position[0] = origin[0]+width/2 - roughness*pRadius*Math.cos(angle/2);
            lowerWallL[i].position[1] = origin[1]+height+width/2.*Math.cos(angle/2) - roughness*pRadius*Math.sin(angle/2);
        }
    }

    private void makeParticles() {
        for (int i = 0; i < particles.length; i++){
            /*
            Set the particles in one line along the whole width. Works in 2D
             */
            particles[i].position[0] = origin[0]+(i+1)*(2*particleDistanceInitial+pRadius);
            particles[i].position[1] = origin[1];
        }
    }

    public void paint(Graphics g) {
        Rectangle bounds = getBounds();

        // Clear window and draw background.
        g.setColor(Color.WHITE);
        paintComponent(g);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        //Draw borders
        g.setColor(Color.BLACK);
        for (int i = 0; i < upperWall.length; i++) {
            g.drawRect((int)upperWall[i].position[0], (int)upperWall[i].position[1], 1, 1 );
        }
        for (int i = 0; i < leftWall.length; i++) {
            g.drawRect((int) leftWall[i].position[0], (int)leftWall[i].position[1], 1, 1 );
        }
        for (int i = 0; i < rightWall.length; i++) {
            g.drawRect((int)rightWall[i].position[0], (int)rightWall[i].position[1], 1, 1 );
        }
        for (int i = 0; i < lowerWallL.length; i++) {
            g.drawRect((int)lowerWallL[i].position[0], (int)lowerWallL[i].position[1], 1, 1 );
        }
        for (int i = 0; i < lowerWallR.length; i++) {
            g.drawRect((int)lowerWallR[i].position[0], (int)lowerWallR[i].position[1], 1, 1 );
        }

        //Draw Particles
        g.setColor(Color.BLUE);
        for (int i = 0; i < particles.length; i++) {
            g.drawOval((int)particles[i].position[0], (int)particles[i].position[1], (int)pRadius, (int)pRadius);
        }

    }
}
