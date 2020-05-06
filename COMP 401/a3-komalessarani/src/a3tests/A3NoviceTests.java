package a3tests;

import a3.*;

import static org.junit.Test.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import org.junit.Test;

public class A3NoviceTests {
	// Initialize different pixel amounts.
	Pixel red = new ColorPixel(1, 0, 0);
	Pixel green = new ColorPixel(0, 1, 0);
	Pixel blue = new ColorPixel(0, 0, 1);
	Pixel randomColor = new ColorPixel(0.545, 0.65, 0.332);
	Pixel red_blue = new ColorPixel(1, 1, 0);
	Pixel grayscale = new GrayPixel(0.5);

	// Valid Pixel 2d Arrays
	Pixel[][] rgbPicture = { { red, red, red }, { green, green, green }, { blue, blue, blue } };
	Pixel[][] randomPicture = { { blue, randomColor, randomColor, red }, { green, red, green, randomColor } };
	// Invalid Pixel 2d Arrays
	Pixel[][] noWidthPicture = { {}, {} };
	Pixel[][] noHeightPicture = { {} };
	Pixel[][] differentHeightPicture = { { red, blue, green }, { red, green, blue }, { red, green } };
	Pixel[][] includesNullRowsPicture = { { red, red }, null, { blue, blue } };
	Pixel[][] includesNullPixelsPicture = { { randomColor, blue, red, blue }, { green, green, randomColor, blue },
			{ blue, blue, randomColor, null } };

	// Valid Mutable Pixel Array Picture initializations.
	Picture redMutablePicture = new MutablePixelArrayPicture(4, 4, red);
	Picture blueMutablePicture = new MutablePixelArrayPicture(3, 3, blue);
	Picture greenMutablePicture = new MutablePixelArrayPicture(3, 4, green);
	Picture randomColorMutablePicture = new MutablePixelArrayPicture(2, 2, randomColor);
	Picture red_BlueMutablePictureLargeHeight = new MutablePixelArrayPicture(2, 1000, red_blue);
	Picture red_BlueMutablePictureLargeWidth = new MutablePixelArrayPicture(542, 2, red_blue);
	Picture rgbMutablePicture = new MutablePixelArrayPicture(rgbPicture);
	Picture randomMutablePicture = new MutablePixelArrayPicture(randomPicture);

	//Valid Grayscale Pixel Mutable Pixel Array 
	Picture grayscalePicture = new MutablePixelArrayPicture(4, 5);
	Picture grayscalePictureWeirdDimensions = new MutablePixelArrayPicture(10000, 3333);

	// Valid Monochrome Picture initializations.
	Picture evenBlueMonochromePicture = new MonochromePicture(2, 2, blue);
	Picture randomMonochromePicture = new MonochromePicture(3, 4, randomColor);

