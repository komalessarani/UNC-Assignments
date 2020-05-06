# Assignment 5

In this repository, you should find the following complete interfaces and classes:
 * The basic underlying interface Pixel and its implementations GrayPixel and ColorPixel from assignments 3 and 4.
 * The Picture and SubPicture interfaces as defined and described in assignment 4.
 * My implementations of MutablePixelArrayPicture and ImmutablePixelArrayPicture from the assignemnt 4 solution.
 
The appropriate code for the following classes from my assignment 4 solution is missing in these classes:
 * The SubPicture interface called SubPictureImpl
 * The Pixel iterators called SampleIterator and ZigZagIterator
 * The SubPicture interators called WindowIterator and TileIterator
 
In this assignment, you'll fill out these missing implementations in order to recreate my a4 solution. Additionally, you will be asked to refactor the code of MutablePixelArrayPictrue, ImmutablePixelArrayPicture, and SubPictureImpl using inheritance.

The first thing you should do is read the implementations of MutablePixelArrayPicture and ImmutablePixelArrayPicture carefully to understand how they work. A lot of the code in these classes is repeated. You should note the ways in which the code is similar and is different. In particular, notice that the rectangle, circle, and picture paint methods of ImmutablePixelArrayPicture go through an extra step of creating an immutable result picture at the very end of these methods using a helper function called copyAsImmutable.

## Novice

Fill out the code for SubPictureImpl. You can and should copy much of this code from the other picture implementations as appropriate. Be sure to reread the A4 description for how this interface should work. In particular, pay attention to the fact that a subpicture's mutabiliity (or immutability) with respect to pixel operations should reflect the underlying source picture and that a subpicture maintains caption information independently of the underyling source picture. 

## Adept

Fill out the code for the various iterators (i.e., SampleIterator, ZigZagIterator, WindowIterator, and TileIterator). Again, look to the A4 description for how these iterators are supposed to work.

## Jedi

Refactor the code of MutablePixelArrayPicture, ImmutablerPixelArrayPicture, and SubPictureImpl into the following class hierarchy:

```
PictureImpl
     |
     |-------------------------------------------------------------------------
     |                                                                        |
PixelArrayPicture                                                        SubPictureImpl
     |
     |------------------------------
     |                             |
MutablePixelArrayPicture      ImmutablePixelArrayPicture

```

The above graph indicates that PictureImpl should be the root parent class of all Picture implementations including SubPictureImpl. PixelArrayPicture should be the common parent class of both MutablePixelArrayPicture and ImmutablePixelArrayPicture.

You should refactor the code from MutablePixzelArrayPicture, ImmutablePixelArrayPicture, and SubPictureImpl as appropriate. Please use these class names (i.e., PictureImpl and PixelArrayPicture) exactly. 

## Grading
 * Novice: 5 points
 * Adept: 15 points
 * Jedi: 5 points
 
 NOTE: all code should be in the a5 package. Do not refer to any code in any prior assignment packages.
