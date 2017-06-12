thf(p, type, p: $i>$o).
thf(witness, type, w:$i).
thf(wp,axiom, p@w). 
%% thf(c,conjecture, (p @ (@+ [X:$i]: (p @ X)))).
thf(c,conjecture, (p @ (@@+ @ $i @ (p)))).
