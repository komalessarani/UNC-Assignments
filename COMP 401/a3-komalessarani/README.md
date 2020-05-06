# Assignment 3

## Pixels and Pictures

Digital pictures are usually represented as a two-dimensional grid of "picture elements" or "pixels". Each pixel represents the color of a particular spot on the picture and the resolution of a picture can be expressed in terms of how wide and tall the picture is in pixel units.  

### Pixels

A color (to a computer) is a specific formula of three components: red, green, and blue. We will be using values in the range of 0.0 up to 1.0 for each of these components. A 0.0 value represents no amount of that component and 1.0 is the maximum amount of that component. When the red, green, and blue components all equal each other, you get a color on the "grayscale" spectrum from black (all 0.0's) to white (all 1.0's). The chromatic colors are formed when the values for red, green, and blue differ from each other.

You can read more about the RGB color model here if you want to learn more: http://en.wikipedia.org/wiki/Red_green_blue

In this assignment, I have provided classes for both color pixels and grayscale pixels (i.e., pixels where the red, green, and blue components by definition are always equal to each other) as two possible implementations of a common Pixel interface. Instances of these classes will be immutable, so they won't change once created. 

You should read through the code for Pixel, ColorPixel, and GrayPixel to understand what the methods of the interface do and how these two classes implement that interface.

### Pictures

The Picture interface defines an abstraction for representing a 2-dimensional frame of pixels. This abstraction will provide a number of methods to query and possibly set various properties including individual pixel values. Individual pixel values are addressed by their position (i.e., x and y coordinates) within the frame. The x-coordinate represents the column of the pixel and the y-coordinate represents the row of the pixel. The top of the picture is the first (i.e., 0th row) and the bottom is the last row. This means the upper left corner of the picture has the (x,y) coordinate (0,0) and the lower right picture has the coordinate (w-1, h-1) where w and h are the width and height of the picture.

