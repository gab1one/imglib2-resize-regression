
package org.knime.knip;

import io.scif.SCIFIO;

import java.util.concurrent.Future;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandModule;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.ui.UserInterface;

/**
 * This example illustrates how to create an ImageJ {@link Command} plugin.
 * <p>
 * The code here is a simple Gaussian blur using ImageJ Ops.
 * </p>
 * <p>
 * You should replace the parameter fields with your own inputs and outputs, and
 * replace the {@link run} method implementation with your own logic.
 * </p>
 */
@Plugin(type = Command.class, menuPath = "Plugins>Resize")
public class Resize<T extends RealType<T>> implements Command {
	//
	// Feel free to add more parameters here...
	//

	@Parameter
	private Dataset currentData;

	@Parameter
	private UIService uiService;

	@Parameter
	private OpService opService;

	@Parameter(type = ItemIO.OUTPUT)
	private Img<T> out;

	@Override
	public void run() {
		final Img<T> inImg = (Img<T>) currentData.getImgPlus();

		final double[] scaleFactors = { 0.9090909090909091, 2.0, 10.0,
			6.666666666666667 };
		final Interval resultingInterval = new FinalInterval(20, 20, 20, 20);

		out = ImgView.wrap(Views.interval(Views.raster(RealViews.affineReal(Views
			.interpolate(Views.extendBorder(inImg),
				new NearestNeighborInterpolatorFactory<T>()), new Scale(scaleFactors))),
			resultingInterval), inImg.factory());
	}

	/**
	 * This main function serves for development purposes. It allows you to run
	 * the plugin immediately out of your integrated development environment
	 * (IDE).
	 *
	 * @param args whatever, it's ignored
	 * @throws Exception
	 */
	public static void main(final String... args) throws Exception {
		// create the ImageJ application context with all available services
		final ImageJ ij = new ImageJ();

		// ask the user for a file to open
		final SCIFIO scifio = ij.scifio();
		// load the dataset
		final Dataset dataset = scifio.datasetIO().open("res/original.ome.tif");

		// show the image
		ij.ui().show(dataset);

		// invoke the plugin
		final Future<CommandModule> commandRun = ij.command().run(Resize.class,
			true);
		final Img resized = (Img) commandRun.get().getOutput("out");

		final Img newResizerResult = scifio.datasetIO().open(
			"res/resizedNew.ome.tif").getImgPlus().getImg();
		final Img diffImgNew = (Img) ij.op().run(Ops.Math.Subtract.class, resized,
			newResizerResult);
		final double sumnew = ij.op().stats().sum(diffImgNew).getRealDouble();

		if (sumnew == 0.0) {
			System.out.println("REGRESSION: no difference to new resizer result");
		}
		else {
			System.out.println("difference to new resizer result: " + sumnew);
		}

		final Img oldResizerResult = scifio.datasetIO().open(
			"res/resizedOld.ome.tif").getImgPlus().getImg();
		final Img diffImgOld = (Img) ij.op().run(Ops.Math.Subtract.class, resized,
			oldResizerResult);
		final double sumOld = ij.op().stats().sum(diffImgOld).getRealDouble();

		if (sumOld == 0.0) {
			System.out.println("no difference to old resizer result");
		}
		else {
			System.out.println("REGRESSION: difference to old resizer result: " +
				sumOld);
		}

		System.exit(0);

	}

}
