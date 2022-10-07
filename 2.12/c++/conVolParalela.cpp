#include <iostream>
#include <stdlib.h>
#include <chrono>
#include <ctime>
#include <random>
#include <thread>
#include <algorithm>    // std::min

using namespace std;

const int dim = 10000, mini = -20, maxi=20,tamMconv = 3, numHilos=16;
int** m1, **m2, **mConvulsion;

void igualar(int**& mConv, int opElegida[][tamMconv])
{
    mConv = (int **)malloc (tamMconv*sizeof(int*));
    for (int i=0;i<tamMconv;i++)
        if ((mConv[i] = (int *) malloc (tamMconv*sizeof(int))) == nullptr)
            cout<<"NO MEMORY";
    for (int i=0;i<tamMconv;i++)
        for (int j=0;j<tamMconv;j++)
            mConv[i][j] = opElegida[i][j];
}
void menu()
{
    int	enfocar[][tamMconv]     =   {{0,-1,0},  {-1,5,-1},  {0,-1,0}},
        realzarB[][tamMconv]    =   {{0,0,0},	{-1,1,0},	{0,0,0}},
        detectarB[][tamMconv]   =   {{0,1,0},	{0,-4,1},	{0,1,0}},
        Sobel[][tamMconv]       =   {{-1,0,1},	{2,0,2},	{-1,0,1}},
        Sharpen[][tamMconv]     =   {{1,-2,1},	{-2,5,-2},	{1,-2,1}};
    int op;

    do {
        cout<<"Elige kernel de convolucion:\n"
            <<"1) Enfocar\n"
            <<"2) Realzar bordes\n"
            <<"3) Detectar bordes\n"
            <<"4) Filtro de Sobel\n"
            <<"5) Filtro de Sharpen\n";
        cin>>op;
    }while(op<1 ||  op>5);

    switch(op)
    {
        case 1: igualar(mConvulsion , enfocar   ); break;
        case 2: igualar(mConvulsion , realzarB  ); break;
        case 3: igualar(mConvulsion , detectarB ); break;
        case 4: igualar(mConvulsion , Sobel     ); break;
        case 5: igualar(mConvulsion , Sharpen   ); break;
    }
}

static void inicializa()
{
    random_device rd;  //Will be used to obtain a seed for the random number engine
    mt19937 gen(rd()); //Standard mersenne_twister_engine seeded with rd()
    uniform_int_distribution<> dis(mini, maxi);


    m1 = (int **)malloc (dim*sizeof(int*));
    for (int i=0;i<dim;i++)
        if ((m1[i] = (int *) malloc (dim*sizeof(int))) == nullptr)
            cout<<"NO MEMORY";

    for (int i = 0; i < dim; i++)
        for (int j = 0; j < dim; j++)
            m1[i][j] = (int) (dis(gen));


    m2 = (int **)malloc ((dim+2)*sizeof(int*));
    for (int i=0;i<(dim+2);i++)
        if ((m2[i] = (int *) malloc ((dim+2)*sizeof(int))) == nullptr)
            cout<<"NO MEMORY";

    for (int i = 1; i < dim+1; i++)
        for (int j = 1; j < dim+1; j++)
            m2[i][j] = 	m1[i-1][j-1];
        //Con esto tenemos que m2 = m1, pero sus bordes son 0

}

static void filtro(int ini)
{
    int fin = min(ini+dim/numHilos,dim);

    for (int i = ini; i < fin; i++)
        for (int j = 0; j < dim-1; j++)
        {
            int cont = 0;
            for (int j2 = 0; j2 < tamMconv-1; j2++)
                for (int k = 0; k < tamMconv-1; k++)
                    cont += m2[i+j2][j+k]*mConvulsion[j2][k];
            m1[i][j] = (cont>maxi?maxi:cont<mini?mini:cont);
        }
    }

 void showMatriz(int** m)
{
    puts("");
    for (int i = 0; i < dim; i++)
    {
        for (int j = 0; j < dim; j++)
            cout<<m[i][j]<<" ";
        cout<<endl;
    }
    puts("");
}

int main()
{
    inicializa();
    menu();
    std::chrono::time_point<std::chrono::system_clock> start, end;
    thread hilos[dim];
    int ini=0;
    start = std::chrono::system_clock::now();

       for(int i=0;i<numHilos;++i)
       {
            hilos[i]=thread(filtro,ini);
            ini+=dim/numHilos;
       }
       for(int i=0;i<numHilos;++i) hilos[i].join();

    end = std::chrono::system_clock::now();
    std::chrono::duration<double> tiempoTranscurrido = end-start;
    cout<<"Tiempo empleado: "<<tiempoTranscurrido.count();
    return 0;
}
