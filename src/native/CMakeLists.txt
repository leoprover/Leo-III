################################################################
# A minimal CMake file that is compatible with sbt-jni         #
#                                                              #
# All settings required by sbt-jni have been marked so, please #
# add/modify/remove settings to build your specific library.   #
################################################################

cmake_minimum_required(VERSION 2.8.0)

# Define project and related variables
# (required by sbt-jni) please use semantic versioning
#
project (picosat)
set(PROJECT_VERSION_MAJOR 0)
set(PROJECT_VERSION_MINOR 0)
set(PROJECT_VERSION_PATCH 0)

set(CMAKE_MACOSX_RPATH 1)

# Setup JNI
find_package(JNI REQUIRED)
if (JNI_FOUND)
    message (STATUS "JNI include directories: ${JNI_INCLUDE_DIRS}")
endif()

# Include directories
include_directories(.)
include_directories(javah_include)
include_directories(${JNI_INCLUDE_DIRS})

# Picosat
set(PICOSAT_DIR picosat-965)
include_directories(${PICOSAT_DIR})

file(READ ${PICOSAT_DIR}/VERSION PICOSAT_VERSION)
string(STRIP ${PICOSAT_VERSION} PICOSAT_VERSION)
set(CMAKE_C_FLAGS "-O3 -Wall -DTRACE -DNDEBUG -fPIC")

configure_file(picosat_config.h ${PICOSAT_DIR}/config.h)
include_directories("${PROJECT_BINARY_DIR}/${PICOSAT_DIR}")

add_library(picosat_native STATIC ${PICOSAT_DIR}/picosat.c ${PICOSAT_DIR}/version.c)

# Sources
file(GLOB LIB_SRC
  "*.c"
)

# Setup installation targets
# (required by sbt-jni) major version should always be appended to library name
#
set (LIB_NAME ${PROJECT_NAME}${PROJECT_VERSION_MAJOR})

add_library(${LIB_NAME} SHARED ${LIB_SRC})
target_link_libraries(${LIB_NAME} picosat_native)

install(TARGETS ${LIB_NAME} LIBRARY DESTINATION .)
