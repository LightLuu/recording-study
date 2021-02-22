#include<reg52.h>
#include<intrins.h>
sfr P4 = 0xe8;
//3——8译码器的三个输入端
sbit A0 = P4^0;
sbit A1 = P2^0;
sbit A2 = P2^7;
//红外传感器的五个方向
sbit irR1 = P2^1;
sbit irR2 = P2^2;
sbit irR3 = P2^3;
sbit irR4 = P2^4;
sbit irR5 = P2^5;
//记录哪个传感器的方向，从而控制小车转向
bit irC =0, irL =0, irR=0, irLu =0,irRu=0; //规定C为1，左前2，左3，右前5，右4

//红外发射控制宏定义(传入传感器组号)
#define MOUSE_IR_ON(GROUP_NO)\
do\
{\
    A0=(GROUP_NO)&0X01;\
    A1=(GROUP_NO)&0X02;\
    A2=(GROUP_NO)&0X04;\
} while (0);

sbit wel1 = P4^3;//数码管的位选
sbit wel2 = P4^2;
sbit Beep = P3^7; //蜂鸣器

unsigned code tabal[] = {0xc0,0xf9,0xa4,0xb0,0x99,0x92,0x82,0xf8,0x80,0x90};  //数码管显示的0-9

unsigned char l1=0,l2=0; //用做转向修正（减小前进步数）

//堆柞，记录岔路口坐标
xdata unsigned char stack_x[20] = {-1};
xdata unsigned char stack_y[20] = {-1};
unsigned char top = 0;//堆柞指针

//单元格目标记录
unsigned char x =0,y =0;
//地图数据
unsigned char map[8][8]; //地图数据  最后都会初始化

xdata unsigned char maze[8][8];  //登高表记录

//方向记录，参考书上 绝对方向与相对方向的和与4的余数
unsigned int dir = 0,dirs = 0; //记录绝对方向  //规定0为上，1为右，2为下，3为左
//对数值进行运算
unsigned int move_x[4] = {0,10,0,-10};
unsigned int move_y[4] = {1,0,-1,0};

//目标单元格坐标
unsigned char target_x = 7;
unsigned char target_y = 7;


//转向数组
unsigned char code turn_right[]={0x11,0x33,0x22,0x66,0x44,0xcc,0x88,0x99};   //左右电机同时右转
unsigned char code turn_left[]={0x11,0x99,0x88,0xcc,0x44,0x66,0x22,0x33};    //左右电机同时左转
unsigned char code turn_right_2[] ={0x01,0x03,0x02,0x06,0x04,0x0c,0x08,0x09};  //左步机右转函数
unsigned char code turn_left_2[] ={0x10,0x90,0x80,0xc0,0x40,060,0x20,0x30};			//右步机左转函数
unsigned char code forward[] ={0x11,0x93,0x82,0xc6,0x44,0x6c,0x28,0x39};

xdata unsigned char path[20]={5};
//初始化函数
void init();
void delay_ms(unsigned int z);
void display(unsigned int k);
void setTime2(unsigned int us);
void initTime2();
void nice_go();
void turn_Right1();
void turn_Left1();
void go_straight();
void nice_modify();
void flash_back();
unsigned int jugment(unsigned int k);
int arrive();
int ending();
void cool_ergodic();
void target();
void cool_findshort();//找到最短路径

unsigned int jugment(unsigned int k)  //判断的第二个条件，及是否已经走过
{
	int happy = 0;
	k = (k+dir)%4;
	happy = dirs + move_x[k] + move_y[k];
	if(happy<0)
	{
		return 0;
	}
	if((0xf0&map[happy/10][happy%10])==0xf0)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}

