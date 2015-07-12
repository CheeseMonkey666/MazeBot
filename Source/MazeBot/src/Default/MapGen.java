package Default;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.*;

public class MapGen implements ActionListener {

	int width, height;
	
	@Override
	public void actionPerformed(ActionEvent a) {
		Generate inst = new Generate();
		Main.map = null;
		Main.checkMapBuffered();
		inst.execute();
	}
	
	class cell{
		int x, y, gridX, gridY;
		Boolean visited, backtracked;
		
		public cell(int X, int Y, int GX, int GY){
			x = X;
			y = Y;
			gridX = GX;
			gridY = GY;
			visited = false;
			backtracked = false;
		}
	}
	
	private int getNextCell(cell CurrentCell, cell Cells[][]){
		Random rand = new Random();
		int direction = -1, buff, triedDirections[] = {0,0,0,0};
		Boolean backtrack = false;
		CurrentCell.visited = true;
		while(!backtrack && direction < 0){
			buff = rand.nextInt(4);
			//JOptionPane.showMessageDialog(Main.parentFrame, "rand: " + buff);
			if(CurrentCell.x > 1 && !Cells[CurrentCell.gridX - 1][CurrentCell.gridY].visited && !Cells[CurrentCell.gridX - 1][CurrentCell.gridY].backtracked){
				if(buff == 0)
					direction = 0;
			}
			else 
				triedDirections[0] = 1;
			if(CurrentCell.y > 1 && !Cells[CurrentCell.gridX][CurrentCell.gridY - 1].visited && !Cells[CurrentCell.gridX][CurrentCell.gridY - 1].backtracked){
				if(buff == 1)
					direction = 1;
			}
			else 
				triedDirections[1] = 1;
			if(CurrentCell.x < width - 2 && !Cells[CurrentCell.gridX + 1][CurrentCell.gridY].visited && !Cells[CurrentCell.gridX + 1][CurrentCell.gridY].backtracked){
				if(buff == 2)
					direction = 2;
			}
			else 
				triedDirections[2] = 1;
			if(CurrentCell.y < height - 2 && !Cells[CurrentCell.gridX][CurrentCell.gridY + 1].visited && !Cells[CurrentCell.gridX][CurrentCell.gridY + 1].backtracked){
				if(buff == 3)
					direction = 3;
			}
			else 
				triedDirections[3] = 1;
			
			if(triedDirections[0] == 1 && triedDirections[1] == 1 && triedDirections[2] == 1 && triedDirections[3] == 1)
				backtrack = true;
		}
		//JOptionPane.showMessageDialog(Main.parentFrame, "D: " + direction);
		return direction;
	}
	
	class Generate extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {
			JTextField mapWidthInput = new JTextField("11"), mapHeightInput = new JTextField("11");
			JPanel optionPanel = new JPanel(new GridLayout(0,1));
			width = 0;
			height = 0;
			cell currentCell;
			String Width = "", Height = "";
			optionPanel.add(new JLabel("Map Width: "));
			optionPanel.add(mapWidthInput);
			optionPanel.add(new JLabel("Map Height: "));
			optionPanel.add(mapHeightInput);
			int result = JOptionPane.showConfirmDialog(Main.parentFrame, optionPanel, "Map Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if(result == JOptionPane.OK_OPTION){
				Width = mapWidthInput.getText();
				Height = mapHeightInput.getText();
				try{
					width = Integer.parseInt(Width);
					height = Integer.parseInt(Height);
				}catch(NumberFormatException err){
					JOptionPane.showMessageDialog(null, "Is it that hard to just use numbers? \n" + err);
					return null;
				}
			}
			if(width >= 900 && width <= 10 || height >= 600 || height <= 10 || width % 2 == 0 || height % 2 == 0){
				JOptionPane.showMessageDialog(null, "Invalid Input(s). Width must be 11-899 and height must be 11-599. Only odd numbers.");
				return null;
			}	
			
