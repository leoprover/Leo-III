thf(a_type, type, a: $o).
thf(b_type, type, b: $o).
thf(p_type, type, p: ($o > $o) > $i).
thf(f_type, type, f: $o > $i).

thf(1, axiom, $true => a).
thf(2, axiom, (f @ (a & b)) = (^[X : $o] : (p @ (^[Y : $o] : (X & Y))))).
thf(3, conjecture, b).
