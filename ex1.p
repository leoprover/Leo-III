thf(human_type, type, human : $i > $o).
thf(mortak_type, type, mortal : $i > $o).
thf(sokrates_type, type, sokrates : $i).
thf(1, axiom, ![X : $i]: (human(X) => mortal(X))).
thf(2, axiom, human(sokrates)).
thf(c, conjecture, mortal(sokrates)).
