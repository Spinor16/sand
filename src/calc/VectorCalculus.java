package calc;

public class VectorCalculus {

    //SE means side effect: the vector passed to the function is altered.

    public static double norm2(double[] vector){

        double ret = 0;
        for (int i = 0; i < vector.length; i++) {
            ret  += Math.pow(vector[i],2);
        }

        return ret;
    }

    public static double norm(double[] vector){
        return Math.sqrt(norm2(vector));
    }

    public static void plusSE(double[] vector, double[] vector2){
        for (int i = 0; i < vector.length; i++) {
            vector[i] += vector2[i];

        }
    }

    public static double[] plus(double[] result, double[] vector, double[] vector2){
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] + vector2[i];

        }
        return result;
    }

    public static double[] plus(double[] vector, double[] vector2){
        double[] ret = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            ret[i] = vector[i] + vector2[i];

        }
        return ret;
    }


    public static void minusSE(double[] vector, double[] vector2){
        for (int i = 0; i < vector.length; i++) {
            vector[i] -= vector2[i];

        }
    }

    public static double[] minus(double[] result, double[] vector, double[] vector2){
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] - vector2[i];

        }
        return result;
    }


    public static double[] minus(double[] vector, double[] vector2){
        double[] ret = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            ret[i] = vector[i] - vector2[i];

        }
        return ret;
    }

    public static double[] LMultMatrix(double[] vector, double[][] matrix){
        double[] ret = new double[vector.length];
        for (int i = 0; i < matrix.length; i++) {
            double entry = 0;
            for (int j = 0; j < matrix.length; j++) {
                entry += matrix[i][j]*vector[j];
            }
            ret[i] = entry;
        }
        return ret;
    }

    public static double[] rotate(double alpha, double[] vector){
        double[][] rotMatrix = new double[][]{{Math.cos(alpha), Math.sin(alpha)},
                                              {-Math.sin(alpha), Math.cos(alpha)}};

        return LMultMatrix(vector,rotMatrix);
    }


    public static void multSE(double b, double[] vector){
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= b;
        }
    }

    public static double[] mult(double[] result, double b, double[] vector){
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] * b;

        }
        return result;
    }

    public static double[] mult(double b, double[] vector){
        double[] ret = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            ret[i] = vector[i] * b;
        }
        return ret;
    }

    public static void divideSE(double b, double[] vector){
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= b;
        }
    }

    public static double[] divide(double[] result, double b, double[] vector){
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] / b;

        }
        return result;
    }

    public static double[] divide(double b, double[] vector){
        double[] ret = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            ret[i] = vector[i] / b;
        }
        return ret;
    }

    public void elementPowerSE(double[] vector, int power){
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.pow(vector[i],power);

        }
    }

    public static double[] elementPower(double[] vector, int power){
        double[] ret = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            ret[i] = Math.pow(vector[i],power);

        }
        return ret;
    }

    public static double dot(double[] vector, double[] vector2){
        double ret = 0;
        for (int i = 0; i < vector.length; i++) {
            ret += vector[i]*vector2[i];

        }
        return ret;
    }

    // Works just in 2dim!!
    public static double[] orthogonal(double[] vector){
        double[] ret = new double[vector.length];
        ret[0] = vector[1];
        ret[1] = -vector[0];

        return ret;
    }

    public static double sqrt(double a, double b, double c) {

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0){
            return -1;
        }
        double q = -(b + Math.copySign(Math.sqrt(discriminant),c));

        double collisionTime = q / 2 / a;
        double collisionTime2 = c / q;
        double min = Math.min(collisionTime, collisionTime2);

        if(min>0){
            return min;
        }
        else{
            return Math.max(collisionTime, collisionTime2);
        }
    }

}
