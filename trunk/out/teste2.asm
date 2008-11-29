__4_inicializa	K	/0
		LV	/0
__6_for1	LV	/64
		MM	__7_temp
		LD	__5_i
		-	__7_temp
		JN	__8_lt
		LV	/0
__8_lt	JZ	__9_for2
		LD	__0_LIVRE
		LD	__5_i
		MM	__7_temp
		LV	/1
		+	__7_temp
		JP	__6_for1
__9_for2	LD	__1_NENHUM
		RS	__4_inicializa
__10_proximo_livre	K	/0
		LV	/0
__12_for1	LV	/64
		MM	__7_temp
		LD	__11_i
		-	__7_temp
		JN	__13_lt
		LV	/0
__13_lt	JZ	__14_for2
		LD	__0_LIVRE
		MM	__7_temp
		LD	__15_const
		MM	__16_load
		LV	/2
		+	__17_p
		MM	__18_temp
		LD	__11_i
		MM	__19_temp
		LV	/4
		*	__19_temp
		+	__18_temp
		+	__16_load
__16_load	K	/0
		-	__7_temp
		JZ	__20_not1
		LV	/0
		JP	__21_not2
__20_not1	LV	/1
__21_not2	JZ	__22_if1
		LD	__11_i
		RS	__10_proximo_livre
__22_if1	LD	__11_i
		MM	__19_temp
		LV	/1
		+	__19_temp
		JP	__12_for1
__14_for2	LD	__1_NENHUM
		RS	__10_proximo_livre
		RS	__10_proximo_livre
__23_ultimo	K	/0
		LD	__3_primeiro
		MM	__24_i
		LD	__1_NENHUM
		MM	__18_temp
		LD	__3_primeiro
		-	__18_temp
		JZ	__25_not1
		LV	/0
		JP	__26_not2
__25_not1	LV	/1
__26_not2	JZ	__27_if1
		LD	__1_NENHUM
		RS	__23_ultimo
__27_if1	LD	__1_NENHUM
		MM	__7_temp
		LD	__15_const
		MM	__28_load
		LV	/2
		+	__17_p
		MM	__19_temp
		LD	__24_i
		MM	__18_temp
		LV	/4
		*	__18_temp
		+	__19_temp
		+	__28_load
__28_load	K	/0
		-	__7_temp
		JZ	__29_while2
		LD	__15_const
		MM	__30_load
		LV	/2
		+	__17_p
		MM	__18_temp
		LD	__24_i
		MM	__19_temp
		LV	/4
		*	__19_temp
		+	__18_temp
		+	__30_load
__30_load	K	/0
		JP	__27_if1
__29_while2	LD	__24_i
		RS	__23_ultimo
		RS	__23_ultimo
__32_insere_no_fim	K	/0
		LD	__1_NENHUM
		MM	__7_temp
		LD	__3_primeiro
		-	__7_temp
		JZ	__33_not1
		LV	/0
		JP	__34_not2
__33_not1	LV	/1
__34_not2	JZ	__35_if1
		LV	/0
		LD	__31_insere_no_fim_valor
		LD	__1_NENHUM
		JP	__36_if2
__35_if1	SC	__10_proximo_livre
		MM	__37_livre
		LD	__37_livre
		LD	__31_insere_no_fim_valor
		LD	__1_NENHUM
__36_if2	RS	__32_insere_no_fim
__39_insere_na_ordem	K	/0
		LD	__1_NENHUM
		MM	__19_temp
		LD	__3_primeiro
		-	__19_temp
		JZ	__40_not1
		LV	/0
		JP	__41_not2
__40_not1	LV	/1
__41_not2	JZ	__42_if1
		LV	/0
		LD	__38_insere_na_ordem_valor
		LD	__1_NENHUM
		JP	__48_if2
__42_if1	LD	__15_const
		MM	__44_load
		LV	/0
		+	__17_p
		MM	__18_temp
		LD	__3_primeiro
		MM	__7_temp
		LV	/4
		*	__7_temp
		+	__18_temp
		+	__44_load
__44_load	K	/0
		MM	__19_temp
		LD	__38_insere_na_ordem_valor
		-	__19_temp
		JN	__45_lt
		LV	/0
__45_lt	JZ	__46_if1
		SC	__10_proximo_livre
		MM	__47_livre
		LD	__38_insere_na_ordem_valor
		LD	__3_primeiro
		LD	__47_livre
		JP	__48_if2
__46_if1	LD	__3_primeiro
		MM	__49_i
		SC	__10_proximo_livre
		MM	__50_livre
