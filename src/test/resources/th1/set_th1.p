thf(in_type, type, (in : (!> [A: $tType]: (A > (A > $o) > $o)))).
thf(in_def, definition, (in = (^ [A: $tType, E: A, S: (A > $o)]: (S @ E)))).


thf(emptyset_type,type,(emptyset: (!> [A: $tType]: (A > $o) ))).
thf(emptyset_def,definition,(emptyset = ^ [A: $tType, E: A]: ($false) )).

thf(unord_pair_type,type,(
    unord_pair: (!> [A: $tType]: (A > A > A > $o )))).

thf(unord_pair_def,definition,
    ( unord_pair
    = ( ^ [T: $tType, X: T,Y: T,U: T] :
          ( ( U = X )
          | ( U = Y ) ) ) )).

thf(singleton_type,type,(
    singleton: (!> [A: $tType]: (A > A > $o)) )).

thf(singleton_def,definition,
    ( singleton
    = ( ^ [A: $tType, X: A,U: A] : ( U = X ) ) )).

thf(union_type,type,(
    union: (!> [A: $tType]: (( A > $o ) > ( A > $o ) > A > $o )))).

thf(union_def,definition,
    ( union
    = ( ^ [T: $tType, X: T > $o,Y: T > $o,U: T] :
          ( ( X @ U )
          | ( Y @ U ) ) ) )).


thf(subset_type,type,(
    subset: (!> [A: $tType]: (( A > $o ) > ( A > $o ) > $o )))).

thf(subset_def,definition,
    ( subset
    = ( ^ [T: $tType, X: T > $o,Y: T > $o] :
        ! [U: T] :
          ( ( X @ U )
         => ( Y @ U ) ) ) )).

%% SET014^4.p stuff
%
%thf(thm,conjecture,(
%    ! [T: $tType, X: T > $o,Y: T > $o,A: T > $o] :
%      ( ( ( subset @ T @ X @ A )
%        & ( subset @ T @ Y @ A ) )
%     => ( subset @ T @ ( union @ T @ X @ Y ) @ A ) ) )).
     
%%  SET067^1.p stuff
thf(thm,conjecture,(
    ! [A: $tType, X: A,Y: A] :
      ( subset @ A @ ( unord_pair @ A @ X @ Y ) @ ( unord_pair @ A @ Y @ X ) ) )).
