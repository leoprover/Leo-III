%-- For any X,Y:i, there is a function swapping X and Y
thf(ifi,conjecture,(
    ! [X: $i,Y: $i] :
    ? [F: $i > $i] :
      ( ( ( F @ X )
        = Y )
      & ( ( F @ Y )
        = X ) ) )).
