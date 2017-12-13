package main;

import data_structures.InitialConditions;

public interface Settings {

    public InitialConditions getInitialConditions();

    public int getResolution();

    public int getnNearestNeighbours();

    public double getMom_coeff();

    public double getRestitution_velocity();

    public double getTimeStep();

    public double getEndTime();

}
