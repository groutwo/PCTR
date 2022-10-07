import java.util.Scanner;

class conVolSecuencial
{
	//static Random r = new Random();
	static Scanner s= new Scanner(System.in);
	static int dim = 10000,
			min=-20, max=20;
	
	static int[][] m1 = new int[dim][dim],
			m2 = new int[m1.length+2][m1[0].length+2],
			 mConvulsion;
	
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
	static void filtro()
	{
		for (int i = 0; i < m1.length; i++)
			for (int j = 0; j < m1[0].length; j++)
			{
				int cont = 0;
				for (int j2 = 0; j2 < mConvulsion.length; j2++)
					for (int k = 0; k < mConvulsion[0].length; k++)
						cont += m2[i+j2][j+k]*mConvulsion[j2][k];
				m1[i][j] = (cont>max?max:cont<min?min:cont);
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
		
		menu();
        long t1 = System.currentTimeMillis();
			filtro();
		long tFinal = System.currentTimeMillis() - t1;
		
		//System.out.print("La matriz final es: "); showMatriz(m1);
		
		System.out.println("Tiempo empleado: " + tFinal+" milisegundos");
	}
}