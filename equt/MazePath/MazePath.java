package cn.edu.equt.MazePath;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

//����Ĺ��ܣ�������Ȼ��ݷ���
//�����ʼ����ںͳ���
//Ѱ��·��
public class MazePath {
	int[][] maze; //�洢��ͼ
	int startx, starty; //��ʼ������
	int endx1,endx2, endy;	//���ڵ�����
	int maxrow, maxline; //��¼�к���
	Random random = new Random(); //�������
	ArrayList<Integer>list = new ArrayList<Integer>();//���ڼ�¼���п���·���ĳ���
	Stack<int[][]> stack = new Stack<int[][]>(); //�洢���п��е�·��
	//�洢·����x,yֵ
	ArrayList<Integer>pathx1 = new ArrayList<Integer>();
	ArrayList<Integer>pathy1 = new ArrayList<Integer>();
	ArrayList<Integer>pathx = new ArrayList<Integer>();
	ArrayList<Integer>pathy = new ArrayList<Integer>();
	//��maze2 ����mazepath����·��Ѱ��
	public MazePath(Maze2 map) {
		this.maze = map.maze;
		this.maxrow = this.maze.length;
		this.maxline = this.maze[0].length;
		initia();
	}
	
	//��ʼ����ڳ���
	void initia() {
		// �ڵ�һ�У�y=0������ķ��䣨x = ?������һ�����
		starty = 0;
		startx = random.nextInt((maze[0].length - 1) / 2) * 2 + 1;
		System.out.println(startx + "     " + starty);
	//	maze[starty][startx] = 6;
		// �����һ�У�y = maxrow�� ����ķ��䣨x = row?������һ������
		endy = maze.length - 1;
		endx1 = random.nextInt((maze[0].length - 1) / 2) * 2 + 1;
		//������������һ������
		int w=0;
		//�˴����ݴ����
		/******************************************************************************/
		//һ���ݴ���ƣ�����ڶ��������������row ��֮��Ҳû�ܺ͵�һ�����ڲ�һ���Ͳ����ټ������ȥ���ɵڶ��ڳ���
		while((endx2 = random.nextInt((maze[0].length - 1) / 2) * 2 + 1)==endx1&&++w<(maze[0].length - 1) / 2) {
			endx2 =  random.nextInt((maze[0].length - 1) / 2) * 2 + 1;
		}
		
		maze[endy][endx1] = 1;
		maze[endy][endx2] = 1;
		System.out.println(endx1 + "     " + endy);
		System.out.println(endx2 + "     " + endy);
		
		//��ʼʹ��������Ȼ��ݷ���Ѱ������·��
		check(starty,startx);
		
	}
	void check(int i,int j) {
		//����Ѿ��ҵ��˾�print������ͬʱ�ѳ���ڶ�����Ϊ6
		if(i == endy&&(j==endx1||j==endx2)) {
			maze[starty][startx] = 6;
			maze[endy][j] = 6;
			print();
			return;
		}
		//�����ֿ����ƶ���ʱ���ȥ�����Ϊ��׼ȥ�ƶ���һ��
		//���ɹ�����ʧ�ܶ��Ὣ�����Ԫ������Ϊ1
		//Ȼ������������
		//�õ�Ԫ����ĸ����򶼻�ȥ�����ƶ�
		
		
		//�����ƶ�
		if(canmove(i,j,i+1,j)) {
			maze[i][j] = 5;
			pathx1.add(i);
			pathy1.add(j);
			check(i+1,j);
			pathx1.remove(pathy1.size()-1);
			pathy1.remove(pathy1.size()-1);
			maze[i][j] = 1;
		}
		//�����ƶ�
		if(canmove(i,j,i-1,j)) {
			maze[i][j] = 5;
			pathx1.add(i);
			pathy1.add(j);
			check(i-1,j);
			pathx1.remove(pathy1.size()-1);
			pathy1.remove(pathy1.size()-1);
			maze[i][j] = 1;
		}
		//�����ƶ�
		if(canmove(i,j,i,j+1)) {
			maze[i][j] = 5;	
			pathx1.add(i);
			pathy1.add(j);
			check(i,j+1);
			pathx1.remove(pathy1.size()-1);
			pathy1.remove(pathy1.size()-1);
			maze[i][j] = 1;
		}
		//�����ƶ�
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
	//�ж��Ƿ�����ƶ�
	boolean canmove(int i,int j,int movei,int movej) {
//		�Ƿ��Ѿ�Խ���򵽴�ǽ��Ե��
		if (movei < 0 || movej < 0 || movei >= maxrow || movej >= maxline) {
          return false;
      }
//      ����ǽ��		
      if (maze[movei][movej] == 0) {
          return false;
      }
//      �������ո���Ƿ���������
      if (maze[movei][movej] == 5) {
          return false;
      }
//�����ǣ����ǿ����ƶ�
      return true;
	}
	
	//��������ڵ�ʱ��ʹ�ӡ������·����ͬʱ������·����ȿ�¡���µ����飬��stack������
	
	 private void print() {
		 int lenth=0;//��¼һ��·�����ܲ���
		 int [][]copy = new int[maxrow][maxline];
	        System.out.println("�õ�һ���⣺");
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
	        list.add(lenth); //��ArrayList�洢ÿ��·����ʵ�ʲ���
	        for(int i=0;i<pathx1.size();i++) {
	        	System.out.println(pathx1.get(i));
	        	pathx.add(pathx1.get(i));
	        	pathy.add(pathy1.get(i));
	        }
	        stack.push(copy); //����·������������
	       //���°ѳ��ں���ڳ�ʼ��
	        maze[endy][endx1] = 1;
	        maze[endy][endx2] = 1;
	        maze[starty][startx] = 0;
	        
	    }

}




