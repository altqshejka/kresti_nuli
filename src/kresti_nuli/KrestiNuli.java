package kresti_nuli;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class KrestiNuli implements ActionListener{
	static SerialPort serialPort = null;
	Random random = new Random();
	JFrame frame = new JFrame();
	JPanel title_panel = new JPanel();
	JPanel button_panel = new JPanel();
	JPanel com_panel = new JPanel();
	JLabel textfield = new JLabel();
	JButton[] buttons = new JButton[9];
	JButton restartBtn = new JButton("Перезапустить игру");
	boolean player1_turn;
	int k=0;
	KrestiNuli(){
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800,800);
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setLayout(new BorderLayout());
		frame.setVisible(true);
		
		textfield.setBackground(new Color(25,25,25));
		textfield.setForeground(new Color(25,255,0));
		textfield.setFont(new Font("Times New Roman",Font.BOLD,75));
		textfield.setHorizontalAlignment(JLabel.CENTER);
		textfield.setText("Крестики-нолики");
		textfield.setOpaque(true);
		
		title_panel.setLayout(new BorderLayout());
		title_panel.setBounds(0,0,800,100);
		com_panel.add(restartBtn);
		
		button_panel.setLayout(new GridLayout(3,3));
		button_panel.setBackground(new Color(150,150,150));
		String[] portNames = SerialPortList.getPortNames(); // получаем список портов
		JComboBox<String> comPorts = new JComboBox<>(portNames); // создаем комбобокс с этим списком
		comPorts.setSelectedIndex(-1); // чтоб не было выбрано ничего в комбобоксе
		comPorts.addActionListener(arg -> { // слушатель выбора порта в комбобоксе
			String choosenPort = comPorts.getItemAt(comPorts.getSelectedIndex()); // получаем название выбранного порта
			//если serialPort еще не связана с портом или текущий порт не равен выбранному в комбо-боксе 
			if (serialPort == null || !serialPort.getPortName().contains(choosenPort)) {
				serialPort = new SerialPort(choosenPort); //задаем выбранный порт
				try { //тут секция с try...catch для работы с портом 
					serialPort.openPort(); //открываем порт
					serialPort.setParams(9600, 8, 1, 0); //задаем параметры порта, 9600 - скорость, такую же нужно задать для Serial.begin в Arduino
					//остальные параметры стандартные для работы с портом
					serialPort.addEventListener(event -> {  //слушатель порта для приема сообщений от ардуино
						if (event.isRXCHAR()) {// если есть данные для приема
							try {  //тут секция с try...catch для работы с портом
								String str = serialPort.readString(); //считываем данные из порта в строку
								str = str.trim(); //убираем лишние символы (типа пробелов, которые могут быть в принятой строке) 
								System.out.println(str); //выводим принятую строку
								if (str.contains("1")) { 
									if (k>2)
									{
										buttons[k].setBorder(BorderFactory.createLineBorder(new Color(32,77,128), 5));
										k-=3;
										buttons[k].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
									}
								}
								if (str.contains("2")) {
									if (k<6)
									{
										buttons[k].setBorder(BorderFactory.createLineBorder(new Color(32,77,128), 5));
										k+=3;
										buttons[k].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
									}
								}
								if (str.contains("3")) { //обработка кнопки сброса
									if (k==2 || k==5 || k==8)
									{
										buttons[k].setBorder(BorderFactory.createLineBorder(new Color(32,77,128), 5));
										k-=1;
										buttons[k].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
									}
								}
								if (str.contains("4"))
								{
									if (k==0 || k==3 || k==6)
									{
										buttons[k].setBorder(BorderFactory.createLineBorder(new Color(32,77,128), 5));
										k+=1;
										buttons[k].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
									}
								}
								if (str.contains("5"))
								{
									buttons[k].doClick();
								}
							} catch (SerialPortException ex) { //для обработки возможных ошибок
								System.out.println(ex);
							}
						}
					});
					
				} catch (SerialPortException e) {//для обработки возможных ошибок
					e.printStackTrace();
				}
			} else
				System.out.println("Same port!!"); //это если выбрали в списке тот же порт, что и до этого
		});
		for(int i=0;i<9;i++) {
			buttons[i] = new JButton();
			buttons[i].setBorder(BorderFactory.createLineBorder(new Color(32,77,128), 5));
			button_panel.add(buttons[i]);
			buttons[i].setFont(new Font("MV Boli",Font.BOLD,120));
			buttons[i].setFocusable(false);
			buttons[i].addActionListener(this);
		}
		restartBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i=0;i<9;i++)
				{
					buttons[i].setText("");
					buttons[i].setEnabled(true);
					buttons[i].setBackground(new JButton().getBackground());
				}
				textfield.setText("Крестики-нолики");
				firstTurn();
				
			}
		});
		title_panel.add(textfield);
		frame.add(title_panel,BorderLayout.NORTH);
		frame.add(button_panel);
		com_panel.add(comPorts);
		frame.add(com_panel,BorderLayout.SOUTH);
		firstTurn();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		for(int i=0;i<9;i++) {
			if(e.getSource()==buttons[i]) {
				if(player1_turn) {
					if(buttons[i].getText()=="") {
						buttons[i].setForeground(new Color(255,0,0));
						buttons[i].setText("X");
						player1_turn=false;
						textfield.setText("O ходят");
						check();
					}
				}
				else {
					if(buttons[i].getText()=="") {
						buttons[i].setForeground(new Color(0,0,255));
						buttons[i].setText("O");
						player1_turn=true;
						textfield.setText("X ходят");
						check();
					}
				}
			}			
		}
	}
	
	public void firstTurn() {
		
		
		if(random.nextInt(2)==0) {
			player1_turn=true;
			textfield.setText("X ходят");
		}
		else {
			player1_turn=false;
			textfield.setText("O ходят");
		}
	}
	
	public void check() {
		//check X win conditions
		if(
				(buttons[0].getText()=="X") &&
				(buttons[1].getText()=="X") &&
				(buttons[2].getText()=="X")
				) {
			xWins(0,1,2);
		}
		if(
				(buttons[3].getText()=="X") &&
				(buttons[4].getText()=="X") &&
				(buttons[5].getText()=="X")
				) {
			xWins(3,4,5);
		}
		if(
				(buttons[6].getText()=="X") &&
				(buttons[7].getText()=="X") &&
				(buttons[8].getText()=="X")
				) {
			xWins(6,7,8);
		}
		if(
				(buttons[0].getText()=="X") &&
				(buttons[3].getText()=="X") &&
				(buttons[6].getText()=="X")
				) {
			xWins(0,3,6);
		}
		if(
				(buttons[1].getText()=="X") &&
				(buttons[4].getText()=="X") &&
				(buttons[7].getText()=="X")
				) {
			xWins(1,4,7);
		}
		if(
				(buttons[2].getText()=="X") &&
				(buttons[5].getText()=="X") &&
				(buttons[8].getText()=="X")
				) {
			xWins(2,5,8);
		}
		if(
				(buttons[0].getText()=="X") &&
				(buttons[4].getText()=="X") &&
				(buttons[8].getText()=="X")
				) {
			xWins(0,4,8);
		}
		if(
				(buttons[2].getText()=="X") &&
				(buttons[4].getText()=="X") &&
				(buttons[6].getText()=="X")
				) {
			xWins(2,4,6);
		}
		//check O win conditions
		if(
				(buttons[0].getText()=="O") &&
				(buttons[1].getText()=="O") &&
				(buttons[2].getText()=="O")
				) {
			oWins(0,1,2);
		}
		if(
				(buttons[3].getText()=="O") &&
				(buttons[4].getText()=="O") &&
				(buttons[5].getText()=="O")
				) {
			oWins(3,4,5);
		}
		if(
				(buttons[6].getText()=="O") &&
				(buttons[7].getText()=="O") &&
				(buttons[8].getText()=="O")
				) {
			oWins(6,7,8);
		}
		if(
				(buttons[0].getText()=="O") &&
				(buttons[3].getText()=="O") &&
				(buttons[6].getText()=="O")
				) {
			oWins(0,3,6);
		}
		if(
				(buttons[1].getText()=="O") &&
				(buttons[4].getText()=="O") &&
				(buttons[7].getText()=="O")
				) {
			oWins(1,4,7);
		}
		if(
				(buttons[2].getText()=="O") &&
				(buttons[5].getText()=="O") &&
				(buttons[8].getText()=="O")
				) {
			oWins(2,5,8);
		}
		if(
				(buttons[0].getText()=="O") &&
				(buttons[4].getText()=="O") &&
				(buttons[8].getText()=="O")
				) {
			oWins(0,4,8);
		}
		if(
				(buttons[2].getText()=="O") &&
				(buttons[4].getText()=="O") &&
				(buttons[6].getText()=="O")
				) {
			oWins(2,4,6);
		} 
		if ((buttons[0].getText()!="") &&
		(buttons[1].getText()!="") &&
		(buttons[2].getText()!="") &&
		(buttons[3].getText()!="") &&
		(buttons[4].getText()!="") &&
		(buttons[5].getText()!="") &&
		(buttons[6].getText()!="") &&
		(buttons[7].getText()!="") &&
		(buttons[8].getText()!="") && 
		(textfield.getText()!="Победа крестиков")&& 
		(textfield.getText()!="Победа ноликов")
				)
		{
			textfield.setText("Победила дружба!");
			for(int i=0;i<9;i++) {
				buttons[i].setEnabled(false);
				buttons[i].setBackground(Color.GREEN);
			}
		}
	}
	
	public void xWins(int a,int b,int c) {
		buttons[a].setBackground(Color.GREEN);
		buttons[b].setBackground(Color.GREEN);
		buttons[c].setBackground(Color.GREEN);
		
		for(int i=0;i<9;i++) {
			buttons[i].setEnabled(false);
		}
		textfield.setText("Победа крестиков");
	}
	public void oWins(int a,int b,int c) {
		buttons[a].setBackground(Color.GREEN);
		buttons[b].setBackground(Color.GREEN);
		buttons[c].setBackground(Color.GREEN);
		
		for(int i=0;i<9;i++) {
			buttons[i].setEnabled(false);
		}
		textfield.setText("Победа ноликов");
	}
}