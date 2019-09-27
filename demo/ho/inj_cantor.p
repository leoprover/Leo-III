
%-- There is no injection from a power set to its underlying set.
thf(inj_cantor, conjecture, (~ ( ? [F: ($i > $o) > $i] : (
                                   ! [X: $i > $o,Y:$i > $o]:
                                      (((F @ X) = (F @ Y)) => (X = Y))
                                 ) ))).
