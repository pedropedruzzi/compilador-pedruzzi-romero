__4_inicializa	K	/0
		HM	/0
__6_for1	HM	/0
		JZ	__7_for2
		HM	/0
		HM	/0
		JP	__6_for1
__7_for2	HM	/0
		RS	__4_inicializa
__8_proximo_livre	K	/0
		HM	/0
__10_for1	HM	/0
		JZ	__11_for2
		HM	/0
		JZ	__12_if1
		HM	/0
		RS	__8_proximo_livre
__12_if1	HM	/0
		JP	__10_for1
__11_for2	HM	/0
		RS	__8_proximo_livre
		RS	__8_proximo_livre
__13_ultimo	K	/0
		HM	/0
		JZ	__15_if1
		HM	/0
		RS	__13_ultimo
__15_if1	HM	/0
		JZ	__16_while2
		HM	/0
		JP	__15_if1
__16_while2	HM	/0
		RS	__13_ultimo
		RS	__13_ultimo
__18_insere_no_fim	K	/0
		HM	/0
		JZ	__19_if1
		HM	/0
		HM	/0
		HM	/0
		JP	__20_if2
__19_if1	HM	/0
		HM	/0
		HM	/0
__20_if2	RS	__18_insere_no_fim
__23_insere_na_ordem	K	/0
		HM	/0
		JZ	__24_if1
		HM	/0
		HM	/0
		HM	/0
		JP	__28_if2
__24_if1	HM	/0
		JZ	__26_if1
		HM	/0
		HM	/0
		HM	/0
		JP	__28_if2
__26_if1	HM	/0
		JZ	__31_while2
		HM	/0
		JP	__26_if1
__31_while2	HM	/0
		HM	/0
		HM	/0
__28_if2	RS	__23_insere_na_ordem
__32_dump_lista	K	/0
__34_while1	HM	/0
		JZ	__35_while2
		HM	/0
		HM	/0
		JP	__34_while1
__35_while2	RS	__32_dump_lista
__36_main	K	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		HM	/0
		RS	__36_main
		RS	__36_main
__37_teste	K	/0
		HM	/0
		RS	__37_teste
__0_LIVRE	K	/0
__1_NENHUM	K	/0
__2_lista	K	/0
__3_primeiro	K	/0
__5_i	K	/0
__9_i	K	/0
__14_i	K	/0
__17_insere_no_fim_valor	K	/0
__21_livre	K	/0
__22_insere_na_ordem_valor	K	/0
__27_livre	K	/0
__29_i	K	/0
__30_livre	K	/0
__33_i	K	/0
__38_a	K	/0
