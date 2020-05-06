package a3;

public interface Picture {
	
	// Getters for the dimensions of a picture.
	// Width refers to the number of columns and 
	// height is the number of rows.
	
	public int getWidth();
	public int getHeight();
	
	// getPixel(x, y) retrieves the pixel at position (x,y) in the
	// picture. The coordinate (0,0) is the upper left
	// corner of the picture. The coordinate (getWidth()-1, getHeight()-1)
	// is the lower right of the picture. An IllegalArgumentException
	// is thrown if x or y are not in range.
	
	public Pixel getPixel(int x, int y);
	
	// The various forms of the paint() method return a new
	// picture object based on this picture with certain pixel
	// positions "painted" with a new value.
	
	// paint(int x, int y, Pixel p) paints the pixel at
	// position (x,y) with the pixel value p. The second 
	// form includes a factor parameter that specifies a
	// blending factor. A factor of 0.0 leaves the specified
	// pixel unchanged. A factor of 1.0 results in replacing
	// the value with the specified pixel value. Values between
	// 0.0 and 1.0 blend proportionally.
	
	default public Picture paint(int x, int y, Pixel p) {
		return paint(x, y, p, 1.0); // we know that the factor is 1
	}
	default public Picture paint(int x, int y, Pixel p, double factor) {
		return paint(x, y, x, y, p, factor);
	}
	
	
	// paint(int ax, int ay, int bx, int by, Pixel p) paints the
	// rectangular region defined by the positions (ax, ay) and
	// (bx, by) with the specified pixel value. The second form
	// should blend with the specified factor as previously described.
	
	default public Picture paint(int ax, int ay, int bx, int by, Pixel p) {
		return paint(ax, ay, bx, by, p, 1.0);
	}
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor);

	// paint(int cx, int cy, double radius, Pixel p) sets all pixels in the
	// picture that are within radius distance of the coordinate (cx, cy) to the
	// Pixel value p.  Only positive values of radius should be allowed. Any
	// value of cx and cy should be allowed (even if negative or otherwise
	// outside of the boundaries of the frame). 
	
	// Calculate the distance of a particular (x,y) position to (cx,cy) with
	// the expression: Math.sqrt((x-cx)*(x-cx)+(y-cy)*(y-cy))	

	// The second form with factor, blends as previously described.
	
	default public Picture paint(int cx, int cy, double radius, Pixel p) {
		return paint(cx, cy, radius, p, 1.0);
	}
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor);
	
}
