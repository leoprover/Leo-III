thf(1, type, list : $tType > $tType).
thf(2, type, nil: (!> [A: $tType]: (list @ A))).
thf(3, type, cons: (!> [A: $tType]: (A > (list @ A) > (list @ A)))).
thf(23, axiom, (! [A: $tType, E: A, L: (list @ A)]: ( (nil @ A) != (cons @ A @ E @ L)))).
thf(asdasd, axiom, ! [A: $tType, L: (list @ A)]: (
    (L = (nil @ A))
    |
    (
      ? [X: A, XS: list @ A]: (
        L = (cons @ A @ X @ XS)
      )
    )  
  )).

thf(6, type, map: (!> [A: $tType]: ((A > A) > (list @ A) > (list @ A)))).

thf(7, axiom, (! [F: $i > $i]: ((map @ $i @ F @ (nil @ $i)) = (nil @ $i)))).
thf(8, axiom, (! [F: $i > $i, X: $i, XS: list @ $i]: ((map @ $i @ F @ (cons @ $i @ X @ XS)) = (cons @ $i @ (F @ X) @ (map @ $i @ F @ XS))) )).
%


thf(9, axiom, (! [P: (list @ $i) > $o]: (
    (
      (
          (P @ (nil @ $i))
        &
          (! [XS: list @ $i, X: $i]: ((P @ XS) => (P @ (cons @ $i @ X @ XS))))
      )
        =>
      (
        ! [XS: list @ $i]: (P @ XS)
      )
    )      
    ))).

thf(conj, conjecture, ( ! [XS: list @ $i] : ((map @ $i @ (^ [X: $i]: X) @ XS) = XS) )).
%
%
% 
% sk1 = (cons @ $i @ X @ XS) ^f
% F <- (^ [X: $i]: X)
%
% (cons @ $i @ (X) @ (map @ $i @ F @ sk1)) = sk1 ^f
