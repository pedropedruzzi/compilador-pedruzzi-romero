__4_inicializa	K	/0
		LV	/0
__6_for1	LD	__5_i
		MM	__7_temp
		LV	/64
		+	__7_temp
		JZ	__8_for2
		LD	__0_LIVRE
		LD	__5_i
		MM	__7_temp
		LV	/1
		+	__7_temp
		JP	__6_for1
__8_for2	LD	__1_NENHUM
		RS	__4_inicializa
__9_proximo_livre	K	/0
		LV	/0
__11_for1	LD	__10_i
		MM	__7_temp
		LV	/64
		+	__7_temp
		JZ	__12_for2
		LD	__13_const
		MM	__14_load
		LV	/2
		+	__15_p
		MM	__7_temp
		LV	/4
		MM	__16_temp
		LD	__10_i
		*	__16_temp
		+	__7_temp
		+	__14_load
__14_load	K	/0
		MM	__16_temp
		LD	__0_LIVRE
		+	__16_temp
		JZ	__17_if1
		LD	__10_i
		RS	__9_proximo_livre
__17_if1	LD	__10_i
		MM	__7_temp
		LV	/1
		+	__7_temp
		JP	__11_for1
__12_for2	LD	__1_NENHUM
		RS	__9_proximo_livre
		RS	__9_proximo_livre
__18_ultimo	K	/0
		LD	__3_primeiro
		MM	__19_i
		LD	__3_primeiro
		MM	__16_temp
		LD	__1_NENHUM
		+	__16_temp
		JZ	__20_if1
		LD	__1_NENHUM
		RS	__18_ultimo
__20_if1	LD	__13_const
		MM	__21_load
		LV	/2
		+	__15_p
		MM	__7_temp
		LV	/4
		MM	__16_temp
		LD	__19_i
		*	__16_temp
		+	__7_temp
		+	__21_load
__21_load	K	/0
		MM	__16_temp
		LD	__1_NENHUM
		+	__16_temp
		JZ	__22_while2
		LD	__13_const
		MM	__23_load
		LV	/2
		+	__15_p
		MM	__7_temp
		LV	/4
		MM	__16_temp
		LD	__19_i
		*	__16_temp
		+	__7_temp
		+	__23_load
__23_load	K	/0
		JP	__20_if1
__22_while2	LD	__19_i
		RS	__18_ultimo
		RS	__18_ultimo
__25_insere_no_fim	K	/0
		LD	__3_primeiro
		MM	__16_temp
		LD	__1_NENHUM
		+	__16_temp
		JZ	__26_if1
		LV	/0
		LD	__24_insere_no_fim_valor
		LD	__1_NENHUM
		JP	__27_if2
__26_if1	SC	__9_proximo_livre
		MM	__28_livre
		LD	__28_livre
		LD	__24_insere_no_fim_valor
		LD	__1_NENHUM
__27_if2	RS	__25_insere_no_fim
__30_insere_na_ordem	K	/0
		LD	__3_primeiro
		MM	__7_temp
		LD	__1_NENHUM
		+	__7_temp
		JZ	__31_if1
		LV	/0
		LD	__29_insere_na_ordem_valor
		LD	__1_NENHUM
		JP	__37_if2
__31_if1	LD	__29_insere_na_ordem_valor
		MM	__16_temp
		LD	__13_const
		MM	__33_load
		LV	/0
		+	__15_p
		MM	__7_temp
		LV	/4
		MM	__34_temp
		LD	__3_primeiro
		*	__34_temp
		+	__7_temp
		+	__33_load
__33_load	K	/0
		+	__16_temp
		JZ	__35_if1
		SC	__9_proximo_livre
		MM	__36_livre
		LD	__29_insere_na_ordem_valor
		LD	__3_primeiro
		LD	__36_livre
		JP	__37_if2
__35_if1	LD	__3_primeiro
		MM	__38_i
		SC	__9_proximo_livre
		MM	__39_livre
