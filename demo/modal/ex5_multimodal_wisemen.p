%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 5: Wise men puzzle
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(wise_men_puzzle_semantics, logic , $modal == [
    $designation == $rigid,
    $domains == $varying,
    $modalities == $modal_system_S5] ).

%--- $i type models the agents's hats
thf(agent_a, type, (a: $i)).
thf(agent_b, type, (b: $i)).
thf(agent_c, type, (c: $i)).

%--- Property of an agent's hat: ws represents "having a white spot"
thf(white_spot, type, (ws: ($i>$o))).

%--- Common knowledge: At least one agent has a white spot
thf(axiom_1, axiom, ({$box(#0)} @ ((ws @ a) | (ws @ b) | (ws @ c)))).

%--- If one agent has a white spot all other agents can see this
thf(axiom_2ab, axiom, ({$box(#0)} @ ((ws @ a) => ({$box(#2)} @ (ws @ a))))).
thf(axiom_2ac, axiom, ({$box(#0)} @ ((ws @ a) => ({$box(#3)} @ (ws @ a))))).
thf(axiom_2ba, axiom, ({$box(#0)} @ ((ws @ b) => ({$box(#1)} @ (ws @ b))))).
thf(axiom_2bc, axiom, ({$box(#0)} @ ((ws @ b) => ({$box(#3)} @ (ws @ b))))).
thf(axiom_2ca, axiom, ({$box(#0)} @ ((ws @ c) => ({$box(#1)} @ (ws @ c))))).
thf(axiom_2cb, axiom, ({$box(#0)} @ ((ws @ c) => ({$box(#2)} @ (ws @ c))))).

%--- If one agent has a black spot all other agents can see this
thf(axiom_3ab, axiom, ({$box(#0)} @ ((~(ws @ a)) => ({$box(#2)} @ (~(ws @ a)))))).
thf(axiom_3ac, axiom, ({$box(#0)} @ ((~(ws @ a)) => ({$box(#3)} @ (~(ws @ a)))))).
thf(axiom_3ba, axiom, ({$box(#0)} @ ((~(ws @ b)) => ({$box(#1)} @ (~(ws @ b)))))).
thf(axiom_3bc, axiom, ({$box(#0)} @ ((~(ws @ b)) => ({$box(#3)} @ (~(ws @ b)))))).
thf(axiom_3ca, axiom, ({$box(#0)} @ ((~(ws @ c)) => ({$box(#1)} @ (~(ws @ c)))))).
thf(axiom_3cb, axiom, ({$box(#0)} @ ((~(ws @ c)) => ({$box(#2)} @ (~(ws @ c)))))).

%--- Agents 1 and 2 do not know their hat color
thf(axiom_9, axiom, ({$box(#0)} @ (~({$box(#1)} @ (ws @ a))))).
thf(axiom_10, axiom, ({$box(#0)} @ (~({$box(#2)} @ (ws @ b))))).

%--- Agent 3 can deduce the color of his hat (white spot)
thf(con, conjecture, ({$box(#3)} @ (ws @ c))).
