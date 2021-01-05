thf(simple_b, logic, ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := $global ,
    $modalities := $modal_system_S5 ] ) ).


thf(axiom_B, conjecture, ( ![A:$o]: ( A => ( $box @ ( $dia @ A ) ) ) )).
