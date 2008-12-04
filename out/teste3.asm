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
teste	K	/0
		LV	/2
		MM	__4_temp
		LD	__12_e
		MM	__14_temp
		LV	/0
		-	__14_temp
		MM	__14_temp
		LV	/0
		-	__14_temp
		*	__15_const
		MM	__14_temp
		LD	__13_f
		-	__14_temp
		MM	__14_temp
		LD	__12_e
		MM	__16_temp
		LD	__11_d
		*	__17_const
		/	__16_temp
		*	__14_temp
		MM	__16_temp
		LD	__10_c
		MM	__14_temp
		LD	__9_b
		+	__14_temp
		+	__16_temp
		-	__4_temp
		JN	__18_lt
		LV	/0
__18_lt	MM	__8_a
		RS	teste
main	K	/0
		LD	vet
		MM	__1_f_a
		LV	/6
		+	vet
		MM	__2_f_b
		LV	/12
		+	vet
		+	__19_const
		MM	__20_load
__20_load	K	/0
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
__8_a	K	/0
__9_b	K	/0
__10_c	K	/0
__11_d	K	/0
__12_e	K	/0
__13_f	K	/0
__14_temp	K	/0
__15_const	K	/4
__16_temp	K	/0
__17_const	K	/2
__19_const	K	/8000
