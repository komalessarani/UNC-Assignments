package a3;

public class MonochromePicture implements Picture {

	private int width;
	private int height;
	private Pixel value = new GrayPixel(0.5);
	
	public MonochromePicture(int width, int height, Pixel value) {
		// check if pixel value is null
		if (value == null)
			throw new IllegalArgumentException("Value cannot be null");
		//check for valid value of width and height
		if(width <= 0 || height <= 0)
			throw new IllegalArgumentException("Height or Width is out of range"); 
		this.width = width;
		this.height = height;
		this.value = value;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public Pixel getPixel(int x, int y) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			throw new IllegalArgumentException("Coordinate out of bounds");
		return this.value;
	}

	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		if (ax < 0 || ay < 0 || bx < 0 || by < 0 || ax >= getWidth() || ay >= getHeight()) 
			throw new IllegalArgumentException("Coordinate out of bounds");
		Picture pixel_array = new MutablePixelArrayPicture(getWidth(), getHeight(), value);
		return pixel_array.paint(ax, ay, bx, by, p, factor);
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		if (radius < 0)
			throw new IllegalArgumentException("Radius must be a non-negative value");
		Picture pixel_array = new MutablePixelArrayPicture(getWidth(), getHeight(), value);
		return pixel_array.paint(cx, cy, radius, p, factor);
	}

}
