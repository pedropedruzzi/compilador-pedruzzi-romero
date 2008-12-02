		SC	main
		HM	/0
f	K	/0
		LV	/4
		LV	/6
		LV	/7
		RS	f
main	K	/0
		LD	vet
		MM	__1_f_a
		LV	/6
		+	vet
		MM	__2_f_b
		LD	__5_const
		MM	__6_load
		LV	/12
		+	vet
		+	__6_load
__6_load	K	/0
		MM	__3_f_c
		SC	f
		RS	main
__0_data_vet	$	/14
vet	K	__0_data_vet
__1_f_a	K	/0
__2_f_b	K	/0
__3_f_c	K	/0
__4_temp	K	/0
__5_const	K	/8000
