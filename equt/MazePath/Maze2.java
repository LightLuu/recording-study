package cn.edu.equt.MazePath;

import java.util.Random;
//ʹ������ķ�㷨�������Թ�
//��һ����Ԫ�񱻷����ĴΣ��������󣩻������ƿ��ʹ��Ѿ����ʹ��ĵ�Ԫ���������¿�ʼ��ǽ��
//ֱ�����еĵ�Ԫ�񶼱����ʹ���Ҳ�������
public class Maze2 {
	int row,col; //���õĺ��ݾ��еĿյ�Ԫ��
	
	public int maze[][];//�洢�Թ���ͼ
	
	int r ,c; //r = 2*row+1,c=2*col+1
	// ��Ԫ������� 
	//�޲ι��췽��
	public Maze2() {
		this(10,10);
	}
	//Maze�Ĺ��캯������ʼ��row��col��ֵ
	public Maze2(int row,int col) {
		this.row = row;
		this.col = col;
		r =  2*row+1;
		c=2*col+1;
		//��ʼ��maze����
		maze = new int[r][c];
		//�����������һ������һ������
		inimaze();
		//����������ʾ�Թ�
		show();
	}
//	��ʼ���Թ� 
// 0 Ϊǽ��1Ϊ·
	public void inimaze() {
		//���е�Ԫ��Ϊǽ
		for(int i=0;i<r;i++) {
			for(int j=0;j<col;j++) {
				maze[i][j] = 0;
			}
		}
		//�����е������г�ʼΪ·
		for(int i=0;i<row;i++) {
			for(int j=0;j<col;j++) {
				maze[2*i+1][2*j+1] = 1;
			}
		}
		//ͨ��prime�㷨�����Թ�
		 primemaze(); 		
	}
	void primemaze() {
		int[] isvist,notvis; //��¼�Ѿ����ʹ��ĺ�û�з��ʹ��ĵ�Ԫ��
		
		int count = row*col;//�洢�ܹ��ĵ�Ԫ�񣨼���ʼ����1��
		int visitsize =0;//��¼�Ѿ����ʹ��ĵ�Ԫ������
		isvist = new int[count];
		notvis = new int[count];
		
		//ͨ�����������ƶ�row,col
		 int []mover = {-1,1,0,0};
		 int []movec = {0,0,1,-1};
		
		//��Ԫ����ƶ�
		 int []moveW={-1,1,row,-row};
		 //��ʼ��isvist �� notvis
		 for(int i=0;i<count;i++) {
			 isvist[i] = 0;
			 notvis[i] = 0;
		 }
		 
		 //���
		 Random random = new Random();
		 isvist[0] = random.nextInt(count);//��ʼ��
		 
		 int pos = isvist[0]; //λ�ü�¼
		 
		 notvis[pos] = 1; //���Ϊ�Ѿ����ʹ�
		 
		 while(visitsize<count) {
			 //ȡ�����ڵĵ�
			 int x = pos%col;
			 int y = pos/col;
			 int offpos = -1;//���ڼ�¼ƫ����
			 int w =0;
			 //�ĸ����򶼳���һ�飬ֱ����ͨΪֹ
			 while(++w<5) {
				 //�����������ĵ�
				 int point = random.nextInt(4);//��������ƶ��ķ���
				 int repos;
				 
				 int movex,movey; //��¼�ƶ�������x��y
				
				 //�ó��ƶ��ķ���
				 repos = pos +moveW[point]; 
				 movex= x+mover[point];
				 movey = y+movec[point];
				 //�ж��ƶ��Ƿ�Ϸ�
				 /* û�г���ǽ��
				  * Ҳû�б����ʹ���
				  * �Ϳ��Դ�����������Ԫ��֮���ǽ
				  * */
				 if(movey>=0&&movex>=0&&movex<row&&movey<col&&repos>=0&&repos<count&&notvis[repos]!=1) {
					 notvis[repos] = 1;//��δ���ʱ�ʾΪ����
					 isvist[++visitsize] = repos;//���ĵ�����Ѿ�����
					 pos = repos;// ���������Ϊ�µ����
					 offpos  = point;
					 maze[2*y+1+movec[point]][2*x+1+mover[point]] = 1; //����������Ԫ��֮���ǽ��ͨ
					 break;//�Ѿ��ҵ��������´���������ֱ���˳�
					 
				 }
				 else {
					 //����Ѿ��ﵽ�˷��ʵ����е�Ԫ��
					 if(visitsize == count-1)
						 return; 
					 continue;
				 }
				
			 }
			 //��û���ƶ��ʹ�����һ����Ԫ��ʼ��ǽ
			 if(offpos<0) {
				 pos  = isvist[random.nextInt(visitsize+1)];
			 }
			 //��ʾ��Ԫ����ƿ�����
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