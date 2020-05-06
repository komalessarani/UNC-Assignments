#---------------------------------
# Lab 6: Pixel Conversion
#
# Name: <YOUR-NAME-HERE>
#
# --------------------------------
# Below is the expected output.
# 
# Converting pixels to grayscale:
# 0
# 1
# 2
# 34
# 5
# 67
# 89
# Finished.
# -- program is finished running --
#---------------------------------

.data 0x0
  startString:  .asciiz "Converting pixels to grayscale:\n"
  finishString: .asciiz "Finished.\n"
  newline:      .asciiz "\n"
  pixels:       .word   0x00010000, 0x010101, 0x6,      0x3333,
                        0x030c,     0x700853, 0x294999, -1

.text 0x3000

main:
  addi $v0, $0, 4       	# system call code 4 for printing a string
  la   $a0, startString      	# put address of startString in $a0
  syscall               	# print the string


#------- INSERT YOUR CODE HERE -------
#
# Read and understand the C version of the program, then convert
#   it to MIPS assembly.  The following gives you step-by-step
#   details on how to do it.
#
# First, make sure you have configured MARS under Settings as follows:
#   "Memory Configuration->Compact, Data at Address 0"
#   "Permit extended (pseudo) instructions and formats" should be checked
#
# Write a loop that reads the array pixels using "lw",
#   one word at a time, until a -1 is encountered, which
#   indicates the end of the array.
loop:
	lw		$t4, pixels($t0)	# $t4 = pixel[i]
	beq		$t4, -1, exit	# if pixel[i] == -1 break
	
	andi	$t3, $t4, 0xff		# $t3 = blue
	srl		$t4, $t4, 8			# t4 >> 8
	andi	$t2, $t4, 0xff		# $t2 = green
	srl		$t4, $t4, 8			# t4 >> 8
	andi	$t1, $t4, 0xff		# $t1 = red
	
	jal		rgb_to_gray			# $t5 = gray
	
	addi	$v0, $0, 1			# system call code 1 for printing integer
	add		$a0, $0, $t5		# put the value of gray into $a0
	syscall
	
	addi	$v0, $0, 4			# system call code 4 for printing string
	la		$a0, newline		# load the address of newline into $a0
	syscall
	
	addi	$t0, $t0, 4			# i = i + 1
	j		loop

#
# Each pixel value is a word, of the form 0x00RRGGBB,
#   so your program here should take the rightmost 2 hexits (8 bits)
#   and use that as the blue value, the next 2 hexits as green, and
#   the next 2 hexits as red.  The leftmost 2 hexits are not needed,
#   and will be zero.
#
# Hint:  Use the andi instruction to extract relevant bits
#   from the pixel value.  For example, andi $2, $3, 0x0000000F
#   extracts that rightmost hexit of $3 and places it in $2.
#   This is because ANDing a bit with 1 keeps the bit value, but
#   ANDing a bit with 0 turns it to 0.
#
# After extracting the R, G and B values into $a0, $a1 and $a2,
#   you must then call the procedure rgb_to_gray below using
#   "jal rgb_to_gray", which will compute and return the gray
#   value.
#
# Your code should then print this gray value on the terminal,
#   before moving on to the next pixel value.  For printing,
#   you will need to use a MIPS system call available in MARS.
#   Specifically, it is syscall 1.  The code to print an integer,
#   say the value in $8, is like this:
#
#     addi $v0, $0, 1  // put 1 in $v0 to indicate which syscall
#     add  $a0, $0, $8 // put value to be printed in $a0
#     syscall          // and then execute "syscall"
#
# Continue looping through the array "pixels", until a -1 is
#   encountered, at which point you should exit your loop.
#
# There is already code below that prints the final message "Finished.",
#   and terminate the program.
#
#
#------------ END CODE ---------------


exit:

  addi $v0, $0, 4            	# system call code 4 for printing a string
  la   $a0, finishString   	# put address of finishString in $a0
  syscall               	# print the string

  addi $v0, $0, 10      	# system call code 10 for exit
  syscall               	# exit the program



#----------------------------------------------------------#



#---- Procedure rgb_to_gray ----#
#
#
#-------------------------------#
            # procedure to calculate gray = (red + green + blue) / 3
                        # red is in $a0, green is in $a1, blue is in $a2
                        # gray should be computed in $v0 (return value)
                        # there is no need to use a stack

#------- INSERT YOUR CODE HERE -------
#
# Simply add instructions here to calculate
#   gray = (red + green + blue) / 3
#
#   i.e., $v0 = ($a0 + $a1 + $a2) / 3
#
#  That's it!
#
#------------ END CODE ---------------
rgb_to_gray:
	add		$t9, $t1, $t2		# temp = red + green
	add		$t9, $t9, $t3		# temp = temp + blue
	div		$t5, $t9, 3			# gray = temp / 3
	
	jr		$ra
