# Assignment 6

The goal of this assignment is to:
* Exercise the Observer/Observable design pattern
* Exercise the Decorator design pattern

Read through the code in the a6 package and make sure you understand it. For the most part, it should be pretty familiar by now. I've streamlined the Picture interface a bit by removing all of the iterators introduced in a5. In addition to the interfaces Picture and SubPicture along with their implmentations are three new interfaces described as follows:

* Region

  This abstraction represents a rectangular area in coordinate space. A region is defined by "top" and "bottom" y-coordinates and a "left" and "right" x-coordinates. An (x,y) pair that is within these coordinates (inclusive), is inside the region. These boundary coordinate values are available via the getters, getTop(), getBottom(), getLeft(), and getRight(). 
  
  A Region also has methods for calculating its intersection and union with another Region. The intersect method should throw a NoIntersectionException (also defined in this package) if there is no intersection with the region passed in as a parameter or if the parameter value passed in is null. The union method should produce a Region object corresponding to the smallest encompassing area of itself and the Region passed in as a parameter. If null is passed to a Region's union method, the method should return itself (i.e., the object it was called on). In other words, while it is illegal to ask for the a region's intersection with null, asking for the union with null is legal. 
  
* ObservablePicture

  This interface extends the Picture interface and represents a mutable picture that is capable of registering "region of interest observers" that implement the ROIObserver interface (described below). The methods of this interface include:
  
  * registerROIObserver(ROIObserver o, Region r)
  
    This method registers the observer "o" with the region "r". This means that if a paint operation affects a pixel within the specified region, this observer should be notified. This observer should NOT be notified of paint operations that only affect pixels outside of the region. The same observer can be registered multiple times with possibly different specified regions of interest.

  * unregisterROIObservers(Region r)
  
    This method unregisters any previously registered observers that have an observed region of interest that intersects the specified region r.

  * unregisterROIObserver(ROIObserver o)
  
    This method unregisters a previously registered observer. Note, this observer may have been registered multiple times with different specified regions of interest. All registrations associated with this observer should be unregistered.

  * findROIObservers(Region r)
  
    This method returns an array of ROIObserver objects (possibly empty) that have registered regions of interest that intersect the given region "r". The same observer object may appear more than once in the result if it happens to be registered more than once with regions of interest that intersect the specified region.

  * suspendObservable()
    
    This method temporarily suspends notification of registered observers. This makes it possible to make changes using a number of paint operations and then notify registered observers of all of the changes at once when resumeObservable() is called.

  * resumeObservable()

    This method resumes notification of registered observers if previously suspended. Observers are notified of all changes made between suspension and resumption immediately upon calling this method.

* ROIObserver

  This interface is the required interface for any object that wants to register with an ObservablePicture as an observer. It includes only a single method:

  * notify(ObservablePicture p, Region r)
  
    This is the method that is called when the observer is being notified of changes. The first argument is the ObservablePicture object that is notifying the observer. The second argument is the intersection of the observer’s registered region of interest and the region of the frame that encompasses all of the changes to be reported. If a ROIObserver is registered more than once with different regions that intersect the changed region, the notify method is called once for each registration with the appropriate intersection.
    
## How ROIObserver Should Work

As described above, a registered ROIObserver should be notified whenever one or more pixels within its region of interest has possibly changed due to a paint operation. By using suspendObservable() and resumeObservable(), the observable picture may in fact report the pixel changes of multiple paint operations all at once. The second argument to the notification method describes the intersection of the region encompassing all of the changed pixels being reported and the region of interest associated with the registered ROIObserver. 

Below is an example scenario that should illustrate how this works. In this example, I’ll use the following shorthand to specificy a region that is defined by the corners (a,b) and (c,d) where (a,b) is the top left of the region and (c,d) is the bottom right of the region:

```
(a,b)->(c,d)
```

Suppose we have a single ObservablePicture object P that is 10 pixels wide and 10 pixels tall.

Suppose ROIObserver A is registered as an observer of P with the region of interest (1,1)->(5,5).

Suppose ROIObserver B is registered as an observer of P twice; once with the region of interest (0,0)->(3,3) and once with the region (2,2)->(7,7).

If we have not called suspendObservable() on P, then calling any paint method will cause these registered observers to be notified if the region that encompasses the changed pixels intersects with the registered region of interest. So, for example, if we call the single pixel version of the paint method on P at position (1,1), then A will be notified once with the region (1,1)->(1,1) as the second argument to the notification method and B will also be notified once, again with the region (1,1)->(1,1) as the second argument to the notification method. Similarly, if we call the rectangular region paint method on P in order to paint the rectangular region from (1,1)->(4,4), then A will be notified once with the region (1,1)->(4,4) as the second argument since the changed region lies entirely within the registered region of interest. B, however, will be notified twice. Once with the region (1,1)->(3,3) as the second parameter because that is the intersection of the region of interest (0,0)->(3,3) with the changed region (1,1)->(4,4); and a second time with the region (2,2)->(4,4) as the second parameter because that is the intersection of the region of interest (2,2)->(7,7) with the changed region (1,1)->(4,4). 

Paint operations that only affect pixels outside of a region of interest do not result in notification. For example, if we call the single pixel paint method on P at position (8,8), no one is notified because the affected pixel position is outside of any registered regions of interest. 

For the circular version of paint, you should inform observers as if the entire rectangular region from (cx-radius, cy-radius) -> (cx+radius, cy+radius) is affected.

Things are a bit trickier if we use suspendObservable() and resumeObservable(). In this case, the ObservablePicture object needs to keep track of the smallest region that encompasses all pixel changes between the time suspendObservable() is called and the time when resumeObservable() is called. At the time that resumeObservable() is called, all registered observers with a region of interest that intersects this accumulated region of changes will be notified. The second argument to the notification method should be the intersection of the region encompassing the pixel changes and each registered region of interest.

For example, suppose we call suspendObservable() and then use the single pixel paint method on P to change the following pixel positions: (2,1), (3,2), and (4,4).

At this point, the changes associated with these multiple calls to paint is encompassed by the region (2,1)->(4,4).

Now if we call resumeObservable(), A should be notified with region (2,1)->(4,4) because that is the intersection of the overall region that changed and its region of interest, namely (1,1)->(5,5). 

B, however, should be notified twice; once with the region (2,1)->(3,3) as the intersection of the changed region and the region of interest (0,0)->(3,3) and once with the region (2,2)->(4,4) as the intersection of the changed region and the region of interest (2,2)->(7,7).

## What To Do

First, create an implementation of the Region interface called RegionImpl. The constructor for RegionImpl should have the following declaration:

```
public RegionImpl(int left, int top, int right, int bottom)
```

The RegionImpl constructor should throw an IllegalArgumentException if left is greater than right or top is greater than bottom.

Second, create an implementation of ObservablePicture called ObservablePictureImpl that uses the decorator pattern to wrap an existing Picture object and makes it observable as described above. The constructor for ObservablePictureImpl should have the following declaration:

```
public ObservablePictureImpl(Picture p)
```

The Picture to be decorated is passed in as the parameter p. Note that while ObservablePictureImpl object is mutable with respect to paint operations (i.e., always returns itself), the underlying decorated picture object may or may not be. In other words, the ObservablePictureImpl object may end up replacing its decorated Picture object if painting on it results in a different object. 

## Grading

This assignbment is worth 20 points. It is not officially separated into subparts, but you can expect up to 5 points will come from testing your implementation of Region and the rest from testing your implementation of ObservablePicture.

