		SC	main
		HM	/0
inicializa	K	/0
		LV	/0
__3_for1	LV	/64
		MM	__4_temp
		LD	__2_i
		-	__4_temp
		JN	__5_lt
		LV	/0
__5_lt	JZ	__6_for2
		LD	LIVRE
		LD	__2_i
		MM	__4_temp
		LV	/1
		+	__4_temp
		JP	__3_for1
__6_for2	LD	NENHUM
		RS	inicializa
proximo_livre	K	/0
		LV	/0
__8_for1	LV	/64
		MM	__4_temp
		LD	__7_i
		-	__4_temp
		JN	__9_lt
		LV	/0
__9_lt	JZ	__10_for2
		LD	LIVRE
		MM	__4_temp
		LD	__11_const
		MM	__12_load
		LV	/2
		+	__13_p
		MM	__14_temp
		LD	__7_i
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__14_temp
		+	__12_load
__12_load	K	/0
		-	__4_temp
		JZ	__16_not1
		LV	/0
		JP	__17_not2
__16_not1	LV	/1
__17_not2	JZ	__18_if1
		LD	__7_i
		RS	proximo_livre
__18_if1	LD	__7_i
		MM	__15_temp
		LV	/1
		+	__15_temp
		JP	__8_for1
__10_for2	LD	NENHUM
		RS	proximo_livre
		RS	proximo_livre
ultimo	K	/0
		LD	__1_primeiro
		MM	__19_i
		LD	NENHUM
		MM	__14_temp
		LD	__1_primeiro
		-	__14_temp
		JZ	__20_not1
		LV	/0
		JP	__21_not2
__20_not1	LV	/1
__21_not2	JZ	__22_if1
		LD	NENHUM
		RS	ultimo
__22_if1	LD	NENHUM
		MM	__4_temp
		LD	__11_const
		MM	__23_load
		LV	/2
		+	__13_p
		MM	__15_temp
		LD	__19_i
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__15_temp
		+	__23_load
__23_load	K	/0
		-	__4_temp
		JZ	__25_while2
		LD	__11_const
		MM	__26_load
		LV	/2
		+	__13_p
		MM	__14_temp
		LD	__19_i
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__14_temp
		+	__26_load
__26_load	K	/0
		JP	__22_if1
__25_while2	LD	__19_i
		RS	ultimo
		RS	ultimo
insere_no_fim	K	/0
		LD	NENHUM
		MM	__4_temp
		LD	__1_primeiro
		-	__4_temp
		JZ	__29_not1
		LV	/0
		JP	__30_not2
__29_not1	LV	/1
__30_not2	JZ	__31_if1
		LV	/0
		LD	__28_insere_no_fim_valor
		LD	NENHUM
		JP	__32_if2
__31_if1	SC	proximo_livre
		MM	__33_livre
		LD	__33_livre
		LD	__28_insere_no_fim_valor
		LD	NENHUM
__32_if2	RS	insere_no_fim
insere_na_ordem	K	/0
		LD	NENHUM
		MM	__15_temp
		LD	__1_primeiro
		-	__15_temp
		JZ	__35_not1
		LV	/0
		JP	__36_not2
__35_not1	LV	/1
__36_not2	JZ	__37_if1
		LV	/0
		LD	__34_insere_na_ordem_valor
		LD	NENHUM
		JP	__44_if2
__37_if1	LD	__11_const
		MM	__39_load
		LV	/0
		+	__13_p
		MM	__14_temp
		LD	__1_primeiro
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__14_temp
		+	__39_load
__39_load	K	/0
		MM	__15_temp
		LD	__34_insere_na_ordem_valor
		-	__15_temp
		JN	__41_lt
		LV	/0
__41_lt	JZ	__42_if1
		SC	proximo_livre
		MM	__43_livre
		LD	__34_insere_na_ordem_valor
		LD	__1_primeiro
		LD	__43_livre
		JP	__44_if2
__42_if1	LD	__1_primeiro
		MM	__45_i
		SC	proximo_livre
		MM	__46_livre
