package a6;

// SubPicture is an interface that represents
// a region of some other source picture and can 
// can itself be treated as a Picture object.
//
// A SubPicture object is originated within a
// source picture at a particular (x,y) offset.
// Getters are provided in the SubPicture interface
// for retrieving the underlying source picture and the 
// x and y offsets.
//
// The paint() methods of a SubPicture object will
// return itself if the underlying source picture object
// is mutable with respect to pixel information and 
// will return a new SubPicture object if the underlying
// source picture is immutable with respect to pixel information.

public interface SubPicture extends Picture {

	public int getXOffset();
	public int getYOffset();
	public Picture getSource();
	
}