void flash_back()  //回溯到上一个岔路口
{
	unsigned char happy;
	unsigned int nice=0;
	while(1)
	{
		happy = map[dirs/10][dirs%10]&0xf0;
		switch(happy)
		{
			case 0x70:nice = 0;break;
			case 0xe0:nice = 1;break;
			case 0xc0:nice = 2;break;
			case 0xd0:nice = 3;break;
			default:break;
		}
		if(nice == dir) //绝对方向相同
		{
			turn_Right1();
			turn_Right1();//两次右转_掉头
			delay_ms(500);
			go_straight();
		}
		else if ((nice+dir)%2==0) //绝对方向相反，直接直行
		{
			delay_ms(500);
			go_straight();
		}
		else  //右转左转的情况
		{
			if(dir==0)
			{
				if(nice==3) turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
			else if(dir==1)
			{
				if(nice==0)  turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
			else if(dir==2)
			{
				if(nice==1)  turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
			else if(dir==3)
			{
				if(nice==2) turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
		}
		if(arrive()||ending())  //达到最近的一个岔路口
		{
			Beep = 0;
			delay_ms(50);
			Beep = 1;
			break;
		}
	}
}
int arrive()
{
	unsigned char i =0;
	if(dirs==0) return 0;
	for(i=0;i<16;i++)
	{
		if((dirs/10)==stack_x[i]&&(dirs%10)==stack_y[i])
		{
			return 1;
		}
	}
	return 0;
}
void jilu()
{
	unsigned char s;
	unsigned char sum =0;
	x = dirs/10;y = dirs%10;
	switch (dir)  //记录进入的绝对方向  上右下左,上下左右
	{
		case 0:map[x][y]&=0x7f;break;  //上
		case 1:map[x][y]&=0xef;break; //右
		case 2:map[x][y]&=0xcf;break;  //下
		case 3:map[x][y]&=0xdf;break;//左
		default:break;
	}
	if(!(dirs==0))  //初始节点的时候是不记录后方的，其他情况都要记录来的方向
	{
		s = (dir + 2)%4;
		switch(s)
		{
				case 0: map[x][y]&=0xf7;break;
				case 1: map[x][y]&=0xfe;break;
				case 2: map[x][y]&=0xfb;break;
				case 3: map[x][y]&=0xfd;break;
				default:break;
		}
	}
	if(!irR)   //记录右方是否无墙    
	{
	sum++;
	s = (dir + 1)%4;
	switch(s)
	{
			case 0: map[x][y]&=0xf7;break;
			case 1: map[x][y]&=0xfe;break;
			case 2: map[x][y]&=0xfb;break;
			case 3: map[x][y]&=0xfd;break;
			default:break;
	}
	}
	if(!irC)  //记录前方是否无墙
	{   
	sum++;
	s = dir;
	switch(s)  //上右下左
	{
			case 0: map[x][y]&=0xf7;break;
			case 1: map[x][y]&=0xfe;break;
			case 2: map[x][y]&=0xfb;break;
			case 3: map[x][y]&=0xfd;break;
			default:break;
	}
	}
	if(!irL)  //记录左方是否无墙
	{
	sum++;
	s = (dir+3)%4;
	switch(s)
	{
			case 0: map[x][y]&=0xf7;break;
			case 1: map[x][y]&=0xfe;break;
			case 2: map[x][y]&=0xfb;break;
			case 3: map[x][y]&=0xfd;break;
			default:break;
	}
	}
	if(sum>=2)
	{
		stack_x[top] = dirs/10;
		stack_y[top] = dirs%10;
		top++;
	}
	//display(map[x][y]&0x0f);
}

void nice_modify()   //针对偏移的修正函数
{
	unsigned char i=0;
	if((irLu||irRu)&&!irC)
	{
		if(irLu)
		{
			
			for(i=0;i<8;i++)
			{
				P1 = turn_right_2[i];
				delay_ms(3);
			}
		}
		if(irRu)
		{
			for(i=0;i<8;i++)
			{
				P1 = turn_left_2[i];
				delay_ms(3);
			}
		}
		l1++;
	}
}

void go_straight()  //前进函数
{
	unsigned char i=0,j=0;
	l1 = 0;
	l2 = 0;
	for ( j = 0; j < 108-l2; j++)
	{                          //由于修正会导致电机前进，所以可以通过减少前进的n来控制小车前进的距离
			for ( i = 0; i < 8; i++)
			{
					P1 = forward[i];
					delay_ms(3);
			}
			nice_modify(); 
			l2 += l1/2;
			l1 = l1%2;
	}
	dirs = dirs+move_x[dir]+move_y[dir];
	display(dirs);
}

void turn_Left1()
{
	unsigned char i=0,j=0;
	for ( j = 0; j < 50; j++)
	{
		 for ( i = 0; i < 8; i++)
			{
					P1 = turn_left[i];
					delay_ms(3);
			}		
	}
	dir =(dir + 3)%4;  
}
void turn_Right1()
{
	unsigned char i=0,j=0;
	for ( j = 0; j < 50; j++)
	{
		 for ( i = 0; i < 8; i++)
			{
					P1 = turn_right[i];
					delay_ms(3);
			}
			
	}
	dir =(dir + 1)%4;    //右转之后绝对方向改变
}

void nice_go() //右转法则,进行判断前进
{
	if(!arrive())jilu();				//如果是已经走过的岔路口就不记录信息
	if(!irR&&jugment(1))  //右方无墙
	{  
		turn_Right1();
		delay_ms(500);
		go_straight();
	}
	else if (!irC&&jugment(0))
	{
		delay_ms(500);
		go_straight();
	}
	else if (!irL&&jugment(3))
	{
		turn_Left1();
		delay_ms(500);
		go_straight();
	}
	else 
	{
		flash_back();
	}
}

void display(unsigned int k)//显示函数
{
	unsigned char a,b;
	a = k/10;b = k%10;
  wel1 = 1;wel2 = 0;//开启数码管一的循环
	P0 = tabal[a];
	wel1 = 0;wel2 = 1;
  P0 = tabal[b];
	wel1 = 0;wel2 = 0;	
}

void setTime2(unsigned int us)//设置T2自动重载寄存器和计数器初值
{
    TH2    = (65536-us)/256;
    RCAP2H = (65536-us)/256;
    TL2    = (65536-us)%256;
    RCAP2L = (65536-us)%256;
}
void initTime2()//初始化中断函数
{
    EA = 1;//所有中断的总开关
    ET2 = 1; //T2 中断
    setTime2(5000);
    TR2 = 1; //启动定时器2
}

void init() //初始化迷宫函数
{
	unsigned char i,j;
    for(i=0;i<8;i++)
	{
		for(j=0;j<8;j++)
		{
				map[i][j]=0xff;
		}
	}
}
void delay_ms(unsigned int z)//软件延时
{
    unsigned char u, o;
    while(--z)
    {
    _nop_();
    u = 2;
    o = 199;
    do
    {
    while (--o);
    } while (--u);
    }
}
void target()  //判断是否到达目标结点
{
	if(dirs == target_x*10 + target_y )
	{
		Beep = 0;
		delay_ms(1000);//判断到终点就响
		Beep = 1;
	}
}
int ending()  //判断是否回到起点
{
	if(dirs==0)
	{
		return 1;
	}
	else 
		return 0;
}
void main()
{	
    init(); //初始化迷宫数据
		initTime2();//初始化中断函数
		while(1)
		{
			delay_ms(1000);
			x = dirs/10;
			y = dirs%10;
			nice_go();
			target();    //走完判断是否是中点，终点就响一秒
			if(ending())  //每走一格或者回溯到了岔路口，或者回到了起点都会进行一次判断
			{
				break;
			}
		}
		//delay_ms(1000);
		//cool_ergodic(); //构建登高表
		//display(99); //可以运行到这里，在cool_findshort里面die了，这是为什么喃，我也不知道啊，cool！！！！！！！！！nice，ggggggggg
		//cool_findshort();//找到最短路径
		//Beep = 0;
		//display(88);
		//delay_ms(1000);
		//Beep = 1;
		while(1);
}


void time2() interrupt 5 //T2中断
{       
	static unsigned int temp =0;
	static bit ir = 0;
	TF2 = 0;
	if(!ir)             //防止判定为改变后的方向
	{   
			MOUSE_IR_ON(temp);
	}     
	else
	{
	switch (temp++)
	{
	case 0://前
			if (!irR1)
			{
					irC = 1;
					//display(1);
			}
			else           
			{
					irC = 0;
					Beep = 1;
			}
			MOUSE_IR_ON(5);break;
	case 1://左前
			if(!irR2)
			{
					irLu = 1;
				Beep = 0;
					//display(2);
					
			}
			else
			{
					irLu = 0;
					Beep = 1;
			}
			MOUSE_IR_ON(5);break;
	case 2://左
			if(!irR3)
			{
					irL = 1;
				display(3);
			}
			else
			{
					irL = 0;
			Beep = 1;
			}
			MOUSE_IR_ON(5);break;
	case 3://右
			if(!irR4)
			{
					irR  = 1;
					//display(4);
			}
			else
			{
					irR = 0;
				Beep = 1;
			}
			MOUSE_IR_ON(5);break;
	case 4://右前
			if(!irR5)
			{
					irRu = 1;
					//display(5);
					Beep = 0; 
			}
			else
			{
					irRu = 0;
					Beep = 1;
					Beep = 1;
			}
			MOUSE_IR_ON(5);break;
			default:break;
	}    
	} 
	ir = ~ir;
	if(temp == 5)
	{
		temp = 0;
	}
}
int cool_ending()  //判断登高表是否已经做完所有遍历
{
	unsigned char i=0,j=0;
	for(i=0;i<8;i++)
	{
		for(j=0;j<8;j++)
		{
			if(maze[i][j]== 55)
			{
				return 1;
			}
		}
	}
	return 0;
}
void init_maze() //初始化登高表
{
	unsigned char i,j;
	for(i=0;i<8;i++)
	{
		for(j=0;j<8;j++)
		{
			maze[i][j] = 55;
		}
	}
}	
xdata unsigned char quen_x[64];  //队列x坐标
xdata unsigned char quen_y[64];  //对列y坐标
void cool_ergodic() //使用广度优先构建登高表 //循环队列 尾部插入，头部取出，即head++，tail++
{
	unsigned char head =0,tail=0;    //头指针和尾部指针
	unsigned char i,j;               //记录当前出队列的单元格的x,y坐标
	unsigned char happy;             //用于对 i 或者 j 进行计算
	Beep = 0;
	delay_ms(5000);
	Beep = 1;
	init_maze();											//初始化登高表 开始全为-1
	quen_x[0] = 0; quen_y[0] = 0;     //起点入队
	tail++;                           //尾节点向后延伸
	maze[0][0] = 1;                    //起点为1步
	while(cool_ending())   //如果头结点追上了尾节点或者整个迷宫单元格已经全部有个步长，即都不在为1 就可以退出循环了
	{
		i = quen_x[head];            //取出头部单元格
		j = quen_y[head];
		head++;											//头结点向后延伸
		if(head==64)                //头结点向后延伸到20，即队列满的情况下会再回到起点开始继续加
		{
			head = 0;
		}
		if((map[i][j]&0x08) == 0x00)//上方无墙
		{
			happy = j+1;
			if(maze[i][happy]==55) //还没有走过
			{
				maze[i][happy] = maze[i][j]+1; //上方单元格加一
				quen_x[tail] = i;  //该单元个入队
				quen_y[tail] = happy;
				tail++; //尾部指正指向下一个空数组
				if(tail==64)
				{
					tail = 0; //循环指针，当已经记录到数组的尾部，就会回到起点开始记录。
				}
			}
		}
		if((map[i][j]&0x01) == 0x00) //右方无墙
		{
			happy = i+1;
			if(happy<8&&(maze[happy][j]==55)) //还没有走过
			{
				maze[happy][j] = maze[i][j]+1; //右方单元格加一
				quen_x[tail] = happy; //该单元格入队
				quen_y[tail] = j;
				tail++;//尾部指针指向下一个空数组
				if(tail==64)
				{
					tail = 0;////循环指针，当已经记录到数组的尾部，就会回到起点开始记录。
				}
			}
		}
		if((map[i][j]&0x02)==0x00)  //左方无墙   有墙时不会进行下面的运算所以不会出现数组越界的情况
		{
			happy = i -1;
			if((happy>=0&&maze[happy][j]==55)) //还没有走过
			{
				maze[happy][j] = maze[i][j]+1; //左方单元格加一
				quen_x[tail] = happy; //该单元格入队
				quen_y[tail] = j;
				tail++;//尾部指针指向下一个空数组
				if(tail==64)
				{
					tail = 0;////循环指针，当已经记录到数组的尾部，就会回到起点开始记录。
				}
			}
		}	
		if((map[i][j]&0x04)==0x00&&!(i==0&&j==0))  //下方无墙  因为初始化的时候默认都是有墙，所以单元格来的方向肯定是无墙的，因此在记录的时候应该把来的方向也记入单元格
		{
			happy = j-1;
			if(happy>=0&&maze[i][happy]==55) //还没有走过
			{
				maze[i][happy] = maze[i][j]+1; //下方单元格加一
				quen_x[tail] = i;  //该单元个入队
				quen_y[tail] = happy;
				tail++; //尾部指正指向下一个空数组
				if(tail==64)
				{
					tail = 0; //循环指针，当已经记录到数组的尾部，就会回到起点开始记录。
				}
			}
		} 
	}
} 
void cool_findshort()  //找到最短路径
{
	
	unsigned char i = target_x,k=0;
	unsigned char j = target_y;
	unsigned char num = 0;
	unsigned char nice;
	Beep = 0;
	delay_ms(1000);
	Beep = 1;
	for(k=0;k<20;k++)
	{
		path[k] = 5; 
	}
	while(!(i==0&&j==0))
	{
		 if(i-1>=0)//左方
		 {
			 if((map[i][j]&0x02)==0x00&&maze[i-1][j]==maze[i][j]-1) //can_go 即没有墙 
			 {
				 i = i-1;j=j;
				 path[num++] = 3;
				 continue;
			 }
		 }
		 if(i+1<8)//右方
		 {
			 if((map[i][j]&0x01) == 0x00&&maze[i+1][j]==maze[i][j]-1)
			 {
				 i = i+1;j=j;
				 path[num++] = 1;
				 continue;
			 }
		 }
		 if(j-1>=0) //下方
		 {
			 if((map[i][j]&0x04)==0x00&&maze[i][j-1]==maze[i][j]-1)
			 {
				 i=i;j=j-1;
				 path[num++] = 2;
				 continue;
			 }
		 }
		 if(j+1<8)//上方
		 {
			 if(maze[i][j+1]==maze[i][j]-1)
			 {
				 i=i;j=j+1;
				 path[num++] = 0;
				 continue;
			 }
		 } 
	}
	Beep = 0;
	delay_ms(1000);
	Beep = 1;
	for(i=19;i>=0;i--)
	{
		if(path[i]==5) continue;
		nice = path[i];
		if(nice == dir) //绝对方向相同
		{
			turn_Right1();
			turn_Right1();//两次右转_掉头
			delay_ms(500);
			go_straight();
		}
		else if ((nice+dir)%2==0) //绝对方向相反，直接直行
		{
			delay_ms(500);
			go_straight();
		}
		else  //右转左转的情况
		{
			if(dir==0)
			{
				if(nice==3) turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
			else if(dir==1)
			{
				if(nice==0)  turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
			else if(dir==2)
			{
				if(nice==1)  turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
			else if(dir==3)
			{
				if(nice==2) turn_Right1();
				else  turn_Left1();
				delay_ms(500);
				go_straight();
			}
		}
		if(dirs==target_x*10+target_y)
		{
			Beep = 0;
			break;
		}
	}
}	

