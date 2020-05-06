package a3;

public class GradientPicture implements Picture {

	private int width;
	private int height;
	private Pixel upper_left;
	private Pixel upper_right;
	private Pixel lower_left;
	private Pixel lower_right;
	
	public GradientPicture(int width, int height, Pixel upper_left, Pixel upper_right, Pixel lower_left, Pixel lower_right) {
		//validate width and height
		if(width <= 0 || height <= 0 || upper_left == null || upper_right == null || lower_left == null || lower_right == null)
			throw new IllegalArgumentException("Invalid values for input");
		this.width = width;
		this.height = height;
		this.upper_left = upper_left;
		this.upper_right = upper_right;
		this.lower_left = lower_left;
		this.lower_right = lower_right;
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
		double yy = (y)/(getHeight()-1.0);
		double xx = (x)/(getWidth()-1.0);
		
		Pixel left = upper_left.blend(lower_left, yy);
		Pixel right = upper_right.blend(lower_right, yy);
		Pixel newPixel = left.blend(right, xx);
		return newPixel;
	}

	public Picture paint(int x, int y, Pixel p, double factor) {
		Picture pixel = new MutablePixelArrayPicture(getArray());
		return pixel.paint(x, y, p, factor);
	}
	
	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		Picture pixel = new MutablePixelArrayPicture(getArray());
		return pixel.paint(ax, ay, bx, by, p, factor);
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		Picture pixel = new MutablePixelArrayPicture(getArray());
		return pixel.paint(cx, cy, radius, p, factor);
	}
	
	private Pixel[][] getArray() {
		Pixel[][] pixel_array = new Pixel[getWidth()][getHeight()];
		for(int i = 0; i < getWidth(); i++) {
			for(int j = 0; j < getHeight(); j++) {
				double yy = (j*1.0)/(getHeight()-1.0);
				double xx = (i*1.0)/(getWidth()-1.0);
				Pixel left = upper_left.blend(lower_left, yy);
				Pixel right = upper_right.blend(lower_right, yy);
				Pixel newPixel = left.blend(right, xx);
				pixel_array[i][j] = newPixel;
			}
		}
		return pixel_array;
	}

}
