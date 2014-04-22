%--------------------------------------------------------------------------
% File     : SYN067-3 : TPTP v6.0.0. Released v1.2.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 38
% Version  : Especial.
%            Theorem formulation : Different clausification.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [TPTP]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.0.0, 0.14 v5.5.0, 0.25 v5.4.0, 0.40 v5.2.0, 0.20 v5.1.0, 0.36 v5.0.0, 0.50 v4.0.1, 0.40 v4.0.0, 0.57 v3.5.0, 0.71 v3.4.0, 0.50 v3.3.0, 0.33 v3.2.0, 0.00 v3.1.0, 0.67 v2.7.0, 0.62 v2.6.0, 0.67 v2.5.0, 0.80 v2.4.0, 0.20 v2.3.0, 0.33 v2.2.1, 0.67 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   46 (  40 non-Horn;   1 unit;  19 RR)
%            Number of atoms       :  259 (   0 equality)
%            Maximal clause size   :    7 (   6 average)
%            Number of predicates  :    2 (   0 propositional; 1-2 arity)
%            Number of functors    :   11 (   5 constant; 0-1 arity)
%            Number of variables   :  120 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : A full predicate logic version of Pelletier 17 and 33.
%          : Created using the TPTP clausifier in Otter mode.
%--------------------------------------------------------------------------
cnf(pel38_1,negated_conjecture,
    ( big_p(A)
    | big_p(B)
    | big_p(sk1(B))
    | big_p(sk3(A))
    | ~ big_p(a) )).

cnf(pel38_2,negated_conjecture,
    ( big_p(C)
    | big_p(D)
    | big_p(sk1(D))
    | big_r(C,sk4(C))
    | ~ big_p(a) )).

cnf(pel38_3,negated_conjecture,
    ( big_p(E)
    | big_p(F)
    | big_p(sk1(F))
    | big_r(sk4(E),sk3(E))
    | ~ big_p(a) )).

cnf(pel38_4,negated_conjecture,
    ( big_p(G)
    | big_p(H)
    | big_p(sk3(G))
    | big_r(H,sk2(H))
    | ~ big_p(a) )).

cnf(pel38_5,negated_conjecture,
    ( big_p(I)
    | big_p(J)
    | big_r(I,sk4(I))
    | big_r(J,sk2(J))
    | ~ big_p(a) )).

cnf(pel38_6,negated_conjecture,
    ( big_p(K)
    | big_p(L)
    | big_r(L,sk2(L))
    | big_r(sk4(K),sk3(K))
    | ~ big_p(a) )).

cnf(pel38_7,negated_conjecture,
    ( big_p(M)
    | big_p(sk1(M))
    | big_p(sk5(N))
    | ~ big_p(O)
    | ~ big_p(a)
    | ~ big_r(N,O) )).

cnf(pel38_8,negated_conjecture,
    ( big_p(P)
    | big_p(sk1(P))
    | big_r(Q,sk6(Q))
    | ~ big_p(R)
    | ~ big_p(a)
    | ~ big_r(Q,R) )).

cnf(pel38_9,negated_conjecture,
    ( big_p(S)
    | big_p(sk1(S))
    | big_r(sk6(T),sk5(T))
    | ~ big_p(U)
    | ~ big_p(a)
    | ~ big_r(T,U) )).

cnf(pel38_10,negated_conjecture,
    ( big_p(V)
    | big_p(sk5(W))
    | big_r(V,sk2(V))
    | ~ big_p(X)
    | ~ big_p(a)
    | ~ big_r(W,X) )).

cnf(pel38_11,negated_conjecture,
    ( big_p(Y)
    | big_r(Z,sk6(Z))
    | big_r(Y,sk2(Y))
    | ~ big_p(A1)
    | ~ big_p(a)
    | ~ big_r(Z,A1) )).

cnf(pel38_12,negated_conjecture,
    ( big_p(B1)
    | big_r(B1,sk2(B1))
    | big_r(sk6(C1),sk5(C1))
    | ~ big_p(D1)
    | ~ big_p(a)
    | ~ big_r(C1,D1) )).

cnf(pel38_13,negated_conjecture,
    ( big_p(E1)
    | big_p(F1)
    | big_p(sk3(E1))
    | big_r(sk2(F1),sk1(F1))
    | ~ big_p(a) )).