			BufferedImage map = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			int GX = 0, GY = 0;
			cell cells[][] = new cell[width][height];
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					if(x % 2 == 0 || y % 2 == 0)
						map.setRGB(x, y, Color.black.getRGB());
					else
						map.setRGB(x, y, Color.white.getRGB());
					if(y % 2 == 1)
						if(x % 2 == 1){
							cells[GX][GY] = new cell(x, y, GX, GY);
							//JOptionPane.showMessageDialog(Main.parentFrame, "GX: " + GX + " GY: " + GY);
							GY++;
						}
				}
				if(x % 2 == 1){
					GX++;
					GY = 0;
				}
			}
			Image image = (Image)map;
			int h = map.getHeight(), w = map.getWidth(), scale = Math.min(600 / h, 900 / w);
			Main.Canvas.setBorder(BorderFactory.createLineBorder(Color.black));
			Main.map = map;
			image = map.getScaledInstance(w * scale, h * scale, Image.SCALE_DEFAULT);
			Main.Canvas.setIcon(new ImageIcon(image));
			currentCell = cells[0][0];
			do{
				int direction = getNextCell(currentCell, cells);
				if(direction >= 0){
					if(direction == 0){
						for(int i = 1; i < 3; i++)
							map.setRGB(currentCell.x - i, currentCell.y, Color.white.getRGB());
						currentCell = cells[currentCell.gridX - 1][currentCell.gridY];
					}
					else if(direction == 1){
						for(int i = 1; i < 3; i++)
							map.setRGB(currentCell.x, currentCell.y - i, Color.white.getRGB());
						currentCell = cells[currentCell.gridX][currentCell.gridY - 1];
					}
					else if(direction == 2){
						for(int i = 1; i < 3; i++)
							map.setRGB(currentCell.x + i, currentCell.y, Color.white.getRGB());
						currentCell = cells[currentCell.gridX + 1][currentCell.gridY];
					}
					else if(direction == 3){
						for(int i = 1; i < 3; i++)
							map.setRGB(currentCell.x, currentCell.y + i, Color.white.getRGB());
						currentCell = cells[currentCell.gridX][currentCell.gridY + 1];
					}
				}
				else{
					//JOptionPane.showMessageDialog(Main.parentFrame, "current X: " + currentCell.x + " current y: " + currentCell.y);
					if(map.getRGB(currentCell.x - 1, currentCell.y) == Color.white.getRGB() && !cells[currentCell.gridX - 1][currentCell.gridY].backtracked){
						currentCell.backtracked = true;
						currentCell = cells[currentCell.gridX - 1][currentCell.gridY];
					}
					else if(map.getRGB(currentCell.x, currentCell.y - 1) == Color.white.getRGB() && !cells[currentCell.gridX][currentCell.gridY - 1].backtracked){
						currentCell.backtracked = true;
						currentCell = cells[currentCell.gridX][currentCell.gridY - 1];
					}
					else if(map.getRGB(currentCell.x + 1, currentCell.y) == Color.white.getRGB() && !cells[currentCell.gridX + 1][currentCell.gridY].backtracked){
						currentCell.backtracked = true;
						currentCell = cells[currentCell.gridX + 1][currentCell.gridY];
					}
					else if(map.getRGB(currentCell.x, currentCell.y + 1) == Color.white.getRGB() && !cells[currentCell.gridX][currentCell.gridY + 1].backtracked){
						currentCell.backtracked = true;
						currentCell = cells[currentCell.gridX][currentCell.gridY + 1];
					}
				}
				
				image = map.getScaledInstance(w * scale, h * scale, Image.SCALE_DEFAULT);
				Main.Canvas.setIcon(new ImageIcon(image));
				//JOptionPane.showMessageDialog(Main.parentFrame, "X: " + currentCell.x + " Y: " + currentCell.y);
			}while(!(currentCell.x == 1 && currentCell.y == 1));
			map.setRGB(1, 1, Color.blue.getRGB());
			map.setRGB(width - 2, height - 2, Color.red.getRGB());
			Main.map = map;
			image = map.getScaledInstance(w * scale, h * scale, Image.SCALE_DEFAULT);
			Main.Canvas.setIcon(new ImageIcon(image));
			Main.checkMapBuffered();
			return null;
		}
		
	}
	
}