__47_while1	LD	__34_insere_na_ordem_valor
		MM	__4_temp
		LD	NENHUM
		MM	__14_temp
		LD	__11_const
		MM	__48_load
		LV	/2
		+	__13_p
		MM	__15_temp
		LD	__45_i
		MM	__50_temp
		LV	/4
		*	__50_temp
		+	__15_temp
		+	__48_load
__48_load	K	/0
		-	__14_temp
		JZ	__51_and
		LD	__11_const
		MM	__52_load
		LV	/0
		+	__13_p
		MM	__50_temp
		LD	__11_const
		MM	__54_load
		LV	/2
		+	__13_p
		MM	__15_temp
		LD	__45_i
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__15_temp
		+	__54_load
__54_load	K	/0
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__50_temp
		+	__52_load
__52_load	K	/0
__51_and	-	__4_temp
		JN	__56_lt
		LV	/0
__56_lt	JZ	__57_while2
		LD	__11_const
		MM	__58_load
		LV	/2
		+	__13_p
		MM	__15_temp
		LD	__45_i
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__15_temp
		+	__58_load
__58_load	K	/0
		JP	__47_while1
__57_while2	LD	__34_insere_na_ordem_valor
		LD	__11_const
		MM	__60_load
		LV	/2
		+	__13_p
		MM	__50_temp
		LD	__45_i
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__50_temp
		+	__60_load
__60_load	K	/0
		LD	__46_livre
__44_if2	RS	insere_na_ordem
printf	K	/0
		RS	printf
teste_func	K	/0
		RS	teste_func
		RS	teste_func
x	K	/0
		LD	__11_const
		MM	__67_load
		LV	/0
		+	__13_p
		MM	__14_temp
		LD	__66_x_i
		MM	__15_temp
		LV	/c
		*	__15_temp
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__14_temp
		+	__67_load
__67_load	K	/0
		MM	__65_teste_func_var
		SC	teste_func
		RS	x
dump_lista	K	/0
		LD	__1_primeiro
		MM	__69_i
__70_while1	LD	NENHUM
		MM	__50_temp
		LD	__69_i
		-	__50_temp
		JZ	__71_while2
		LD	__73_p
		MM	__62_printf_texto
		LD	__69_i
		MM	__63_printf_valor1
		LD	__11_const
		MM	__74_load
		LV	/0
		+	__13_p
		MM	__15_temp
		LD	__69_i
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__15_temp
		+	__74_load
__74_load	K	/0
		MM	__64_printf_valor2
		SC	printf
		LD	__11_const
		MM	__76_load
		LV	/2
		+	__13_p
		MM	__14_temp
		LD	__69_i
		MM	__50_temp
		LV	/4
		*	__50_temp
		+	__14_temp
		+	__76_load
__76_load	K	/0
		JP	__70_while1
__71_while2	RS	dump_lista
main	K	/0
		SC	inicializa
		LV	/7
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/3
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/8
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/2
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/1
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/6
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/4
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/7
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/0
		MM	__34_insere_na_ordem_valor
		SC	insere_na_ordem
		SC	dump_lista
		LV	/0
		RS	main
		RS	main
LIVRE	K	/FFFE
NENHUM	K	/FFFF
__0_lista	K	/0
__1_primeiro	K	/0
__2_i	K	/0
__4_temp	K	/0
__7_i	K	/0
__11_const	K	/8000
__13_p	K	__0_lista
__14_temp	K	/0
__15_temp	K	/0
__19_i	K	/0
__28_insere_no_fim_valor	K	/0
__33_livre	K	/0
__34_insere_na_ordem_valor	K	/0
__43_livre	K	/0
__45_i	K	/0
__46_livre	K	/0
__50_temp	K	/0
__62_printf_texto	K	/0
__63_printf_valor1	K	/0
__64_printf_valor2	K	/0
__65_teste_func_var	K	/0
__66_x_i	K	/0
__69_i	K	/0
__72_string	K	/5b25
		K	/645d
		K	/203d
		K	/2025
		K	/643b
		K	/a00
__73_p	K	__72_string
