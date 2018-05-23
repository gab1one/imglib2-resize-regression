
package org.knime.knip;

import io.scif.SCIFIO;

import java.util.Arrays;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.Ops;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.type.numeric.RealType;
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

		boolean newFailed = checkImages(resized, newResizerResult);

		final Img oldResizerResult = scifio.datasetIO().open(
			"res/resizedOld.ome.tif").getImgPlus().getImg();

		boolean oldFailed = checkImages(resized, oldResizerResult);

		if (newFailed) {
			System.out.println("new failed");
		}
		if (oldFailed) {
			System.out.println("old failed");
		}

	}

	private static boolean checkImages(final Img resized,
		final Img resizerResult)
	{
		final Cursor<RealType> resizedCursor = resized.localizingCursor();
		final RandomAccess<RealType> resizerRa = resizerResult.randomAccess();

		boolean failed = false;

		final int[] pos = new int[4];
		while (resizedCursor.hasNext()) {
			final RealType resPixel = resizedCursor.next();
			resizedCursor.localize(pos);
			resizerRa.setPosition(pos);
			final RealType newPixel = resizerRa.get();
			if (newPixel.getRealDouble() != resPixel.getRealDouble()) {
				failed = true;
//				System.out.println("Pixel values not identical at position " + Arrays
//					.toString(pos));
			}
		}
		return failed;
	}

}
