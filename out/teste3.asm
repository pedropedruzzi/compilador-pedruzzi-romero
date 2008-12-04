		SC	main
		HM	/0
f	K	/0
		LV	/4
		MM	__4_temp
		LV	/4
		+	__1_f_a
		+	__5_const
		MM	__6_store
		LD	__4_temp
__6_store	K	/0
		LV	/6
		MM	__4_temp
		LD	__2_f_b
		+	__5_const
		MM	__7_store
		LD	__4_temp
__7_store	K	/0
		LV	/7
		MM	__3_f_c
		RS	f
main	K	/0
		LD	vet
		MM	__1_f_a
		LV	/6
		+	vet
		MM	__2_f_b
		LV	/12
		+	vet
		+	__8_const
		MM	__9_load
__9_load	K	/0
		MM	__3_f_c
		SC	f
		RS	main
__0_data_vet	$	/14
vet	K	__0_data_vet
__1_f_a	K	/0
__2_f_b	K	/0
__3_f_c	K	/0
__4_temp	K	/0
__5_const	K	/9000
__8_const	K	/8000
