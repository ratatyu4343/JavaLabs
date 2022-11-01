#include <iostream>
#include "math.h"
#include <chrono>
#include <mpich/mpi.h>

//послідовний алгоритм
void simplMultiplikation(double **A, double **B, double **C, int size);
//стрічковий алгоритм
void lentaMultiplikation(double **A, double **B, double **C, int size);
//алгоритм Фокса
void foxMultiplication(double **A, double **B, double **C, int size);
//алгоритм Кенона
void kenonMultiplication(double **A, double **B, double **C, int size);
//створення матриці
double** creatMatrix(int m, int n);
//видалення матриці
void deleteMatrix(double **M, int m);
//виведення на екран
void printMatrix(double **M, int m, int n);
//присвоєння випадкових значень
void setMatrix(double **M, int size);
//число по модулю
int Module(int n, int mod) {
    return n >= 0 ? n % mod : (mod + n);
}

int main(int argc, char *argv[]) {
    MPI_Init(&argc, &argv);
    int myId;
    MPI_Comm_rank(MPI_COMM_WORLD, &myId);
    const int size = 512;
    double **A;
    double **B;
    double **C1;
    double **C2;
    double **C3;
    double **C4;
    std::chrono::steady_clock::time_point beginExecution;
    std::chrono::steady_clock::time_point endExecution;

    if(myId == 0) {
        A = creatMatrix(size, size);
        B = creatMatrix(size, size);
        C1 = creatMatrix(size, size);
        C2 = creatMatrix(size, size);
        C3 = creatMatrix(size, size);
        C4 = creatMatrix(size, size);
        setMatrix(A, size);
        setMatrix(B, size);
        beginExecution  = std::chrono::steady_clock::now();
        simplMultiplikation(A, B, C1, size);
        endExecution  = std::chrono::steady_clock::now();
        std::cout << "Simpl alg: " << std::chrono::duration_cast<std::chrono::milliseconds>(endExecution - beginExecution).count() << "ms\n";
    }

    beginExecution  = std::chrono::steady_clock::now();
    lentaMultiplikation(A, B, C2, size);
    endExecution  = std::chrono::steady_clock::now();
    if(myId == 0) {
        std::cout << "Lenta alg: " << std::chrono::duration_cast<std::chrono::milliseconds>(endExecution - beginExecution).count() << "ms\n";
    }
    beginExecution  = std::chrono::steady_clock::now();
    foxMultiplication(A, B, C3, size);
    endExecution  = std::chrono::steady_clock::now();
    if(myId == 0) {
        std::cout << "Fox alg: " << std::chrono::duration_cast<std::chrono::milliseconds>(endExecution - beginExecution).count() << "ms\n";
    }
    beginExecution  = std::chrono::steady_clock::now();
    kenonMultiplication(A, B, C4, size);
    endExecution  = std::chrono::steady_clock::now();
    if(myId == 0) {
        std::cout << "Kenon alg: " << std::chrono::duration_cast<std::chrono::milliseconds>(endExecution - beginExecution).count() << "ms\n";
    }
    MPI_Finalize();
}

//послідовний алгоритм
void simplMultiplikation(double **A, double **B, double **C, int size) {
    for(int i = 0; i < size; i++) {
        for(int j = 0; j < size; j++) {
            C[i][j] = 0;
            for(int k = 0; k < size; k++) {
                C[i][j] += A[i][k] * B[k][j];
            }
        }
    }
}

//стрічковий алгоритм
void lentaMultiplikation(double **A, double **B, double **C, int size) {
    int myNumber, processCount;
    MPI_Comm_rank(MPI_COMM_WORLD, &myNumber);
    MPI_Comm_size(MPI_COMM_WORLD, &processCount);
    MPI_Status status;
    int m = size;
    int n = size / processCount;
    double **bufA = creatMatrix(n, m);
    double **bufB = creatMatrix(m, n);
    double **bufC = creatMatrix(n, n);
    //початкове розподілення
    if (myNumber == 0) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                bufA[j][i] = A[j][i];
                bufB[i][j] = B[i][j];
            }
        }
        for (int i = 1; i < processCount; i++) {
            for (int j = 0; j < n; j++) {
                MPI_Send(&(A[i*n+j][0]), m, MPI_DOUBLE, i, 0, MPI_COMM_WORLD);
            }
            for (int j = 0; j < m; j++) {
                MPI_Send(&(B[j][i * n]), n, MPI_DOUBLE, i, 0, MPI_COMM_WORLD);
            }
        }
    } else {

        for (int j = 0; j < n; j++) {
            MPI_Recv(&(bufA[j][0]), m, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
        }
        for (int j = 0; j < m; j++) {
            MPI_Recv(&(bufB[j][0]), n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
        }
    }
    for (int N = 0; N < processCount; N++) {
        //calculating bufC
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                bufC[i][j] = 0;
                for (int k = 0; k < m; k++) {
                    bufC[i][j] += bufA[i][k] * bufB[k][j];
                }
            }
        }
        if (myNumber == 0) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j+ Module(processCount-N, processCount)*n] = bufC[i][j];
                }
            }
            for (int p = 1; p < processCount; p++) {
                for (int i = 0; i < n; i++) {
                    MPI_Recv(&(bufC[i][0]), n, MPI_DOUBLE, p, 0, MPI_COMM_WORLD, &status);
                    for (int j = 0; j < n; j++) {
                        C[p*n+i][Module(p-N, processCount)*n+j] = bufC[i][j];
                    }
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                MPI_Send(&(bufC[i][0]), n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
            }
        }
        for (int i = 0; i < m; i++) {
            MPI_Send(&(bufB[i][0]), n, MPI_DOUBLE, Module(myNumber+1, processCount), 0, MPI_COMM_WORLD);
            MPI_Recv(&(bufB[i][0]), n, MPI_DOUBLE, Module(myNumber-1, processCount), 0, MPI_COMM_WORLD, &status);
        }
    }
    deleteMatrix(bufA, n);
    deleteMatrix(bufB, m);
    deleteMatrix(bufC, n);
}

