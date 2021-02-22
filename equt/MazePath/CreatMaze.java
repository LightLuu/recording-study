package cn.edu.equt.MazePath;


import java.util.ArrayList;
import java.util.Stack;



import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

//一个入口，两个出口
//实现对路径步数的统计，从而能够使用最短路径，复选框来实现选择
//还需要实现的功能有：
//1.无动画生成路径（单选框来实现）
//2.刷新迷宫，即将红色路径给抹掉
//3.重新随机生成一个迷宫
//设置两个textare来控制行和列
public class CreatMaze extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	// 申请一个矩形数组空间
	Rectangle[][] rec;
	// 横纵
	int i = 0, j = 0; 
	// 申请一个创造迷宫maze2的空间
	Maze2 maze2;
	// 申请一个找寻迷宫的路径的空间
	MazePath mazePath; 
	 // 一个对于矩形位置分布的gridpane
	GridPane gridPane = new GridPane();
	//生成一个组成所有选项按钮的gridpane
	GridPane gridPane2;
	// 一个可以改变通过输入数值来改变迷宫大小的的文本域
	TextField tfrc = new TextField(); 
	// 设置一个组合框，具有三个选项，两条路径和一个最短路径
	private String[] path = { "Pathone", "Pathtwo", "Shortpath" };  // 用于生成组合框的选项
	private ComboBox<String> cbo = new ComboBox<String>();     // 组合框
	Timeline animation; // 自主生成的动画
	// 用于展示错 误和路径的步数
	Label lashow = new Label("Your game is show time!"); 
	RadioButton rdred;
	RadioButton rdpurple;
	@Override
	public void start(Stage primaryStage) throws Exception{
		lashow.setFont(Font.font(20)); //设置字体的大小
		lashow.setStyle("-fx-color: green");  //设置字体的颜色
		try{
			tfrc.setText("20");  // 将初始的大小设置为20
			restMaze();         // 开始的时候调用初始化迷宫

			// 为路径组合框设置选项
			ObservableList<String> items = FXCollections.observableArrayList(path);
			cbo.getItems().addAll(items);
			cbo.setStyle("-fx-color: green");
			
			// 将迷宫显示出来的按钮
			Button btbuiltmaze = new Button("BuiltMaze"); 
			btbuiltmaze.setStyle("-fx-color: pink");
			// 重新生成一个迷宫的按钮
			Button btrest = new Button("RestMze"); 
			btrest.setStyle("-fx-color: pink");
			//动画生成路径按钮
			Button  btanimation = new Button(" BtAnimation");
			// 单选框确定是否动画生成迷宫
			RadioButton rdcananimation = new RadioButton("Animation");
			rdcananimation.setSelected(true);			// 初始的时候的迷宫显示为动画显示
			RadioButton rdnotanimation = new RadioButton("Direct");
			//单选框设置是鼠标点击生成路径还是自动生成路径
			rdred = new RadioButton("Red");
			rdred.setSelected(true);
			rdpurple = new RadioButton("Purple");
			//设置滑动块
			Slider slider = new Slider();
			
			// 右边所有选项的排序布局
			gridPane2 = new GridPane();
			gridPane2.add(new Label("Please enter the row:"), 0, 0);
			gridPane2.add(tfrc, 1, 0);
			gridPane2.add(rdcananimation, 0, 1);
			gridPane2.add(rdnotanimation, 1, 1);
			gridPane2.add(btbuiltmaze, 0, 2);
			gridPane2.add(btrest, 0, 3);
			gridPane2.add(rdred,0,4);
			gridPane2.add(rdpurple, 1, 4);
			gridPane2.add(new Label("Please chose the Path:"), 0, 5);
			gridPane2.add(cbo, 1, 5);
			gridPane2.add(new Label("Control the speed"), 0, 6);
			gridPane2.add(slider, 0, 7);
			gridPane2.add(btanimation, 0, 8);
			gridPane2.setHgap(20);
			gridPane2.setVgap(20);
			
			// 将有无动画设置一个组
			ToggleGroup group = new ToggleGroup();
			rdcananimation.setToggleGroup(group);
			rdnotanimation.setToggleGroup(group);
			//将紫色和红色设置一个组
			ToggleGroup group2 = new ToggleGroup();
			rdred.setToggleGroup(group2);
			rdpurple.setToggleGroup(group2);
			// 布局
			BorderPane borderPane = new BorderPane();
			// 属性绑定
			gridPane.maxWidthProperty().bind(borderPane.widthProperty().divide(2.0));
			gridPane.maxHeightProperty().bind(borderPane.heightProperty().divide(2.0));
			gridPane2.maxWidthProperty().bind(borderPane.widthProperty().divide(2.0));
			gridPane2.maxHeightProperty().bind(borderPane.heightProperty().divide(2.0));

			Label la = new Label("This is MoLu's Maze");		//标题
			la.setFont(Font.font(20));		   				 //设置顶部标题的大小
			borderPane.setTop(la);                         //设置标题在top部
			BorderPane.setAlignment(la, Pos.CENTER);       //顶部中心对齐
			borderPane.setRight(gridPane2);               //将所有布局按钮放置在界面右部
			borderPane.setCenter(gridPane);              //将迷宫放置在界面中心
			borderPane.setBottom(lashow);                //底部设置有个显示文本，显示进程消息
			BorderPane.setAlignment(lashow, Pos.CENTER); //底部中心对齐
			
			//设置scene和stage
			Scene scene = new Scene(borderPane, 1000, 500);
			scene.setFill(Color.PINK);
			// 舞台show
			primaryStage.setTitle("This Is A Lovely Maze");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// 为组合框设置事件进程
			//此处有容错机制
			/******************************************************************************/
			cbo.setOnAction(e -> {
				
				//路径生成过程中可能出现的无第二条路径而出现的越界的情况
				//通过try-catch处理错误
				try {
					builtMazepath(cbo.getValue());
				} catch (IndexOutOfBoundsException e1) {
					lashow.setText("This maze is so samll that no the second  Path !");
				}catch(Exception e2) {
					lashow.setText(e2.getMessage());
				}
			});
			
			//此处有容错机制
			/******************************************************************************/
			//为滑动块设置进程
			slider.valueProperty().addListener(ov -> {
				try{
					animation.setRate(1.0 + slider.getValue() / 10);
				}
				catch(NullPointerException e) {
					lashow.setText("当前并没有程序在运行!");
				}
			});
			
			// 属性绑定
			borderPane.maxWidthProperty().bind(scene.widthProperty());
			borderPane.maxHeightProperty().bind(scene.heightProperty());


			// 为按钮设置事件进程
			btanimation.setOnAction(e->{
				 btanimation();
			});
			btbuiltmaze.setOnAction(e -> {
				if (rdcananimation.isSelected()) {
					anbuiltmaze(mazePath, gridPane);
				} else if (rdnotanimation.isSelected()) {
					builtmaze();
				}
			});
			// 重新输入大小，重新初始化一个相应大小的迷宫
			btrest.setOnAction(e -> {
				gridPane.getChildren().clear();
				restMaze();
			});

			// 为文本框设置事件进程
			tfrc.setOnAction(e -> {
				gridPane.getChildren().clear();
				restMaze();
			});
		}catch (Exception e) {
			lashow.setText(e.getMessage());
		}
	}

	// 创建或者重新创建 一个迷宫
	void restMaze() {
		// gridPane = new GridPane();
		int row = 0;
		try {
			row = Integer.valueOf(tfrc.getText());
			// 构造一个迷宫
			maze2 = new Maze2(row, row);
			// 找到这个迷宫从出口到入口的路径
			mazePath = new MazePath(maze2);
			// girdpane将单元格设置到节点中
			// gridPane = new GridPane();
			rec = new Rectangle[mazePath.maxrow][mazePath.maxline];
			for (int i = 0; i < mazePath.maxrow; i++) {
				for (int j = 0; j < mazePath.maxline; j++) {
					rec[i][j] = new Rectangle(10, 10);
					rec[i][j].setStroke(Color.PINK);
					rec[i][j].setFill(Color.WHITE);
					gridPane.add(rec[i][j], j, i);
				}
			}
			// 捕获文本不是数值的异常
		} catch (NumberFormatException e) {
			lashow.setText("您输入的不是一个数值! 请你重新输入!");
		}
	
	}

	// 直接生成迷宫
	void builtmaze() {
		for (int i = 0; i < mazePath.maxrow; i++) {
			for (int j = 0; j < mazePath.maxline; j++) {
				rec[i][j] = new Rectangle(10, 10);
				rec[i][j].setStroke(Color.PINK);
				rec[i][j].setFill(Color.WHITE);
				gridPane.add(rec[i][j], j, i);
			}
		}
		for (int i = 0; i < mazePath.maxrow; i++) {
			for (int j = 0; j < mazePath.maxline; j++) {
				if (mazePath.maze[i][j] == 0) {
					rec[i][j].setFill(Color.GOLD);
				} else {
					rec[i][j].setFill(Color.WHITE);
				}
			}
		}
	}

	// 进行迷宫动画实现
	void anbuiltmaze(MazePath mazePath, GridPane gridPane) {
		for (int i = 0; i < mazePath.maxrow; i++) {
			for (int j = 0; j < mazePath.maxline; j++) {
				rec[i][j] = new Rectangle(10, 10);
				rec[i][j].setStroke(Color.PINK);
				rec[i][j].setFill(Color.WHITE);
				gridPane.add(rec[i][j], j, i);
			}
		}
		i = 0;
		j = 0;
		// 自己设置一个动画，在一个时间内亮起一个单元格
		animation = new Timeline(new KeyFrame(Duration.millis(100), e -> {
			// 当一行已经被浏览完后就执行后进入下一行
			if (j >= mazePath.maxline) {
				i++;
				j = 0;
			}
			if (mazePath.maze[i][j] == 0) {
				rec[i][j].setFill(Color.GOLD);
			} else {
				rec[i][j].setFill(Color.WHITE);
			}
			// 反向生成
			if (mazePath.maze[mazePath.maxrow - 1 - i][mazePath.maxline - 1 - j] == 0) {
				rec[mazePath.maxrow - 1 - i][mazePath.maxline - 1 - j].setFill(Color.GOLD);
			} else {
				rec[mazePath.maxrow - 1 - i][mazePath.maxline - 1 - j].setFill(Color.WHITE);
			}
			j++;
		}));
//		animation.setRate(animation.getRate()+10);
		// 动画执行的次数，因为有来回所以为一半的数加一
		animation.setCycleCount(mazePath.maxrow * mazePath.maxline / 2 + 1);
		System.out.println(animation.getRate());
		// 开始执行动画
		animation.play();
	}

	// 进行迷宫路径动画的实现
	void builtMazepath(String path) throws Exception{
		// 每一次更新路径都要重新把迷宫生成一下达到清空上一个路径的功能;
		builtmaze();
		//得到找寻的路径
		Stack<int[][]> stack = mazePath.stack;
		//得到每一条路径需要的步数
		ArrayList<Integer> list = mazePath.list;
		int temp = 0;// 用于记录satck的第几个
		//判断选择的路径
		if (path.equals("Pathone")) {
			temp = 0;
		} else if (path.equals("Pathtwo")) {
			temp = 1;
		} else if (path.equals("Shortpath")) {
			
			int lenth = list.get(0);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) < lenth)
					temp = i;
			}
		}
		if(rdpurple.isSelected()) {
			//修改显示文本
			lashow.setText("This path is run "+list.get(temp)+"steps");
			for (int i = 0; i < stack.get(temp).length; i++) {
			for (int j = 0; j < stack.get(temp)[0].length; j++) {
				if (stack.get(temp)[i][j] == 5 || stack.get(temp)[i][j] == 6) {
					rec[i][j].setFill(Color.PURPLE);
				}
			}
		}
		}
		else {
			//修改显示文本
			lashow.setText("This path is run "+list.get(temp)+"steps");
			for (int i = 0; i < stack.get(temp).length; i++) {
			for (int j = 0; j < stack.get(temp)[0].length; j++) {
				if (stack.get(temp)[i][j] == 5 || stack.get(temp)[i][j] == 6) {
					rec[i][j].setFill(Color.RED);
				}
			}
		}
		}
		
	}
	//设置的路径动画进程
	void btanimation() {
		ArrayList<Integer> x=mazePath.pathx;
		ArrayList<Integer>y = mazePath.pathy;
		 i=0;
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e1->{
			lashow.setText("X:"+x.get(i)+"     "
					+ "GY:"+y.get(i));
			rec[x.get(i)][y.get(i)].setFill(Color.RED);
			i++;
		}));
		timeline.setCycleCount(x.size());
		timeline.play();
	}
	
}
