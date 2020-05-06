.data 0x0
	stack:		.space 80					# 20 * 4 = 80
	postfix:	.space 32

.text 0x3000
main:
	# top -> $s0 (stack)
	# p -> $s1 (postfix)
	
	la		$a0, postfix
	ori		$a1, $zero, 32
	ori		$v0, $zero, 8
	syscall
	
	lbu		$t1, postfix($zero)
	addi	$t0, $zero, '0'
	beq		$t0, $t1, exit
	
	add		$s0, $zero, $zero
	add		$s1, $zero, $zero

parse:
	# get(postfix, p), change to p will be made directly to p,
	#	and the result would be in $v0
	jal		get
	
	slt		$t0, $v0, $zero
	bne		$t0, $zero, operator
	
number:
	sw		$v0, stack($s0)
	addiu	$s0, $s0, 4
	
	j		parse

operator:
	addi	$t0, $zero, -1
	beq		$t0, $v0, case_1
	
	addi	$t0, $zero, -2
	beq		$t0, $v0, case_2
	
	addi	$t0, $zero, -3
	beq		$t0, $v0, case_3
	
	addi	$t0, $zero, -4
	beq		$t0, $v0, case_4
	
	addi	$t0, $zero, -5
	beq		$t0, $v0, case_5
	
case_1:
	addiu	$s0, $s0, -4
	lw		$t2, stack($s0)
	addiu	$s0, $s0, -4
	lw		$t1, stack($s0)
	
	add		$t0, $t1, $t2
	sw		$t0, stack($s0)
	addiu	$s0, $s0, 4
	
	j		parse

case_2:
	addiu	$s0, $s0, -4
	lw		$t2, stack($s0)
	addiu	$s0, $s0, -4
	lw		$t1, stack($s0)
	
	sub		$t0, $t1, $t2
	sw		$t0, stack($s0)
	addiu	$s0, $s0, 4
	
	j		parse

case_3:
	addiu	$s0, $s0, -4
	lw		$t2, stack($s0)
	addiu	$s0, $s0, -4
	lw		$t1, stack($s0)
	
	mult	$t1, $t2
	mflo	$t0
	sw		$t0, stack($s0)
	addiu	$s0, $s0, 4
	
	j		parse

case_4:
	addiu	$s0, $s0, -4
	lw		$t2, stack($s0)
	addiu	$s0, $s0, -4
	lw		$t1, stack($s0)
	
	div		$t1, $t2
	mflo	$t0
	sw		$t0, stack($s0)
	addiu	$s0, $s0, 4
	
	j		parse
	
case_5:
	addiu	$s0, $s0, -4
	lw		$a0, stack($s0)
	ori		$v0, $zero, 1
	syscall
	
	addi	$a0, $zero, '\n'
	ori		$v0, $zero, 11
	syscall

end_parse:
	j		main

exit:
	ori		$v0, $zero, 10
	syscall
	
get:
	# $s1 = p
	# while (postfix[p] == ' ') p++;
	lbu		$t1, postfix($s1)
	addi	$t0, $zero, ' '
	bne		$t0, $t1, begin
	
	addiu	$s1, $s1, 1
	j		get
	
begin:
	lbu		$t1, postfix($s1)

	addi	$t0, $zero, '+'
	beq		$t0, $t1, return_1
	
	addi	$t0, $zero, '-'
	beq		$t0, $t1, return_2
	
	addi	$t0, $zero, '*'
	beq		$t0, $t1, return_3
	
	addi	$t0, $zero, '/'
	beq		$t0, $t1, return_4
	
	addi	$t0, $zero, '\n'
	beq		$t0, $t1, return_5
	
	add		$t2, $zero, $zero					# $t2 = temp
normal:
	lbu		$t1, postfix($s1)
	
	addiu	$t3, $zero, '0'
	addiu	$t3, $t3, -1
	slt		$t0, $t3, $t1
	beq		$t0, $zero, return
	
	addiu	$t3, $zero, '9'
	addiu	$t3, $t3, 1
	slt		$t0, $t1, $t3
	beq		$t0, $zero, return
	
	addi	$t0, $zero, 10
	mult	$t0, $t2
	mflo	$t2
	
	addi	$t0, $zero, '0'
	subu	$t0, $t1, $t0
	add		$t2, $t2, $t0
	
	addiu	$s1, $s1, 1
	j		normal

return:
	add		$v0, $zero, $t2
	jr		$ra

return_1:
	addiu	$s1, $s1, 1
	addi	$v0, $zero, -1
	jr		$ra

return_2:
	addiu	$s1, $s1, 1
	addi	$v0, $zero, -2
	jr		$ra

return_3:
	addiu	$s1, $s1, 1
	addi	$v0, $zero, -3
	jr		$ra
	
return_4:
	addiu	$s1, $s1, 1
	addi	$v0, $zero, -4
	jr		$ra
	
return_5:
	addiu	$s1, $s1, 1
	addi	$v0, $zero, -5
	jr		$ra