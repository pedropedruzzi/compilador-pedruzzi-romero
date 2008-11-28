__4_inicializa	K	/0
		HM	/0
__6_for1	HM	/0
		JZ	__7_for2
		HM	/0
		HM	/0
		JP	__6_for1
__7_for2	HM	/0
		RS	__4_inicializa
__9_proximo_livre	K	/0
		HM	/0
__11_for1	HM	/0
		JZ	__12_for2
		HM	/0
		JZ	__13_if1
		HM	/0
		MM	__8_ret_proximo_livre
		RS	__9_proximo_livre
__13_if1	HM	/0
		JP	__11_for1
__12_for2	HM	/0
		MM	__8_ret_proximo_livre
		RS	__9_proximo_livre
		RS	__9_proximo_livre
__15_ultimo	K	/0
		HM	/0
		JZ	__17_if1
		HM	/0
		MM	__14_ret_ultimo
		RS	__15_ultimo
__17_if1	HM	/0
		JZ	__18_while2
		HM	/0
		JP	__17_if1
__18_while2	HM	/0
		MM	__14_ret_ultimo
		RS	__15_ultimo
		RS	__15_ultimo
__20_insere_no_fim	K	/0
		HM	/0
		JZ	__21_if1
		HM	/0
		HM	/0
		HM	/0
		JP	__22_if2
__21_if1	HM	/0
		HM	/0
		HM	/0
__22_if2	RS	__20_insere_no_fim
__25_insere_na_ordem	K	/0
		HM	/0
		JZ	__26_if1
		HM	/0
		HM	/0
		HM	/0
		JP	__30_if2
__26_if1	HM	/0
		JZ	__28_if1
		HM	/0
		HM	/0
		HM	/0
		JP	__30_if2
__28_if1	HM	/0
		JZ	__33_while2
		HM	/0
		JP	__28_if1
__33_while2	HM	/0
		HM	/0
		HM	/0
__30_if2	RS	__25_insere_na_ordem
__34_dump_lista	K	/0
__36_while1	HM	/0
		JZ	__37_while2
		HM	/0
		HM	/0
		JP	__36_while1
__37_while2	RS	__34_dump_lista
__39_main	K	/0
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
		MM	__38_ret_main
		RS	__39_main
		RS	__39_main
__40_teste	K	/0
		HM	/0
		RS	__40_teste
__0_LIVRE	K	/0
__1_NENHUM	K	/0
__2_lista	K	/0
__3_primeiro	K	/0
__5_i	K	/0
__8_ret_proximo_livre	K	/0
__10_i	K	/0
__14_ret_ultimo	K	/0
__16_i	K	/0
__19_insere_no_fim_valor	K	/0
__23_livre	K	/0
__24_insere_na_ordem_valor	K	/0
__29_livre	K	/0
__31_i	K	/0
__32_livre	K	/0
__35_i	K	/0
__38_ret_main	K	/0
__41_a	K	/0
