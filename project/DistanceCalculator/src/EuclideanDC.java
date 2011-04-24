

  public class EuclideanDC implements IDistanceCalculator {
    public double distance(Double[] left, Double[] right) {
      assert left.length == right.length;

      double sumOfPowers = 0;

      for(int i = 0; i < left.length; ++i) {
        sumOfPowers += Math.pow(left[i] - right[i], 2);
      }

      return Math.sqrt(sumOfPowers);
    }
  }

