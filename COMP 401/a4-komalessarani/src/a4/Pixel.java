package a4;

public interface Pixel {

	// These constants are standard relative intensity factors
	// associated with the red, green, and blue components of
	// a color. 
	
	public final static double RED_INTENSITY_FACTOR =   0.2126;
	public final static double GREEN_INTENSITY_FACTOR = 0.7152;
	public final static double BLUE_INTENSITY_FACTOR =  0.0722;
	
	
	// Named constants for instances of white and black
	
	public final static Pixel WHITE = new GrayPixel(1.0);
	public final static Pixel BLACK = new GrayPixel(0.0);
	
	// Named constant for default precision used when comparing
	// the intensities of two pixels.
	
	public static final double DEFAULT_EPSILON = 0.05;


	// Getters for the red, green, and blue components of a pixel.
	
	public double getRed();
	public double getGreen();
	public double getBlue();

	// Getter for the  relative intensity of a pixel (i.e., how bright it is). 
	
	default public double getIntensity() {
		return ((getRed() * RED_INTENSITY_FACTOR) +
				(getGreen() * GREEN_INTENSITY_FACTOR) +
				(getBlue() * BLUE_INTENSITY_FACTOR));
	}
	
	// equals() returns true if the intensity of the pixel
	// provided as "other" is within a specified precision
	// bound of the intensity of this pixel. The first form uses the
	// default precision bound defined as DEFAULT_EPSILON. The second
	// form uses the bound specified by the parameter epsilon.
	
	default public boolean equals(Pixel other) {
		return equals(other, DEFAULT_EPSILON);
	}

	default public boolean equals(Pixel other, double epsilon) {
		return Math.abs(getIntensity() - other.getIntensity()) < epsilon;
	}

	// lighten() creates and returns a new Pixel object that is a lightened
	// version of this pixel. The value of factor controls the amount 
	// by which the new pixel is lightened. A factor of 0.0 will have no
	// effect (i.e., return a pixel that is the same as this pixel). A factor of 1.0
	// will have a maximum effect (i.e., return a white pixel).
	
	default public Pixel lighten(double factor) {
		return blend(WHITE, factor);
	}

	// darken() creates and returns a new Pixel object that is a darkened
	// version of this pixel. The value of factor controls the amount of 
	// by which the new pixel is darkened. A factor of 0.0 will have no
	// effect (i.e., return a pixel that is the same as this pixel). A factor of 1.0
	// will have a maximum effect (i.e., return a black pixel).

	default public Pixel darken(double factor) {
		return blend(BLACK, factor);
	}

	// blend() creates a new pixel that is a blend of this pixel and
	// the pixel specified as other. The parameter factor controls the
	// degree of blending. A factor of 0.0 will have no effect (i.e., return
	// a pixel that is the same as this pixel). A factor of 1.0 will 
	// have a maximum effect (i.e., return a pixel that is the same as other).
	// Values between 0.0 and 1.0 will return a proportional blend of this
	// pixel and other.
	
	default public Pixel blend(Pixel other, double factor) {
		if (other == null) {
			throw new IllegalArgumentException("other is null");
		}
		
		if (factor < 0 || factor > 1.0) {
			throw new IllegalArgumentException("Illegal factor");
		}
		
		double diff_red = other.getRed() - getRed();
		double diff_green = other.getGreen() - getGreen();
		double diff_blue = other.getBlue() - getBlue();
		
		return new ColorPixel(getRed()+(diff_red*factor),
 				getGreen()+(diff_green*factor),	
				getBlue()+(diff_blue*factor));
	}
}