__40_while1	LD	__13_const
		MM	__41_load
		LV	/2
		+	__15_p
		MM	__34_temp
		LV	/4
		MM	__7_temp
		LD	__38_i
		*	__7_temp
		+	__34_temp
		+	__41_load
__41_load	K	/0
		MM	__16_temp
		LD	__1_NENHUM
		+	__16_temp
		MM	__7_temp
		LD	__13_const
		MM	__42_load
		LV	/0
		+	__15_p
		MM	__34_temp
		LV	/4
		MM	__16_temp
		LD	__13_const
		MM	__43_load
		LV	/2
		+	__15_p
		MM	__44_temp
		LV	/4
		MM	__45_temp
		LD	__38_i
		*	__45_temp
		+	__44_temp
		+	__43_load
__43_load	K	/0
		*	__16_temp
		+	__34_temp
		+	__42_load
__42_load	K	/0
		+	__7_temp
		MM	__45_temp
		LD	__29_insere_na_ordem_valor
		+	__45_temp
		JZ	__46_while2
		LD	__13_const
		MM	__47_load
		LV	/2
		+	__15_p
		MM	__44_temp
		LV	/4
		MM	__16_temp
		LD	__38_i
		*	__16_temp
		+	__44_temp
		+	__47_load
__47_load	K	/0
		JP	__40_while1
__46_while2	LD	__29_insere_na_ordem_valor
		LD	__13_const
		MM	__48_load
		LV	/2
		+	__15_p
		MM	__34_temp
		LV	/4
		MM	__7_temp
		LD	__38_i
		*	__7_temp
		+	__34_temp
		+	__48_load
__48_load	K	/0
		LD	__39_livre
__37_if2	RS	__30_insere_na_ordem
__51_printf	K	/0
		RS	__51_printf
__52_dump_lista	K	/0
		LD	__3_primeiro
		MM	__53_i
__54_while1	LD	__53_i
		MM	__45_temp
		LD	__1_NENHUM
		+	__45_temp
		JZ	__55_while2
		LD	__57_p
		MM	__49_printf_texto
		LD	__13_const
		MM	__58_load
		LV	/0
		+	__15_p
		MM	__16_temp
		LV	/4
		MM	__44_temp
		LD	__53_i
		*	__44_temp
		+	__16_temp
		+	__58_load
__58_load	K	/0
		MM	__50_printf_valor
		SC	__51_printf
		LD	__13_const
		MM	__59_load
		LV	/2
		+	__15_p
		MM	__7_temp
		LV	/4
		MM	__34_temp
		LD	__53_i
		*	__34_temp
		+	__7_temp
		+	__59_load
__59_load	K	/0
		JP	__54_while1
__55_while2	RS	__52_dump_lista
__60_main	K	/0
		SC	__4_inicializa
		LV	/7
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/3
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/8
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/2
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/1
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/6
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/4
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/7
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		LV	/0
		MM	__29_insere_na_ordem_valor
		SC	__30_insere_na_ordem
		SC	__52_dump_lista
		LV	/0
		RS	__60_main
		RS	__60_main
__0_LIVRE	K	/FFFE
__1_NENHUM	K	/FFFF
__2_lista	K	/0
__3_primeiro	K	/0
__5_i	K	/0
__7_temp	K	/0
__10_i	K	/0
__13_const	K	/0
__15_p	K	__2_lista
__16_temp	K	/0
__19_i	K	/0
__24_insere_no_fim_valor	K	/0
__28_livre	K	/0
__29_insere_na_ordem_valor	K	/0
__34_temp	K	/0
__36_livre	K	/0
__38_i	K	/0
__39_livre	K	/0
__44_temp	K	/0
__45_temp	K	/0
__49_printf_texto	K	/0
__50_printf_valor	K	/0
__53_i	K	/0
__56_string	K	/0
		K	/0
__57_p	K	__56_string
