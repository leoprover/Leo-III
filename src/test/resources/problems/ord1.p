thf(h_type,type,(
    h: $i )).

thf(p_type,type,(
    p: $i > $o )).


thf(role_hypothesis,hypothesis,
    ( p @ h )).
    
thf(role_conjecture,conjecture,(
    ? [X: $i] :
      ( p @ X ) )).
