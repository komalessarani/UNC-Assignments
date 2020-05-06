package a7;

import java.util.ArrayList;
import java.util.List;

public class ObservablePictureImpl implements ObservablePicture {

	private Picture _picture;
	private List<RegisteredROIObserver> _observers;
	private boolean _suspended;
	private Region _changed_region;
	private Region _picture_region;
	
	public ObservablePictureImpl(Picture p) {
		if (p == null) {
			throw new IllegalArgumentException("Picture p is null");
		}

		_picture = p;
		_observers = new ArrayList<RegisteredROIObserver>();
		_suspended = false;
		_changed_region = null;
		_picture_region = new RegionImpl(0, 0, p.getWidth()-1, p.getHeight()-1);
	}

	private void notifyObservers(Region r) {

		if (r == null) {
			// This will happen if we resume from being suspended but there were
			// no paint operations while suspended.
			return;
		}
		
		try {
			// Restrict changed region to valid coordinates for picture.
			// Allows us to not have to worry about checking this in each 
			// version of paint.
			r = r.intersect(_picture_region);
			
			// Update changed region. Calculate as union on r since
			// _changed_region could be null if no changes yet.
			_changed_region = r.union(_changed_region);

			// Notify any observers if not suspended. Detecting appropriate intersection 
			// if any done by each registered observer itself.
			
			if (!_suspended) {
				for (RegisteredROIObserver o : _observers) {
					o.notify(this, _changed_region);
				}
				
				// Reset changed region now that we've notified.
				_changed_region = null;
			}
		}
		catch (NoIntersectionException e) {
			// Supposed change was completely outside of picture, so don't do anything.
		}
	}

	@Override
	public int getWidth() {
		return _picture.getWidth();
	}

	@Override
	public int getHeight() {
		return _picture.getHeight();
	}

	@Override
	public Pixel getPixel(int x, int y) {
		return _picture.getPixel(x, y);
	}

	@Override
	public Picture paint(int x, int y, Pixel p) {
		return paint(x,y,p,1.0);
	}

	@Override
	public Picture paint(int x, int y, Pixel p, double factor) {
		_picture = _picture.paint(x, y, p, factor);

		notifyObservers(new RegionImpl(x, y, x, y));
		return this;
	}

	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p) {
		return paint(ax, ay, bx, by, p, 1.0);
	}

	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		_picture = _picture.paint(ax, ay, bx, by, p, factor);

		int left = (ax < bx) ? ax : bx;
		int right = (ax > bx) ? ax : bx;
		int top = (ay < by) ? ay : by;
		int bottom = (ay > by) ? ay : by;

		notifyObservers(new RegionImpl(left, top, right, bottom));
		return this;
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p) {
		return paint(cx, cy, radius, p, 1.0);
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		_picture = _picture.paint(cx, cy, radius, p);

		notifyObservers(new RegionImpl((int) (cx-radius), (int) (cy-radius), (int) (cx+radius), (int) (cy+radius)));
		return this;
	}

	@Override
	public Picture paint(int x, int y, Picture p) {
		return paint(x, y, p, 1.0);
	}

	@Override
	public Picture paint(int x, int y, Picture p, double factor) {
		_picture = _picture.paint(x, y, p, 1.0);

		notifyObservers(new RegionImpl(x, y, x+p.getWidth()-1, y+p.getHeight()-1));
		return this;
	}

	@Override
	public String getCaption() {
		return _picture.getCaption();
	}

	@Override
	public void setCaption(String caption) {
		_picture.setCaption(caption);
	}

	@Override
	public SubPicture extract(int x, int y, int width, int height) {
		return _picture.extract(x, y, width, height);
	}

	@Override
	public void registerROIObserver(ROIObserver observer, Region r) {
		if (observer == null) {
			throw new IllegalArgumentException("Observer is null");
		}
		if (r == null) {
			throw new IllegalArgumentException("Region r is null");
		}

		_observers.add(new RegisteredROIObserverImpl(observer, r));
	}

	@Override
	public void unregisterROIObservers(Region r) {
		if (r == null) {
			throw new IllegalArgumentException("Region r is null");
		}
		
		List<RegisteredROIObserver> remaining_observers = new ArrayList<RegisteredROIObserver>();
		
		for (RegisteredROIObserver o : _observers) {
			try {
				Region intersection = o.getRegion().intersect(r);
			}
			catch (NoIntersectionException e) {
				remaining_observers.add(o);
			}
		}
		_observers = remaining_observers;
	}

	@Override
	public void unregisterROIObserver(ROIObserver observer) {
		if (observer == null) {
			throw new IllegalArgumentException("Observer is null");
		}
		
		List<RegisteredROIObserver> remaining_observers = new ArrayList<RegisteredROIObserver>();
		
		for (RegisteredROIObserver o : _observers) {
			if (o.getObserver() != observer) {
				remaining_observers.add(o);
			}
		}
		_observers = remaining_observers;
	}

	@Override
	public ROIObserver[] findROIObservers(Region r) {
		if (r == null) {
			throw new IllegalArgumentException("Region r is null");
		}
		
		List<ROIObserver> found_observers = new ArrayList<ROIObserver>();
		
		for (RegisteredROIObserver o : _observers) {
			try {
				Region intersection = o.getRegion().intersect(r);
				found_observers.add(o.getObserver());
			}
			catch (NoIntersectionException e) {
			}
		}
		
		return found_observers.toArray(new ROIObserver[found_observers.size()]);
	}

	@Override
	public void suspendObservable() {
		_suspended = true;
	}

	@Override
	public void resumeObservable() {
		_suspended = false;
		notifyObservers(_changed_region);
	}

}
