package a3;

public class ImmutablePixelArrayPicture implements Picture {

	private int width;
	private int height;
	private Pixel[][] pixel_array;
	private Pixel initial_value = new GrayPixel(0.5);

	// Creates new object using values provided by pixel_array, matching in size.
	public ImmutablePixelArrayPicture(Pixel[][] pixel_array) {
		// check if pixel array is null
		if (pixel_array == null)
			throw new IllegalArgumentException("Pixel array cannot be null");

		// check is height and width have valid values
		if (pixel_array.length <= 0 || pixel_array[0].length <= 0)
			throw new IllegalArgumentException("The height and width must not equal 0");

		// check if row has null value
		for (int i = 0; i < pixel_array.length; i++) {
			if (pixel_array[i] == null)
				throw new IllegalArgumentException("The row must not have a null value");
		}

		// check if array is lop sided, and if so throw an exception
		for (int i = 0; i < pixel_array.length; i++) {
			if (pixel_array[0].length != pixel_array[i].length)
				throw new IllegalArgumentException("The pixel array must be a matrix");
		}

		// check if array values are null
		for (int i = 0; i < pixel_array.length; i++) {
			for (int j = 0; j < pixel_array[0].length; j++)
				if (pixel_array[i][j] == null)
					throw new IllegalArgumentException("Array cannot store null values");
		}

		// clone array
		this.pixel_array = pixel_array.clone();
	}

	// Creates new object by providing geometry and initial value for all pixels.
	public ImmutablePixelArrayPicture(int width, int height, Pixel initial_value) {
		if (width <= 0 || height <= 0 || initial_value == null) {
			throw new IllegalArgumentException("Values out of bound");
		}
		this.width = width;
		this.height = height;
		pixel_array = new Pixel[width][height];
		// get initial value
		for (int i = 0; i < pixel_array.length; i++) {
			for (int j = 0; j < pixel_array[i].length; j++) {
				pixel_array[i][j] = initial_value;
			}
		}
		this.initial_value = initial_value;
	}

	// Creates new object by providing geometry. Initial value should be medium
	// gray.
//	public ImmutablePixelArrayPicture(int width, int height) {
//		this(width, height, new GrayPixel(0.5));
//	}

	@Override
	public int getWidth() {
		return pixel_array.length;
	}

	@Override
	public int getHeight() {
		return pixel_array[0].length;
	}

	@Override
	public Pixel getPixel(int x, int y) {
		if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			throw new IllegalArgumentException("Coordinate out of bounds");
		return this.pixel_array[x][y];
	}

	@Override
	public Picture paint(int x, int y, Pixel p, double factor) {
		Pixel[][] pixel_array = new Pixel[getWidth()][];
		for (int i = 0; i < getWidth(); i++) {
			pixel_array[i] = this.pixel_array[i].clone();
		}
		Picture pixel = new MutablePixelArrayPicture(pixel_array);
		return pixel.paint(x, y, p, factor);
	}

	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		Pixel[][] pixel_array = new Pixel[getWidth()][];
		Picture pixel = new MutablePixelArrayPicture(pixel_array);
		return pixel.paint(ax, ay, bx, by, p, factor);
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		Pixel[][] pixel_array = new Pixel[getWidth()][];
		Picture pixel = new MutablePixelArrayPicture(pixel_array);
		return pixel.paint(cx, cy, radius, p, factor);
	}
}
