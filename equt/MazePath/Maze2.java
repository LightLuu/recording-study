package cn.edu.equt.MazePath;

import java.util.Random;
//使用普兰姆算法来生成迷宫
//在一个单元格被访问四次（随机方向后）还不能破开就从已经访问过的单元格里面重新开始破墙；
//直到所有的单元格都被访问过了也就完成了
public class Maze2 {
	int row,col; //设置的横纵具有的空单元格
	
	public int maze[][];//存储迷宫地图
	
	int r ,c; //r = 2*row+1,c=2*col+1
	// 单元格的数量 
	//无参构造方法
	public Maze2() {
		this(10,10);
	}
	//Maze的构造函数，初始化row，col的值
	public Maze2(int row,int col) {
		this.row = row;
		this.col = col;
		r =  2*row+1;
		c=2*col+1;
		//初始化maze数组
		maze = new int[r][c];
		//数组里面放零一来生成一个数组
		inimaze();
		//测试用于显示迷宫
		show();
	}
//	初始化迷宫 
// 0 为墙，1为路
	public void inimaze() {
		//所有单元格为墙
		for(int i=0;i<r;i++) {
			for(int j=0;j<col;j++) {
				maze[i][j] = 0;
			}
		}
		//奇数行的奇数列初始为路
		for(int i=0;i<row;i++) {
			for(int j=0;j<col;j++) {
				maze[2*i+1][2*j+1] = 1;
			}
		}
		//通过prime算法生成迷宫
		 primemaze(); 		
	}
	void primemaze() {
		int[] isvist,notvis; //记录已经访问过的和没有访问过的单元格
		
		int count = row*col;//存储总共的单元格（即初始化的1）
		int visitsize =0;//记录已经访问过的单元格数量
		isvist = new int[count];
		notvis = new int[count];
		
		//通过两个数来移动row,col
		 int []mover = {-1,1,0,0};
		 int []movec = {0,0,1,-1};
		
		//单元格的移动
		 int []moveW={-1,1,row,-row};
		 //初始化isvist 和 notvis
		 for(int i=0;i<count;i++) {
			 isvist[i] = 0;
			 notvis[i] = 0;
		 }
		 
		 //起点
		 Random random = new Random();
		 isvist[0] = random.nextInt(count);//起始点
		 
		 int pos = isvist[0]; //位置记录
		 
		 notvis[pos] = 1; //标记为已经访问过
		 
		 while(visitsize<count) {
			 //取除现在的点
			 int x = pos%col;
			 int y = pos/col;
			 int offpos = -1;//用于记录偏移量
			 int w =0;
			 //四个方向都尝试一遍，直到挖通为止
			 while(++w<5) {
				 //随机访问最近的点
				 int point = random.nextInt(4);//随机生成移动的方向
				 int repos;
				 
				 int movex,movey; //记录移动过后下x，y
				
				 //得出移动的方向
				 repos = pos +moveW[point]; 
				 movex= x+mover[point];
				 movey = y+movec[point];
				 //判断移动是否合法
				 /* 没有超出墙，
				  * 也没有被访问过，
				  * 就可以打破这两个单元格之间的墙
				  * */
				 if(movey>=0&&movex>=0&&movex<row&&movey<col&&repos>=0&&repos<count&&notvis[repos]!=1) {
					 notvis[repos] = 1;//将未访问表示为访问
					 isvist[++visitsize] = repos;//将改点放入已经访问
					 pos = repos;// 将这个点作为新的起点
					 offpos  = point;
					 maze[2*y+1+movec[point]][2*x+1+mover[point]] = 1; //将这两个单元的之间的墙打通
					 break;//已经找到可以重新从新起点出发直接退出
					 
				 }
				 else {
					 //如果已经达到了访问的所有单元格
					 if(visitsize == count-1)
						 return; 
					 continue;
				 }
				
			 }
			 //并没有移动就从另外一个单元格开始破墙
			 if(offpos<0) {
				 pos  = isvist[random.nextInt(visitsize+1)];
			 }
			 //显示单元格的破开数量
			 System.out.println(visitsize);
			 
		 }
		 
		 
		 
		 
	}
	void show() {
		for(int i=0;i<r;i++) {
			for(int j=0;j<c;j++) {
				if(maze[i][j] ==0) {
					System.out.print(0+" ");
				}
				else {
					System.out.print(1+" ");
				}
			}
			System.out.println();
		}
	}
	
}