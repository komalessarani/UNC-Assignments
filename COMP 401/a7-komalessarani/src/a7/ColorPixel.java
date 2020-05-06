package a7;

public class ColorPixel implements Pixel {

	double _red;
	double _green;
	double _blue;
	
	public ColorPixel(double r, double g, double b) {
		if (r < 0.0 || r > 1.0 || g < 0.0 || g > 1.0 || b < 0.0 || b > 1.0) {
			throw new IllegalArgumentException("Color component out of range");
		}
		
		_red = r;
		_green = g;
		_blue = b;
	}
	
	@Override
	public double getRed() {
		return _red;
	}

	@Override
	public double getGreen() {
		return _green;
	}

	@Override
	public double getBlue() {
		return _blue;
	}


}
