package main;

import calc.Collision;
import data_structures.*;
import exceptions.HeapException;
import exceptions.TimeException;

import javax.swing.*;
import java.util.ArrayList;

public class Main extends JPanel{

    public static void main(String[] args) {


        Main main = new Main();
        JFrame top = new JFrame("Particles doing Stuff");
        top.setBounds(0, 0, 900, 900);
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.getContentPane().add(main);
        top.setVisible(true);

        run(0.1,1);

    }

    public static void run(double timeStep, double endTime) {

        //Settings
        double time = 0;
//        int nParticles = 50;
//        int nBoundaries = 2;
        int nNearestNeighbours = 10;

        InitialConditions init = new InitialConditions(1,1,1);
        Particle[] particles = init.getParticles();
        Boundary[] boundaries = init.getBoundaries();
        BinaryTree tree = new BinaryTree(particles);

        //Initializations of temp vars
        double t = 0;
        double DeltaT = 0;
        CollisionEvent minPP;
        CollisionEvent minPB;

        while(time<endTime){
            CollisionHeap ParticleHeap = new CollisionHeap(particles.length);
            CollisionHeap BoundaryHeap = new CollisionHeap(boundaries.length);


            //Add CollisionTimes particle - particle
            for (int i = 0; i < particles.length; i++) {

                //Look for nearestNeighbours
                int[] nearestNeighbours = tree.getIndiceskNearestNeighbours(particles[i].position,nNearestNeighbours);

                for (int j = 0; j < nNearestNeighbours; j++) {
                    try {
                        ParticleHeap.insert(new CollisionEvent(Collision.findCollisionTime(particles[i],
                                            particles[nearestNeighbours[j]]),i,nearestNeighbours[j]));
                    } catch (TimeException e) {
                        continue;
                    } catch (HeapException e){
                        e.printStackTrace();
                    }
                }

            }

            //Add CollisionTimes particle - boundaries
            for (int i = 0; i < particles.length; i++) {
                for (int j = 0; j < boundaries.length; j++) {
                    try {
                        BoundaryHeap.insert(new CollisionEvent(Collision.findCollisionTime(particles[i],particles[j]),i,j));
                    } catch (TimeException e) {
                        continue;
                    } catch (HeapException e) {
                        e.printStackTrace();
                    }
                }
            }

            while(DeltaT < timeStep){
                minPP = ParticleHeap.min();
                minPB = BoundaryHeap.min();
                ArrayList<CollisionEvent> events = new ArrayList<>(); //temporary storing of CollisionEvents

                if (minPP.t() < minPB.t()){
                    try {
                        minPP = ParticleHeap.removeMin();
                        Collision.resolveCollision(particles[minPP.i()],particles[minPP.j()],minPP.t());

                        ParticleHeap.removeEventsContainingIndexSE(minPP.i(),events);
                        ParticleHeap.removeEventsContainingIndexSE(minPP.j(),events);

                        //Insert new CollisionEvents for i
                        for (int j = 0; j < boundaries.length; j++) {
                            events.get(j).reset(Collision.findCollisionTime(particles[minPP.i()],particles[j]),minPP.i(),j); // take unused CollisionEvent
                            ParticleHeap.insert(events.get(j));
                        }

                        //Insert new CollisionEvents for j
                        for (int i = 0; i < boundaries.length; i++) {
                            events.get(i).reset(Collision.findCollisionTime(particles[minPP.j()],particles[i]),minPP.j(),i); // take unused CollisionEvent
                            ParticleHeap.insert(events.get(i));
                        }

                    } catch (HeapException e) {
                        e.printStackTrace();
                    } catch (TimeException e) {
                        continue;
                    }

                    DeltaT += minPP.t();
                }
                else {
                    try {
                        minPB = BoundaryHeap.removeMin();
                        Collision.resolveCollision(particles[minPP.i()],boundaries[minPP.j()],minPP.t());

                        BoundaryHeap.removeEventsContainingIndexSE(minPP.i(),events);
                        BoundaryHeap.removeEventsContainingIndexSE(minPP.j(),events);

                        //Insert new CollisionEvents for i
                        for (int j = 0; j < boundaries.length; j++) {
                            events.get(j).reset(Collision.findCollisionTime(particles[minPP.i()],particles[j]),minPP.i(),j); // take unused CollisionEvent
                            BoundaryHeap.insert(events.get(j));
                        }

                        //Insert new CollisionEvents for j
                        for (int i = 0; i < boundaries.length; i++) {
                            events.get(i).reset(Collision.findCollisionTime(particles[minPP.j()],particles[i]),minPP.j(),i); // take unused CollisionEvent
                            BoundaryHeap.insert(events.get(i));
                        }
                    } catch (HeapException e) {
                        e.printStackTrace();
                    } catch (TimeException e) {
                        continue;
                    }
                    DeltaT += minPB.t();
                }
            }

            //project forward particles
            for (int i = 0; i < particles.length; i++) {
                Collision.projectForwardParticle(particles[i],timeStep);
            }

            //project forward boundaries
            for (int i = 0; i < boundaries.length; i++) {
                Collision.projectForwardBoundary(boundaries[i],timeStep);
            }

            tree.buildTree(tree.root);
            time += timeStep;
            DeltaT = 0;
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
