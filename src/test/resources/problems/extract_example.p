thf(a_type,type,(
    a: $i > $o )).

thf(b_type,type,(
    b: $i > $o )).

thf(p_type,type,(
    p: $o > $o )).

thf(c,conjecture,
    ( ! [X: $i] :
        ( a @ X )
    & ? [Y: $o] : Y
    & ! [Y: $i] :
        ( p
        @ ! [X: $i] :
            ( ( b @ Y )
           => ( a @ X ) ) ) )).
