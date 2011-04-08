import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class GistCalculator {

  private static final int IMG_HEIGHT = 480;
  private static final int IMG_WIDTH = 480;

  private class PixelValue {
    public int r;
    public int g;
    public int b;

    public PixelValue(int r, int g, int b) {
      super();
      this.r = r;
      this.g = g;
      this.b = b;
    }
  }

  public native float[] calculateGist(float[] red, float[] green,
      float[] blue, int width, int height);

  static {
    System.loadLibrary("gist");
  }

  private List<PixelValue> getPixels(BufferedImage image) throws IOException {
    List<PixelValue> pixels = new ArrayList<GistCalculator.PixelValue>();
    for (int i = 0; i < image.getWidth(); ++i) {
      for (int j = 0; j < image.getHeight(); ++j) {
        int rgb = image.getRGB(i, j);
        int red = (rgb & 0x00ff0000) >> 16;
        int green = (rgb & 0x0000ff00) >> 8;
        int blue = rgb & 0x000000ff;
        pixels.add(new PixelValue(red, green, blue));
      }
    }
    return pixels;
  }

  private static BufferedImage resizeImage(BufferedImage originalImage, int type){
    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
    g.dispose();

    return resizedImage;
  }

  public float[] getGist(InputStream imageStream) throws Exception {
    BufferedImage originalImage = ImageIO.read(new MemoryCacheImageInputStream(imageStream));
    int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
    BufferedImage image = resizeImage(originalImage, type);

    List<PixelValue> pixels = getPixels(image);
    int numPixels = pixels.size();
    float[] red = new float[numPixels];
    float[] green = new float[numPixels];
    float[] blue = new float[numPixels];
    int i = 0;
    for (PixelValue pv : pixels) {
      red[i] = pv.r;
      green[i] = pv.g;
      blue[i] = pv.b;
      ++i;
    }
    float[] gistValues = calculateGist(red, green, blue,
        image.getWidth(), image.getHeight());
    return gistValues;
  }
}
