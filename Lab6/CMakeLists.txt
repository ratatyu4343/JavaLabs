cmake_minimum_required(VERSION 3.16)
project(_6)
set(CMAKE_CXX_STANDARD 14)
find_package(MPI)
include_directories("/usr/include/mpi/")

add_executable(_6 main.cpp)
