
package org.knime.knip;

import io.scif.SCIFIO;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.Ops;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.view.Views;

public class Resize {

	/**
	 * Demonstrates Imglib2 regression
	 */
	public static void main(final String... args) throws Exception {

		// create the ImageJ application context with all available services
		final ImageJ ij = new ImageJ();

		// ask the user for a file to open
		final SCIFIO scifio = ij.scifio();
		// load the dataset
		final Dataset dataset = scifio.datasetIO().open("res/original.ome.tif");
		final Img inImg = dataset.getImgPlus().getImg();

		// the resizing
		final double[] scaleFactors = { 0.9090909090909091, 2.0, 10.0,
			6.666666666666667 };
		final Interval resultingInterval = new FinalInterval(20, 20, 20, 20);

		final Img resized = ImgView.wrap(Views.interval(Views.raster(RealViews
			.affineReal(Views.interpolate(Views.extendBorder(inImg),
				new NearestNeighborInterpolatorFactory()), new Scale(scaleFactors))),
			resultingInterval), inImg.factory());

		// testing the result

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

	}

}
