thf(type_a, type, a : $o).
thf(type_b, type, b : $o).
thf(type_c, type, c : $o).
thf(type_p, type, p : ($i > $o)).

thf(prinzip, axiom, ! [X: $o] : (! [Y: $o] : ((X = Y) => (Y = X)))).
thf(1, axiom, ! [X: $o] : ((p @ X) => a)).
thf(2, axiom, b => a).
thf(3, axiom, c & d).
thf(c, conjecture, b).
