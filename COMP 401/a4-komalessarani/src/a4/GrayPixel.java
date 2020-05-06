package a4;

public class GrayPixel implements Pixel {

	private double _intensity;
	
	public GrayPixel(double intensity) {
		
		if (intensity < 0 || intensity > 1.0) {
			throw new IllegalArgumentException("intensity out of range");
		}
		
		_intensity = intensity;
	}
	
	@Override
	public double getRed() {
		return _intensity;
	}

	@Override
	public double getGreen() {
		return _intensity;
	}

	@Override
	public double getBlue() {
		return _intensity;
	}

	// Below are grayscale-specific implementations of 
	// getIntensity(), lighten() and darken() to replace default
	// implementations provided in interface that are more general.

	@Override
	public double getIntensity() {
		return _intensity;
	}

	@Override
	public Pixel lighten(double factor) {
		return new GrayPixel((1.0 - _intensity) * factor + _intensity);
	}

	@Override
	public Pixel darken(double factor) {
		return new GrayPixel(_intensity * (1.0 - factor));
	}
}
