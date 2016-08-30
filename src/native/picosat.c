#include "leo_modules_sat_solver_PicoSAT.h"
#include "leo_modules_sat_solver_PicoSAT__.h"

#include "picosat.h"

#define CON (PicoSAT*)context

JNIEXPORT jstring JNICALL Java_leo_modules_sat_1solver_PicoSAT_00024_picosat_1version
(JNIEnv *env, jobject clazz) {
    jstring result = (*env)->NewStringUTF(env, picosat_version());
    return result;
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_00024_picosat_1api_1version
(JNIEnv *env, jobject clazz) {
    return  PICOSAT_API_VERSION;
}

JNIEXPORT jlong JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1init
(JNIEnv *env, jobject clazz) {
    PicoSAT* ps = picosat_init();
    return (jlong)ps;
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1enable_1trace_1generation
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_enable_trace_generation(CON);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1reset
(JNIEnv *env, jobject clazz, jlong context) {
    picosat_reset(CON);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1res
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_res(CON);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1inc_1max_1var
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_inc_max_var(CON);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1add
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    return picosat_add(CON, lit);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1sat
(JNIEnv *env, jobject clazz, jlong context, jint decision_limit) {
    return picosat_sat(CON, decision_limit);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1set_1global_1default_1phase
(JNIEnv *env, jobject clazz, jlong context , jint phase) {
    picosat_set_global_default_phase(CON, phase);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1set_1default_1phase_1lit
(JNIEnv *env, jobject clazz, jlong context, jint lit, jint phase) {
    picosat_set_default_phase_lit(CON, lit, phase);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1reset_1phases
(JNIEnv *env, jobject clazz, jlong context) {
    picosat_reset_phases(CON);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1reset_1scores
(JNIEnv *env, jobject clazz, jlong context) {
    picosat_reset_scores(CON);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1remove_1learned
(JNIEnv *env, jobject clazz, jlong context, jint percentage) {
    picosat_remove_learned(CON, (unsigned)percentage);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1set_1more_1important_1lit
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    picosat_set_more_important_lit(CON, lit);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1set_1less_1important_1lit
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    picosat_set_less_important_lit(CON, lit);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1adjust
(JNIEnv *env, jobject clazz, jlong context, jint max_idx) {
    picosat_adjust(CON, max_idx);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1variables
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_variables(CON);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1added_1original_1clauses
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_added_original_clauses(CON);
}

JNIEXPORT jdouble JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1seconds
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_seconds(CON);
}

JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1assume
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    picosat_assume(CON, lit);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1deref
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    return picosat_deref(CON, lit);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1deref_1toplevel
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    return picosat_deref_toplevel(CON, lit);
}


JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1inconsistent
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_inconsistent(CON);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1failed_1assumption
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    return picosat_failed_assumption(CON, lit);
}

JNIEXPORT jintArray JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1failed_1assumptions
(JNIEnv *env, jobject clazz, jlong context) {
    const int* ar = picosat_failed_assumptions(CON);
    const int* it;
    for(it = ar; *it; ++it);
    jsize len = it - ar;

    jintArray array = (*env)->NewIntArray(env, len);
    jint* narr = (*env)->GetIntArrayElements(env, array, NULL);
    jint* nit =  narr;

    for(it = ar; *it; ++it) {
        *nit = *it;
        ++nit;
    }

    (*env)->ReleaseIntArrayElements(env, array, narr, 0);
    return array;
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1changed
(JNIEnv *env, jobject clazz, jlong context) {
    return picosat_changed(CON);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1coreclause
(JNIEnv *env, jobject clazz, jlong context, jint i) {
    return picosat_coreclause(CON, i);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1corelit
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    return picosat_corelit(CON, lit);
}

JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1usedlit
(JNIEnv *env, jobject clazz, jlong context, jint lit) {
    return picosat_usedlit(CON, lit);
}