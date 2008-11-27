int LIVRE = -2;
int NENHUM = -1;

type item {
	int valor;
	int proximo; /* itens livres tem proximo = LIVRE; */
};

type item[100] lista;
int primeiro;

void inicializa() {
	int i;
	for (i=0; i<100; i=i+1) lista[i].proximo = LIVRE;
	primeiro = NENHUM;
}

int proximo_livre() {
	int i;
	for (i=0; i<100; i=i+1)
		if (lista[i].proximo == LIVRE) return i;
	
	return NENHUM;
}

int ultimo() {
	int i = primeiro;
	if (primeiro == NENHUM) return NENHUM;
	while (lista[i].proximo != NENHUM) i = lista[i].proximo;
	return i;
}

void insere_no_fim(int valor) {
	if (primeiro == NENHUM) {
		primeiro = 0;
		lista[0].valor = valor;
		lista[0].proximo = NENHUM;
	} else {
		int livre = proximo_livre();
		lista[ultimo()].proximo = livre;
		lista[livre].valor = valor;
		lista[livre].proximo = NENHUM;
	}
}

void insere_na_ordem(int valor) {
	if (primeiro == NENHUM) {
		primeiro = 0;
		lista[0].valor = valor;
		lista[0].proximo = NENHUM;
	} else if (valor < lista[primeiro].valor) {
		int livre = proximo_livre();
		lista[livre].valor = valor;
		lista[livre].proximo = primeiro;
		primeiro = livre;
	} else {
		int i = primeiro;
		int livre = proximo_livre();
		while (lista[i].proximo != NENHUM && lista[lista[i].proximo].valor < valor) i = lista[i].proximo;
		lista[livre].valor = valor;
		lista[livre].proximo = lista[i].proximo;
		lista[i].proximo = livre;
	}
}

void dump_lista() {
	int i = primeiro;
	while (i != NENHUM) {
		printf("%d\n", lista[i].valor);
		i = lista[i].proximo;
	}
}

int main() {
	inicializa();
	insere_na_ordem(7);
	insere_na_ordem(3);
	insere_na_ordem(8);
	insere_na_ordem(2);
	insere_na_ordem(1);
	insere_na_ordem(6);
	insere_na_ordem(4);
	insere_na_ordem(7);
	insere_na_ordem(0);
	dump_lista();
	return 0;
}
