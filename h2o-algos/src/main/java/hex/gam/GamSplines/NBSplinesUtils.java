package hex.gam.GamSplines;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NBSplinesUtils {
    
    public static List<Double> fillKnots(double[] knots, int m, boolean forISpline) {
        List<Double> knotsNew = new ArrayList<>();
        if (forISpline) // I-spline, looking at NBSplineTypeII with order m=k+1, integration looking at Mi,k(t)
            m = m-1;
        int upperBound = m-1;
        for (int index=0; index < upperBound; index++)    // m lower knots, all equal value
            knotsNew.add(knots[0]);
        int knotLen = knots.length;
        for (int index=0; index < knotLen; index++) // N-2 interior knots
            knotsNew.add(knots[index]);
        double upperVal = knots[knots.length-1];
        for (int index=0; index < upperBound; index++)
            knotsNew.add(upperVal);
        assert knotsNew.size()==knots.length+2*m-2:"knots length is incorrect";
        return knotsNew;
    }

    /**
     * This method is used to extract the knots over which a basis function is supposed to be non-zero.
     */
    public static List<Double> extractKnots(int index, int order, List<Double> knots) {
        List<Double> newKnots = new ArrayList<>();
        int upperIndex = Math.min(index+order, knots.size()-1);
        int[] validIndices = IntStream.rangeClosed(index, upperIndex).toArray();
        for (int ind : validIndices)
            newKnots.add(knots.get(ind));
        return newKnots;
    }

    static double[] formNumerator(int index, int order, List<Double> knots) {
        double[] numerator = new double[2];
        if (order == 1) {
            numerator[0] = 1;
            numerator[1] = 1;
        } else {
            if (index >= knots.size())
                return numerator;
            numerator[0] = knots.get(index);
            if ((index+order) >= knots.size())
                return numerator;
            numerator[1] = knots.get(index+order);
        }
        return numerator;
    }
    
    static double[] formDenominator(int index, int order, List<Double> knots) {
        double[] oneOverDenominator = new double[2];
        if (order == 1) {
            oneOverDenominator[0] = 1;
            oneOverDenominator[1] = 1;
        } else {
            if (index >= knots.size() || (index+order-1) >= knots.size())
                return oneOverDenominator;
            double tempDenom = knots.get(index+order-1)-knots.get(index);
            oneOverDenominator[0] = tempDenom==0 
                    ? 0 : 1.0/tempDenom;
            if ((index+order) >= knots.size() || (index+1) >= knots.size())
                return oneOverDenominator;
            tempDenom = knots.get(index+order)-knots.get(index+1);
            oneOverDenominator[1] = tempDenom==0
                    ? 0 : 1.0/tempDenom;
        }
        return oneOverDenominator;
    }
}
