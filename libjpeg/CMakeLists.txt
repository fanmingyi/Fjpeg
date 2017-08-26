#设置cmake最小支持版本
cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             native-lib
             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp
             #src/main/cpp/parseImage.cpp
             #src/main/cpp/queue-lib.c
             )


set(distribution_DIR   ${CMAKE_SOURCE_DIR}/libs/)



find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

find_library( # Sets the name of the path variable.
              android-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              android )




#libjpegbither.so
#add_library(libjpegbither-lib SHARED IMPORTED)
#set_target_properties(libjpegbither-lib  PROPERTIES IMPORTED_LOCATION
#${distribution_DIR}/${ANDROID_ABI}/libjpegbither.so)


#libjpeg.a
add_library(libjpeg-lib STATIC IMPORTED)
set_target_properties(libjpeg-lib  PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/${ANDROID_ABI}/libjpeg.a)


include_directories(src/main/cpp/include/)
target_link_libraries( # Specifies the target library.
                       native-lib
                       jnigraphics
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       ${android-lib}
                      #libjpegbither-lib
                      libjpeg-lib
                        )