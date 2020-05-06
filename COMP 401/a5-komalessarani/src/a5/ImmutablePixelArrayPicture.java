package a5;

import java.util.Iterator;

public class ImmutablePixelArrayPicture implements Picture {

	private String _caption;
	private Pixel[][] _pixels;

	public ImmutablePixelArrayPicture(Pixel[][] parray, String caption) {
		if (caption == null) {
			throw new IllegalArgumentException("caption should not be null");
		}
		_caption = caption;
		_pixels = copyPixelArray(parray);
	}

	@Override
	public int getWidth() {
		return _pixels.length;
	}

	@Override
	public int getHeight() {
		return _pixels[0].length;
	}

	@Override
	public Pixel getPixel(int x, int y) {		
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("x or y out of bounds");
		}
		return _pixels[x][y];
	}


	public Picture paint(int x, int y, Pixel p) {
		return paint(x,y,p,1.0);
	}
	@Override
	public Picture paint(int x, int y, Pixel p, double factor) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("x or y out of bounds");
		}
		
		if (p == null) {
			throw new IllegalArgumentException("Pixel p is null");
		}
		
		if (factor < 0.0 || factor > 1.0) {
			throw new IllegalArgumentException("factor is out of bounds");
		}

		return (new MutablePixelArrayPicture(_pixels, getCaption())).paint(x, y, p,factor);
	}

	
	
	public Picture paint(int ax, int ay, int bx, int by, Pixel p) {
		return paint(ax, ay, bx, by, p, 1.0);
	}
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		if (p == null) {
			throw new IllegalArgumentException("Pixel p is null");
		}
		
		if (factor < 0 || factor > 1.0) {
			throw new IllegalArgumentException("factor out of range");			
		}
		
		int min_x = (ax < bx) ? ax : bx;
		int max_x = (ax > bx) ? ax : bx;
		int min_y = (ay < by) ? ay : by;
		int max_y = (ay > by) ? ay : by;

		min_x = (min_x < 0) ? 0 : min_x;
		min_y = (min_y < 0) ? 0 : min_y;
		max_x = (max_x > getWidth()-1) ? getWidth()-1 : max_x;
		max_y = (max_y > getHeight()-1) ? getHeight()-1 : max_y;
		
		Picture result = this;
		for (int x=min_x; x <= max_x; x++) {
			for (int y=min_y; y<= max_y; y++) {
				result = result.paint(x,y,p,factor);
			}
		}
		return copyAsImmutable(result);
	}

	public Picture paint(int cx, int cy, double radius, Pixel p) {
		return paint(cx, cy, radius, p, 1.0);
	}
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		if (p == null) {
			throw new IllegalArgumentException("Pixel p is null");
		}
		
		if (factor < 0 || factor > 1.0) {
			throw new IllegalArgumentException("factor out of range");			
		}
		
		if (radius < 0) {
			throw new IllegalArgumentException("radius is negative");
		}

		int min_x = (int) (cx - (radius+1));
		int max_x = (int) (cx + (radius+1));
		int min_y = (int) (cy - (radius+1));
		int max_y = (int) (cy + (radius+1));

		min_x = (min_x < 0) ? 0 : min_x;
		min_y = (min_y < 0) ? 0 : min_y;
		max_x = (max_x > getWidth()-1) ? getWidth()-1 : max_x;
		max_y = (max_y > getHeight()-1) ? getHeight()-1 : max_y;
		
		Picture result = this;
		for (int x=min_x; x <= max_x; x++) {
			for (int y=min_y; y<= max_y; y++) {
				if (Math.sqrt((cx-x)*(cx-x)+(cy-y)*(cy-y)) <= radius) {
					result = result.paint(x,y,p,factor);
				}
			}
		}
		return copyAsImmutable(result);
	}

	
	public Picture paint(int x, int y, Picture p) {
		return paint(x,y,p,1.0);
	}
	public Picture paint(int x, int y, Picture p, double factor) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("x or y out of range");
		}

		if (factor < 0 || factor > 1.0) {
			throw new IllegalArgumentException("factor out of range");			
		}
		
		if (p == null) {
			throw new IllegalArgumentException("Picture p is null");
		}
		
		Picture result = this;
		for (int px=0; px < p.getWidth() && px + x < getWidth(); px++) {
			for (int py=0; py < p.getHeight() && py + y < getHeight(); py++) {
				result = result.paint(px+x, py+y, p.getPixel(px, py), factor);
			}
		}
		return copyAsImmutable(result);
	}
	
	@Override
	public String getCaption() {
		return _caption;
	}

	@Override
	public void setCaption(String caption) {
		if (caption == null) {
			throw new IllegalArgumentException("caption is null");
		}
		
		_caption = caption;
	}

	public SubPicture extract(int x, int y, int width, int height) {
		return new SubPictureImpl(this, x, y, width, height);
	}

	public Iterator<Pixel> sample(int init_x, int init_y, int dx, int dy) {
		return new SampleIterator(this, init_x, init_y, dx, dy);
	}

	public Iterator<SubPicture> window(int window_width, int window_height) {
		return new WindowIterator(this, window_width, window_height);
	}
	
	public Iterator<SubPicture> tile(int tile_width, int tile_height) {
		return new TileIterator(this, tile_width, tile_height);
	}

	public Iterator<Pixel> zigzag() {
		return new ZigZagIterator(this);
	}

	
	private static Picture copyAsImmutable(Picture p) {
		if (p == null) {
			throw new IllegalArgumentException("Picture p is null");
		}
		
		Pixel[][] parray = new Pixel[p.getWidth()][p.getHeight()];
		for (int x=0; x<p.getWidth(); x++) {
			for (int y=0; y<p.getHeight(); y++) {
				parray[x][y] = p.getPixel(x, y);
			}
		}
		return new ImmutablePixelArrayPicture(parray, p.getCaption());		
	}
	

	private static Pixel[][] copyPixelArray(Pixel[][] pixel_array) {
		
		if (pixel_array == null) {
			throw new IllegalArgumentException("pixel_array is null");
		}
		int width = pixel_array.length;
		
		if (width == 0) {
			throw new IllegalArgumentException("pixel_array has illegal geometry");			
		}
		
		for (int x=0; x<width; x++) {
			if (pixel_array[x] == null) {
				throw new IllegalArgumentException("pixel_array includes null columns");			
			}
		}
		
		int height = pixel_array[0].length;
		if (height == 0) {
			throw new IllegalArgumentException("pixel_array has illegal geometry");						
		}
		
		for (int x=0; x<width; x++) {
			if (pixel_array[x].length != height) {
				throw new IllegalArgumentException("Columns in picture are not all the same height.");			
			}
		}

		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				if (pixel_array[x][y] == null) {
					throw new IllegalArgumentException("pixel_array includes null pixels");								
				}
			}
		}
		
		Pixel[][] copy = new Pixel[width][height];
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				copy[x][y] = pixel_array[x][y];
			}
		}

		return copy;
	}

}
