# Starter file for ex1.asm

.data 0x0
newline: 		.asciiz "\n"
pattern: 		.space 80

.text 0x3000
.globl main

#a0 is n
#a1 is currentlevel

main:
	ori $sp, $0, 0x3000	#initialize stack pointer to the top word below .text
	addi $fp, $sp, -4	#set $fp to start of main's stack frame
	
	li $v0, 5		#scan N, store $v0
	syscall
	
	ori $a0, $v0, 0		#put N in $a0
	ori $a1, $0, 0		#put currentlevel in $a1
	jal makePatterns
	
	j end
end: 
	ori   $v0, $0, 10     # system call 10 for exit
	syscall               # we are out of here.



makePatterns:    		
	addi   $sp, $sp, -8		# move stack pointer
	sw      $ra, 4($sp)		# store return address
	sw      $fp, 0($sp)
	
	addi	$fp, $sp, 4
	addi 	$sp, $sp, -8
	sw 	$a0, 4($sp)
	sw	$a1, 0($sp)
	
	beq $a1, $a0, print		#if currentlevel = N, go to print
	sll $t0, $a1, 2			#pattern[currentlevel]
	sb $0, pattern($t0)		
	addi $a1, $a1, 1		#currentlevel + 1

	jal makePatterns
	
	addi $a1, $a1, -1		#go back to original currentlevel
	sll $t1, $a1, 2			#in order to set pattern[currentlevel]
	addi $t2, $0, 1
	sb $t2, pattern($t1)
	addi $a1, $a1, 1
	jal makePatterns
	j restore
	
print:
	addi $t4, $0, 0
	loop:
	sll $t1, $t4, 2
	lb $a0, pattern($t1)
	li $v0, 1
	syscall
	
	addi $t4, $t4, 1
	lw $a0, -8($fp)
	
	bne $t4, $a0, loop
	
	li $v0, 4
	la $a0, newline
	syscall
	
restore:
	lw      $a0, -8($fp)		# save temp1
	lw      $a1, -12($fp)		# save temp0
	addi 	$sp, $fp, 4
	lw      $ra, 0($fp)		# restore return address
	lw	$fp, -4($fp)
	jr	$ra
