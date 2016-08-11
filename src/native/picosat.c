#include "leo_modules_sat_solver_PicoSAT.h"
#include "leo_modules_sat_solver_PicoSAT__.h"

#include "picosat.h"

/*
 * Class:     leo_modules_sat_solver_PicoSAT__
 * Method:    picosat_version
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_leo_modules_sat_1solver_PicoSAT_00024_picosat_1version
  (JNIEnv *env, jobject clazz) {
    jstring result = (*env)->NewStringUTF(env, picosat_version());
    return result;
  }

/*
 * Class:     leo_modules_sat_solver_PicoSAT__
 * Method:    picosat_api_version
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_leo_modules_sat_1solver_PicoSAT_00024_picosat_1api_1version
  (JNIEnv *env, jobject clazz) {
    return  PICOSAT_API_VERSION;
  }

/*
 * Class:     leo_modules_sat_solver_PicoSAT__
 * Method:    picosat_init
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_leo_modules_sat_1solver_PicoSAT_00024_picosat_1init
  (JNIEnv *env, jobject clazz) {
    PicoSAT* ps = picosat_init();
    return (jlong)ps;
  }