__51_while1	LD	__38_insere_na_ordem_valor
		MM	__7_temp
		LD	__1_NENHUM
		MM	__18_temp
		LD	__15_const
		MM	__52_load
		LV	/2
		+	__17_p
		MM	__19_temp
		LD	__49_i
		MM	__53_temp
		LV	/4
		*	__53_temp
		+	__19_temp
		+	__52_load
__52_load	K	/0
		-	__18_temp
		JZ	__54_and
		LD	__15_const
		MM	__55_load
		LV	/0
		+	__17_p
		MM	__53_temp
		LD	__15_const
		MM	__56_load
		LV	/2
		+	__17_p
		MM	__19_temp
		LD	__49_i
		MM	__18_temp
		LV	/4
		*	__18_temp
		+	__19_temp
		+	__56_load
__56_load	K	/0
		MM	__18_temp
		LV	/4
		*	__18_temp
		+	__53_temp
		+	__55_load
__55_load	K	/0
__54_and	-	__7_temp
		JN	__57_lt
		LV	/0
__57_lt	JZ	__58_while2
		LD	__15_const
		MM	__59_load
		LV	/2
		+	__17_p
		MM	__19_temp
		LD	__49_i
		MM	__18_temp
		LV	/4
		*	__18_temp
		+	__19_temp
		+	__59_load
__59_load	K	/0
		JP	__51_while1
__58_while2	LD	__38_insere_na_ordem_valor
		LD	__15_const
		MM	__60_load
		LV	/2
		+	__17_p
		MM	__53_temp
		LD	__49_i
		MM	__7_temp
		LV	/4
		*	__7_temp
		+	__53_temp
		+	__60_load
__60_load	K	/0
		LD	__50_livre
__48_if2	RS	__39_insere_na_ordem
__64_printf	K	/0
		RS	__64_printf
__66_teste_func	K	/0
		RS	__66_teste_func
		RS	__66_teste_func
__68_x	K	/0
		LD	__15_const
		MM	__69_load
		LV	/0
		+	__17_p
		MM	__18_temp
		LD	__67_x_i
		MM	__19_temp
		LV	/c
		*	__19_temp
		MM	__7_temp
		LV	/4
		*	__7_temp
		+	__18_temp
		+	__69_load
__69_load	K	/0
		MM	__65_teste_func_var
		SC	__66_teste_func
		RS	__68_x
__70_dump_lista	K	/0
		LD	__3_primeiro
		MM	__71_i
__72_while1	LD	__1_NENHUM
		MM	__53_temp
		LD	__71_i
		-	__53_temp
		JZ	__73_while2
		LD	__75_p
		MM	__61_printf_texto
		LD	__71_i
		MM	__62_printf_valor1
		LD	__15_const
		MM	__76_load
		LV	/0
		+	__17_p
		MM	__19_temp
		LD	__71_i
		MM	__7_temp
		LV	/4
		*	__7_temp
		+	__19_temp
		+	__76_load
__76_load	K	/0
		MM	__63_printf_valor2
		SC	__64_printf
		LD	__15_const
		MM	__77_load
		LV	/2
		+	__17_p
		MM	__18_temp
		LD	__71_i
		MM	__53_temp
		LV	/4
		*	__53_temp
		+	__18_temp
		+	__77_load
__77_load	K	/0
		JP	__72_while1
__73_while2	RS	__70_dump_lista
__78_main	K	/0
		SC	__4_inicializa
		LV	/7
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/3
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/8
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/2
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/1
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/6
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/4
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/7
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		LV	/0
		MM	__38_insere_na_ordem_valor
		SC	__39_insere_na_ordem
		SC	__70_dump_lista
		LV	/0
		RS	__78_main
		RS	__78_main
__0_LIVRE	K	/FFFE
__1_NENHUM	K	/FFFF
__2_lista	K	/0
__3_primeiro	K	/0
__5_i	K	/0
__7_temp	K	/0
__11_i	K	/0
__15_const	K	/8000
__17_p	K	__2_lista
__18_temp	K	/0
__19_temp	K	/0
__24_i	K	/0
__31_insere_no_fim_valor	K	/0
__37_livre	K	/0
__38_insere_na_ordem_valor	K	/0
__47_livre	K	/0
__49_i	K	/0
__50_livre	K	/0
__53_temp	K	/0
__61_printf_texto	K	/0
__62_printf_valor1	K	/0
__63_printf_valor2	K	/0
__65_teste_func_var	K	/0
__67_x_i	K	/0
__71_i	K	/0
__74_string	K	/5b25
		K	/645d
		K	/203d
		K	/2025
		K	/643b
		K	/a00
__75_p	K	__74_string
