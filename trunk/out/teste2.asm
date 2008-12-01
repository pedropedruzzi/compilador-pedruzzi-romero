		SC	main
		HM	/0
inicializa	K	/0
		LV	/0
__3_for1	LD	__2_i
		MM	__5_temp
		LV	/64
		-	__5_temp
		JN	__4_for2
		LD	LIVRE
		LD	__2_i
		MM	__5_temp
		LV	/1
		+	__5_temp
		JP	__3_for1
__4_for2	LD	NENHUM
		RS	inicializa
proximo_livre	K	/0
		LV	/0
__7_for1	LD	__6_i
		MM	__5_temp
		LV	/64
		-	__5_temp
		JN	__8_for2
		LD	LIVRE
		MM	__5_temp
		LD	__11_const
		MM	__12_load
		LV	/2
		+	__13_p
		MM	__14_temp
		LD	__6_i
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__14_temp
		+	__12_load
__12_load	K	/0
		-	__5_temp
		JZ	__10_notc1
		JP	__9_if1
__10_notc1	LD	__6_i
		RS	proximo_livre
__9_if1	LD	__6_i
		MM	__15_temp
		LV	/1
		+	__15_temp
		JP	__7_for1
__8_for2	LD	NENHUM
		RS	proximo_livre
		RS	proximo_livre
ultimo	K	/0
		LD	__1_primeiro
		MM	__16_i
		LD	NENHUM
		MM	__14_temp
		LD	__1_primeiro
		-	__14_temp
		JZ	__18_notc1
		JP	__17_if1
__18_notc1	LD	NENHUM
		RS	ultimo
__17_if1	LD	NENHUM
		MM	__5_temp
		LD	__11_const
		MM	__20_load
		LV	/2
		+	__13_p
		MM	__15_temp
		LD	__16_i
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__15_temp
		+	__20_load
__20_load	K	/0
		-	__5_temp
		JZ	__19_while2
		LD	__11_const
		MM	__22_load
		LV	/2
		+	__13_p
		MM	__14_temp
		LD	__16_i
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__14_temp
		+	__22_load
__22_load	K	/0
		JP	__17_if1
__19_while2	LD	__16_i
		RS	ultimo
		RS	ultimo
insere_no_fim	K	/0
		LD	NENHUM
		MM	__5_temp
		LD	__1_primeiro
		-	__5_temp
		JZ	__26_notc1
		JP	__25_if1
__26_notc1	LV	/0
		LD	__24_insere_no_fim_valor
		LD	NENHUM
		JP	__27_if2
__25_if1	SC	proximo_livre
		MM	__28_livre
		LD	__28_livre
		LD	__24_insere_no_fim_valor
		LD	NENHUM
__27_if2	RS	insere_no_fim
insere_na_ordem	K	/0
		LD	NENHUM
		MM	__15_temp
		LD	__1_primeiro
		-	__15_temp
		JZ	__31_notc1
		JP	__30_if1
__31_notc1	LV	/0
		LD	__29_insere_na_ordem_valor
		LD	NENHUM
		JP	__37_if2
__30_if1	LD	__29_insere_na_ordem_valor
		MM	__14_temp
		LD	__11_const
		MM	__34_load
		LV	/0
		+	__13_p
		MM	__5_temp
		LD	__1_primeiro
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__5_temp
		+	__34_load
__34_load	K	/0
		-	__14_temp
		JN	__33_if1
		SC	proximo_livre
		MM	__36_livre
		LD	__29_insere_na_ordem_valor
		LD	__1_primeiro
		LD	__36_livre
		JP	__37_if2
__33_if1	LD	__1_primeiro
		MM	__38_i
		SC	proximo_livre
		MM	__39_livre
__40_while1	LD	NENHUM
		MM	__15_temp
		LD	__11_const
		MM	__43_load
		LV	/2
		+	__13_p
		MM	__5_temp
		LD	__38_i
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__5_temp
		+	__43_load
__43_load	K	/0
		-	__15_temp
		JZ	__42_and
		LD	__11_const
		MM	__45_load
		LV	/0
		+	__13_p
		MM	__14_temp
		LD	__11_const
		MM	__47_load
		LV	/2
		+	__13_p
		MM	__5_temp
		LD	__38_i
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__5_temp
		+	__47_load
__47_load	K	/0
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__14_temp
		+	__45_load
__45_load	K	/0
__42_and	MM	__5_temp
		LD	__29_insere_na_ordem_valor
		-	__5_temp
		JN	__41_while2
		LD	__11_const
		MM	__49_load
		LV	/2
		+	__13_p
		MM	__15_temp
		LD	__38_i
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__15_temp
		+	__49_load
__49_load	K	/0
		JP	__40_while1
__41_while2	LD	__29_insere_na_ordem_valor
		LD	__11_const
		MM	__51_load
		LV	/2
		+	__13_p
		MM	__5_temp
		LD	__38_i
		MM	__14_temp
		LV	/4
		*	__14_temp
		+	__5_temp
		+	__51_load
__51_load	K	/0
		LD	__39_livre
__37_if2	RS	insere_na_ordem
printf	K	/0
		RS	printf
teste_func	K	/0
		RS	teste_func
		RS	teste_func
x	K	/0
		LD	__11_const
		MM	__58_load
		LV	/0
		+	__13_p
		MM	__15_temp
		LD	__57_x_i
		MM	__14_temp
		LV	/c
		*	__14_temp
		MM	__5_temp
		LV	/4
		*	__5_temp
		+	__15_temp
		+	__58_load
__58_load	K	/0
		MM	__56_teste_func_var
		SC	teste_func
		RS	x
dump_lista	K	/0
		LD	__1_primeiro
		MM	__60_i
__61_while1	LD	NENHUM
		MM	__14_temp
		LD	__60_i
		-	__14_temp
		JZ	__62_while2
		LD	__64_p
		MM	__53_printf_texto
		LD	__60_i
		MM	__54_printf_valor1
		LD	__11_const
		MM	__65_load
		LV	/0
		+	__13_p
		MM	__5_temp
		LD	__60_i
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__5_temp
		+	__65_load
__65_load	K	/0
		MM	__55_printf_valor2
		SC	printf
		LD	__11_const
		MM	__67_load
		LV	/2
		+	__13_p
		MM	__14_temp
		LD	__60_i
		MM	__15_temp
		LV	/4
		*	__15_temp
		+	__14_temp
		+	__67_load
__67_load	K	/0
		JP	__61_while1
__62_while2	RS	dump_lista
main	K	/0
		SC	inicializa
		LV	/7
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/3
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/8
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/2
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/1
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/6
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/4
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/7
		MM	__29_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/0
		MM	__29_insere_na_ordem_valor
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
__5_temp	K	/0
__6_i	K	/0
__11_const	K	/8000
__13_p	K	__0_lista
__14_temp	K	/0
__15_temp	K	/0
__16_i	K	/0
__24_insere_no_fim_valor	K	/0
__28_livre	K	/0
__29_insere_na_ordem_valor	K	/0
__36_livre	K	/0
__38_i	K	/0
__39_livre	K	/0
__53_printf_texto	K	/0
__54_printf_valor1	K	/0
__55_printf_valor2	K	/0
__56_teste_func_var	K	/0
__57_x_i	K	/0
__60_i	K	/0
__63_string	K	/5b25
		K	/645d
		K	/203d
		K	/2025
		K	/643b
		K	/a00
__64_p	K	__63_string
