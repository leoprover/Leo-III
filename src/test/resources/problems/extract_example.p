thf(a_type,type,(
    a: $i > $o )).

thf(b_type,type,(
    b: $i > $o )).

thf(p_type,type,(
    p: $o > $o )).

thf(c_type, type, (
    c : $i > $i)).

thf(c_def, definition, (
    (c = ^[X : $i] : (X)))).


%
% Result : 
%
%  Extraction in p (! [X: $i] : ( b @ Y ) => ( a @ X ) )
%
%	sk @ Y = ! [X: $i] : ( b @ Y ) => ( a @ X ) 
%
% After boolExt & cnf
%
%  ~sk Y | ~ b Y | a X
%  sk Y | b Y
%  sk Y | ~ a Y 
%
%

thf(c,conjecture,
    ( ! [X: $i] :
        ( a @ X )
    & ? [Y: $o] : Y
    & ! [Y: $i] :
        ( p
        @ ! [X: $i] :
            ( ( b @ Y )
           => ( a @ X ) ) ) )).
