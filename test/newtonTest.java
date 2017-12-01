import data_structures.CollisionEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import calc.VectorCalculus;

public class newtonTest {

    @Test
    void poly() {
        double[] coeffs = new double[]{5,0,3,2,1};
        double val = VectorCalculus.computePolyHorner(coeffs,4,2);
        assertEquals(val, 97);
    }

    @Test
    void newton() {
        double[] coeffs = new double[]{5,0,3,2,-96};
        double root = VectorCalculus.newtonRootsPoly4(coeffs,-10000,1e-2);
        assert(root-2 < 1e-7);
    }


}
