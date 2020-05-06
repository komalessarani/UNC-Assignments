package a3tests;

import a3.*;

import static org.junit.Test.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import org.junit.Test;

public class A3JediTests {

	
	
	@Test
	public void testGamma() {
		PixelTransformation gamma200_xform = new GammaCorrect(2.0);
		PixelTransformation gamma1000_xform = new GammaCorrect(10.0);
		PixelTransformation gamma_identity = new GammaCorrect(1.0);
		PixelTransformation gamma050_xform = new GammaCorrect(0.5);
		PixelTransformation gamma010_xform = new GammaCorrect(0.1);

		Pixel white = Pixel.WHITE;
		Pixel black = Pixel.BLACK;
		Pixel med_gray = new GrayPixel(0.5);
		Pixel dark_chroma = new ColorPixel(0.2, 0.3, 0.25);
		Pixel light_chroma = new ColorPixel(0.6, 0.7, 1.0);

		assertTrue(check_for_white(gamma200_xform.transform(white)));
		assertTrue(check_for_white(gamma1000_xform.transform(white)));
		assertTrue(check_for_white(gamma_identity.transform(white)));
		assertTrue(check_for_white(gamma050_xform.transform(white)));
		assertTrue(check_for_white(gamma010_xform.transform(white)));

		assertTrue(check_for_black(gamma200_xform.transform(black)));
		assertTrue(check_for_black(gamma1000_xform.transform(black)));
		assertTrue(check_for_black(gamma_identity.transform(black)));
		assertTrue(check_for_black(gamma050_xform.transform(black)));
		assertTrue(check_for_black(gamma010_xform.transform(black)));

		assertTrue(check_for_component_equality(white, gamma_identity.transform(white)));
		assertTrue(check_for_component_equality(black, gamma_identity.transform(black)));
		assertTrue(check_for_component_equality(med_gray, gamma_identity.transform(med_gray)));
		assertTrue(check_for_component_equality(dark_chroma, gamma_identity.transform(dark_chroma)));
		assertTrue(check_for_component_equality(light_chroma, gamma_identity.transform(light_chroma)));

		double[] gamma_vals = new double[] {2.0, 10.0, 0.5, 0.1};
		PixelTransformation[] gamma_xforms = new PixelTransformation[] {gamma200_xform, gamma1000_xform, gamma050_xform, gamma010_xform};
		
		for (Pixel p : new Pixel[] {med_gray, dark_chroma, light_chroma}) {
			for (int i=0; i< gamma_vals.length; i++) {
				Pixel gamma_corrected = new ColorPixel(
						Math.pow(p.getRed(), 1.0/gamma_vals[i]),
						Math.pow(p.getGreen(), 1.0/gamma_vals[i]),
						Math.pow(p.getBlue(), 1.0/gamma_vals[i]));
				
				assertTrue(check_for_component_equality(gamma_corrected, gamma_xforms[i].transform(p)));
			}
		}
	}
	
