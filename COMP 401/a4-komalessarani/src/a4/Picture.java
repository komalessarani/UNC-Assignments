package a4;

import java.util.Iterator;

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
	
	public Picture paint(int x, int y, Pixel p);
	public Picture paint(int x, int y, Pixel p, double factor);
	
	
	// paint(int ax, int ay, int bx, int by, Pixel p) paints the
	// rectangular region defined by the positions (ax, ay) and
	// (bx, by) with the specified pixel value. The second form
	// should blend with the specified factor as previously described.
	
	public Picture paint(int ax, int ay, int bx, int by, Pixel p);
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor);

	// paint(int cx, int cy, double radius, Pixel p) sets all pixels in the
	// picture that are within radius distance of the coordinate (cx, cy) to the
	// Pixel value p.  Only positive values of radius should be allowed. Any
	// value of cx and cy should be allowed (even if negative or otherwise
	// outside of the boundaries of the frame). 
	
	// Calculate the distance of a particular (x,y) position to (cx,cy) with
	// the expression: Math.sqrt((x-cx)*(x-cx)+(y-cy)*(y-cy))	

	// The second form with factor, blends as previously described.
	
	public Picture paint(int cx, int cy, double radius, Pixel p);
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor);
	
	// paint(int x, int y, Picture p) paints pixels starting at position (x,y)
	// and extending to the right and downwards
	// with pixels from the provided Picture object p starting at (0,0). 
	
	// For example, if Picture p is 3 pixels wide and 2 pixels tall,
	// and assuming that the current picture is sufficiently wide/tall, 
	// a call to this method as paint(2,7,p) would be equivalent to
	// the following:
	// paint(2, 7, p.getPixel(0,0);
	// paint(3, 7, p.getPixel(1,0);
	// paint(4, 7, p.getPixel(2,0);
	// paint(2, 8, p.getPixel(0,1);
	// paint(3, 8, p.getPixel(1,1);
	// paint(4, 8, p.getPixel(2,1);
	
	// Although the dimensions of p are allowed to extend beyond the 
	// right or bottom edges of this picture, an IllegalArgumentException is thrown 
	// if the specified starting point (x,y) is illegal or if the parameter p is null.
	
	// The second form with factor, blends as previously described.

	public Picture paint(int x, int y, Picture p);
	public Picture paint(int x, int y, Picture p, double factor);
	
	// Get/set caption
	public String getCaption();
	public void setCaption(String caption);
	
	// Creates and returns a new SubPicture object representing
	// the rectangular region of this picture originating at
	// (x,y) and extending to the right by width and downward by
	// height. See the SubPicture interface.
	
	public SubPicture extract(int x, int y, int width, int height);
	

	// sample(int init_x, int init_y, int dx, int dy) creates and returns an iterator
	// of Pixel objects from this picture. The returned iterator will iterate over 
	// the pixel starting at (init_x, init_y) and proceeding left to right by dx columns.
	// When the end of the row is encountered, the iteration picks up again at the
	// init_x column down by dy rows. 
	//
	// IllegalArgumentException is thrown if init_x or init_y are not within the bounds
	// of the picture, or if dx and dy are not positive.
	
	public Iterator<Pixel> sample(int init_x, int init_y, int dx, int dy);
	
	// window(int window_width, int window_height) creates and returns an iterator
	// of SubPicture objects extracted from this picture. The returned iterator begins
	// with a SubPicture object originating in the upper left corner (i.e., at coordinate
	// (0,0)) with width and height of window_width and window_height. Subsequent 
	// SubPicture objects produced by the iterator represent "sliding" the window to the
	// right until the right edge is encountered at which point the window is reset to 
	// the left edge and sliding down one row. This continues until the last SubPicture
	// produced by the iterator which represents the window slid until its lower right corner
	// is at the lower right corner of the source picture.
	//
	// IllegalArgumentException is thrown if window_width or window_height is greater
	// than the width or height of the source picture respectively.

	public Iterator<SubPicture> window(int window_width, int window_height);

	// tile(int tile_width, int tile_height) creates and returns an iterator
	// of SubPicture objects that represent a non-overlapped tiling of this picture. 
	// The first SubPicture produced by this iterator is originated at the upper left 
	// corner of the source picture and is tile_width wide and tile_height tall. 
	// Subsequent SubPicture objects produced by this iterator proceed in the tiling
	// from left to right and then top to bottom. Partial tiles are not produced so if
	// the dimensions of this picture are not a multiple of window_width or window_height,
	// last tile area of a row may not extend all the way to the right edge of the picture
	// and/or the last row of tiles may not extend all the way to the bottom edge of the picture.

	// IllegalArgumentException is thrown if tile_width or tile_heigt are greater
	// then the width or height of the picture.
	
	public Iterator<SubPicture> tile(int tile_width, int tile_height);

	// zigzag() creates and returns a Pixel iterator that produces the pixels of the picture
	// in "zigzag" order. See the assignment write up for a description of how zigzag should work.
	
	public Iterator<Pixel> zigzag();
}
