import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
c++-> 02: 1.46668
c++-> 04: 1.49235
c++-> 06: 1.32633
c++-> 08: 1.29064
c++-> 10: 1.28945
c++-> 12: 1.28449
c++-> 14: 1.31994
c++-> 16: 1.27113


java-> 02: 1609
java-> 04: 1361
java-> 06: 1347
java-> 08: 1258
java-> 10: 1335
java-> 12: 1255
java-> 14: 1201
java-> 16: 1197
*/
class conVolParalela implements Runnable
{
	//static Random r = new Random();
	static Scanner s= new Scanner(System.in);
	static int dim = 10000,
			min=-20, max=20,filaActual;
	static int nNucleos = 16/*Runtime.getRuntime().availableProcessors()*/;
	
	static int[][] m1 = new int[dim][dim],
			m2 = new int[m1.length+2][m1[0].length+2],
			 mConvulsion;
	
	public conVolParalela(int i)
	{ filaActual = i; }

	static void menu()
	{
		int[][] 	enfocar 	= {{0,-1,0},	{-1,5,-1},		{0,-1,0}},
					realzarB	= {{0,0,0},		{-1,1,0},		{0,0,0}},
					detectarB 	= {{0,1,0},		{0,-4,1},		{0,1,0}},
					Sobel		= {{-1,0,1},	{2,0,2},		{-1,0,1}},
					Sharpen		= {{1,-2,1},	{-2,5,-2},		{1,-2,1}};
		int op;
		
		do {
			System.out.println("Elige kernel de convolucion:\n"
					+ "1) Enfocar\n"
					+ "2) Realzar bordes\n"
					+ "3) Detectar bordes\n"
					+ "4) Filtro de Sobel\n"
					+ "5) Filtro de Sharpen\n");
			op = s.nextInt();
		}while(op<1 ||  op>5);
		
		switch(op)
		{
			case 1: mConvulsion = enfocar; break;
			case 2: mConvulsion = realzarB; break;
			case 3: mConvulsion = detectarB; break;
			case 4: mConvulsion = Sobel; break;
			case 5: mConvulsion = Sharpen; break;
		}
	}
	
	static void inicializa()
	{
		for (int i = 0; i < m1.length; i++)
			for (int j = 0; j < m1[0].length; j++)
				m1[i][j] = (int) (Math.random()*2*max+min);
				//m1[i][j] = r.nextInt(2*max+1)+min;
		
		for (int i = 1; i < m2.length-1; i++)
			for (int j = 1; j < m2[0].length-1; j++)
				m2[i][j] = 	m1[i-1][j-1];
		//Con esto tenemos que m2=m1, pero sus bordes son 0
	}
	
	public void run()
	{
			for (int j = 0; j < m1[0].length; j++)
			{
				int cont = 0;
				for (int j2 = 0; j2 < mConvulsion.length; j2++)
					for (int k = 0; k < mConvulsion[0].length; k++)
						cont += m2[filaActual+j2][j+k]*mConvulsion[j2][k];
				m1[filaActual][j] = (cont>max?max:cont<min?min:cont);
			}
		}
	
	static void showMatriz(int[][] m)
	{
		System.out.println();
		for (int i = 0; i < m.length; i++)
		{
			for (int j = 0; j < m[0].length; j++)
				System.out.print(m[i][j]+" ");
			System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		inicializa();
		
		//System.out.print("La matriz original es: "); showMatriz(m1);
		ThreadPoolExecutor pool = new ThreadPoolExecutor(nNucleos, nNucleos, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());        
		
		menu();
		
		long t1 = System.currentTimeMillis();
			for (int i = 0; i < m1.length; i++)
				pool.execute(new conVolParalela(i));
			pool.shutdown();
			while(!pool.isTerminated());
		long tFinal = System.currentTimeMillis() - t1;
		
		//System.out.print("La matriz final es: "); showMatriz(m1);
			System.out.println("Tiempo empleado: " + tFinal+" milisegundos");
	}

}