Read through the code for Picture and be sure you understand what each of the methods is supposed to do. In particular:

 * Notice that the various forms of the paint method return a Picture object with the required changes if any.  If the underlying implementation of Picture is mutable (i.e., is allowed to change the value of one or more of its fields including element values of arrays), then the return value of these methods will be the original object since that object reflects the change. Getting back the same object that you started with implies that the object was mutated. However, immutable implementations of picture are possible. Such an implementation will need to create a new Picture object that reflects the result of the paint operation and return that new object as the result. Getting back a different object than you started with thus implies immutability. For this assignment, the resulting object returned as a result of a paint method is allowed to be either mutable or immutable. This will come up again later. 
 * The form of the paint method that paints a region specifies two opposite corners, namely (ax, ay) and (bx, by). Depending on the values of ax, ay, bx, and by these might represent the upper left and lower right corners or these might represent the lower left and upper right corners. Which situation is in effect is determined by the values provided and you should not make any assumptions about which corner will be which when the method is called. 
 * All parameters should be checked for being within their legal values (i.e., coordinates are all non-negative and within the picture's dimensions, pixel values are non-null, factor values are between 0.0 and 1.0, etc.). Any illegal values should result in throwing an IllegalArgumentException. 
 
## Novice

Create two implementations of Picture as follows.

 * MutablePixelArrayPicture
   * MutablePixelArrayPicture should implement Picture by encapsulating a 2D array of pixels that are mutable (i.e., allowed to change). It should have the following constructor forms:   
   ```
   // Creates new object using values provided by pixel_array, matching in size. 
   public MutablePixelArrayPicture(Pixel[][] pixel_array);
   
   // Creates new object by providing geometry of picture and an initial value for all pixels.
   public MutablePixelArrayPicture(int width, int height, Pixel initial_value);
   
   // Creates new object by providing geometry of picture. 
   // Initial value of all pixels should be medium gray (i.e., a grayscale pixel with intensity 0.5)
   public MutablePixelArrayPicture(int width, int height);
   ```
   The first dimension of pixel_array is the width and the second is the height. In other words, pixel_array.length will be the width of the picture and pixel_array[0].length will be the height of the picture. The pixel at coordinate (x,y) is located at pixel_array[x][y]. 
   
 * MonochromePicture
   * MonochromePicture should implement a Picture that has the same value for Pixel in every position. This value is provided to the constructor along with the width and height of the picture. The constructor should have the following form:
   ```
   public MonochromePicture(int width, int height, Pixel value)
   ```
   MonochromePicture should NOT create and encapsulate an array of Pixel objects. The three values provide to the constructor are the only fields it should need to encapsulate. This kind of Picture is immutable by definition and should create new objects to return as the result of its paint methods.
   
Submit novice as a branch called 'submit-novice'.

 ## Adept
 Create four more implementations of Picture as follows.

 * ImmutablePixelArrayPicture
   * ImmutablePixelArrayPicture should implement Picture by encapsulating a 2D array of pixels that are immutable (i.e., NOT allowed to change). It should have the following constructor forms:   
   ```
   // Creates new object using values provided by pixel_array, matching in size.
   public ImmutablePixelArrayPicture(Pixel[][] pixel_array);
   
   // Creates new object by providing geometry and initial value for all pixels.
   public ImmutablePixelArrayPicture(int width, int height, Pixel initial_value);
   
   // Creates new object by providing geometry. Initial value should be medium gray.
   public ImmutablePixelArrayPicture(int width, int height);
   ```
 
 * GradientPicture
   * GradientPicture should implement a Picture that is a smooth blend of pixel values specified for its four corners. In other words, any pixel in the middle of the picture is a proportional blend of the pixel values associated with its corners. The blend is inversely proportional to the distance of the pixel to those corners. For example, pixel values along the top row of the picture start off as the specified upper_left value and then become more and more like the upper_right value as you go across (HINT: use the blend method of Pixel). The constructor of GradientPicture should have the form:
   ```
   public GradientPicture(int width, int height, Pixel upper_left, Pixel upper_right, Pixel lower_left, Pixel lower_right)
   ```
   Like MonochromePicture, GradientPicture should only need to encapsulate the values of the parameters provided to the constructor and should be immutable once created.
   
   When calculating a pixel value in the middle of the picture at (x,y), your best approach is to first calculate the value of the pixel at the beginning of the desired row (i.e., at (0,y)) as the appropriate blend of the upper left and lower left corners. Then calculate the value of the pixel at the end of the row (i.e. at (getWidth()-1,y)). Now calculate the value of (x,y) as the appropriate blend of the beginning and end of the row.
   
 * HorizontalStackPicture and VerticalStackPicture
   * These implementations will encapsulate references to two Picture objects and will represent them as if they were a larger Picture object that resulted from "stacking" them either horizontally or vertically. The constructors for these new classes should have the following form:
   
   ```
   public HorizontalStackPicture(Picture left, Picture right)
   public VerticalStackPicture(Picture top, Picture bottom)
   ```
   
   For example, if I have two Picture objects that are 5 pixels tall and 10 pixels wide (call them A and B), and then create a new HorizontalStackPicture object using A as my "left" and B as my "right", then the resulting object will be a Picture object that is 20 pixels wide and 5 pixels tall. In the new object, the upper left pixel at (0,0) will correspond to the pixel at (0,0) in A and the lower right pixel at (19,4) will correspond to the pixel at (9,4) in B. The following picture may help illustrate the situation:
   
   ![Horizontal Stack Picture Example](http://www.cs.unc.edu/~kmp/comp401fall18/assignments/a3/horiz-stack-example.png "Horizontal Stack Picture Example")

   The constructors should throw an IllegalArgumentException if any of the parameters are null or if the geometry of the objects provided are not compatible (i.e., if the heights of left and right differ for HorizontalStackPicture or if the widths of top and bottom differ for VerticalStackPicture).
   
   These classes should be implemented as mutable and the value returned from paint should be the original object updated to reflect the appropriate changes.

Submit adept as a branch called 'submit-adept'.
   
## Jedi

Create an interface called PixelTransformation as follows:

```
public interface PixelTransformation {
   Pixel transform(Pixel p);
}
```

Now create two implementations of PixelTransformation called: Threshold and GammaCorrect

The constructor for Threshold should have this signature:

```
public Threshold (double threshold)
```

The transform method of a Threshold object should produce either a white pixel or a black pixel depending on the intensity (i.e., brightness) of the pixel p passed to it. If p’s brightness is strictly above the threshold value specified in the constructor, then a white pixel is returned. Otherwise, a black pixel is returned.

The constructor for GammaCorrect should have this signature:

```
public GammaCorrect (double gamma)
```

The transform method of a GammaCorrect object should produce a "gamma corrected" version of pixel p passed to it. The components (i.e., red, green, and blue) of the gamma corrected pixel are the result of raising them to the (1.0/gamma) power. In other words, if "old" is the original component value in the range from 0.0 to 1.0, then "new" can be calculated as:

```
new = Math.pow(old, (1.0/gamma));
```

Create a new implementation of Picture called TransformedPicture. The constructor should have the following signature:

```
public TransformedPicture (Picture source, PixelTransformation xform)
```

A TransformedPicture should encapsulate the provided source Picture object and PixelTransformation object. A TransformedPicture object is expected to transform the pixel values of the source frame on demand when getPixel is called using the pixel transformation object provided to the constructor.

The implementation of TransformedPicture is expected to be immutable. 

# Hints

Start by trying to implement as many of the methods of the Picture interface as default implementations defined in the interface itself. This will reduce the number of methods you actually need to implement for each type of Picture. 

The easiest way to implement the paint methods for any of the immutable picture types is to create a 2D array of pixels, copy all of the pixels from the current object to this new 2D array, make the changes needed for the paint operation, and then return a new MutablePixelArrayPicture object created from this 2D array. With this approach, the first paint operation on any immutable implementation will require copying everything into a new mutable implementation and then any subsequent paint operations will not since the mutable implementation can make changes directly.

However, there is another approach for immutable picture types that involves creating new classes that implement Picture which represent the result of applying a particular paint operation on a picture object without actually changing any pixels or creating a 2D array of pixels for a new object. In fact, in this approach the resulting objects returned by the paint methods are themselves also immutable. This approach makes the paint operations very efficient computationally at the cost of having to do more work when the values of the pixels are retrieved (i.e., getPixel() becomes less efficient). In terms of memory, this alternate approach may or may not use less memory depending on the overall number and type of paint operations that occur. I'm purposefully not describing this approach in more detail to leave it as an exercise to the student. I recommend completing the assignment the easy way first and then if you are up to it, thinking about how this alternate approach might work and trying to implement it.

# Grading

* 2 points for Novice
* 6 points for Adept
* 2 points for Jedi

