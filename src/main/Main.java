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
//        int nParticles = 50;
//        int nBoundaries = 2;
        int nNearestNeighbours = 10;

        InitialConditions init = new InitialConditions(1,1,Math.PI/2);
        particles = init.getParticles();
        boundaries = init.getBoundaries();
        tree = new BinaryTree(particles);

        //Initializations of temp vars
        double time = 0;
        double DeltaT = 0;
        CollisionEvent minPP;
        CollisionEvent minPB;
        double collisionTime;
        while(time<endTime){
            CollisionHeap heapPP = new CollisionHeap(particles.length);
            CollisionHeap heapPB = new CollisionHeap(boundaries.length);


            //Look for nearestNeighbours
            int[][] nearestNeighbours = new int[particles.length][nNearestNeighbours];
            for (Particle particle  : particles) {
                nearestNeighbours[particle.index] = tree.getIndiceskNearestNeighbours(particle.position,nNearestNeighbours);
            }


            //Add CollisionTimes particle - particle
            for (int i = 0; i < particles.length; i++) {
                for (int j = 0; j < nNearestNeighbours; j++) {
                    if (i < nearestNeighbours[i][j]) {
                        collisionTime = Collision.findCollisionTime(particles[i], particles[nearestNeighbours[i][j]]);
                        if (collisionTime > 0) {
                            try {
                                heapPP.insert(
                                        new CollisionEvent(
                                                collisionTime,
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
            }



            //Add CollisionTimes particle - boundaries
            for (int i = 0; i < particles.length; i++) {
                for (int j = 0; j < boundaries.length; j++) {
                    collisionTime = Collision.findCollisionTime(particles[i], boundaries[j]);
                    if (collisionTime > 0) {
                        try {
                            heapPB.insert(
                                    new CollisionEvent(
                                            collisionTime,
                                            i,
                                            j
                                    )
                            );
                        } catch (HeapException e) {
                            e.printStackTrace();
                        }
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
                        resetInsertCollisionEvents(minPP.i(), minPP, heapPP, nearestNeighbours[minPP.i()], events, particles);

                        //Insert new CollisionEvents for j
                        resetInsertCollisionEvents(minPP.j(), minPP, heapPP, nearestNeighbours[minPP.i()], events, particles);

                    } catch (HeapException e) {
                        e.printStackTrace();
                    }

                    DeltaT = minPP.t();
                }
                else {
                    try {
                        minPB = heapPB.removeMin();
                        Collision.resolveCollision(particles[minPB.i()], boundaries[minPB.j()], minPB.t());

                        //Events involving particle i are removed from heap
                        //and stored in array events for further use after reset
                        //Events involving boundary aren't updated as boundary
                        //isn't affected from the collision
                        heapPB.removeEventsContainingIndexSE(minPB.i(),events);

                        //Insert new CollisionEvents for i
                        resetInsertCollisionEvents(minPP.i(), minPP, heapPP, nearestNeighbours[minPP.i()], events, boundaries);

                    } catch (HeapException e) {
                        e.printStackTrace();
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
        time += timeStep;
    }

    private void resetInsertCollisionEvents(int resetIndex, CollisionEvent minEvent, CollisionHeap heapPP,
                                            int[] nearestNeighbour, ArrayList<CollisionEvent> events,
                                            CollisionPartner[] collisionPartners)
                                            throws HeapException {
        for (int index : nearestNeighbour) {
            // take unused CollisionEvent
            int min = Math.min(resetIndex,index);
            int max = Math.max(resetIndex,index);
            double collisionTime = 0;

            if (collisionPartners instanceof Particle[]){
                collisionTime = Collision.findCollisionTime(particles[min], (Particle) collisionPartners[max]);
            }
            else if (collisionPartners instanceof Boundary[]){
                collisionTime = Collision.findCollisionTime(particles[min], (Boundary) collisionPartners[max]);
            }

            if (collisionTime > minEvent.t()) {
                events.get(0).reset(
                        collisionTime,
                        min,
                        max
                );

                heapPP.insert(events.get(0));
            }
            events.remove(0);
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