//алгоритм Фокса
void foxMultiplication(double **A, double **B, double **C, int size) {
    int myNumber, processCount;
    MPI_Comm_rank(MPI_COMM_WORLD, &myNumber);
    MPI_Comm_size(MPI_COMM_WORLD, &processCount);
    MPI_Status status;
    //---------------------------
    int q = (int)sqrt((double)processCount);
    int processesTable[q][q];
    int myI, myJ;
    for(int i = 0, id = 0; i < q; i++) {
        for (int j = 0; j < q; j++, id++) {
            if(id == myNumber) {
                myI = i;
                myJ = j;
            }
            processesTable[i][j] = id;
        }
    }
    //---------------------------
    int n = size / q;
    double **bufA0 = creatMatrix(n, n);
    double **bufA = creatMatrix(n, n);
    double **bufB = creatMatrix(n, n);
    double **bufC = creatMatrix(n, n);
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            bufC[i][j] = 0;
    //----initialization---------
    if(myNumber == 0) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                bufA0[i][j] = A[i+myI*n][j+myJ*n];
                bufB[i][j] = B[i+myI*n][j+myJ*n];
            }
        }
        for (int i = 0; i < q; i++) {
            for (int j = 0; j < q; j++) {
                if (processesTable[i][j] == 0) continue;
                for (int k = 0; k < n; k++) {
                    MPI_Send(&(A[i*n+k][j*n]), n, MPI_DOUBLE, processesTable[i][j], 0, MPI_COMM_WORLD);
                    MPI_Send(&(B[i*n+k][j*n]), n, MPI_DOUBLE, processesTable[i][j], 0, MPI_COMM_WORLD);
                }
            }
        }
    } else {
        for (int k = 0; k < n; k++) {
            MPI_Recv(&(bufA0[k][0]), n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(&(bufB[k][0]),  n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
        }
    }
    //--------calculating---------------
    for (int l = 0; l < q; l++) {
        //--------------------------------
        for (int i = 0; i < q; i++) {
            int j = Module(i+l, q);
            if (myI == i) {
                if(myJ == j) {
                    for (int k = 0; k < q; k++) {
                        if(myJ == k) {
                            for(int r = 0; r < n; r++) {
                                for(int h = 0; h < n; h++) {
                                    bufA[r][h] = bufA0[r][h];
                                }
                            }
                        } else {
                            for (int r = 0; r < n; r++) {
                                MPI_Send(&(bufA0[r][0]), n, MPI_DOUBLE, processesTable[i][k], 0, MPI_COMM_WORLD);
                            }
                        }
                    }
                } else {
                    for (int r = 0; r < n; r++) {
                        MPI_Recv(&(bufA[r][0]), n, MPI_DOUBLE, processesTable[i][j], 0, MPI_COMM_WORLD, &status);
                    }
                }
            }
        }

        //---------------------------------------
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++)
                    bufC[i][j] += bufA[i][k]*bufB[k][j];
            }
        }
        //-------------------------------------
        for (int i = 0; i < n; i++) {
            MPI_Send(&(bufB[i][0]), n, MPI_DOUBLE, processesTable[Module(myI-1, q)][myJ], 0, MPI_COMM_WORLD);
            MPI_Recv(&(bufB[i][0]), n, MPI_DOUBLE, processesTable[Module(myI+1, q)][myJ], 0, MPI_COMM_WORLD, &status);
        }
    }
    //------get---results--------------
    if(myNumber == 0) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = bufC[i][j];
            }
        }
        for (int i = 1; i < processCount; i++) {
            int I, J;
            MPI_Recv(&I, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(&J, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            for (int j = 0; j < n; j++) {
                MPI_Recv(&(bufC[j][0]), n, MPI_DOUBLE, i, 0, MPI_COMM_WORLD, &status);
            }
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[I*n+j][J*n+k] =bufC[j][k];
                }
            }
        }
    } else {
        MPI_Send(&myI, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        MPI_Send(&myJ, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        for(int j = 0; j < n; j++) {
            MPI_Send(&(bufC[j][0]), n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
        }
    }
    deleteMatrix(bufA0, n);
    deleteMatrix(bufA, n);
    deleteMatrix(bufB, n);
    deleteMatrix(bufC, n);
}

//алгоритм Кенона
void kenonMultiplication(double **A, double **B, double **C, int size) {
    int myNumber, processCount;
    MPI_Comm_rank(MPI_COMM_WORLD, &myNumber);
    MPI_Comm_size(MPI_COMM_WORLD, &processCount);
    MPI_Status status;
    //---------------------------
    int q = (int)sqrt((double)processCount);
    int processesTable[q][q];
    int myI, myJ;
    for(int i = 0, id = 0; i < q; i++) {
        for (int j = 0; j < q; j++, id++) {
            if(id == myNumber) {
                myI = i;
                myJ = j;
            }
            processesTable[i][j] = id;
        }
    }
    //---------------------------
    int n = size / q;
    double **bufA = creatMatrix(n, n);
    double **bufB = creatMatrix(n, n);
    double **bufC = creatMatrix(n, n);
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            bufC[i][j] = 0;
    //----initialization---------
    if(myNumber == 0) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                bufA[i][j] = A[i+myI*n][j+myJ*n];
                bufB[i][j] = B[i+myI*n][j+myJ*n];
            }
        }
        for (int i = 0; i < q; i++) {
            for (int j = 0; j < q; j++) {
                if (processesTable[i][j] == 0) continue;
                for (int k = 0; k < n; k++) {
                    MPI_Send(&(A[i*n+k][j*n]), n, MPI_DOUBLE, processesTable[i][Module(j-i, q)], 0, MPI_COMM_WORLD);
                    MPI_Send(&(B[i*n+k][j*n]), n, MPI_DOUBLE, processesTable[Module(i-j, q)][j], 0, MPI_COMM_WORLD);
                }
            }
        }
    } else {
        for (int k = 0; k < n; k++) {
            MPI_Recv(&(bufA[k][0]), n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(&(bufB[k][0]),  n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
        }
    }
    //--------calculating---------------
    for (int l = 0; l < q; l++) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++)
                    bufC[i][j] += bufA[i][k]*bufB[k][j];
            }
        }
        //-------------------------------------
        for (int i = 0; i < n; i++) {
            MPI_Send(&(bufA[i][0]), n, MPI_DOUBLE, processesTable[myI][Module(myJ-1, q)], 0, MPI_COMM_WORLD);
            MPI_Recv(&(bufA[i][0]), n, MPI_DOUBLE, processesTable[myI][Module(myJ+1, q)], 0, MPI_COMM_WORLD, &status);
            MPI_Send(&(bufB[i][0]), n, MPI_DOUBLE, processesTable[Module(myI-1, q)][myJ], 0, MPI_COMM_WORLD);
            MPI_Recv(&(bufB[i][0]), n, MPI_DOUBLE, processesTable[Module(myI+1, q)][myJ], 0, MPI_COMM_WORLD, &status);
        }
    }
    //------get---results--------------
    if(myNumber == 0) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = bufC[i][j];
            }
        }
        for (int i = 1; i < processCount; i++) {
            int I, J;
            MPI_Recv(&I, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(&J, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            for (int j = 0; j < n; j++) {
                MPI_Recv(&(bufC[j][0]), n, MPI_DOUBLE, i, 0, MPI_COMM_WORLD, &status);
            }
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[I*n+j][J*n+k] = bufC[j][k];
                }
            }
        }
    } else {
        MPI_Send(&myI, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        MPI_Send(&myJ, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        for(int j = 0; j < n; j++) {
            MPI_Send(&(bufC[j][0]), n, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
        }
    }
    deleteMatrix(bufA, n);
    deleteMatrix(bufB, n);
    deleteMatrix(bufC, n);
}

//створення матриці
double** creatMatrix(int m, int n) {
    double **M = new double* [m];
    for (int i = 0; i < m; i++) {
        M[i] = new double[n];
    }
    return M;
}

//видалення матриці
void deleteMatrix(double **M, int m) {
    for(int i = 0; i < m; i++) {
        delete [] M[i];
    }
    delete []M;
}

//виведення на екран
void printMatrix(double **M, int m, int n) {
    std::cout << "-----------------------\n";
    for(int i = 0; i < m; i++) {
        for(int j = 0; j < n; j++) {
            std::cout << M[i][j] << "\t";
        }
        std::cout << std::endl;
    }
    std::cout << "-----------------------\n";
}

//присвоєння випадкових значень
void setMatrix(double **M, int size) {
    for(int i = 0; i < size; i++) {
        for(int j = 0; j < size; j++) {
            M[i][j] = rand()%100;
        }
    }
}