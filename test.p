thf(a_type, type, a: $o).
thf(b_type, type, b: $o).
thf(c_type, type, c: $o).
thf(a3, axiom, ((((a) => ((b) => (c))) = ($true)))).
thf(domainConstrainedAxiom3, axiom, (((? [A:$i,B:$i]: ((~ (((B) = (A)))))) = ($true)))).
thf(domainConstrainedAxiom2, axiom, (((? [A:$i,B:$i]: ((! [C:$i]: ((((C) = (A)) | ((C) = (B))))))) = ($true)))).
thf(a1, axiom, (((a) = ($true)))).
thf(a2, axiom, ((((a) => (b)) = ($true)))).
thf(conj, conjecture, (((c) = ($false)))).
