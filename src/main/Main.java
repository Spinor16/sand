package main;

import calc.Collision;
import calc.VectorCalculus;
import data_structures.*;
import exceptions.HeapException;
import utils.Drawing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main extends JPanel{

    static BinaryTree tree;
    public static Particle[] particles;
    public static Boundary[] boundaries;
    static boolean paint = false;

    public static SymmetricCollisionHeap heapPP;
    public static CollisionHeap heapPB;
    public static void main(String[] args) {


        Main main = new Main();
        JFrame top = new JFrame("Particles doing Stuff");
        top.setBounds(0, 0, 700, 700);
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.getContentPane().add(main);
        top.setVisible(true);

        main.run(0.002,200);

    }



    public void run(double timeStep, double endTime) {

        //Settings
//        int nParticles = 50;
//        int nBoundaries = 2;
        int nNearestNeighbours = 3;
        double movieTime = 0;
        double movieTimeStep = 0.1;

        InitialConditions init = new InitialConditions(3,1,1,Math.PI/2);
        particles = init.getParticles();
        boundaries = init.getBoundaries();
        tree = new BinaryTree(particles);


        //Initializations of temp vars
        double time = 0;
        CollisionEvent minPP;
        CollisionEvent minPB;
        double collisionTime;
        double tMinPP;
        double tMinPB;
        ArrayList<CollisionEvent> events = new ArrayList<>(); //temporary storing of CollisionEvents

        //Set correct normal vectors. Must point away from particles. This assumes all particles are
        //on the same side of any wall.
        for (Boundary boundary : boundaries) {
            double[] DX = VectorCalculus.minus(boundary.getPosition(), particles[0].getPosition());
            //Set boundary.normal in same direction as DXn
            int sign = (int) Math.signum(VectorCalculus.dot(DX, boundary.getNormal()));
            boundary.switchNormalSignTo(sign);
        }

        while(time<endTime){

            heapPP = new SymmetricCollisionHeap(particles.length);
            heapPB = new CollisionHeap(particles.length, boundaries.length);


            //Look for nearestNeighbours
            int[][] nearestNeighbours = new int[particles.length][nNearestNeighbours];
            for (Particle particle  : particles) {
                nearestNeighbours[particle.getIndex()] = tree.getIndiceskNearestNeighbours(particle.getPosition(),nNearestNeighbours);
            }

//            //check if particles are onBoundary and set accordingly
//            for (Particle particle : particles) {
//                Collision.checkIfOnBoundaries(particle, boundaries);
//            }


            //Add CollisionTimes particle - particle
            for (Particle particle : particles) {
                for (int j = 0; j < nNearestNeighbours; j++) {
                    int NNj = nearestNeighbours[particle.getIndex()][j]; // particle index of current nearest neighbour
                    if (particle.getIndex() < NNj) {
                        collisionTime = Collision.findCollisionTime(particle, particles[NNj]);
                        if (collisionTime >= 0) {
                            try {
                                heapPP.insert(
                                        new CollisionEvent(
                                                collisionTime,
                                                particle.getIndex(),
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
                                            particle.getIndex(), // assertion fails
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
            while(true){

                tMinPP = Double.POSITIVE_INFINITY;
                tMinPB = Double.POSITIVE_INFINITY;

                if (!heapPP.isEmpty()) {
                    tMinPP = heapPP.min().t();
                }

                if (!heapPB.isEmpty()) {
                    tMinPB = heapPB.min().t();
                }

                if (tMinPB > timeStep && tMinPP > timeStep) {
                    break;
                }
                else if (tMinPP < tMinPB){
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
                        updatePP(minPP.i(), minPP, heapPP, nearestNeighbours[minPP.i()], events, true);

                        //Insert new CollisionEvents for j, particle-particle
                        updatePP(minPP.j(), minPP, heapPP, nearestNeighbours[minPP.j()], events, true);

                        //Insert new CollisionEvents for i, particle-boundary
                        updatePB(minPP.i(), minPP, heapPB, events, false);

                        //Insert new CollisionEvents for j, particle-boundary
                        updatePB(minPP.j(), minPP, heapPB, events, false);

                        //save for later
                        events.add(minPP);

                    } catch (HeapException e) {
                        e.printStackTrace();
                    }
                }
                else if (tMinPP > tMinPB){
                    try {
                        minPB = heapPB.removeMin();

                        Collision.resolveCollision(particles[minPB.i()], boundaries[minPB.j()], minPB.t());

                        //Events involving particle i is removed from heap
                        //and stored in array events for further use after reset
                        heapPP.removeEventsContainingIndexSE(minPB.i(), events);

                        //Events involving particle i are removed from heap
                        //and stored in array events for further use after reset
                        //Other events involving boundary are still valid as boundary
                        //is not changed
                        heapPB.removeEventsInRowSE(minPB.i(),events);


                        //Insert new CollisionEvents for i, particle-particle
                        updatePP(minPB.i(), minPB, heapPP, nearestNeighbours[minPB.i()], events, false);

                        //Insert new CollisionEvents for i, particle-boundary
                        updatePB(minPB.i(), minPB, heapPB, events, true);

                        //save for later
                        events.add(minPB);

                    } catch (HeapException e) {
                        e.printStackTrace();
                    }
                }

//                //check if particles are onBoundary and set accordingly
//                for (Particle particle : particles) {
//                    Collision.checkIfOnBoundaries(particle, boundaries);
//                }
            }

            //project forward particles
            for (int i = 0; i < particles.length; i++) {
                particles[i].projectForward(timeStep);
            }

            //project forward boundaries
            for (int i = 0; i < boundaries.length; i++) {
                boundaries[i].projectForward(timeStep);
            }

            tree.buildTree(tree.root);
            time += timeStep;

            paint = true;
            if (paint) {
                repaint();
                try {
                    //wait after every calculation to slow motion down
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }



        }
    }

    private void updatePP(int pUpdateIndex, CollisionEvent minEvent, SymmetricCollisionHeap heap,
                          int[] nearestNeighbours, ArrayList<CollisionEvent> events, boolean isSameCollisionType)
                                            throws HeapException {

        for (int index : nearestNeighbours) {
            double collisionTime = 0;

            int min = Math.min(pUpdateIndex, index);
            int max = Math.max(pUpdateIndex, index);

            //exclude possibility of having same collision twice
            if (isSameCollisionType && min == minEvent.i() && max == minEvent.j()){
                continue;
            }

            collisionTime = Collision.findCollisionTime(particles[min], particles[max]);

            //check if the collision happens after minTime, i.e. if *real* collision
            if (collisionTime >= minEvent.t()) {
                resetAndInsert(min, max, heap, events, collisionTime);
            }
        }
    }

    private void updatePB(int pUpdateIndex, CollisionEvent minEvent, CollisionHeap heap,
                          ArrayList<CollisionEvent> events, boolean isSameCollisionType) throws HeapException {

        for (int boundaryIndex = 0; boundaryIndex < boundaries.length; boundaryIndex++) {
            double collisionTime = 0;

            //exclude possibility of having same collision twice
            if (isSameCollisionType && pUpdateIndex == minEvent.i() && boundaryIndex == minEvent.j()){
                continue;
            }
            collisionTime = Collision.findCollisionTime(particles[pUpdateIndex], boundaries[boundaryIndex]);

            //check if the collision happens after minTime, i.e. if *real* collision
            //implicitly check whether time is positive
            if (collisionTime >= minEvent.t()) {
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
                particle.paint2D(g, scale, bounds.height, particles.length);
            }

            //Draw Boundaries
            g.setColor(Color.PINK);
            for (Boundary boundary: boundaries){
                boundary.paint2D(g,1,1, scale);
            }
        }
    }

}
