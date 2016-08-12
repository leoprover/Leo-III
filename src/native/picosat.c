#include "leo_modules_sat_solver_PicoSAT.h"
#include "leo_modules_sat_solver_PicoSAT__.h"

#include "picosat.h"

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
    return picosat_enable_trace_generation((PicoSAT*)context);
  }

 JNIEXPORT void JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1reset
  (JNIEnv *env, jobject clazz, jlong context) {
    picosat_reset((PicoSAT*)context);
  }

  JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1res
  (JNIEnv *env, jobject clazz, jlong context) {
    return picosat_res((PicoSAT*)context);
  }

  JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1inc_1max_1var
  (JNIEnv *env, jobject clazz, jlong context) {
    return picosat_inc_max_var((PicoSAT*)context);
  }

  JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1add
  (JNIEnv *env, jobject clazz, jlong context, jint lit) {
    return picosat_add((PicoSAT*)context, lit);
  }

  JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_picosat_1sat
  (JNIEnv *env, jobject clazz, jlong context, jint decision_limit) {
    return picosat_sat((PicoSAT*)context, decision_limit);
  }