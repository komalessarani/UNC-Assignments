package a3;

public class MutablePixelArrayPicture implements Picture {

	private Pixel[][] pixel_array;
	private Pixel initial_value = new GrayPixel(0.5);
	
	// Creates new object using values provided by pixel_array, matching in size. 
	public MutablePixelArrayPicture(Pixel[][] pixel_array) {
		
		//check if pixel array is null
		if(pixel_array == null)
			throw new IllegalArgumentException("Pixel array cannot be null");
		
		//check is height and width have valid values
		if(pixel_array.length == 0 || pixel_array[0].length == 0)
			throw new IllegalArgumentException("The height and width must not equal 0");
		
		//check if row has null value
		for(int i = 0; i < pixel_array.length; i++) {
			if(pixel_array[i] == null)
				throw new IllegalArgumentException("The row must not have a null value");
		}
	
		//check if array is lop sided, and if so throw an exception
		for(int i = 0; i < pixel_array.length; i++) {
			if(pixel_array[0].length != pixel_array[i].length)
				throw new IllegalArgumentException("The pixel array must be a matrix");
		}
		
		//check if array values are null
		for(int i = 0; i < pixel_array.length; i++) {
			for(int j = 0; j < pixel_array[0].length; j++)
				if(pixel_array[i][j] == null)
					throw new IllegalArgumentException("Array cannot store null values");
		}
		
		//clone array
		this.pixel_array = pixel_array.clone();
	}
	
	// Creates new object by providing geometry of picture and an initial value for all pixels.
	public MutablePixelArrayPicture(int width, int height, Pixel initial_value) {
		
		//check for valid value of width and height
		if(width <= 0 || height <= 0)
			throw new IllegalArgumentException("Height or Width is out of range"); 
		//check for valid value for initial value
		if(initial_value == null)
			throw new IllegalArgumentException("Initial value cannot be null");
			
		this.pixel_array = new Pixel[width][height];
		
		//get initial value
		for(int i = 0; i < pixel_array.length; i++) {
			for(int j = 0; j < pixel_array[i].length; j++) {
				this.pixel_array[i][j] = initial_value;
			}
		}
		this.initial_value = initial_value;
	}
	
	// Creates new object by providing geometry of picture. 
	public MutablePixelArrayPicture(int width, int height) {
		this(width, height, new GrayPixel(0.5));
	}
	
	//return width
	public int getWidth() {
		return pixel_array.length;
	}

	//return height
	public int getHeight() {
		return pixel_array[0].length;
	}
	
	
	public Pixel getPixel(int x, int y) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			throw new IllegalArgumentException("Coordinate out of bounds");
		return this.pixel_array[x][y];
	}

	@Override
	public Picture paint(int x, int y, Pixel p, double factor) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			throw new IllegalArgumentException("Coordinate out of bounds");
		pixel_array[x][y] = pixel_array[x][y].blend(p, factor);
		return this;
	}
	
	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		if(ax < 0 || ay < 0 || bx < 0 || by < 0 || ax >= getWidth() || ay >= getHeight() || bx >= getWidth() || by >= getHeight()) {
			ax = 0;
			ay = 0;
		}
		for(int i = ax; i <= bx; i++) {
			for(int j = ay; j <= by; j++) {
				pixel_array[i][j] = pixel_array[i][j].blend(p, factor);
			}
		}
		return this;
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		//validate radius value 
		if(radius < 0)
			throw new IllegalArgumentException("Radius must be a non-negative value");
		for(int i = 0; i < pixel_array.length; i++) {
			for(int j = 0; j < pixel_array[i].length; j++) {
				if(Math.sqrt((i-cx)*(i-cx)+(j-cy)*(j-cy)) <= radius)
					pixel_array[i][j] = pixel_array[i][j].blend(p, factor);
			}
		}
		return this;
	}
}
