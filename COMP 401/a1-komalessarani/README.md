# Assignment 1

## A Java Programming Warm-Up

This assignment is intended to warm up your Java programming skills. It does not exercise any object-oriented programming concepts. You can (and should) simply use static public class functions and local variables. It will, however, exercise basic programming constructs such as loops and conditional execution and require you to use arrays for at least one part.

The assignment is in three parts that are in increasing level of difficulty called A1Novice, A1Adept, and A1Jedi. In all three parts, the starter code provides a class with a main method that creates an input Scanner object (the use of which will be demonstrated in class and described below). 

You are responsible, in each case, for adding code to read input from the console in a specific format, use those inputs to calculate and produce output to the console using System.out (see http://docs.oracle.com/javase/10/docs/api/java/io/PrintStream.html for its documentation).

You must match the format of the example outputs provided and should not produce any additional output of any kind. In particular, you should not print any input prompts or debugging statements.

You are free to (and in fact encouraged to) create additional static class methods to use as helper functions as you see fit (although each part can be done relatively easily without having to create any additional methods).

## Scanner

This assignment requires you to make use of a Scanner object. You can read the documentation for Scanner here: http://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html

A Scanner object is associated with a source of input. In our case, this will be keyboard input from the console. By default, a Scanner object will parse input as whitespace separated tokens and provides methods for parsing the next available token as a particular type. 

For example, the method `next()` will retrieve the next token (i.e., word) as a String. Similarly, the method `nextInt()` interprets the next token as an integer while `nextDouble()` will interpret the next token as a double value. For this assignment, you shouldn't have to use any Scanner methods other than `next()`, `nextInt()`, and `nextDouble()`. In particular, you should avoid using `nextLine()`.

For this assignment, you can assume that the input will always be valid and conform to the description below. In other words, you do not have to worry about validating the input or being able to deal with unexpected input.

## A1Novice

This program should read in data that represents COMP 401 grade components for a set of students and computes the letter grade that they should receive. The first input will be an integer greater than 0 indicating the number of students in the class. Following this will be input for each student in the following form:

FirstName LastName AssignmentGrade ParticipationGrade Midterm FinalExam

FirstName and LastName are single word tokens that represent the first and last name of a student. All of the other entries on the line will be double values between 0.0 and 100.0 representing the percentage of points earned for those components.

To calculate the letter grade of each student, you should first calculate a weighted average (WA) of the grade components as specified in our syllabus (i.e., 40% assignment, 15% participation, 20% for the midterm, 25% final). You can assume that the participation grade has already factored in the 20% discount allowing for absences described on the first day of class. You should map the weighted average to a letter grade as follows:

| Weighted Average 	| Letter Grade  |
|-------------------|---------------|
| WA >= 94%	       	| A             |
| 90% <= WA < 94%  	| A-				|
| 86% <= WA < 90%  	| B+				|
| 83% <= WA < 86%	| B				|
| 80% <= WA < 83%	| B-				|
| 76% <= WA < 80%	| C+				|
| 73% <= WA < 76%	| C				|
| 70% <= WA < 73%	| C-				|
| 65% <= WA < 70%	| D+				|
| 60% <= WA < 65%	| D				|
| WA < 60%			| F				|


For each student, you should print one line of output in the following form:

`F. Last_Name Grade`

Where "F." represents the first letter initial of the first name for the student. For example, if the input to your program was this:

```
3
Carrie Brownstein 89.5 100.0 82.1 91.5
Corin Tucker 96.0 90.0 88.0 80.0
Janet Weiss 73.5 88.8 75.0 81.3
```

Your program should produce the following output:

```
C. Brownstein A-
C. Tucker B+
J. Weiss C+
```

## A1Adept

For this program, the input again represents a COMP 401 gradebook and you will calculate grades, but the format of the input is a bit different and you will have to calculate the assignment and participation components from raw scores.

The first input will be an integer greater than 0 that indicates the total number of assignments in the course. The next set of inputs will be a list of integers that indicate the total number of points each assignment is worth. After this list will be an integer that indicates the total number of participation points available during the semester. Following this, will be an integer greater than 0 indicating the number of students in the class followed by a line for each student in the following form:

```
FirstName LastName ParticipationPoints A1_Points ... An_Points Midterm FinalExam
```

The ParticipationPoints entry will be an integer between 0 and the previously specified maximum number of participation points available during the semester. The entries A1\_Points ... An\_Points will be a series double values that indicate the number of points the student received for each assignment. Remember that you do not need to validate these values and can assume that the value given will be in the range from 0 to the maximum number of points for the assignment as indicated at the beginning of the input. The Midterm and FinalExam entries will be double values representing percentages between 0.0 and 100.0 as before.

To calculate a student's grade, you will need to first calculate the participation and assignment components as a percentage then calculate the weighted average of the participation, assignment, midterm, and final components as you did in A1Novice.

The participation percentage should be calculated relative to only 80% of the overall number of available points and should be capped at 100%. In other words, if the total number of participation points available is 125 and a particular student receives 110 participation points, their participation percentage should be calculated as 100 * (110 / (125 * 0.8)) which is 110%. Since this value is greater than 100%, this student's participation component should be capped at 100%.

The assignment percentage is simply the sum of the points earned across all assignments divided by the total number of assignment points possible and then multiplied by 100.

Your program should produce output in the same form as before in A1Novice.

Example input:

```
5
5 10 10 10 25
125
3
Carrie Brownstein 110 5 7 8.5 10 21 88.5 91.3 
Corin Tucker 85 0 10 10 8 20 75.4 81.2
Janet Weiss 70 4 9 9.5 7.5 22.5 60.2 78.3
```

Corresponding output:

```
C. Brownstein B+
C. Tucker B-
J. Weiss C+
```

##A1 Jedi

For this final program, the input will be in the same format as A1Adept except that the midterm and final exam scores will be given as raw integer scores. This means you will have to convert those raw scores into a curved percentage grade by calculating the appropriate exam curve.

To calculate the curve for an exam, first calculate the average and standard deviation for that exam. Here is a web page the explains how to calculate a standard deviation:

https://www.mathsisfun.com/data/standard-deviation-formulas.html

To calculate a square root, use the Math.sqrt() method. This method is available as part of the built-in standard Java library and you should just be able to use it directly wherever you need.

Once you have the class average and standard deviation for the raw scores, you can calculate a "normalized" score for each student using the following formula:

```
Normalized = (Raw - Average) / StdDev
```

The normalized score represents how many standard deviations above or below the average a student scored. A negative number means the student was below the average. A positive number means the student was above the average. A normalized score of 0.0 corresponds exactly to the average.

Now use the following mapping to convert the normalized score to a curved percentage scale:

| Normalized	 | Curved Percentage |
|------------|-------------------|
| >= 2.0	     | 100%  |
| 1.0	     | 94.0% |
| 0.0        | 85.0% |
| -1.0       | 75.0% |
| -1.5       | 65.0% |
| -2.0       | 55.0% |
| -3.0       | 30.0% |
| <= -4.0    | 0.0%  |


In between values should be interpolated linearly. To interpolate, first find the range in the table above within which lies the normalized score. Let's call the lower boundary of this range `low_normal` and the upper boundary of this range `high_normal`. Let's call the corresponding curved percentages for these boundaries `low_curved ` and `high_curved`. Then, given the student's normalized score, you can calculate the interpolated curved percentage result as:

```
curved_score = (((normalized_score - low_normal) /
                 (high_normal - low_normal)) *
               (high_curved - low_curved)) + low_curved
```

For example, suppose a student has a raw score of 62 on the midterm. Furthermore, suppose the class average for the midterm was 73.4 and the standard deviation was 12.8. The student's normalized score is calculated as:

```
normalized_score = (62 - 73.4 ) / 12.8
normalized_score = -0.89
```

Since -0.89 is between -1.0 and 0.0, we use -1.0 as `low_normal` and 0.0 as `high_normal`. The corresponding curved percentages are 75.0% for `low_curved` and 85.0% for `high_curved`. Using the interpolation formula above we get:

```
curved_score = ((((-0.89) - (-1.0)) /
                 (0.0 - (-1.0))) *
               (85.0 - 75.0)) + 75.0
curved_score = 76.09
```

For output, instead of printing the name and grade of each student, print out the number of students that received each letter grade as per the example below.

Example input:

```
5
5 10 10 10 25
125
5
Carrie Brownstein 110 5 7 8.5 10 21 75 94
Corin Tucker 85 0 10 10 8 20 77 90
Janet Weiss 70 4 9 9.5 7.5 22.5 76 50
Polly Perfect 125 5 10 10 10 25 95 100
Frank Failure 25 5 5 5 5 5 45 50
```

Example output:

```
A : 1
A-: 0
B+: 1
B : 1
B-: 1
C+: 0
C : 0
C-: 0
D+: 0
D : 0
F : 1
```

## Hints

A1Novice and A1Adept can be done without using arrays and by simply producing the output for each student as you read in the data for each student within a for loop.

A1Jedi is easiest to do by creating an array for each of the different grade components (participation, assignment, raw midterm, and raw final exam) with each student represented by a different index (i.e., 0 is the first student, 1 is the second student, etc.)

As you read in the input, calculate the recitation and assignment components as you did in A1Adept and store them in the corresponding array while storing the raw midterm and final scores in their corresponding arrays.

Once you have read in all of the input, calculate the average and standard deviation for the midterm and final from the values in those arrays. With these values calculated, you can now go back through one more time and for each student calculate the curved exam scores using the average and std. deviation for the midterm and final and combine that with the assignment and recitation components in order to arrive at a letter grade for each student. As you do so, keep track of the total number of each letter grade so that you can produce your output at the end.

## Grading

The assignment is worth 10 points as follows:

* 4 points for A1Novice
* 4 points for A1Adept
* 2 points for A1Jedi

## Submitting Your Code

Instructions for submitting your code to the autograder will be forthcoming.