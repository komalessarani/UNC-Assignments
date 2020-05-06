# Starter file for ex1.asm

.data
	#...
        
.text 

main:
	#...
	#...
	addi $a0, $zero, 0			#n = 0
	addi $a1, $zero, 0			#k = 0
	
	while:
	li $v0, 5
	syscall
	move $a0, $v0				#get n
	beq $a0, $zero, end			#if n = 0, exit
	li $v0, 5
	syscall
	move $a1, $v0				#get k
	jal NchooseK
	move $a0, $v0
	jal print
	j while


print:
	addiu   $sp, $sp, -4
	sw      $ra, 0($sp)
	li      $v0, 1
	syscall
	li      $a0, '\n'
	li 	$v0, 11
	syscall
	lw      $ra, 0($sp)
	addiu   $sp, $sp, 4
	jr      $ra
	#----------------------------------------------------------------#
	# Write code here to do exactly what main does in the C program.
	#
	# Please follow these guidelines:
	#
	#	Use syscall 5 each time to read an integer (scanf("%d", ...))
	#	Then call NchooseK to compute the function
	#	Then use syscall 1 to print the result
	#   Put all of the above inside a loop
	#----------------------------------------------------------------#


end: 
	ori   $v0, $0, 10     # system call 10 for exit
	syscall               # we are out of here.



NchooseK:    		# PLEASE DO NOT CHANGE THE NAME "NchooseK"
	#----------------------------------------------------------------#
	# $a0 has the number n, $a1 has k, from which to compute n choose k
	#
	# Write code here to implement the function you wrote in C.
	# Your implementation MUST be recursive; an iterative
	# implementation is not acceptable.
	#
	# $v0 should have the NchooseK result to be returned to main.
	#----------------------------------------------------------------#

	#...
	#...
	addiu   $sp, $sp, -16		# move stack pointer
	sw      $ra, 0($sp)		# store return address
	sw      $s2, 4($sp)		# saved temp2
	sw      $s1, 8($sp)		# saved temp1
	sw      $s0, 12($sp)		# saved temp0
	
	beq $a1, $0, return1
	beq $a0, $a1, return1
	beq $a0, $0, return0
	blt $a0, $a1, return0
	
	addiu $a0, $a0, -1		#n-1
	move $s0, $a0			#move new value to s0
	move $s1, $a1			#move k to s1
	jal NchooseK			#loop to nchoosek with values n-1 and k
	move $s2, $v0			#move the value returned to s2
	move $a0, $s0			#move n back to a0
	addi $a1, $s1, -1		#k-1
	jal NchooseK			#loop to nchoosek with values with k-1 and n-1
	addu $v0, $v0, $s2		#add the two loop values returned
	j return			#go to return
	
	return0:
	li $v0, 0
	j return
	
	return1:
	li $v0, 1
	
	return:
	lw	$s0, 12($sp)		# restore save temp2
	lw      $s1, 8($sp)		# save temp1
	lw      $s2, 4($sp)		# save temp0
	lw      $ra, 0($sp)		# restore return address
	addiu   $sp, $sp, 16		# release stack memory
	jr	$ra			# return to main
