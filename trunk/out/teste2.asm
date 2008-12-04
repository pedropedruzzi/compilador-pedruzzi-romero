		SC	main
		HM	/0
inicializa	K	/0
		LV	/0
		MM	__1_i
__2_for1	LD	__1_i
		MM	__4_temp
		LV	/64
		-	__4_temp
		JN	__3_for2
		LD	LIVRE
		MM	__4_temp
		LV	/2
		+	lista
		MM	__5_temp
		LD	__1_i
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__5_temp
		+	__7_const
		MM	__8_store
		LD	__4_temp
__8_store	K	/0
		LD	__1_i
		MM	__6_temp
		LV	/1
		+	__6_temp
		MM	__1_i
		JP	__2_for1
__3_for2	LD	NENHUM
		MM	primeiro
		RS	inicializa
proximo_livre	K	/0
		LV	/0
		MM	__9_i
__10_for1	LD	__9_i
		MM	__5_temp
		LV	/64
		-	__5_temp
		JN	__11_for2
		LD	LIVRE
		MM	__4_temp
		LV	/2
		+	lista
		MM	__6_temp
		LD	__9_i
		MM	__5_temp
		LV	/4
		*	__5_temp
		+	__6_temp
		+	__14_const
		MM	__15_load
__15_load	K	/0
		-	__4_temp
		JZ	__13_notc1
		JP	__12_if1
__13_notc1	LD	__9_i
		RS	proximo_livre
__12_if1	LD	__9_i
		MM	__5_temp
		LV	/1
		+	__5_temp
		MM	__9_i
		JP	__10_for1
__11_for2	LD	NENHUM
		RS	proximo_livre
		RS	proximo_livre
ultimo	K	/0
		LD	primeiro
		MM	__16_i
		LD	NENHUM
		MM	__6_temp
		LD	primeiro
		-	__6_temp
		JZ	__18_notc1
		JP	__17_if1
__18_notc1	LD	NENHUM
		RS	ultimo
__17_if1	LD	NENHUM
		MM	__4_temp
		LV	/2
		+	lista
		MM	__5_temp
		LD	__16_i
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__5_temp
		+	__14_const
		MM	__20_load
__20_load	K	/0
		-	__4_temp
		JZ	__19_while2
		LV	/2
		+	lista
		MM	__6_temp
		LD	__16_i
		MM	__5_temp
		LV	/4
		*	__5_temp
		+	__6_temp
		+	__14_const
		MM	__21_load
__21_load	K	/0
		MM	__16_i
		JP	__17_if1
__19_while2	LD	__16_i
		RS	ultimo
		RS	ultimo
insere_no_fim	K	/0
		LD	NENHUM
		MM	__4_temp
		LD	primeiro
		-	__4_temp
		JZ	__24_notc1
		JP	__23_if1
__24_notc1	LV	/0
		MM	primeiro
		LD	__22_insere_no_fim_valor
		MM	__5_temp
		LD	lista
		+	__7_const
		MM	__25_store
		LD	__5_temp
__25_store	K	/0
		LD	NENHUM
		MM	__6_temp
		LV	/2
		+	lista
		+	__7_const
		MM	__26_store
		LD	__6_temp
__26_store	K	/0
		JP	__27_if2
__23_if1	SC	proximo_livre
		MM	__28_livre
		LD	__28_livre
		MM	__4_temp
		LV	/2
		+	lista
		MM	__5_temp
		SC	ultimo
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__5_temp
		+	__7_const
		MM	__29_store
		LD	__4_temp
__29_store	K	/0
		LD	__22_insere_no_fim_valor
		MM	__6_temp
		LD	lista
		MM	__5_temp
		LD	__28_livre
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__5_temp
		+	__7_const
		MM	__30_store
		LD	__6_temp
__30_store	K	/0
		LD	NENHUM
		MM	__4_temp
		LV	/2
		+	lista
		MM	__5_temp
		LD	__28_livre
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__5_temp
		+	__7_const
		MM	__31_store
		LD	__4_temp
__31_store	K	/0
__27_if2	RS	insere_no_fim
insere_na_ordem	K	/0
		LD	NENHUM
		MM	__6_temp
		LD	primeiro
		-	__6_temp
		JZ	__34_notc1
		JP	__33_if1
__34_notc1	LV	/0
		MM	primeiro
		LD	__32_insere_na_ordem_valor
		MM	__5_temp
		LD	lista
		+	__7_const
		MM	__35_store
		LD	__5_temp
__35_store	K	/0
		LD	NENHUM
		MM	__4_temp
		LV	/2
		+	lista
		+	__7_const
		MM	__36_store
		LD	__4_temp
__36_store	K	/0
		JP	__43_if2
__33_if1	LD	__32_insere_na_ordem_valor
		MM	__6_temp
		LD	lista
		MM	__5_temp
		LD	primeiro
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__5_temp
		+	__14_const
		MM	__39_load
__39_load	K	/0
		-	__6_temp
		JN	__38_if1
		SC	proximo_livre
		MM	__40_livre
		LD	__32_insere_na_ordem_valor
		MM	__4_temp
		LD	lista
		MM	__5_temp
		LD	__40_livre
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__5_temp
		+	__7_const
		MM	__41_store
		LD	__4_temp
