type t {
	int a;
};

type t[10] vet;

void f(type t[] a, type t b, int c) {
	a[2].a = 4;
	b.a = 6;
	c = 7;
}

void teste() {
	int a, b, c, d, e, f;
	a = b + c + d * 2 / e * (f - - -e * 4) < 2;
}

void main() {
	f(vet, vet[3], vet[9].a);
}

