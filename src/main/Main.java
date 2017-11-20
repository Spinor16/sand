package main;

import calc.Collision;
import data_structures.*;

import javax.swing.*;
import java.util.ArrayList;

public class Main extends JPanel{

    public static void main(String[] args) {
        InitialConditions init = new InitialConditions(1,1,1,1);

        Particle[] particles = init.getParticles();
        Boundary[] boundaries = init.getBoundaries();
        Main main = new Main();
        JFrame top = new JFrame("Particles doing Stuff");
        top.setBounds(0, 0, 900, 900);
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.getContentPane().add(main);
        top.setVisible(true);

        run(0.1,1, particles, boundaries);

    }

    public void run(double timeStep, double endTime, Particle[] particles, Boundary[] boundaries) {

        double time = 0;
        int nParticles = 50;
        int nBoundaries = 2;
        int nearestNeighbours = 10;
        BinaryTree tree = new BinaryTree(2, nParticles);


        while(time<endTime){
            CollisionHeap ParticleHeap = new CollisionHeap(nParticles);
            CollisionHeap BoundaryHeap = new CollisionHeap(nBoundaries);
            ArrayList<CollisionEvent> events = new ArrayList<>();
            //Add CollisionTimes particle - particle
            for (int j = 0; j < particles.length; j++) {
                for (int i = 0; i < particles.length; i++) {
                     events.add(new CollisionEvent(Collision.findCollisionTime(particles[i],particles[j]),i,j));
                }
            }

            //Add CollisionTimes particle - boundaries
            for (int j = 0; j < particles.length; j++) {
                for (int i = 0; i < particles.length; i++) {
                    events.add(new CollisionEvent(Collision.findCollisionTime(particles[i],particles[j]),i,j));
                }
            }

            leapFrog(timeStep, particles, tree);
            tree.buildTree(tree.root);
            time += timeStep;
            repaint();

            try {
                //wait after every calculation to slow motion down
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}
