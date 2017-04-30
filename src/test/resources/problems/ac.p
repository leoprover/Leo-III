thf(0,type, f: $i>$i>$i).
thf(1,axiom, ![X:$i, Y:$i]: ((f @ X @ Y) = (f @ Y @ X))).

thf(2,axiom, ![X:$i, Y:$i, Z:$i]: ((f @ (f @ X @ Y) @ Z) = (f @ X @ (f @ Y @ Z)))).
