
%-- There is no surjective function from a set to its power set.
thf(sur_cantor, conjecture, (~ ( ? [F: $i > ($i > $o)] : (
                                   ! [Y: $i > $o] :
                                    ? [X: $i] : (
                                      (F @ X) = Y
                                    )
                                 ) ))).
                                 
