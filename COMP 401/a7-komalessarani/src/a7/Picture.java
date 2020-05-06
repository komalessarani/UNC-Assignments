package a7;

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
	
	// Wraps this picture into an ObservablePicture and returns it.
	
	default public ObservablePicture createObservable() {
		return new ObservablePictureImpl(this);
	}

}
