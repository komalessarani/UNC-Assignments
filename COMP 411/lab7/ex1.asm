.data
  AA:     .space 400  		# int AA[100]
  BB:     .space 400  		# int BB[100]
  CC:     .space 400  		# int CC[100]
  m:      .space 4   		# m is an int whose value is at most 10
                     		# actual size of the above matrices is mxm
  newline: .asciiz "\n"
  space: .asciiz " "
      # You may add more variables here if you need to



.text

main:
	li $v0, 5					#get m
	syscall
	add $s0, $v0, $zero				#store m in s0
	addi $t0, $zero, 0				#i =0 
outer1:
	beq $s0, $t0, outer2				#if i = m, go to next thing
	addi $t0, $t0, 1				#i++
	addi $t1, $zero, 0				#j = 0
	inner1:
	beq $s0, $t1, outer1
	mult $s0, $t0					#i*m
	mflo $t2
	add $t2, $t2, $t1				#i*m + j
	sll $t2, $t2, 2					#align ^
	li $v0, 5
	syscall
	sw $v0, AA($t2)
	addi $t1, $t1, 1				#j++
	j inner1
	j outer1
	
	addi $t3, $zero, 0				#i =0 
outer2:
	beq $s0, $t3, outer3				#if i = m, go to next thing
	addi $t3, $t3, 1				#i++
	addi $t1, $zero, 0				#j = 0
	inner2:
	beq $s0, $t1, outer2
	mult $s0, $t3					#i*m
	mflo $t4
	add $t4, $t4, $t1				#i*m + j
	sll $t4, $t4, 2					#align ^
	li $v0, 5
	syscall
	sw $v0, BB($t4)
	addi $t1, $t1, 1				#j++
	j inner2
	j outer2
	
	addi $t5, $zero, 0				#i=0
outer3:
	beq $s0, $t5, print				
	addi $t5, $t5, 1				#i++
	addi $t0, $zero, 0
	inner3: 
	beq $s0, $t0, outer3
	mult $t5, $s0
	mflo $t6
	add $t6, $t6, $t0				#i*m+j
	sll $t6, $t6, 2
	sw $0, CC($t6)
	addi $t0, $t0, 1				#j++
	addi $t1, $zero, 0				#k = 0
	inner4:
	beq $s0, $t1, inner3
	add $t9, $t6, $t0				#i*m+j
	add $s1, $t6, $t1				#i*m+k
	mult $t1,$s0
	mflo $s2
	add $s2,$s2, $t0				#k*m+j 
	j inner4
	j inner3
	j outer3

	addi $t7, $zero, 0
print:
	beq $s0, $t7, exit
	addi $t1, $zero, 0
	addi $t7, $t7, 1
	innerprint:
	beq $s0, $t1, print
	mult $s0, $t7
	mflo $t8
	add $t8, $t8, $t1
	sll $t8, $t8, 2
	li $v0, 1
	move $a0, $t8
	syscall
	addi $v0, $0, 4
	la $a0, space
	syscall
	addi $t1, $t1, 1
	j innerprint
	j print
	
#------- INSERT YOUR CODE HERE for main -------
#
#  Best is to convert the C program line by line
#    into its assembly equivalent.  Carefully review
#    the coding templates near the end of Lecture 8.
#
#
#  1.  First, read m (the matrices will then be size mxm).
#  2.  Next, read matrix A followed by matrix B.
#  3.  Compute matrix product.  You will need triple-nested loops for this.
#  4.  Print the result, one row per line, with one (or more) space(s) between
#      values within a row.
#  5.  Exit.
#
#------------ END CODE ---------------


exit:                     # this is code to terminate the program -- don't mess with this!
  addi $v0, $0, 10      	# system call code 10 for exit
  syscall               	# exit the program



#------- If you decide to make other functions, place their code here -------
#
#  You do not have to use helper methods, but you may if you would like to.
#  If you do use them, be sure to do all the proper stack management.
#  For this exercise though, it is easy enough to write all your code
#  within main.
#
#------------ END CODE ---------------
