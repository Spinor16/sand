package main;

import calc.Collision;
import data_structures.*;
import exceptions.HeapException;
import utils.Drawing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main extends JPanel{

    static BinaryTree tree;
    static Particle[] particles;
    static Boundary[] boundaries;
    static boolean paint = false;

    public static void main(String[] args) {


        Main main = new Main();
        JFrame top = new JFrame("Particles doing Stuff");
        top.setBounds(0, 0, 700, 700);
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.getContentPane().add(main);
        top.setVisible(true);

        main.run(0.001,200);

    }



    public void run(double timeStep, double endTime) {

        //Settings
//        int nParticles = 50;
//        int nBoundaries = 2;
        int nNearestNeighbours = 30;
        double movieTime = 0;
        double movieTimeStep = 0.1;

        InitialConditions init = new InitialConditions(30,1,1,Math.PI/2);
        particles = init.getParticles();
        boundaries = init.getBoundaries();
        tree = new BinaryTree(particles);


        //Initializations of temp vars
        double time = 0;
        double DeltaT = 0;
        CollisionEvent minPP;
        CollisionEvent minPB;
        double collisionTime;
        double tMinPP;
        double tMinPB;
        ArrayList<CollisionEvent> events = new ArrayList<>(); //temporary storing of CollisionEvents

        while(time<endTime){

            SymmetricCollisionHeap heapPP = new SymmetricCollisionHeap(particles.length);
            CollisionHeap heapPB = new CollisionHeap(particles.length,boundaries.length);


            //Look for nearestNeighbours
            int[][] nearestNeighbours = new int[particles.length][nNearestNeighbours];
            for (Particle particle  : particles) {
                nearestNeighbours[particle.index] = tree.getIndiceskNearestNeighbours(particle.position,nNearestNeighbours);
            }


            //Add CollisionTimes particle - particle
            for (Particle particle : particles) {
                for (int j = 0; j < nNearestNeighbours; j++) {
                    int NNj = nearestNeighbours[particle.index][j]; // particle index of current nearest neighbour
                    if (particle.index < NNj) {
                        collisionTime = Collision.findCollisionTime(particle, particles[NNj]);
                        if (collisionTime > 0) {
                            try {
                                heapPP.insert(
                                        new CollisionEvent(
                                                collisionTime,
                                                particle.index,
                                                NNj
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
            for (Particle particle : particles) {
                for (int j = 0; j < boundaries.length; j++) {
                    collisionTime = Collision.findCollisionTime(particle, boundaries[j]);
                    if (collisionTime > 0) {
                        try {
                            heapPB.insert(
                                    new CollisionEvent(
                                            collisionTime,
                                            particle.index, // assertion fails
                                            j
                                    )
                            );
                        } catch (HeapException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }



            paint = false;
            while(DeltaT < timeStep){

                tMinPP = Double.POSITIVE_INFINITY;
                tMinPB = Double.POSITIVE_INFINITY;

                if (!heapPP.isEmpty()) {
                    tMinPP = heapPP.min().t();
                }
                if (!heapPB.isEmpty()) {
                    tMinPB = heapPB.min().t();
                }

                if (tMinPP < tMinPB){
                    try {
                        minPP = heapPP.removeMin();

                        Collision.resolveCollision(particles[minPP.i()],particles[minPP.j()],minPP.t());

                        //Events involving particle i or j are removed from heap
                        //and stored in array events for further use after reset
                        heapPP.removeEventsContainingIndexSE(minPP.i(), events);
                        heapPP.removeEventsContainingIndexSE(minPP.j(), events);

                        //Events involving particle i are removed from heap
                        //and stored in array events for further use after reset
                        heapPB.removeEventsInRowSE(minPP.i(),events);
                        heapPB.removeEventsInRowSE(minPP.j(),events);

                        //Insert new CollisionEvents for i, particle-particle
                        updatePP(minPP.i(), minPP, heapPP, nearestNeighbours[minPP.i()], events);

                        //Insert new CollisionEvents for j, particle-particle
                        updatePP(minPP.j(), minPP, heapPP, nearestNeighbours[minPP.j()], events);

                        //Insert new CollisionEvents for i, particle-boundary
                        updatePB(minPP.i(), minPP, heapPB, events);

                        //Insert new CollisionEvents for j, particle-boundary
                        updatePB(minPP.j(), minPP, heapPB, events);

                        //save for later
                        events.add(minPP);

                    } catch (HeapException e) {
                        e.printStackTrace();
                    }

                    DeltaT = tMinPP;
                }
                else if (tMinPP > tMinPB){
                    try {
                        minPB = heapPB.removeMin();

                        Collision.resolveCollision(particles[minPB.i()], boundaries[minPB.j()], minPB.t());

                        //Events involving particle i or j are removed from heap
                        //and stored in array events for further use after reset
                        heapPP.removeEventsContainingIndexSE(minPB.i(), events);

                        //Events involving particle i are removed from heap
                        //and stored in array events for further use after reset
                        //Other events invloving boundary are still valid as boundary
                        //is not changed
                        heapPB.removeEventsInRowSE(minPB.i(),events);


                        //Insert new CollisionEvents for i, particle-particle
                        updatePP(minPB.i(), minPB, heapPP, nearestNeighbours[minPB.i()], events);

                        //Insert new CollisionEvents for i, particle-boundary
                        updatePB(minPB.i(), minPB, heapPB, events);

                        //save for later
                        events.add(minPB);

                    } catch (HeapException e) {
                        e.printStackTrace();
                    }

                    DeltaT = tMinPB;
                }
            }

            //project forward particles
            for (int i = 0; i < particles.length; i++) {
                Collision.projectForwardParticle(particles[i],DeltaT);
            }

            //project forward boundaries
            for (int i = 0; i < boundaries.length; i++) {
                Collision.projectForwardBoundary(boundaries[i],DeltaT);
            }

            tree.buildTree(tree.root);
            time += DeltaT;
            DeltaT = 0;
            //paint = time - movieTime > movieTimeStep;
            paint = true;
            if (paint) {
                repaint();
                movieTime = time;

                try {
                    //wait after every calculation to slow motion down
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }



        }
    }

    private void updatePP(int pUpdateIndex, CollisionEvent minEvent, SymmetricCollisionHeap heap,
                          int[] nearestNeighbours, ArrayList<CollisionEvent> events)
                                            throws HeapException {

        for (int index : nearestNeighbours) {
            double collisionTime = 0;

            int min = Math.min(pUpdateIndex,index);
            int max = Math.max(pUpdateIndex,index);

            //exclude possibility of having same collision twice
            if (min == minEvent.i() && max == minEvent.j()){
                return;
            }

            collisionTime = Collision.findCollisionTime(particles[min], particles[max]);

            //check if the collision happens after minTime, i.e. if *real* collision
            //implicitly check whether time is positive
            if (collisionTime > minEvent.t()) {
                resetAndInsert(min, max, heap, events, collisionTime);
            }
        }
    }

    private void updatePB(int pUpdateIndex, CollisionEvent minEvent, CollisionHeap heap,
                          ArrayList<CollisionEvent> events) throws HeapException {

        for (int boundaryIndex = 0; boundaryIndex < boundaries.length; boundaryIndex++) {
            double collisionTime = 0;

            //exclude possibility of having same collision twice
            if (pUpdateIndex == minEvent.i() && boundaryIndex == minEvent.j()){
                return;
            }
            collisionTime = Collision.findCollisionTime(particles[pUpdateIndex], boundaries[boundaryIndex]);

            //check if the collision happens after minTime, i.e. if *real* collision
            //implicitly check whether time is positive
            if (collisionTime > minEvent.t()) {
                resetAndInsert(pUpdateIndex, boundaryIndex, heap, events, collisionTime);
            }
        }
    }

    private void resetAndInsert(int i,  int j, CollisionHeap heap,
                                ArrayList<CollisionEvent> events, double collisionTime) throws HeapException {


        if (events.size() > 0) {
            events.get(0).reset(
                    collisionTime,
                    i,
                    j
            );

            heap.insert(events.get(0));
            events.remove(0);
        }
        else if (events.size() == 0) {
            heap.insert(
                    new CollisionEvent(
                            collisionTime,
                            i,
                            j
                    )
            );
        }
    }


    public void paint(Graphics g) {
        Rectangle bounds = getBounds();

        if (paint) {
            double scale = Drawing.scale(bounds, tree.posMin(), tree.posMax());
            // Clear window and draw background.
            g.setColor(Color.WHITE);
            paintComponent(g);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);


            //Draw Particles
            //g.setColor(Color.BLACK);
            for (Particle particle : particles) {
                particle.paint2D(g,scale,7, bounds.height, particles.length);
            }

            //Draw Boundaries
            g.setColor(Color.PINK);
            for (Boundary boundary: boundaries){
                boundary.paint2D(g,bounds.width,bounds.height,scale);
            }
        }
    }


}