cnf(pel38_14,negated_conjecture,
    ( big_p(G1)
    | big_p(H1)
    | big_r(G1,sk4(G1))
    | big_r(sk2(H1),sk1(H1))
    | ~ big_p(a) )).

cnf(pel38_15,negated_conjecture,
    ( big_p(I1)
    | big_p(J1)
    | big_r(sk2(J1),sk1(J1))
    | big_r(sk4(I1),sk3(I1))
    | ~ big_p(a) )).

cnf(pel38_16,negated_conjecture,
    ( big_p(K1)
    | big_p(sk1(L1))
    | big_p(sk3(K1))
    | ~ big_p(M1)
    | ~ big_p(a)
    | ~ big_r(L1,M1) )).

cnf(pel38_17,negated_conjecture,
    ( big_p(N1)
    | big_p(sk3(N1))
    | big_r(O1,sk2(O1))
    | ~ big_p(P1)
    | ~ big_p(a)
    | ~ big_r(O1,P1) )).

cnf(pel38_18,negated_conjecture,
    ( big_p(Q1)
    | big_p(sk1(R1))
    | big_r(Q1,sk4(Q1))
    | ~ big_p(S1)
    | ~ big_p(a)
    | ~ big_r(R1,S1) )).

cnf(pel38_19,negated_conjecture,
    ( big_p(T1)
    | big_r(T1,sk4(T1))
    | big_r(U1,sk2(U1))
    | ~ big_p(V1)
    | ~ big_p(a)
    | ~ big_r(U1,V1) )).

cnf(pel38_20,negated_conjecture,
    ( big_p(W1)
    | big_p(sk3(W1))
    | big_r(sk2(X1),sk1(X1))
    | ~ big_p(Y1)
    | ~ big_p(a)
    | ~ big_r(X1,Y1) )).

cnf(pel38_21,negated_conjecture,
    ( big_p(Z1)
    | big_r(Z1,sk4(Z1))
    | big_r(sk2(A2),sk1(A2))
    | ~ big_p(B2)
    | ~ big_p(a)
    | ~ big_r(A2,B2) )).

cnf(pel38_22,negated_conjecture,
    ( big_p(C2)
    | big_p(sk1(D2))
    | big_r(sk4(C2),sk3(C2))
    | ~ big_p(E2)
    | ~ big_p(a)
    | ~ big_r(D2,E2) )).

cnf(pel38_23,negated_conjecture,
    ( big_p(F2)
    | big_r(G2,sk2(G2))
    | big_r(sk4(F2),sk3(F2))
    | ~ big_p(H2)
    | ~ big_p(a)
    | ~ big_r(G2,H2) )).

cnf(pel38_24,negated_conjecture,
    ( big_p(I2)
    | big_r(sk2(J2),sk1(J2))
    | big_r(sk4(I2),sk3(I2))
    | ~ big_p(K2)
    | ~ big_p(a)
    | ~ big_r(J2,K2) )).

cnf(pel38_25,negated_conjecture,
    ( big_p(L2)
    | big_p(sk5(M2))
    | big_r(sk2(L2),sk1(L2))
    | ~ big_p(N2)
    | ~ big_p(a)
    | ~ big_r(M2,N2) )).

cnf(pel38_26,negated_conjecture,
    ( big_p(O2)
    | big_r(P2,sk6(P2))
    | big_r(sk2(O2),sk1(O2))
    | ~ big_p(Q2)
    | ~ big_p(a)
    | ~ big_r(P2,Q2) )).

cnf(pel38_27,negated_conjecture,
    ( big_p(R2)
    | big_r(sk2(R2),sk1(R2))
    | big_r(sk6(S2),sk5(S2))
    | ~ big_p(T2)
    | ~ big_p(a)
    | ~ big_r(S2,T2) )).

cnf(pel38_28,negated_conjecture,
    ( big_p(sk1(U2))
    | big_p(sk5(V2))
    | ~ big_p(W2)
    | ~ big_p(X2)
    | ~ big_p(a)
    | ~ big_r(V2,W2)
    | ~ big_r(U2,X2) )).

