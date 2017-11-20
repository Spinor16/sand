package main;

import calc.Collision;
import data_structures.*;
import exceptions.HeapException;
import exceptions.TimeException;
import utils.Drawing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main extends JPanel{

    static BinaryTree tree;
    static Particle[] particles;
    static Boundary[] boundaries;

    public static void main(String[] args) {


        Main main = new Main();
        JFrame top = new JFrame("Particles doing Stuff");
        top.setBounds(0, 0, 900, 900);
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.getContentPane().add(main);
        top.setVisible(true);

        main.run(0.1,1);

    }



    public void run(double timeStep, double endTime) {

        //Settings
        double time = 0;
//        int nParticles = 50;
//        int nBoundaries = 2;
        int nNearestNeighbours = 10;

        InitialConditions init = new InitialConditions(1,1,1);
        particles = init.getParticles();
        boundaries = init.getBoundaries();
        tree = new BinaryTree(particles);

        //Initializations of temp vars
        double t = 0;
        double DeltaT = 0;
        CollisionEvent minPP;
        CollisionEvent minPB;

        while(time<endTime){
            CollisionHeap heapPP = new CollisionHeap(particles.length);
            CollisionHeap heapPB = new CollisionHeap(boundaries.length);


            //Look for nearestNeighbours
            int[][] nearestNeighbours;
            for (Particle particle  : particles) {
                nearestNeighbours[particle.index] = tree.getIndiceskNearestNeighbours(particle.position,nNearestNeighbours);
            }


            //Add CollisionTimes particle - particle
            for (int i = 0; i < particles.length; i++) {
                for (int j = 0; j < nNearestNeighbours; j++) {
                    if (i < nearestNeighbours[i][j]) {
                        try {
                            heapPP.insert(
                                    new CollisionEvent(
                                            Collision.findCollisionTime(particles[i], particles[nearestNeighbours[i][j]]),
                                            i,
                                            nearestNeighbours[i][j]
                                    )
                            );
                        } catch (HeapException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            //Add CollisionTimes particle - boundaries
            for (int i = 0; i < particles.length; i++) {
                for (int j = 0; j < boundaries.length; j++) {
                    try {
                        heapPB.insert(
                                new CollisionEvent(
                                        Collision.findCollisionTime(particles[i],particles[j]),
                                        i,
                                        j
                                )
                        );
                    } catch (HeapException e) {
                        e.printStackTrace();
                    }
                }
            }

            ArrayList<CollisionEvent> events = new ArrayList<>(); //temporary storing of CollisionEvents

            while(DeltaT < timeStep){

                minPP = heapPP.min();
                minPB = heapPB.min();

                if (minPP.t() < minPB.t()){
                    try {
                        minPP = heapPP.removeMin();
                        Collision.resolveCollision(particles[minPP.i()],particles[minPP.j()],minPP.t());

                        //Events involving particle i or j are removed from heap
                        //and stored in array events for further use after reset
                        heapPP.removeEventsContainingIndexSE(minPP.i(), events);
                        heapPP.removeEventsContainingIndexSE(minPP.j(), events);

                        //Insert new CollisionEvents for i
                        for (int index : nearestNeighbours[minPP.i()]) {
                            // take unused CollisionEvent
                            int min = Math.min(minPP.i(),index);
                            int max = Math.max(minPP.i(),index);
                            double collisionTime = Collision.findCollisionTime(particles[min], particles[max]);

                            if (collisionTime > minPP.t()) {
                                events.get(0).reset(
                                        collisionTime,
                                        min,
                                        max
                                );

                                heapPP.insert(events.get(0));
                            }

                            events.remove(0);
                        }

                        //Insert new CollisionEvents for j
                        for (int index : nearestNeighbours[minPP.i()]) {
                            // take unused CollisionEvent
                            int min = Math.min(minPP.i(),index);
                            int max = Math.max(minPP.i(),index);
                            double collisionTime = Collision.findCollisionTime(particles[min], particles[max]);

                            if (collisionTime > minPP.t()) {
                                events.get(0).reset(
                                        collisionTime,
                                        min,
                                        max
                                );

                                heapPP.insert(events.get(0));
                            }

                            events.remove(0);
                        }

                    } catch (HeapException e) {
                        e.printStackTrace();
                    }

                    DeltaT = minPP.t();
                }
                else {
                    try {
                        minPB = heapPB.removeMin();
                        Collision.resolveCollision(particles[minPP.i()],boundaries[minPP.j()],minPP.t());

                        heapPB.removeEventsContainingIndexSE(minPP.i(),events);
                        heapPB.removeEventsContainingIndexSE(minPP.j(),events);

                        //Insert new CollisionEvents for i
                        for (int j = 0; j < boundaries.length; j++) {
                            events.get(j).reset(Collision.findCollisionTime(particles[minPP.i()],particles[j]),minPP.i(),j); // take unused CollisionEvent
                            heapPB.insert(events.get(j));
                        }

                        //Insert new CollisionEvents for j
                        for (int i = 0; i < boundaries.length; i++) {
                            events.get(i).reset(Collision.findCollisionTime(particles[minPP.j()],particles[i]),minPP.j(),i); // take unused CollisionEvent
                            heapPB.insert(events.get(i));
                        }
                    } catch (HeapException e) {
                        e.printStackTrace();
                    } catch (TimeException e) {
                        continue;
                    }
                    DeltaT = minPB.t();
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


    public void paint(Graphics g) {
        Rectangle bounds = getBounds();

        if (tree!= null && tree.isBuilt()) {
            double scale = Drawing.scale(bounds, tree.posMin(), tree.posMax());

            // Clear window and draw background.
            g.setColor(Color.WHITE);
            paintComponent(g);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);


            //Draw Particles
            g.setColor(Color.BLACK);
            for (Particle particle : particles) {
                particle.paint2D(g,scale,7);
            }

            //Draw Boundaries
            g.setColor(Color.PINK);
            for (Boundary boundary: boundaries){
                boundary.paint2D(g,bounds.width,bounds.height);
            }


            // Draw tree.
//            g.setColor(Color.BLACK);
            //tree.paint(g, scale);

            // Draw circle for ballwalk.
//            g.setColor(Color.BLACK);
//            Rectangle ballwalkCoords = Drawing.transform(0.5, 0.5, 0.1, 0.1,
//                    scale, true);
//            g.drawOval(ballwalkCoords.x, ballwalkCoords.y, ballwalkCoords.width, ballwalkCoords.height);

        }
    }


}
