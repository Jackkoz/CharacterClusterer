import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class SumMinDistance implements DistanceMeasure
{
    public SumMinDistance(){}

    @Override
    public double compute(double[] doubles, double[] doubles2)
    {
        double[] set1, set2;
        if (doubles.length > doubles2.length)
        {
            set1 = doubles;
            set2 = doubles2;
        }
        else
        {
            set1 = doubles2;
            set2 = doubles;
        }

        int i = 0;
        double distance = 0;
        while (i < set1.length)
        {
            double x = set1[i];
            double y = set1[i + 1];

            int j = 0;
            double minDistance = Double.MAX_VALUE;
            while (j < set2.length)
            {
                double x2 = set2[j];
                double y2 = set2[j + 1];
                minDistance = Math.min(minDistance, Math.pow(x - x2, 2) + Math.pow(y - y2, 2));

                j += 2;
            }
            distance += Math.sqrt(minDistance);

            i += 2;
        }
//        distance /= (set1.length / 2);
//        System.out.println("Distance: " + distance);
        return distance;
    }
}
