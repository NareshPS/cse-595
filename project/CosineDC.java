
  public class CosineDC implements IDistanceCalculator {
    public double distance(Double[] left, Double[] right) {
      assert left.length == right.length;

      double numerator = 0;
      double modLeftSq = 0.0, modRightSq = 0.0;

      for(int i =0 ; i < left.length; ++i) {
        numerator += left[i] * right[i];
        modLeftSq += Math.pow(left[i], 2);
        modRightSq += Math.pow(right[i], 2);
      }

      return numerator / (Math.sqrt(modLeftSq) * Math.sqrt(modRightSq));
    }
  }

