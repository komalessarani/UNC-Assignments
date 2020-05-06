package a5;

import java.util.Iterator;

public class SubPictureImpl implements SubPicture {

	
	public SubPictureImpl(Picture source, int xoffset, int yoffset, int width, int height) {
		
	}
	
	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Pixel getPixel(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int x, int y, Pixel p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int x, int y, Pixel p, double factor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int x, int y, Picture p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture paint(int x, int y, Picture p, double factor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCaption(String caption) {
		// TODO Auto-generated method stub

	}

	@Override
	public SubPicture extract(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Pixel> sample(int init_x, int init_y, int dx, int dy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SubPicture> window(int window_width, int window_height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SubPicture> tile(int tile_width, int tile_height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Pixel> zigzag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getXOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getYOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Picture getSource() {
		// TODO Auto-generated method stub
		return null;
	}

}
