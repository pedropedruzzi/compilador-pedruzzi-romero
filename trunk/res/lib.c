int equal(int a, int b) {
	if (a - b) return 0;
	else return 1;
}

int equal2(int a, int b) {
	return !(a - b);
}

int not_equal(int a, int b) {
	return a - b;
}

int greater_than(int a, int b) {
	a - b;
}
