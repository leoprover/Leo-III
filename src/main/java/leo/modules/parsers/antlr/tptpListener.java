// Generated from tptp.g4 by ANTLR 4.5.3

package leo.modules.parsers.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link tptpParser}.
 */
public interface tptpListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link tptpParser#tptp_file}.
	 * @param ctx the parse tree
	 */
	void enterTptp_file(tptpParser.Tptp_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tptp_file}.
	 * @param ctx the parse tree
	 */
	void exitTptp_file(tptpParser.Tptp_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tptp_input}.
	 * @param ctx the parse tree
	 */
	void enterTptp_input(tptpParser.Tptp_inputContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tptp_input}.
	 * @param ctx the parse tree
	 */
	void exitTptp_input(tptpParser.Tptp_inputContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#include}.
	 * @param ctx the parse tree
	 */
	void enterInclude(tptpParser.IncludeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#include}.
	 * @param ctx the parse tree
	 */
	void exitInclude(tptpParser.IncludeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#formula_selection}.
	 * @param ctx the parse tree
	 */
	void enterFormula_selection(tptpParser.Formula_selectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#formula_selection}.
	 * @param ctx the parse tree
	 */
	void exitFormula_selection(tptpParser.Formula_selectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#name_list}.
	 * @param ctx the parse tree
	 */
	void enterName_list(tptpParser.Name_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#name_list}.
	 * @param ctx the parse tree
	 */
	void exitName_list(tptpParser.Name_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(tptpParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(tptpParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#atomic_word}.
	 * @param ctx the parse tree
	 */
	void enterAtomic_word(tptpParser.Atomic_wordContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#atomic_word}.
	 * @param ctx the parse tree
	 */
	void exitAtomic_word(tptpParser.Atomic_wordContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#atomic_defined_word}.
	 * @param ctx the parse tree
	 */
	void enterAtomic_defined_word(tptpParser.Atomic_defined_wordContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#atomic_defined_word}.
	 * @param ctx the parse tree
	 */
	void exitAtomic_defined_word(tptpParser.Atomic_defined_wordContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#atomic_system_word}.
	 * @param ctx the parse tree
	 */
	void enterAtomic_system_word(tptpParser.Atomic_system_wordContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#atomic_system_word}.
	 * @param ctx the parse tree
	 */
	void exitAtomic_system_word(tptpParser.Atomic_system_wordContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(tptpParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(tptpParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#file_name}.
	 * @param ctx the parse tree
	 */
	void enterFile_name(tptpParser.File_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#file_name}.
	 * @param ctx the parse tree
	 */
	void exitFile_name(tptpParser.File_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#annotated_formula}.
	 * @param ctx the parse tree
	 */
	void enterAnnotated_formula(tptpParser.Annotated_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#annotated_formula}.
	 * @param ctx the parse tree
	 */
	void exitAnnotated_formula(tptpParser.Annotated_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_annotated}.
	 * @param ctx the parse tree
	 */
	void enterThf_annotated(tptpParser.Thf_annotatedContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_annotated}.
	 * @param ctx the parse tree
	 */
	void exitThf_annotated(tptpParser.Thf_annotatedContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_annotated}.
	 * @param ctx the parse tree
	 */
	void enterTff_annotated(tptpParser.Tff_annotatedContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_annotated}.
	 * @param ctx the parse tree
	 */
	void exitTff_annotated(tptpParser.Tff_annotatedContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_annotated}.
	 * @param ctx the parse tree
	 */
	void enterFof_annotated(tptpParser.Fof_annotatedContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_annotated}.
	 * @param ctx the parse tree
	 */
	void exitFof_annotated(tptpParser.Fof_annotatedContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#annotations}.
	 * @param ctx the parse tree
	 */
	void enterAnnotations(tptpParser.AnnotationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#annotations}.
	 * @param ctx the parse tree
	 */
	void exitAnnotations(tptpParser.AnnotationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#formula_role}.
	 * @param ctx the parse tree
	 */
	void enterFormula_role(tptpParser.Formula_roleContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#formula_role}.
	 * @param ctx the parse tree
	 */
	void exitFormula_role(tptpParser.Formula_roleContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#source}.
	 * @param ctx the parse tree
	 */
	void enterSource(tptpParser.SourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#source}.
	 * @param ctx the parse tree
	 */
	void exitSource(tptpParser.SourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#optional_info}.
	 * @param ctx the parse tree
	 */
	void enterOptional_info(tptpParser.Optional_infoContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#optional_info}.
	 * @param ctx the parse tree
	 */
	void exitOptional_info(tptpParser.Optional_infoContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#general_term}.
	 * @param ctx the parse tree
	 */
	void enterGeneral_term(tptpParser.General_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#general_term}.
	 * @param ctx the parse tree
	 */
	void exitGeneral_term(tptpParser.General_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#general_data}.
	 * @param ctx the parse tree
	 */
	void enterGeneral_data(tptpParser.General_dataContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#general_data}.
	 * @param ctx the parse tree
	 */
	void exitGeneral_data(tptpParser.General_dataContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#general_function}.
	 * @param ctx the parse tree
	 */
	void enterGeneral_function(tptpParser.General_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#general_function}.
	 * @param ctx the parse tree
	 */
	void exitGeneral_function(tptpParser.General_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#formula_data}.
	 * @param ctx the parse tree
	 */
	void enterFormula_data(tptpParser.Formula_dataContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#formula_data}.
	 * @param ctx the parse tree
	 */
	void exitFormula_data(tptpParser.Formula_dataContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#general_list}.
	 * @param ctx the parse tree
	 */
	void enterGeneral_list(tptpParser.General_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#general_list}.
	 * @param ctx the parse tree
	 */
	void exitGeneral_list(tptpParser.General_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#general_terms}.
	 * @param ctx the parse tree
	 */
	void enterGeneral_terms(tptpParser.General_termsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#general_terms}.
	 * @param ctx the parse tree
	 */
	void exitGeneral_terms(tptpParser.General_termsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_formula(tptpParser.Thf_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_formula(tptpParser.Thf_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_logic_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_logic_formula(tptpParser.Thf_logic_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_logic_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_logic_formula(tptpParser.Thf_logic_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_binary_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_binary_formula(tptpParser.Thf_binary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_binary_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_binary_formula(tptpParser.Thf_binary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_binary_pair}.
	 * @param ctx the parse tree
	 */
	void enterThf_binary_pair(tptpParser.Thf_binary_pairContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_binary_pair}.
	 * @param ctx the parse tree
	 */
	void exitThf_binary_pair(tptpParser.Thf_binary_pairContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_binary_tuple}.
	 * @param ctx the parse tree
	 */
	void enterThf_binary_tuple(tptpParser.Thf_binary_tupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_binary_tuple}.
	 * @param ctx the parse tree
	 */
	void exitThf_binary_tuple(tptpParser.Thf_binary_tupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_or_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_or_formula(tptpParser.Thf_or_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_or_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_or_formula(tptpParser.Thf_or_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_and_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_and_formula(tptpParser.Thf_and_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_and_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_and_formula(tptpParser.Thf_and_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_apply_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_apply_formula(tptpParser.Thf_apply_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_apply_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_apply_formula(tptpParser.Thf_apply_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_unitary_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_unitary_formula(tptpParser.Thf_unitary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_unitary_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_unitary_formula(tptpParser.Thf_unitary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_quantified_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_quantified_formula(tptpParser.Thf_quantified_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_quantified_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_quantified_formula(tptpParser.Thf_quantified_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_quantification}.
	 * @param ctx the parse tree
	 */
	void enterThf_quantification(tptpParser.Thf_quantificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_quantification}.
	 * @param ctx the parse tree
	 */
	void exitThf_quantification(tptpParser.Thf_quantificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_variable_list}.
	 * @param ctx the parse tree
	 */
	void enterThf_variable_list(tptpParser.Thf_variable_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_variable_list}.
	 * @param ctx the parse tree
	 */
	void exitThf_variable_list(tptpParser.Thf_variable_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_variable}.
	 * @param ctx the parse tree
	 */
	void enterThf_variable(tptpParser.Thf_variableContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_variable}.
	 * @param ctx the parse tree
	 */
	void exitThf_variable(tptpParser.Thf_variableContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_typed_variable}.
	 * @param ctx the parse tree
	 */
	void enterThf_typed_variable(tptpParser.Thf_typed_variableContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_typed_variable}.
	 * @param ctx the parse tree
	 */
	void exitThf_typed_variable(tptpParser.Thf_typed_variableContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_unary_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_unary_formula(tptpParser.Thf_unary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_unary_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_unary_formula(tptpParser.Thf_unary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_atom}.
	 * @param ctx the parse tree
	 */
	void enterThf_atom(tptpParser.Thf_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_atom}.
	 * @param ctx the parse tree
	 */
	void exitThf_atom(tptpParser.Thf_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_function}.
	 * @param ctx the parse tree
	 */
	void enterThf_function(tptpParser.Thf_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_function}.
	 * @param ctx the parse tree
	 */
	void exitThf_function(tptpParser.Thf_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_plain_term}.
	 * @param ctx the parse tree
	 */
	void enterThf_plain_term(tptpParser.Thf_plain_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_plain_term}.
	 * @param ctx the parse tree
	 */
	void exitThf_plain_term(tptpParser.Thf_plain_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_defined_term}.
	 * @param ctx the parse tree
	 */
	void enterThf_defined_term(tptpParser.Thf_defined_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_defined_term}.
	 * @param ctx the parse tree
	 */
	void exitThf_defined_term(tptpParser.Thf_defined_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_system_term}.
	 * @param ctx the parse tree
	 */
	void enterThf_system_term(tptpParser.Thf_system_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_system_term}.
	 * @param ctx the parse tree
	 */
	void exitThf_system_term(tptpParser.Thf_system_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_conditional}.
	 * @param ctx the parse tree
	 */
	void enterThf_conditional(tptpParser.Thf_conditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_conditional}.
	 * @param ctx the parse tree
	 */
	void exitThf_conditional(tptpParser.Thf_conditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_let}.
	 * @param ctx the parse tree
	 */
	void enterThf_let(tptpParser.Thf_letContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_let}.
	 * @param ctx the parse tree
	 */
	void exitThf_let(tptpParser.Thf_letContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_arguments}.
	 * @param ctx the parse tree
	 */
	void enterThf_arguments(tptpParser.Thf_argumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_arguments}.
	 * @param ctx the parse tree
	 */
	void exitThf_arguments(tptpParser.Thf_argumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_type_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_type_formula(tptpParser.Thf_type_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_type_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_type_formula(tptpParser.Thf_type_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_typeable_formula}.
	 * @param ctx the parse tree
	 */
	void enterThf_typeable_formula(tptpParser.Thf_typeable_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_typeable_formula}.
	 * @param ctx the parse tree
	 */
	void exitThf_typeable_formula(tptpParser.Thf_typeable_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_subtype}.
	 * @param ctx the parse tree
	 */
	void enterThf_subtype(tptpParser.Thf_subtypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_subtype}.
	 * @param ctx the parse tree
	 */
	void exitThf_subtype(tptpParser.Thf_subtypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_top_level_type}.
	 * @param ctx the parse tree
	 */
	void enterThf_top_level_type(tptpParser.Thf_top_level_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_top_level_type}.
	 * @param ctx the parse tree
	 */
	void exitThf_top_level_type(tptpParser.Thf_top_level_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_unitary_type}.
	 * @param ctx the parse tree
	 */
	void enterThf_unitary_type(tptpParser.Thf_unitary_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_unitary_type}.
	 * @param ctx the parse tree
	 */
	void exitThf_unitary_type(tptpParser.Thf_unitary_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_binary_type}.
	 * @param ctx the parse tree
	 */
	void enterThf_binary_type(tptpParser.Thf_binary_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_binary_type}.
	 * @param ctx the parse tree
	 */
	void exitThf_binary_type(tptpParser.Thf_binary_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_mapping_type}.
	 * @param ctx the parse tree
	 */
	void enterThf_mapping_type(tptpParser.Thf_mapping_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_mapping_type}.
	 * @param ctx the parse tree
	 */
	void exitThf_mapping_type(tptpParser.Thf_mapping_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_xprod_type}.
	 * @param ctx the parse tree
	 */
	void enterThf_xprod_type(tptpParser.Thf_xprod_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_xprod_type}.
	 * @param ctx the parse tree
	 */
	void exitThf_xprod_type(tptpParser.Thf_xprod_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_union_type}.
	 * @param ctx the parse tree
	 */
	void enterThf_union_type(tptpParser.Thf_union_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_union_type}.
	 * @param ctx the parse tree
	 */
	void exitThf_union_type(tptpParser.Thf_union_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_sequent}.
	 * @param ctx the parse tree
	 */
	void enterThf_sequent(tptpParser.Thf_sequentContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_sequent}.
	 * @param ctx the parse tree
	 */
	void exitThf_sequent(tptpParser.Thf_sequentContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_tuple}.
	 * @param ctx the parse tree
	 */
	void enterThf_tuple(tptpParser.Thf_tupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_tuple}.
	 * @param ctx the parse tree
	 */
	void exitThf_tuple(tptpParser.Thf_tupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_formula_list}.
	 * @param ctx the parse tree
	 */
	void enterThf_formula_list(tptpParser.Thf_formula_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_formula_list}.
	 * @param ctx the parse tree
	 */
	void exitThf_formula_list(tptpParser.Thf_formula_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_conn_term}.
	 * @param ctx the parse tree
	 */
	void enterThf_conn_term(tptpParser.Thf_conn_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_conn_term}.
	 * @param ctx the parse tree
	 */
	void exitThf_conn_term(tptpParser.Thf_conn_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_quantifier}.
	 * @param ctx the parse tree
	 */
	void enterThf_quantifier(tptpParser.Thf_quantifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_quantifier}.
	 * @param ctx the parse tree
	 */
	void exitThf_quantifier(tptpParser.Thf_quantifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#th0_quantifier}.
	 * @param ctx the parse tree
	 */
	void enterTh0_quantifier(tptpParser.Th0_quantifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#th0_quantifier}.
	 * @param ctx the parse tree
	 */
	void exitTh0_quantifier(tptpParser.Th0_quantifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#th1_quantifier}.
	 * @param ctx the parse tree
	 */
	void enterTh1_quantifier(tptpParser.Th1_quantifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#th1_quantifier}.
	 * @param ctx the parse tree
	 */
	void exitTh1_quantifier(tptpParser.Th1_quantifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_pair_connective}.
	 * @param ctx the parse tree
	 */
	void enterThf_pair_connective(tptpParser.Thf_pair_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_pair_connective}.
	 * @param ctx the parse tree
	 */
	void exitThf_pair_connective(tptpParser.Thf_pair_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#thf_unary_connective}.
	 * @param ctx the parse tree
	 */
	void enterThf_unary_connective(tptpParser.Thf_unary_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#thf_unary_connective}.
	 * @param ctx the parse tree
	 */
	void exitThf_unary_connective(tptpParser.Thf_unary_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#th1_unary_connective}.
	 * @param ctx the parse tree
	 */
	void enterTh1_unary_connective(tptpParser.Th1_unary_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#th1_unary_connective}.
	 * @param ctx the parse tree
	 */
	void exitTh1_unary_connective(tptpParser.Th1_unary_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_type}.
	 * @param ctx the parse tree
	 */
	void enterDefined_type(tptpParser.Defined_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_type}.
	 * @param ctx the parse tree
	 */
	void exitDefined_type(tptpParser.Defined_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_formula(tptpParser.Tff_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_formula(tptpParser.Tff_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_logic_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_logic_formula(tptpParser.Tff_logic_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_logic_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_logic_formula(tptpParser.Tff_logic_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_binary_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_binary_formula(tptpParser.Tff_binary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_binary_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_binary_formula(tptpParser.Tff_binary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_binary_nonassoc}.
	 * @param ctx the parse tree
	 */
	void enterTff_binary_nonassoc(tptpParser.Tff_binary_nonassocContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_binary_nonassoc}.
	 * @param ctx the parse tree
	 */
	void exitTff_binary_nonassoc(tptpParser.Tff_binary_nonassocContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_binary_assoc}.
	 * @param ctx the parse tree
	 */
	void enterTff_binary_assoc(tptpParser.Tff_binary_assocContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_binary_assoc}.
	 * @param ctx the parse tree
	 */
	void exitTff_binary_assoc(tptpParser.Tff_binary_assocContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_or_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_or_formula(tptpParser.Tff_or_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_or_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_or_formula(tptpParser.Tff_or_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_and_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_and_formula(tptpParser.Tff_and_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_and_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_and_formula(tptpParser.Tff_and_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_unitary_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_unitary_formula(tptpParser.Tff_unitary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_unitary_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_unitary_formula(tptpParser.Tff_unitary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_quantified_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_quantified_formula(tptpParser.Tff_quantified_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_quantified_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_quantified_formula(tptpParser.Tff_quantified_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_variable_list}.
	 * @param ctx the parse tree
	 */
	void enterTff_variable_list(tptpParser.Tff_variable_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_variable_list}.
	 * @param ctx the parse tree
	 */
	void exitTff_variable_list(tptpParser.Tff_variable_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_variable}.
	 * @param ctx the parse tree
	 */
	void enterTff_variable(tptpParser.Tff_variableContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_variable}.
	 * @param ctx the parse tree
	 */
	void exitTff_variable(tptpParser.Tff_variableContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_typed_variable}.
	 * @param ctx the parse tree
	 */
	void enterTff_typed_variable(tptpParser.Tff_typed_variableContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_typed_variable}.
	 * @param ctx the parse tree
	 */
	void exitTff_typed_variable(tptpParser.Tff_typed_variableContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_unary_formula}.
	 * @param ctx the parse tree
	 */
	void enterTff_unary_formula(tptpParser.Tff_unary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_unary_formula}.
	 * @param ctx the parse tree
	 */
	void exitTff_unary_formula(tptpParser.Tff_unary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_conditional}.
	 * @param ctx the parse tree
	 */
	void enterTff_conditional(tptpParser.Tff_conditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_conditional}.
	 * @param ctx the parse tree
	 */
	void exitTff_conditional(tptpParser.Tff_conditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let}.
	 * @param ctx the parse tree
	 */
	void enterTff_let(tptpParser.Tff_letContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let}.
	 * @param ctx the parse tree
	 */
	void exitTff_let(tptpParser.Tff_letContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_term_defns}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_term_defns(tptpParser.Tff_let_term_defnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_term_defns}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_term_defns(tptpParser.Tff_let_term_defnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_term_list}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_term_list(tptpParser.Tff_let_term_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_term_list}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_term_list(tptpParser.Tff_let_term_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_term_defn}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_term_defn(tptpParser.Tff_let_term_defnContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_term_defn}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_term_defn(tptpParser.Tff_let_term_defnContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_term_binding}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_term_binding(tptpParser.Tff_let_term_bindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_term_binding}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_term_binding(tptpParser.Tff_let_term_bindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_formula_defns}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_formula_defns(tptpParser.Tff_let_formula_defnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_formula_defns}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_formula_defns(tptpParser.Tff_let_formula_defnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_formula_list}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_formula_list(tptpParser.Tff_let_formula_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_formula_list}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_formula_list(tptpParser.Tff_let_formula_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_formula_defn}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_formula_defn(tptpParser.Tff_let_formula_defnContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_formula_defn}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_formula_defn(tptpParser.Tff_let_formula_defnContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_let_formula_binding}.
	 * @param ctx the parse tree
	 */
	void enterTff_let_formula_binding(tptpParser.Tff_let_formula_bindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_let_formula_binding}.
	 * @param ctx the parse tree
	 */
	void exitTff_let_formula_binding(tptpParser.Tff_let_formula_bindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_sequent}.
	 * @param ctx the parse tree
	 */
	void enterTff_sequent(tptpParser.Tff_sequentContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_sequent}.
	 * @param ctx the parse tree
	 */
	void exitTff_sequent(tptpParser.Tff_sequentContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_formula_tuple}.
	 * @param ctx the parse tree
	 */
	void enterTff_formula_tuple(tptpParser.Tff_formula_tupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_formula_tuple}.
	 * @param ctx the parse tree
	 */
	void exitTff_formula_tuple(tptpParser.Tff_formula_tupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_formula_tuple_list}.
	 * @param ctx the parse tree
	 */
	void enterTff_formula_tuple_list(tptpParser.Tff_formula_tuple_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_formula_tuple_list}.
	 * @param ctx the parse tree
	 */
	void exitTff_formula_tuple_list(tptpParser.Tff_formula_tuple_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_typed_atom}.
	 * @param ctx the parse tree
	 */
	void enterTff_typed_atom(tptpParser.Tff_typed_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_typed_atom}.
	 * @param ctx the parse tree
	 */
	void exitTff_typed_atom(tptpParser.Tff_typed_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_untyped_atom}.
	 * @param ctx the parse tree
	 */
	void enterTff_untyped_atom(tptpParser.Tff_untyped_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_untyped_atom}.
	 * @param ctx the parse tree
	 */
	void exitTff_untyped_atom(tptpParser.Tff_untyped_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_top_level_type}.
	 * @param ctx the parse tree
	 */
	void enterTff_top_level_type(tptpParser.Tff_top_level_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_top_level_type}.
	 * @param ctx the parse tree
	 */
	void exitTff_top_level_type(tptpParser.Tff_top_level_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tf1_quantified_type}.
	 * @param ctx the parse tree
	 */
	void enterTf1_quantified_type(tptpParser.Tf1_quantified_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tf1_quantified_type}.
	 * @param ctx the parse tree
	 */
	void exitTf1_quantified_type(tptpParser.Tf1_quantified_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_monotype}.
	 * @param ctx the parse tree
	 */
	void enterTff_monotype(tptpParser.Tff_monotypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_monotype}.
	 * @param ctx the parse tree
	 */
	void exitTff_monotype(tptpParser.Tff_monotypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_unitary_type}.
	 * @param ctx the parse tree
	 */
	void enterTff_unitary_type(tptpParser.Tff_unitary_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_unitary_type}.
	 * @param ctx the parse tree
	 */
	void exitTff_unitary_type(tptpParser.Tff_unitary_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_atomic_type}.
	 * @param ctx the parse tree
	 */
	void enterTff_atomic_type(tptpParser.Tff_atomic_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_atomic_type}.
	 * @param ctx the parse tree
	 */
	void exitTff_atomic_type(tptpParser.Tff_atomic_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_type_arguments}.
	 * @param ctx the parse tree
	 */
	void enterTff_type_arguments(tptpParser.Tff_type_argumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_type_arguments}.
	 * @param ctx the parse tree
	 */
	void exitTff_type_arguments(tptpParser.Tff_type_argumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_mapping_type}.
	 * @param ctx the parse tree
	 */
	void enterTff_mapping_type(tptpParser.Tff_mapping_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_mapping_type}.
	 * @param ctx the parse tree
	 */
	void exitTff_mapping_type(tptpParser.Tff_mapping_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#tff_xprod_type}.
	 * @param ctx the parse tree
	 */
	void enterTff_xprod_type(tptpParser.Tff_xprod_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#tff_xprod_type}.
	 * @param ctx the parse tree
	 */
	void exitTff_xprod_type(tptpParser.Tff_xprod_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_formula(tptpParser.Fof_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_formula(tptpParser.Fof_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_logic_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_logic_formula(tptpParser.Fof_logic_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_logic_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_logic_formula(tptpParser.Fof_logic_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_binary_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_binary_formula(tptpParser.Fof_binary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_binary_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_binary_formula(tptpParser.Fof_binary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_binary_nonassoc}.
	 * @param ctx the parse tree
	 */
	void enterFof_binary_nonassoc(tptpParser.Fof_binary_nonassocContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_binary_nonassoc}.
	 * @param ctx the parse tree
	 */
	void exitFof_binary_nonassoc(tptpParser.Fof_binary_nonassocContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_binary_assoc}.
	 * @param ctx the parse tree
	 */
	void enterFof_binary_assoc(tptpParser.Fof_binary_assocContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_binary_assoc}.
	 * @param ctx the parse tree
	 */
	void exitFof_binary_assoc(tptpParser.Fof_binary_assocContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_or_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_or_formula(tptpParser.Fof_or_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_or_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_or_formula(tptpParser.Fof_or_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_and_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_and_formula(tptpParser.Fof_and_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_and_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_and_formula(tptpParser.Fof_and_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_unitary_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_unitary_formula(tptpParser.Fof_unitary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_unitary_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_unitary_formula(tptpParser.Fof_unitary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_quantified_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_quantified_formula(tptpParser.Fof_quantified_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_quantified_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_quantified_formula(tptpParser.Fof_quantified_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_variable_list}.
	 * @param ctx the parse tree
	 */
	void enterFof_variable_list(tptpParser.Fof_variable_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_variable_list}.
	 * @param ctx the parse tree
	 */
	void exitFof_variable_list(tptpParser.Fof_variable_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_unary_formula}.
	 * @param ctx the parse tree
	 */
	void enterFof_unary_formula(tptpParser.Fof_unary_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_unary_formula}.
	 * @param ctx the parse tree
	 */
	void exitFof_unary_formula(tptpParser.Fof_unary_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_sequent}.
	 * @param ctx the parse tree
	 */
	void enterFof_sequent(tptpParser.Fof_sequentContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_sequent}.
	 * @param ctx the parse tree
	 */
	void exitFof_sequent(tptpParser.Fof_sequentContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_formula_tuple}.
	 * @param ctx the parse tree
	 */
	void enterFof_formula_tuple(tptpParser.Fof_formula_tupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_formula_tuple}.
	 * @param ctx the parse tree
	 */
	void exitFof_formula_tuple(tptpParser.Fof_formula_tupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fof_formula_tuple_list}.
	 * @param ctx the parse tree
	 */
	void enterFof_formula_tuple_list(tptpParser.Fof_formula_tuple_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fof_formula_tuple_list}.
	 * @param ctx the parse tree
	 */
	void exitFof_formula_tuple_list(tptpParser.Fof_formula_tuple_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fol_infix_unary}.
	 * @param ctx the parse tree
	 */
	void enterFol_infix_unary(tptpParser.Fol_infix_unaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fol_infix_unary}.
	 * @param ctx the parse tree
	 */
	void exitFol_infix_unary(tptpParser.Fol_infix_unaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#atomic_formula}.
	 * @param ctx the parse tree
	 */
	void enterAtomic_formula(tptpParser.Atomic_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#atomic_formula}.
	 * @param ctx the parse tree
	 */
	void exitAtomic_formula(tptpParser.Atomic_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#plain_atomic_formula}.
	 * @param ctx the parse tree
	 */
	void enterPlain_atomic_formula(tptpParser.Plain_atomic_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#plain_atomic_formula}.
	 * @param ctx the parse tree
	 */
	void exitPlain_atomic_formula(tptpParser.Plain_atomic_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_atomic_formula}.
	 * @param ctx the parse tree
	 */
	void enterDefined_atomic_formula(tptpParser.Defined_atomic_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_atomic_formula}.
	 * @param ctx the parse tree
	 */
	void exitDefined_atomic_formula(tptpParser.Defined_atomic_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_plain_formula}.
	 * @param ctx the parse tree
	 */
	void enterDefined_plain_formula(tptpParser.Defined_plain_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_plain_formula}.
	 * @param ctx the parse tree
	 */
	void exitDefined_plain_formula(tptpParser.Defined_plain_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#system_atomic_formula}.
	 * @param ctx the parse tree
	 */
	void enterSystem_atomic_formula(tptpParser.System_atomic_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#system_atomic_formula}.
	 * @param ctx the parse tree
	 */
	void exitSystem_atomic_formula(tptpParser.System_atomic_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#fol_quantifier}.
	 * @param ctx the parse tree
	 */
	void enterFol_quantifier(tptpParser.Fol_quantifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#fol_quantifier}.
	 * @param ctx the parse tree
	 */
	void exitFol_quantifier(tptpParser.Fol_quantifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#binary_connective}.
	 * @param ctx the parse tree
	 */
	void enterBinary_connective(tptpParser.Binary_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#binary_connective}.
	 * @param ctx the parse tree
	 */
	void exitBinary_connective(tptpParser.Binary_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#assoc_connective}.
	 * @param ctx the parse tree
	 */
	void enterAssoc_connective(tptpParser.Assoc_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#assoc_connective}.
	 * @param ctx the parse tree
	 */
	void exitAssoc_connective(tptpParser.Assoc_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#unary_connective}.
	 * @param ctx the parse tree
	 */
	void enterUnary_connective(tptpParser.Unary_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#unary_connective}.
	 * @param ctx the parse tree
	 */
	void exitUnary_connective(tptpParser.Unary_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_infix_formula}.
	 * @param ctx the parse tree
	 */
	void enterDefined_infix_formula(tptpParser.Defined_infix_formulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_infix_formula}.
	 * @param ctx the parse tree
	 */
	void exitDefined_infix_formula(tptpParser.Defined_infix_formulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_infix_pred}.
	 * @param ctx the parse tree
	 */
	void enterDefined_infix_pred(tptpParser.Defined_infix_predContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_infix_pred}.
	 * @param ctx the parse tree
	 */
	void exitDefined_infix_pred(tptpParser.Defined_infix_predContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#infix_equality}.
	 * @param ctx the parse tree
	 */
	void enterInfix_equality(tptpParser.Infix_equalityContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#infix_equality}.
	 * @param ctx the parse tree
	 */
	void exitInfix_equality(tptpParser.Infix_equalityContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#infix_inequality}.
	 * @param ctx the parse tree
	 */
	void enterInfix_inequality(tptpParser.Infix_inequalityContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#infix_inequality}.
	 * @param ctx the parse tree
	 */
	void exitInfix_inequality(tptpParser.Infix_inequalityContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(tptpParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(tptpParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#function_term}.
	 * @param ctx the parse tree
	 */
	void enterFunction_term(tptpParser.Function_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#function_term}.
	 * @param ctx the parse tree
	 */
	void exitFunction_term(tptpParser.Function_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#plain_term}.
	 * @param ctx the parse tree
	 */
	void enterPlain_term(tptpParser.Plain_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#plain_term}.
	 * @param ctx the parse tree
	 */
	void exitPlain_term(tptpParser.Plain_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(tptpParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(tptpParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#functor}.
	 * @param ctx the parse tree
	 */
	void enterFunctor(tptpParser.FunctorContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#functor}.
	 * @param ctx the parse tree
	 */
	void exitFunctor(tptpParser.FunctorContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_term}.
	 * @param ctx the parse tree
	 */
	void enterDefined_term(tptpParser.Defined_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_term}.
	 * @param ctx the parse tree
	 */
	void exitDefined_term(tptpParser.Defined_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_atom}.
	 * @param ctx the parse tree
	 */
	void enterDefined_atom(tptpParser.Defined_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_atom}.
	 * @param ctx the parse tree
	 */
	void exitDefined_atom(tptpParser.Defined_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_atomic_term}.
	 * @param ctx the parse tree
	 */
	void enterDefined_atomic_term(tptpParser.Defined_atomic_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_atomic_term}.
	 * @param ctx the parse tree
	 */
	void exitDefined_atomic_term(tptpParser.Defined_atomic_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_plain_term}.
	 * @param ctx the parse tree
	 */
	void enterDefined_plain_term(tptpParser.Defined_plain_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_plain_term}.
	 * @param ctx the parse tree
	 */
	void exitDefined_plain_term(tptpParser.Defined_plain_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_constant}.
	 * @param ctx the parse tree
	 */
	void enterDefined_constant(tptpParser.Defined_constantContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_constant}.
	 * @param ctx the parse tree
	 */
	void exitDefined_constant(tptpParser.Defined_constantContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#defined_functor}.
	 * @param ctx the parse tree
	 */
	void enterDefined_functor(tptpParser.Defined_functorContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#defined_functor}.
	 * @param ctx the parse tree
	 */
	void exitDefined_functor(tptpParser.Defined_functorContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#system_term}.
	 * @param ctx the parse tree
	 */
	void enterSystem_term(tptpParser.System_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#system_term}.
	 * @param ctx the parse tree
	 */
	void exitSystem_term(tptpParser.System_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#system_constant}.
	 * @param ctx the parse tree
	 */
	void enterSystem_constant(tptpParser.System_constantContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#system_constant}.
	 * @param ctx the parse tree
	 */
	void exitSystem_constant(tptpParser.System_constantContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#system_functor}.
	 * @param ctx the parse tree
	 */
	void enterSystem_functor(tptpParser.System_functorContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#system_functor}.
	 * @param ctx the parse tree
	 */
	void exitSystem_functor(tptpParser.System_functorContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#conditional_term}.
	 * @param ctx the parse tree
	 */
	void enterConditional_term(tptpParser.Conditional_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#conditional_term}.
	 * @param ctx the parse tree
	 */
	void exitConditional_term(tptpParser.Conditional_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#let_term}.
	 * @param ctx the parse tree
	 */
	void enterLet_term(tptpParser.Let_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#let_term}.
	 * @param ctx the parse tree
	 */
	void exitLet_term(tptpParser.Let_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(tptpParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(tptpParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tptpParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(tptpParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link tptpParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(tptpParser.VariableContext ctx);
}