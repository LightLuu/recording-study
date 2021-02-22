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

//һ����ڣ���������
//ʵ�ֶ�·��������ͳ�ƣ��Ӷ��ܹ�ʹ�����·������ѡ����ʵ��ѡ��
//����Ҫʵ�ֵĹ����У�
//1.�޶�������·������ѡ����ʵ�֣�
//2.ˢ���Թ���������ɫ·����Ĩ��
//3.�����������һ���Թ�
//��������textare�������к���
public class CreatMaze extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	// ����һ����������ռ�
	Rectangle[][] rec;
	// ����
	int i = 0, j = 0; 
	// ����һ�������Թ�maze2�Ŀռ�
	Maze2 maze2;
	// ����һ����Ѱ�Թ���·���Ŀռ�
	MazePath mazePath; 
	 // һ�����ھ���λ�÷ֲ���gridpane
	GridPane gridPane = new GridPane();
	//����һ���������ѡ�ť��gridpane
	GridPane gridPane2;
	// һ�����Ըı�ͨ��������ֵ���ı��Թ���С�ĵ��ı���
	TextField tfrc = new TextField(); 
	// ����һ����Ͽ򣬾�������ѡ�����·����һ�����·��
	private String[] path = { "Pathone", "Pathtwo", "Shortpath" };  // ����������Ͽ��ѡ��
	private ComboBox<String> cbo = new ComboBox<String>();     // ��Ͽ�
	Timeline animation; // �������ɵĶ���
	// ����չʾ�� ���·���Ĳ���
	Label lashow = new Label("Your game is show time!"); 
	RadioButton rdred;
	RadioButton rdpurple;
	@Override
	public void start(Stage primaryStage) throws Exception{
		lashow.setFont(Font.font(20)); //��������Ĵ�С
		lashow.setStyle("-fx-color: green");  //�����������ɫ
		try{
			tfrc.setText("20");  // ����ʼ�Ĵ�С����Ϊ20
			restMaze();         // ��ʼ��ʱ����ó�ʼ���Թ�

			// Ϊ·����Ͽ�����ѡ��
			ObservableList<String> items = FXCollections.observableArrayList(path);
			cbo.getItems().addAll(items);
			cbo.setStyle("-fx-color: green");
			
			// ���Թ���ʾ�����İ�ť
			Button btbuiltmaze = new Button("BuiltMaze"); 
			btbuiltmaze.setStyle("-fx-color: pink");
			// ��������һ���Թ��İ�ť
			Button btrest = new Button("RestMze"); 
			btrest.setStyle("-fx-color: pink");
			//��������·����ť
			Button  btanimation = new Button(" BtAnimation");
			// ��ѡ��ȷ���Ƿ񶯻������Թ�
			RadioButton rdcananimation = new RadioButton("Animation");
			rdcananimation.setSelected(true);			// ��ʼ��ʱ����Թ���ʾΪ������ʾ
			RadioButton rdnotanimation = new RadioButton("Direct");
			//��ѡ�����������������·�������Զ�����·��
			rdred = new RadioButton("Red");
			rdred.setSelected(true);
			rdpurple = new RadioButton("Purple");
			//���û�����
			Slider slider = new Slider();
			
			// �ұ�����ѡ������򲼾�
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
			
			// �����޶�������һ����
			ToggleGroup group = new ToggleGroup();
			rdcananimation.setToggleGroup(group);
			rdnotanimation.setToggleGroup(group);
			//����ɫ�ͺ�ɫ����һ����
			ToggleGroup group2 = new ToggleGroup();
			rdred.setToggleGroup(group2);
			rdpurple.setToggleGroup(group2);
			// ����
			BorderPane borderPane = new BorderPane();
			// ���԰�
			gridPane.maxWidthProperty().bind(borderPane.widthProperty().divide(2.0));
			gridPane.maxHeightProperty().bind(borderPane.heightProperty().divide(2.0));
			gridPane2.maxWidthProperty().bind(borderPane.widthProperty().divide(2.0));
			gridPane2.maxHeightProperty().bind(borderPane.heightProperty().divide(2.0));

			Label la = new Label("This is MoLu's Maze");		//����
			la.setFont(Font.font(20));		   				 //���ö�������Ĵ�С
			borderPane.setTop(la);                         //���ñ�����top��
			BorderPane.setAlignment(la, Pos.CENTER);       //�������Ķ���
			borderPane.setRight(gridPane2);               //�����в��ְ�ť�����ڽ����Ҳ�
			borderPane.setCenter(gridPane);              //���Թ������ڽ�������
			borderPane.setBottom(lashow);                //�ײ������и���ʾ�ı�����ʾ������Ϣ
			BorderPane.setAlignment(lashow, Pos.CENTER); //�ײ����Ķ���
			
			//����scene��stage
			Scene scene = new Scene(borderPane, 1000, 500);
			scene.setFill(Color.PINK);
			// ��̨show
			primaryStage.setTitle("This Is A Lovely Maze");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// Ϊ��Ͽ������¼�����
			//�˴����ݴ����
			/******************************************************************************/
			cbo.setOnAction(e -> {
				
				//·�����ɹ����п��ܳ��ֵ��޵ڶ���·�������ֵ�Խ������
				//ͨ��try-catch�������
				try {
					builtMazepath(cbo.getValue());
				} catch (IndexOutOfBoundsException e1) {
					lashow.setText("This maze is so samll that no the second  Path !");
				}catch(Exception e2) {
					lashow.setText(e2.getMessage());
				}
			});
			
			//�˴����ݴ����
			/******************************************************************************/
			//Ϊ���������ý���
			slider.valueProperty().addListener(ov -> {
				try{
					animation.setRate(1.0 + slider.getValue() / 10);
				}
				catch(NullPointerException e) {
					lashow.setText("��ǰ��û�г���������!");
				}
			});
			
			// ���԰�
			borderPane.maxWidthProperty().bind(scene.widthProperty());
			borderPane.maxHeightProperty().bind(scene.heightProperty());


			// Ϊ��ť�����¼�����
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
			// ���������С�����³�ʼ��һ����Ӧ��С���Թ�
			btrest.setOnAction(e -> {
				gridPane.getChildren().clear();
				restMaze();
			});

			// Ϊ�ı��������¼�����
			tfrc.setOnAction(e -> {
				gridPane.getChildren().clear();
				restMaze();
			});
		}catch (Exception e) {
			lashow.setText(e.getMessage());
		}
	}

	// �����������´��� һ���Թ�
	void restMaze() {
		// gridPane = new GridPane();
		int row = 0;
		try {
			row = Integer.valueOf(tfrc.getText());
			// ����һ���Թ�
			maze2 = new Maze2(row, row);
			// �ҵ�����Թ��ӳ��ڵ���ڵ�·��
			mazePath = new MazePath(maze2);
			// girdpane����Ԫ�����õ��ڵ���
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
			// �����ı�������ֵ���쳣
		} catch (NumberFormatException e) {
			lashow.setText("������Ĳ���һ����ֵ! ������������!");
		}
	
	}

	// ֱ�������Թ�
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

	// �����Թ�����ʵ��
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
		// �Լ�����һ����������һ��ʱ��������һ����Ԫ��
		animation = new Timeline(new KeyFrame(Duration.millis(100), e -> {
			// ��һ���Ѿ����������ִ�к������һ��
			if (j >= mazePath.maxline) {
				i++;
				j = 0;
			}
			if (mazePath.maze[i][j] == 0) {
				rec[i][j].setFill(Color.GOLD);
			} else {
				rec[i][j].setFill(Color.WHITE);
			}
			// ��������
			if (mazePath.maze[mazePath.maxrow - 1 - i][mazePath.maxline - 1 - j] == 0) {
				rec[mazePath.maxrow - 1 - i][mazePath.maxline - 1 - j].setFill(Color.GOLD);
			} else {
				rec[mazePath.maxrow - 1 - i][mazePath.maxline - 1 - j].setFill(Color.WHITE);
			}
			j++;
		}));
//		animation.setRate(animation.getRate()+10);
		// ����ִ�еĴ�������Ϊ����������Ϊһ�������һ
		animation.setCycleCount(mazePath.maxrow * mazePath.maxline / 2 + 1);
		System.out.println(animation.getRate());
		// ��ʼִ�ж���
		animation.play();
	}

	// �����Թ�·��������ʵ��
	void builtMazepath(String path) throws Exception{
		// ÿһ�θ���·����Ҫ���°��Թ�����һ�´ﵽ�����һ��·���Ĺ���;
		builtmaze();
		//�õ���Ѱ��·��
		Stack<int[][]> stack = mazePath.stack;
		//�õ�ÿһ��·����Ҫ�Ĳ���
		ArrayList<Integer> list = mazePath.list;
		int temp = 0;// ���ڼ�¼satck�ĵڼ���
		//�ж�ѡ���·��
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
			//�޸���ʾ�ı�
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
			//�޸���ʾ�ı�
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
	//���õ�·����������
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