cnf(pel38_29,negated_conjecture,
    ( big_p(sk5(Y2))
    | big_r(Z2,sk2(Z2))
    | ~ big_p(A3)
    | ~ big_p(B3)
    | ~ big_p(a)
    | ~ big_r(Y2,A3)
    | ~ big_r(Z2,B3) )).

cnf(pel38_30,negated_conjecture,
    ( big_p(sk1(C3))
    | big_r(D3,sk6(D3))
    | ~ big_p(E3)
    | ~ big_p(F3)
    | ~ big_p(a)
    | ~ big_r(D3,E3)
    | ~ big_r(C3,F3) )).

cnf(pel38_31,negated_conjecture,
    ( big_r(G3,sk6(G3))
    | big_r(H3,sk2(H3))
    | ~ big_p(I3)
    | ~ big_p(J3)
    | ~ big_p(a)
    | ~ big_r(G3,I3)
    | ~ big_r(H3,J3) )).

cnf(pel38_32,negated_conjecture,
    ( big_p(sk5(K3))
    | big_r(sk2(L3),sk1(L3))
    | ~ big_p(M3)
    | ~ big_p(N3)
    | ~ big_p(a)
    | ~ big_r(K3,M3)
    | ~ big_r(L3,N3) )).

cnf(pel38_33,negated_conjecture,
    ( big_r(O3,sk6(O3))
    | big_r(sk2(P3),sk1(P3))
    | ~ big_p(Q3)
    | ~ big_p(R3)
    | ~ big_p(a)
    | ~ big_r(O3,Q3)
    | ~ big_r(P3,R3) )).

cnf(pel38_34,negated_conjecture,
    ( big_p(sk1(S3))
    | big_r(sk6(T3),sk5(T3))
    | ~ big_p(U3)
    | ~ big_p(V3)
    | ~ big_p(a)
    | ~ big_r(T3,U3)
    | ~ big_r(S3,V3) )).

cnf(pel38_35,negated_conjecture,
    ( big_r(W3,sk2(W3))
    | big_r(sk6(X3),sk5(X3))
    | ~ big_p(Y3)
    | ~ big_p(Z3)
    | ~ big_p(a)
    | ~ big_r(X3,Y3)
    | ~ big_r(W3,Z3) )).

cnf(pel38_36,negated_conjecture,
    ( big_r(sk2(A4),sk1(A4))
    | big_r(sk6(B4),sk5(B4))
    | ~ big_p(C4)
    | ~ big_p(D4)
    | ~ big_p(a)
    | ~ big_r(B4,C4)
    | ~ big_r(A4,D4) )).

cnf(pel38_37,negated_conjecture,
    ( big_p(a) )).

cnf(pel38_38,negated_conjecture,
    ( big_p(sk10)
    | big_p(sk8)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_39,negated_conjecture,
    ( big_p(sk8)
    | big_r(sk9,sk10)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_40,negated_conjecture,
    ( big_p(sk10)
    | big_r(sk7,sk8)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_41,negated_conjecture,
    ( big_r(sk7,sk8)
    | big_r(sk9,sk10)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_42,negated_conjecture,
    ( big_p(sk8)
    | ~ big_p(E4)
    | ~ big_p(sk7)
    | ~ big_r(F4,E4)
    | ~ big_r(sk9,F4) )).

cnf(pel38_43,negated_conjecture,
    ( big_r(sk7,sk8)
    | ~ big_p(G4)
    | ~ big_p(sk7)
    | ~ big_r(H4,G4)
    | ~ big_r(sk9,H4) )).

cnf(pel38_44,negated_conjecture,
    ( big_p(sk10)
    | ~ big_p(I4)
    | ~ big_p(sk9)
    | ~ big_r(J4,I4)
    | ~ big_r(sk7,J4) )).

cnf(pel38_45,negated_conjecture,
    ( big_r(sk9,sk10)
    | ~ big_p(K4)
    | ~ big_p(sk9)
    | ~ big_r(L4,K4)
    | ~ big_r(sk7,L4) )).

cnf(pel38_46,negated_conjecture,
    ( ~ big_p(M4)
    | ~ big_p(N4)
    | ~ big_r(O4,M4)
    | ~ big_r(P4,N4)
    | ~ big_r(sk7,P4)
    | ~ big_r(sk9,O4) )).

%--------------------------------------------------------------------------
