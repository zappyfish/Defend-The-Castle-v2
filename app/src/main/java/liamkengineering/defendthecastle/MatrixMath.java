package liamkengineering.defendthecastle;

/**
 * Created by Liam on 3/15/2017.
 */

public class MatrixMath {

    // does what the function name says
    public static float[] multMatrixAndVector(float[][] m, float[] v, int n) {
        float[] result = new float[n];
        for(int i = 0; i<n; ++i) {
            result[i] = 0;
            for(int j = 0; j<n; ++j) {
                result[i] += m[i][j]*v[j];
            }
        }
        return result;
    }
}
