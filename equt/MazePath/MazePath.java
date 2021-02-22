package cn.edu.equt.MazePath;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

//该类的功能（深度优先回溯法）
//该类初始化入口和出口
//寻找路径
public class MazePath {
	int[][] maze; //存储地图
	int startx, starty; //开始的坐标
	int endx1,endx2, endy;	//出口的坐标
	int maxrow, maxline; //记录行和列
	Random random = new Random(); //随机数组
	ArrayList<Integer>list = new ArrayList<Integer>();//用于记录所有可行路径的长度
	Stack<int[][]> stack = new Stack<int[][]>(); //存储所有可行的路径
	//存储路径的x,y值
	ArrayList<Integer>pathx1 = new ArrayList<Integer>();
	ArrayList<Integer>pathy1 = new ArrayList<Integer>();
	ArrayList<Integer>pathx = new ArrayList<Integer>();
	ArrayList<Integer>pathy = new ArrayList<Integer>();
	//把maze2 传给mazepath进行路径寻找
	public MazePath(Maze2 map) {
		this.maze = map.maze;
		this.maxrow = this.maze.length;
		this.maxline = this.maze[0].length;
		initia();
	}
	
	//初始化入口出口
	void initia() {
		// 在第一行（y=0）随机的房间（x = ?）生成一个入口
		starty = 0;
		startx = random.nextInt((maze[0].length - 1) / 2) * 2 + 1;
		System.out.println(startx + "     " + starty);
	//	maze[starty][startx] = 6;
		// 在最后一行（y = maxrow） 随机的房间（x = row?）生成一个出口
		endy = maze.length - 1;
		endx1 = random.nextInt((maze[0].length - 1) / 2) * 2 + 1;
		//用于生成另外一个出口
		int w=0;
		//此处有容错机制
		/******************************************************************************/
		//一个容错机制，如果第二个出口在随机了row 次之后也没能和第一个出口不一样就不会再继续随机去生成第二口出口
		while((endx2 = random.nextInt((maze[0].length - 1) / 2) * 2 + 1)==endx1&&++w<(maze[0].length - 1) / 2) {
			endx2 =  random.nextInt((maze[0].length - 1) / 2) * 2 + 1;
		}
		
		maze[endy][endx1] = 1;
		maze[endy][endx2] = 1;
		System.out.println(endx1 + "     " + endy);
		System.out.println(endx2 + "     " + endy);
		
		//开始使用深度优先回溯法来寻找所有路径
		check(starty,startx);
		
	}
	void check(int i,int j) {
		//如果已经找到了就print（），同时把出入口都设置为6
		if(i == endy&&(j==endx1||j==endx2)) {
			maze[starty][startx] = 6;
			maze[endy][j] = 6;
			print();
			return;
		}
		//当出现可以移动的时候就去以这个为基准去移动下一个
		//当成功或者失败都会将这个单元格重置为1
		//然后进其余个方向
		//该单元格的四个方向都会去尝试移动
		
		
		//向下移动
		if(canmove(i,j,i+1,j)) {
			maze[i][j] = 5;
			pathx1.add(i);
			pathy1.add(j);
			check(i+1,j);
			pathx1.remove(pathy1.size()-1);
			pathy1.remove(pathy1.size()-1);
			maze[i][j] = 1;
		}
		//向上移动
		if(canmove(i,j,i-1,j)) {
			maze[i][j] = 5;
			pathx1.add(i);
			pathy1.add(j);
			check(i-1,j);
			pathx1.remove(pathy1.size()-1);
			pathy1.remove(pathy1.size()-1);
			maze[i][j] = 1;
		}
		//向右移动
		if(canmove(i,j,i,j+1)) {
			maze[i][j] = 5;	
			pathx1.add(i);
			pathy1.add(j);
			check(i,j+1);
			pathx1.remove(pathy1.size()-1);
			pathy1.remove(pathy1.size()-1);
			maze[i][j] = 1;
		}
		//向左移动
		if(canmove(i,j,i,j-1)) {
			maze[i][j] = 5;
			pathx1.add(i);
			pathy1.add(j);
			check(i,j-1);
			pathx1.remove(pathy1.size()-1);
			pathy1.remove(pathy1.size()-1);
			maze[i][j] = 1;
		}
	}
	//判定是否可以移动
	boolean canmove(int i,int j,int movei,int movej) {
//		是否已经越过或到达墙边缘了
		if (movei < 0 || movej < 0 || movei >= maxrow || movej >= maxline) {
          return false;
      }
//      遇到墙了		
      if (maze[movei][movej] == 0) {
          return false;
      }
//      在两个空格间是否在来回走
      if (maze[movei][movej] == 5) {
          return false;
      }
//都不是，就是可以移动
      return true;
	}
	
	//当到达出口的时候就打印出这条路径，同时将这条路径深度克隆到新的数组，用stack存起来
	
	 private void print() {
		 int lenth=0;//记录一条路径的总步数
		 int [][]copy = new int[maxrow][maxline];
	        System.out.println("得到一个解：");
	        for (int i = 0; i < maxrow; i++) {
	            for (int j = 0; j < maxline; j++) {
	            	if(maze[i][j]==5)
	            		lenth++;
	            	copy[i][j] = maze[i][j];
	                System.out.print(maze[maxrow-1-i][j] + " ");
	            }
	            System.out.println();
	        }
	        pathx.clear();
	        pathy.clear();
	        list.add(lenth); //用ArrayList存储每条路径的实际步数
	        for(int i=0;i<pathx1.size();i++) {
	        	System.out.println(pathx1.get(i));
	        	pathx.add(pathx1.get(i));
	        	pathy.add(pathy1.get(i));
	        }
	        stack.push(copy); //将该路径放入柞里面
	       //重新把出口和入口初始化
	        maze[endy][endx1] = 1;
	        maze[endy][endx2] = 1;
	        maze[starty][startx] = 0;
	        
	    }

}




