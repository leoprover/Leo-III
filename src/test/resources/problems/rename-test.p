thf(t1, type, a : $o).
thf(t2, type, b : $o).
thf(t3, type, c : $o).
thf(t4, type, d : $o).
thf(t5, type, e : $o).
thf(t6, type, f : $o > $o).

thf(a1, axiom, a).
thf(a2, axiom, d).




%
% Naive :
%
% (~a & ~b & ~c) | (~d & ~e & ~f)
%
% ~a|~d , ~a|~e, ~a|~f, ~b|~d, ~b|~e, ~b|~f, ~c|~d, ~c|~e, ~c|~f
%
% Rename : 
%
% sk|~d, sk|~e, sk|~f, ~a|~sk, ~b|~sk, ~c|~sk
%
% Additional f(a&b) extraction :
% 
% sk1|~a|~b, ~sk1|a, ~sk1|b
% 
% Result : 11 axioms (small)
%	   14 axioms (naive with extraction)
%
%
thf(a3, axiom, ~ @ (( (a | b) | c) & ( (d | e) | (f @ (a & b)) ) )).

thf(f1,conjecture, $false).
