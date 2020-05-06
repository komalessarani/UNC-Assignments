# Assignment 4

For this assignment, you will write at least 10 JUnit tests that test some aspect of the code described below. 


In this repository, you'll find an a4 package with the following interfaces and classes already written:
 * The Pixel interface along with two implementations GrayPixel and ColorPixel.
   * These are the same as in a3.
 * The Picture interface.
   * This is mostly the same as before with the following changes:
     * There is a new form of the paint method which allows the pixel information of one Picture object
       to be painted on to another Picture object.
     * Pictures now have a String caption that can be set or retrieved. Note that from now on, when
       an implementation of Picture is described as "immutable", we mean that only with respect to
       pixel information. All Picture implementations are mutable with respect to caption information.
     * The extract method of a Picture object can be used to create a SubPicture object that represents a 
       rectangular region within the picture. See below for more information about SubPicture objects.
     * The sample method of a Picture object creates a Pixel iterator that produces Pixel objects from the
       picture. The iterator begins with the specified initial pixel location and then samples left to right
       the specified dx columns and then top to bottom by the specified dy rows. For example, suppose
       picture p is 15 pixels wide and 10 pixels tall and a pixel iterator is created by calling
       the sample method as follows:
       ```
       Iterator<Pixel> sample_iter = p.sample(2,3,3,4);
       ```
       You should expect sample_iter to produce pixels from p at the following coordinates in this order:
       (2,3) (5,3) (8,3) (11,3) (14,3) (2,7) (5,7) (8,7) (11,7) (14,7)
     * The window and tile methods produce SubPicture iterators. The window method should produce a sequence of 
       SubPicture objects as if you were sliding a "window" that was window_width wide and window_height tall across the picture 
       starting from the upper left corner and proceeding from left to right and top to bottom until the window hits 
       the lower right corner.
     
       For example, if picture p is 5 pixels wide and 5 pixels tall, and the window method was called as follows:
       ``` 
       Iterator<SubPicture> window_iter = p.window(3, 2);
       ```
       The resulting iterator (i.e, window_iter) should produce a sequence of SubPicture objects that is 
       equivalent to the results of the following sequence of calls to extract():
       ```
       p.extract(0,0,3,2)
       p.extract(1,0,3,2)
       p.extract(2,0,3,2)
       p.extract(0,1,3,2)
       p.extract(1,1,3,2)
       p.extract(2,1,3,2)
       p.extract(0,2,3,2)
       p.extract(1,2,3,2)
       p.extract(2,2,3,2)
       p.extract(0,3,3,2)
       p.extract(1,3,3,2)
       p.extract(2,3,3,2)
       ```
       
       The tile method should produce a sequence of SubPicture objects as if you had cut the original picture 
       into tiles that were tile_width wide and tile_height tall. Again, the SubPicture for the upper left tile is
       produced first and the iterator proceeds left to right and top to bottom. Partial tiles if the original 
       picture width/height is not a perfect multiple of the tile width/height are not produced. (i.e., 
       portion of the picture at the right or bottom that does not fit into a whole tile are not included in any
       tile produced by the iterator).

       For example, if the picture p is 5 pixels wide and 5 pixels tall, and the tile method was called as follows:
        ```
        Iterator<SubPicture> tile_iter = p.tile(2, 2);
        ```
       The resulting iterator (i.e, tile_iter) should produce a sequence of SubPicture objects that is equivalent to 
       the results of the following sequence of calls to extract():
       ```
       p.extract(0,0,2,2);
       p.extract(2,0,2,2);
       p.extract(0,2,2,2);
       p.extract(2,2,2,2);
       ```
     * The zigzag method produces a Pixel iterator that iterates over the pixels of a Picture in zigzag order as shown
       in this example:
       
       ![Zig Zag Order Example](http://www.cs.unc.edu/~kmp/comp401fall18/assignments/a4/zigzag.gif "Zig Zag Order Example")
       
       Note that the first move is to go from the upper-left corner across to the right if possible. 
       The pattern will work for any size picture (it need not be a perfect square). A good way of looking at the pattern is to 
       think of the picture as a series of diagonals that are either "even" or "odd" where the coordinate (0,0) 
       is on an even diagonal. Generally, the pattern traverses the pixels going up and to the right on even diagonals 
       and down and to the left on odd diagonals. 
     
   * The SubPicture interface.
     * The idea behind SubPicture is that it represents a subarea of some other Picture object. When a 
       SubPicture object is created, a source picture and an offset (xOffset, yOffset) is specifed.
       The origin (i.e., pixel at coordinates (0,0)) of the subpicture corresponds to the pixel 
       (xOffset, yOffset) of the source picture. These values can be retrieved via the getter methods
       defined in the SubPicture interface. 
       
       Because the SubPicture interface is an extension of the Picture
       interface, a SubPicture object also implements all of the methods in Picture including the getters
       getWidth, getHeight, and getPixel along with all of the various forms of paint and the getter/setter for
       the caption. The getWidth and getHeight methods return the width and height of the subpicture region.
       The getPixel method of a SubPicture will translate the (x,y) coordinates provided by 
       xOffset and yOffset and retrieve the corresponding pixel from the source picture.
       The paint operations are delegated to the underlying source picture object and the result
       of a paint operation for a SubPicture will either be itself if the underlying source Picture 
       object is mutable with respect to pixel information or a new SubPicture object if the 
       underlying source Picture object is immutable with respect to pixel information. 
       
       A SubPicture maintains its own caption information independently of the underlying source Picture.
       A SubPicture created as a result of the extract() method on a Picture will start with a caption that
       is the same as the source picture.

## What To Do

In the package a4test you will find the class A4Test. This class is already setup to import the corresponding a4 package it is meant to test. You will need to add JUnit 4 to your project setup. In A4Test is a static method called getTestNames. This method needs to return an array of strings that correspond to each JUnit test in the class. You will see that A4Test currently contains an empty example test method which should serve as an example of the correct way to declare a JUnit test within the class. In particular, you must have the "@Test" annotation and the correct signature (i.e., public void). You can remove the example test if you want to. If you do, however, be sure to remove its name from the array of test names returned by getTestNames.

Write 10 JUnit tests that test various aspects of the interfaces described above in A4Test. Submit by pushing a 'submit' branch to GitHub. Your tests will be tested against an implementation of these interfaces that is known to work. This means if any of your tests fail, it is because there is a bug in your test (not the implementation). This implementation will be made available to you as a downloadable, precompiled, JAR file that you can add to your project so that you can run the tests yourself. The link to this implementation will be made available on Piazza soon. 

The implementation will include the following two implementations of Picture:
 * a4.MutablePixelArrayPicture
   This will be a mutable (with respect to pixel information) implementation with the following constructor:
   ```
   MutablePixelArrayPicture(Pixel[][] pixels, String caption);
   ```
 * a4.ImmutablePixelArrayPicture
   This will be an immutable (with respect to pixel information) implementation with the following constructor:
   ```
   ImmutablePixelArrayPicture(Pixel[][] pixels, String caption);
   ```
   
   ## Grading
   
   Each test will be worth one point for a maximum of 10 points.
   
