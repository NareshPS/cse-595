import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.Map.Entry;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.fs.*;

class ImageDistance implements Comparable<ImageDistance> {
	public String url;
	public double distance;

	public ImageDistance(String url, double distance) {
		this.url = url;
		this.distance = distance;
	}

	@Override
	public int compareTo(ImageDistance o) {
		// TODO Auto-generated method stub
		return (int) (distance - o.distance);
	}

}

public class DistanceCalculatorMR {

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		GoogleGists gg = null;
		Configuration conf = null;
		public IDistanceCalculator[] dcs = null;

		@Override
		public void setup(Context context) {
			conf = context.getConfiguration();
			dcs = new IDistanceCalculator[] { new EuclideanDC(), new CosineDC() };
			System.out.println(((FileSplit) context.getInputSplit()).getPath()
					.getName());
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			if (null == gg) {
				gg = new GoogleGists(FileSystem.get(conf), new Path(
						conf.get("googleImages.dir")));
			}

			String line = value.toString();
			String data = line.substring(line.indexOf("\t") + 1);
			String image = line.substring(0, line.indexOf("\t"));
			String[] numbers = data.split(",");

			ArrayList<Double> vector = new ArrayList<Double>();

			for (String number : numbers) {
				number = number.trim();

				if (number.length() > 0) {
					vector.add(Double.parseDouble(number));
				}
			}

			Double[] arrayVector = vector.toArray(new Double[] {});

			Set<String> categories = gg.googleGistsByCategory.keySet();

			// now we loop through each category, and for every canonical vector
			// in that category, we output the sum of all distances in that
			// category

			for (String category : categories) {
				List<Double[]> canonicalVectors = gg.googleGistsByCategory
						.get(category);

				double[] sumDistances = new double[dcs.length];

				for (int i = 0; i < canonicalVectors.size(); ++i) {
					for (int dci = 0; dci < dcs.length; ++dci) {
						sumDistances[dci] += dcs[dci].distance(
								canonicalVectors.get(i), arrayVector);
					}
				}

				StringBuffer sumDistancesString = new StringBuffer();

				for (Double distance : sumDistances) {
					sumDistancesString.append(String.valueOf(distance) + " ");
				}

				context.write(new Text(category), new Text(image + "|"
						+ sumDistancesString.toString().trim()));
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		int LIMIT = 3000;

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) {
			List<ImageDistance> imageDistances = new ArrayList<ImageDistance>();

			System.out.println("got " + key);
			
			try {
				for (Text text : values) {
					
					String[] parts = text.toString().split("\\|");
					String url = parts[0];
					System.out.println("url " + url);
					System.out.println("data " + parts[1]);
					double distance = Double
							.parseDouble(parts[1].split(" ")[0]);

					imageDistances.add(new ImageDistance(url, distance));
				}

				Collections.sort(imageDistances);
				
				System.out.println("got " + imageDistances.size() + " values for " + key);

				for (int i = 0; i < LIMIT && i < imageDistances.size(); ++i) {
					context.write(new Text(key), new Text(
							imageDistances.get(i).url));
				}

			} catch (Exception ioe) {
				System.out.println(ioe.toString());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("googleImages.dir", args[1]);

		Job job = new Job(conf, "Distance Calculator");

		job.setJarByClass(DistanceCalculatorMR.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		job.setNumReduceTasks(36);

		job.waitForCompletion(true);
	}
}