	@Test
	public void testMutableArrayPictureConstructor2dPixelArray() {
		try {
			Picture invalidNullPixelArray = new MutablePixelArrayPicture(null);
			fail("Pixel 2d array cannot be null");
		} catch (IllegalArgumentException e) {
		}

		try {
			Picture noWidth2dPixelsArray = new MutablePixelArrayPicture(noWidthPicture);
			fail("Pixel array has illegal geometry.");
		} catch (IllegalArgumentException e) {
		}

		try {
			Picture noHeight2dPixelsArray = new MutablePixelArrayPicture(noHeightPicture);
			fail("Pixel array has illegal geometry.");
		} catch (IllegalArgumentException e) {
		}
		try {
			Picture differentHeight2dPixelsArray = new MutablePixelArrayPicture(differentHeightPicture);
			fail("Columns in picture are not all the same height.");
		} catch (IllegalArgumentException e) {
		}
		try {
			Picture invalidNullRow2dPixelArray = new MutablePixelArrayPicture(includesNullRowsPicture);
			fail("Pixel array includes null rows.");
		} catch (IllegalArgumentException e) {
		}
		try {
			Picture invalidNullPixels2dPixelArray = new MutablePixelArrayPicture(includesNullPixelsPicture);
			fail("Pixel array includes null pixels.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testMutableArrayPictureConstructorGeometry() {
		try {
			Picture invalidMutablePictureWidth = new MutablePixelArrayPicture(0, 2, red);
			fail("Width is illegal.");
		} catch (IllegalArgumentException e) {
		}
		try {
			Picture invalidMutablePictureHeight = new MutablePixelArrayPicture(2, 0, red);
			fail("Height is illegal.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testMutableArrayPictureGetters() {
		assertEquals(4, redMutablePicture.getWidth());
		assertEquals(4, redMutablePicture.getHeight());
		assertEquals(red, redMutablePicture.getPixel(2, 3));

		assertEquals(3, blueMutablePicture.getWidth());
		assertEquals(3, blueMutablePicture.getHeight());
		assertEquals(blue, blueMutablePicture.getPixel(0, 2));
		
		assertEquals(3, greenMutablePicture.getWidth());
		assertEquals(4, greenMutablePicture.getHeight());
		assertEquals(green, greenMutablePicture.getPixel(2, 3));
		
		assertEquals(2, red_BlueMutablePictureLargeHeight.getWidth());
		assertEquals(1000, red_BlueMutablePictureLargeHeight.getHeight());
		assertEquals(red_blue, red_BlueMutablePictureLargeHeight.getPixel(1, 85));

		assertEquals(2, randomColorMutablePicture.getWidth());
		assertEquals(2, randomColorMutablePicture.getHeight());
		assertEquals(randomColor, randomColorMutablePicture.getPixel(0, 1));

		assertEquals(3, rgbMutablePicture.getWidth());
		assertEquals(3, rgbMutablePicture.getHeight());
		assertEquals(green, rgbMutablePicture.getPixel(1, 1));

		assertEquals(2, randomMutablePicture.getWidth());
		assertEquals(4, randomMutablePicture.getHeight());
		assertEquals(randomColor, randomMutablePicture.getPixel(1, 3));
		
		assertEquals(4, grayscalePicture.getWidth());
		assertEquals(5, grayscalePicture.getHeight());
		
		assertEquals(10000, grayscalePictureWeirdDimensions.getWidth());
		assertEquals(3333, grayscalePictureWeirdDimensions.getHeight());
		
		
	}

	@Test
	public void testMutableArrayPicturePaintMethod() {
		try {
			redMutablePicture.paint(1, 1, null, 1.0);
			fail("Null pixel.");
		} catch (IllegalArgumentException e) {
		}
		try {
			redMutablePicture.paint(-1, 1, red, 1.0);
			fail("Illegal x.");
		} catch (IllegalArgumentException e) {
		}
		try {
			redMutablePicture.paint(redMutablePicture.getWidth(), 1, red, 0.5);
			fail("Illegal x.");
		} catch (IllegalArgumentException e) {
		}
		try {
			blueMutablePicture.paint(1, -1, blue, 0.5);
			fail("Illegal y.");
		} catch (IllegalArgumentException e) {
		}
		try {
			blueMutablePicture.paint(1, blueMutablePicture.getHeight(), blue, 0.5);
			fail("Illegal y.");
		} catch (IllegalArgumentException e) {
		}
		try {
			rgbMutablePicture.paint(1, 1, green, 1.5);
			fail("Illegal factor.");
		} catch (IllegalArgumentException e) {
		}
		try {
			rgbMutablePicture.paint(1, 1, green, -1);
			fail("Illegal factor.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testMonochromePictureConstructor() {
		try {
			Picture invalidHeightPixelValue = new MonochromePicture(4, 0, red);
			fail("Illegal height.");
		} catch (IllegalArgumentException e) {
		}
		try {
			Picture invalidWidthPixelValue = new MonochromePicture(0, 5, blue);
			fail("Illegal width.");
		} catch (IllegalArgumentException e) {
		}
		try {
			Picture invalidNullPixelValue = new MonochromePicture(4, 5, null);
			fail("Pixel value is null");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testMonochromePictureGetters() {
		assertEquals(2, evenBlueMonochromePicture.getWidth());
		assertEquals(2, evenBlueMonochromePicture.getHeight());

		assertEquals(3, randomMonochromePicture.getWidth());
		assertEquals(4, randomMonochromePicture.getHeight());

		assertEquals(blue, evenBlueMonochromePicture.getPixel(1, 1));
		assertEquals(randomColor, randomMonochromePicture.getPixel(1, 1));

		try {
			evenBlueMonochromePicture.getPixel(-1, 1);
			fail("x is illegal");
		} catch (IllegalArgumentException e) {
		}
		try {
			evenBlueMonochromePicture.getPixel(evenBlueMonochromePicture.getWidth(), 1);
			fail("x is illegal");
		} catch (IllegalArgumentException e) {
		}

		try {
			randomMonochromePicture.getPixel(1, -1);
			fail("y is illegal");
		} catch (IllegalArgumentException e) {
		}
		try {
			randomMonochromePicture.getPixel(1, randomMonochromePicture.getHeight());
			fail("y is illegal");
		} catch (IllegalArgumentException e) {
		}
	}
	@Test
	public void testMutablePixelArrayInitializedCorrectly() {
		for (int width = 0; width < redMutablePicture.getWidth(); width++) {
			for (int height = 0; height < redMutablePicture.getHeight(); height++) {
				assertEquals(redMutablePicture.getPixel(width, height), red);
			}
		}
		for (int width = 0; width < blueMutablePicture.getWidth(); width++) {
			for (int height = 0; height < blueMutablePicture.getHeight(); height++) {
				assertEquals(blueMutablePicture.getPixel(width, height), blue);
			}
		}
		for (int width = 0; width < greenMutablePicture.getWidth(); width++) {
			for (int height = 0; height < greenMutablePicture.getHeight(); height++) {
				assertEquals(greenMutablePicture.getPixel(width, height), green);
			}
		}
		for (int width = 0; width < randomColorMutablePicture.getWidth(); width++) {
			for (int height = 0; height < randomColorMutablePicture.getHeight(); height++) {
				assertEquals(randomColorMutablePicture.getPixel(width, height), randomColor);
			}
		}
		for (int width = 0; width < red_BlueMutablePictureLargeHeight.getWidth(); width++) {
			for (int height = 0; height < red_BlueMutablePictureLargeHeight.getHeight(); height++) {
				assertEquals(red_BlueMutablePictureLargeHeight.getPixel(width, height), red_blue);
			}
		}
		for (int width = 0; width < red_BlueMutablePictureLargeWidth.getWidth(); width++) {
			for (int height = 0; height < red_BlueMutablePictureLargeWidth.getHeight(); height++) {
				assertEquals(red_BlueMutablePictureLargeWidth.getPixel(width, height), red_blue);
			}
		}
	}
	@Test
	public void testMutablePixelArrayGrayScaleInitializedCorrectly() {
		//Testing both grayscale's match grayscale intensity of 0.5
		for (int width = 0; width < grayscalePicture.getWidth(); width++) {
			for (int height = 0; height < grayscalePicture.getHeight(); height++) {
				assertEquals(0.5, grayscalePicture.getPixel(width, height).getIntensity(), 0.001);
				assertEquals(0.5, grayscalePicture.getPixel(width, height).getBlue(), 0.001);
				assertEquals(0.5, grayscalePicture.getPixel(width, height).getRed(), 0.001);
				assertEquals(0.5, grayscalePicture.getPixel(width, height).getGreen(), 0.001);
			}
		}
		
		for (int width = 0; width < grayscalePictureWeirdDimensions.getWidth(); width++) {
			for (int height = 0; height < grayscalePictureWeirdDimensions.getHeight(); height++) {
				assertEquals(0.5, grayscalePictureWeirdDimensions.getPixel(width, height).getIntensity(), 0.001);
				assertEquals(0.5, grayscalePictureWeirdDimensions.getPixel(width, height).getBlue(), 0.001);
				assertEquals(0.5, grayscalePictureWeirdDimensions.getPixel(width, height).getRed(), 0.001);
				assertEquals(0.5, grayscalePictureWeirdDimensions.getPixel(width, height).getGreen(), 0.001);
			}
		}
	}
	@Test(expected = Exception.class)
	public void testIllegalArgumentExceptionForGetPixel() {
		redMutablePicture.getPixel(redMutablePicture.getWidth(), redMutablePicture.getHeight());
		grayscalePicture.getPixel(grayscalePicture.getWidth(), grayscalePicture.getHeight());
		evenBlueMonochromePicture.getPixel(evenBlueMonochromePicture.getWidth(), evenBlueMonochromePicture.getHeight());
	}

	@Test
	public void testMutablePixelArrayPicturePaintOnePixel() {
		Pixel[][] parray = new Pixel[5][10];
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				parray[x][y] = red;
			}
		}
		
		Picture p = new MutablePixelArrayPicture(parray);
		
		Picture result = p.paint(0, 0, blue);
		assertEquals(p, result);
		
		Pixel pix00 = p.getPixel(0, 0);
		check_for_component_equality(blue, pix00);

		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (x != 0 && y !=0) {
					check_for_component_equality(red, p.getPixel(x, y));
				}
			}
		}		
	}
	
	@Test
	public void testMonochromPicturePaintOnePixel() {
		Picture p = new MonochromePicture(5, 10, red);
		
		Picture result = p.paint(0, 0, blue);
		assertNotEquals(p, result);
		
		Pixel pix00_orig = p.getPixel(0, 0);
		check_for_component_equality(red, pix00_orig);

		Pixel pix00_res = result.getPixel(0, 0);
		check_for_component_equality(blue, pix00_res);
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (x != 0 && y !=0) {
					check_for_component_equality(red, result.getPixel(x, y));
				}
			}
		}		
	}

	
	@Test
	public void testMutablePixelArrayPicturePaintRectangle() {
		Pixel[][] parray = new Pixel[5][10];
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				parray[x][y] = red;
			}
		}
		
		Picture p = new MutablePixelArrayPicture(parray);
		
		Picture result = p.paint(1, 1, 3, 3, blue);
		assertEquals(p, result);
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (x >= 1 && x <=3 && y >= 1 && y <= 3) {
					check_for_component_equality(blue, p.getPixel(x, y));
				} else {
					check_for_component_equality(red, p.getPixel(x, y));		
				}
			}
		}		
	}

	@Test
	public void testMonochromPicturePaintRectangle() {
		Picture p = new MonochromePicture(5, 10, red);
		
		Picture result = p.paint(1, 1, 3, 3, blue);
		assertNotEquals(p, result);

		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (x >= 1 && x <=3 && y >= 1 && y <= 3) {
					check_for_component_equality(blue, result.getPixel(x, y));
				} else {
					check_for_component_equality(red, result.getPixel(x, y));		
				}
			}
		}		

	}

	@Test
	public void testMutablePixelArrayPicturePaintCircle() {
		Pixel[][] parray = new Pixel[5][10];
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				parray[x][y] = red;
			}
		}
		
		Picture p = new MutablePixelArrayPicture(parray);
		
		Picture result = p.paint(2, 5, 2.5, blue);
		assertEquals(p, result);
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (Math.sqrt((x-2)*(x-2)+(y-5)*(y-5)) <= 2.5) {
					check_for_component_equality(blue, p.getPixel(x, y));					
				} else {
					check_for_component_equality(red, p.getPixel(x, y));		
				}
			}
		}		
	}

	@Test
	public void testMonochromePicturePaintCircle() {
		Picture p = new MonochromePicture(5, 10, red);
		
		Picture result = p.paint(2, 5, 2.5, blue);
		assertNotEquals(p, result);
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (Math.sqrt((x-2)*(x-2)+(y-5)*(y-5)) <= 2.5) {
					check_for_component_equality(blue, result.getPixel(x, y));					
				} else {
					check_for_component_equality(red, result.getPixel(x, y));		
				}
			}
		}		
	}
	
	@Test
	public void testPartialOverlapPaintRectangle() {
		Pixel[][] parray = new Pixel[5][10];
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				parray[x][y] = red;
			}
		}
		
		Picture p = new MutablePixelArrayPicture(parray);
		
		Picture result = p.paint(-1, -1, 1, 1, blue);
		assertEquals(p, result);
		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (x >= -1 && x <=1 && y >= -1 && y <= 1) {
					check_for_component_equality(blue, p.getPixel(x, y));
				} else {
					check_for_component_equality(red, p.getPixel(x, y));		
				}
			}
		}		
		
	}

	@Test
	public void testPartialOverlapPaintCircle() {
		Picture p = new MonochromePicture(5, 10, red);
		
		Picture result = p.paint(0, 0, 2.5, blue);		
		for (int x=0; x<5; x++) {
			for (int y=0; y<10; y++) {
				if (Math.sqrt(x*x+y*y) <= 2.5) {
					check_for_component_equality(blue, result.getPixel(x, y));					
				} else {
					check_for_component_equality(red, result.getPixel(x, y));		
				}
			}
		}		
	}


	private static boolean check_for_component_equality(Pixel a, Pixel b) {
		assertEquals(a.getRed(), b.getRed(), 0.001);
		assertEquals(a.getGreen(), b.getGreen(), 0.001);
		assertEquals(a.getBlue(), b.getBlue(), 0.001);

		return true;
	}

}

