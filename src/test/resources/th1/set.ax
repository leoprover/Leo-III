thf(set_type, type, (set : $tType > $tType)).

thf(emptyset_type, type, (emptyset: !> [T:$tType]: (set @ T))).

thf(isEmpty_type, type, (isEmpty : !> [T:$tType]: ((set @ T) > $o))).
thf(nonEmpty_type, type, (nonEmpty : !> [T:$tType]: ((set @ T) > $o))).
thf(nonEmpty_def, definition, (nonEmpty = (^ [$tType, S:(set@T)]: (~(isEmpty @ T @ S))))).
thf(emptysetIsEmpty, axiom, (! [T:$tType]: (isEmpty @ T @ (emptyset @ T)))).

thf(insert_type, type, (insert : !> [T:$tType]: (T > (set @ T) > (set @ T)))).
thf(insert_asso, axiom, (! [T:$tType, E:T, F:T, S:(set@T)]: ((insert @ T @ E @ (insert @ T @ F @ S)) = (insert @ T @ F @ (insert @ T @ E @ S))))).
thf(insert_nonempty, axiom, (! [T:$tType, E:T, S:(set@T)]: (~(isEmpty @ T @ (insert @ T @ E @ S))))).

thf(singleton_type, type, (singleton: !> [T:$tType]: (T > (set @ T)))).
thf(singleton_def, definition, (singleton = (^ [T:$tType, E:T]: (insert @ T @ E @ (emptyset @ T))))).

thf(member_type, type, (member : !> [T:$tType]: (T > (set @ T) > $o))).
thf(memberEmptyset, axiom, (! [T:$tType, E:T]: (~(member @ T @ E @ (emptyset @ T))))).
thf(memberEmptyset, axiom, (! [T:$tType, E:T, S:(set@T)]: ((member @ T @ E @ (insert @ T @ E @ S))))).


thf(p_type, type, (p : $i)).
thf(con, conjecture, (nonEmpty @ $i @ (insert @ $i @ i @ (emptyset @ $i))).