__41_store	K	/0
		LD	primeiro
		MM	__6_temp
		LV	/2
		+	lista
		MM	__5_temp
		LD	__40_livre
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__5_temp
		+	__7_const
		MM	__42_store
		LD	__6_temp
__42_store	K	/0
		LD	__40_livre
		MM	primeiro
		JP	__43_if2
__38_if1	LD	primeiro
		MM	__44_i
		SC	proximo_livre
		MM	__45_livre
__46_while1	LD	NENHUM
		MM	__4_temp
		LV	/2
		+	lista
		MM	__5_temp
		LD	__44_i
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__5_temp
		+	__14_const
		MM	__48_load
__48_load	K	/0
		-	__4_temp
		JZ	__47_while2
		LD	lista
		MM	__6_temp
		LV	/2
		+	lista
		MM	__5_temp
		LD	__44_i
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__5_temp
		+	__14_const
		MM	__49_load
__49_load	K	/0
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__6_temp
		+	__14_const
		MM	__50_load
__50_load	K	/0
		MM	__5_temp
		LD	__32_insere_na_ordem_valor
		-	__5_temp
		JN	__47_while2
		LV	/2
		+	lista
		MM	__4_temp
		LD	__44_i
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__4_temp
		+	__14_const
		MM	__51_load
__51_load	K	/0
		MM	__44_i
		JP	__46_while1
__47_while2	LD	__32_insere_na_ordem_valor
		MM	__5_temp
		LD	lista
		MM	__6_temp
		LD	__45_livre
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__6_temp
		+	__7_const
		MM	__52_store
		LD	__5_temp
__52_store	K	/0
		LV	/2
		+	lista
		MM	__4_temp
		LD	__44_i
		MM	__6_temp
		LV	/4
		*	__6_temp
		+	__4_temp
		+	__14_const
		MM	__53_load
__53_load	K	/0
		MM	__5_temp
		LV	/2
		+	lista
		MM	__6_temp
		LD	__45_livre
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__6_temp
		+	__7_const
		MM	__54_store
		LD	__5_temp
__54_store	K	/0
		LD	__45_livre
		MM	__4_temp
		LV	/2
		+	lista
		MM	__6_temp
		LD	__44_i
		MM	__5_temp
		LV	/4
		*	__5_temp
		+	__6_temp
		+	__7_const
		MM	__55_store
		LD	__4_temp
__55_store	K	/0
__43_if2	RS	insere_na_ordem
printf	K	/0
		RS	printf
teste_func	K	/0
		RS	teste_func
		RS	teste_func
x	K	/0
		LD	lista
		MM	__5_temp
		LD	__60_x_i
		MM	__6_temp
		LV	/c
		*	__6_temp
		MM	__4_temp
		LV	/4
		*	__4_temp
		+	__5_temp
		MM	__59_teste_func_var
		SC	teste_func
		RS	x
dump_lista	K	/0
		LD	primeiro
		MM	__61_i
__62_while1	LD	NENHUM
		MM	__6_temp
		LD	__61_i
		-	__6_temp
		JZ	__63_while2
		LD	__65_p
		MM	__56_printf_texto
		LD	__61_i
		MM	__57_printf_valor1
		LD	lista
		MM	__4_temp
		LD	__61_i
		MM	__5_temp
		LV	/4
		*	__5_temp
		+	__4_temp
		+	__14_const
		MM	__66_load
__66_load	K	/0
		MM	__58_printf_valor2
		SC	printf
		LV	/2
		+	lista
		MM	__6_temp
		LD	__61_i
		MM	__5_temp
		LV	/4
		*	__5_temp
		+	__6_temp
		+	__14_const
		MM	__67_load
__67_load	K	/0
		MM	__61_i
		JP	__62_while1
__63_while2	RS	dump_lista
main	K	/0
		SC	inicializa
		LV	/7
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/3
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/8
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/2
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/1
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/6
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/4
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/7
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		LV	/0
		MM	__32_insere_na_ordem_valor
		SC	insere_na_ordem
		SC	dump_lista
		LV	/0
		RS	main
		RS	main
LIVRE	K	/FFFE
NENHUM	K	/FFFF
__0_data_lista	$	/190
lista	K	__0_data_lista
primeiro	K	/0
__1_i	K	/0
__4_temp	K	/0
__5_temp	K	/0
__6_temp	K	/0
__7_const	K	/9000
__9_i	K	/0
__14_const	K	/8000
__16_i	K	/0
__22_insere_no_fim_valor	K	/0
__28_livre	K	/0
__32_insere_na_ordem_valor	K	/0
__40_livre	K	/0
__44_i	K	/0
__45_livre	K	/0
__56_printf_texto	K	/0
__57_printf_valor1	K	/0
__58_printf_valor2	K	/0
__59_teste_func_var	K	/0
__60_x_i	K	/0
__61_i	K	/0
__64_string	K	/5b25
		K	/645d
		K	/203d
		K	/2025
		K	/643b
		K	/a00
__65_p	K	__64_string