	@Test
	public void testThreshold() {
		PixelTransformation thresh000_xform = new Threshold(0.00);
		PixelTransformation thresh025_xform = new Threshold(0.25);
		PixelTransformation thresh050_xform = new Threshold(0.50);
		PixelTransformation thresh075_xform = new Threshold(0.75);
		PixelTransformation thresh100_xform = new Threshold(1.00);
		
		Pixel white = Pixel.WHITE;
		Pixel black = Pixel.BLACK;
		Pixel med_gray = new GrayPixel(0.5);
		Pixel dark_chroma = new ColorPixel(0.2, 0.3, 0.25);
		Pixel light_chroma = new ColorPixel(0.6, 0.7, 1.0);
		
		assertTrue(check_for_white(thresh000_xform.transform(white)));
		assertTrue(check_for_black(thresh000_xform.transform(black)));
		assertTrue(check_for_white(thresh000_xform.transform(med_gray)));
		assertTrue(check_for_white(thresh000_xform.transform(dark_chroma)));
		assertTrue(check_for_white(thresh000_xform.transform(light_chroma)));
		
		assertTrue(check_for_white(thresh025_xform.transform(white)));
		assertTrue(check_for_black(thresh025_xform.transform(black)));
		assertTrue(check_for_white(thresh025_xform.transform(med_gray)));
		assertTrue(check_for_white(thresh025_xform.transform(dark_chroma)));
		assertTrue(check_for_white(thresh025_xform.transform(light_chroma)));

		assertTrue(check_for_white(thresh050_xform.transform(white)));
		assertTrue(check_for_black(thresh050_xform.transform(black)));
		assertTrue(check_for_black(thresh050_xform.transform(med_gray)));
		assertTrue(check_for_black(thresh050_xform.transform(dark_chroma)));
		assertTrue(check_for_white(thresh050_xform.transform(light_chroma)));

		assertTrue(check_for_white(thresh075_xform.transform(white)));
		assertTrue(check_for_black(thresh075_xform.transform(black)));
		assertTrue(check_for_black(thresh075_xform.transform(med_gray)));
		assertTrue(check_for_black(thresh075_xform.transform(dark_chroma)));
		assertTrue(check_for_black(thresh075_xform.transform(light_chroma)));

		assertTrue(check_for_black(thresh100_xform.transform(white)));
		assertTrue(check_for_black(thresh100_xform.transform(black)));
		assertTrue(check_for_black(thresh100_xform.transform(med_gray)));
		assertTrue(check_for_black(thresh100_xform.transform(dark_chroma)));
		assertTrue(check_for_black(thresh100_xform.transform(light_chroma)));

	}
	
	@Test
	public void testTransformedPicture() {
		Picture source = new MonochromePicture(5, 5, new GrayPixel(0.25));
		source = source.paint(0, 0, 1, 1, new GrayPixel(0.75));
				
		PixelTransformation thresh050 = new Threshold(0.5);
		
		Picture xformed_picture = new TransformedPicture(source, thresh050);
		
		assertEquals(source.getWidth(), xformed_picture.getWidth());
		assertEquals(source.getHeight(), xformed_picture.getHeight());
		
		for (int x=0; x<source.getWidth(); x++) {
			for (int y=0; y<source.getHeight(); y++) {
				if (x < 2 && y < 2) {
					assertTrue(check_for_white(xformed_picture.getPixel(x, y)));
				} else {
					assertTrue(check_for_black(xformed_picture.getPixel(x, y)));
				}
			}
		}

		
		Pixel pixel_a = new ColorPixel(0.75, 0.5, 0.25);
		Pixel pixel_b = new ColorPixel(0.2, 0.4, 0.8);
		
		source = new MonochromePicture(5, 5, pixel_a);
		source = source.paint(0, 0, 1, 1, pixel_b);
		
		PixelTransformation gamma050 = new GammaCorrect(0.50);
		xformed_picture = new TransformedPicture(source, gamma050);
		
		assertEquals(source.getWidth(), xformed_picture.getWidth());
		assertEquals(source.getHeight(), xformed_picture.getHeight());

		Pixel corrected_a = gamma050.transform(pixel_a);
		Pixel corrected_b = gamma050.transform(pixel_b);
		
		for (int x=0; x<source.getWidth(); x++) {
			for (int y=0; y<source.getHeight(); y++) {
				if (x < 2 && y < 2) {
					assertTrue(check_for_component_equality(corrected_b, xformed_picture.getPixel(x, y)));
				} else {
					assertTrue(check_for_component_equality(corrected_a, xformed_picture.getPixel(x, y)));
				}
			}
		}		
	}
	
	private static boolean check_for_white(Pixel p) {
		assertEquals(p.getRed(), 1.0, 0.001);
		assertEquals(p.getGreen(), 1.0, 0.001);
		assertEquals(p.getBlue(), 1.0, 0.001);
		return true;
	}
	
	private static boolean check_for_black(Pixel p) {
		assertEquals(p.getRed(), 0.0, 0.001);
		assertEquals(p.getGreen(), 0.0, 0.001);
		assertEquals(p.getBlue(), 0.0, 0.001);
		return true;
	}
	
	private static boolean check_for_component_equality(Pixel a, Pixel b) {
		assertEquals(a.getRed(), b.getRed(), 0.001);
		assertEquals(a.getGreen(), b.getGreen(), 0.001);
		assertEquals(a.getBlue(), b.getBlue(), 0.001);

		return true;
	}
}