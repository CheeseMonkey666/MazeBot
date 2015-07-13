package Default;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class AI implements ActionListener {

	int startCoords[], endCoords[];
	int h, w;
	BufferedImage map, overlay;
	String mode;
	static runAI inst;
	node activeNode;
	
	public AI(String command){
		startCoords = new int[2];
		startCoords[0] = 0;
		startCoords[1] = 0;
		endCoords = new int[2];
		endCoords[0] = 0;
		endCoords[1] = 0;
		mode = command;
		map = Main.map;
		h = map.getHeight();
		w = map.getWidth();
		inst = new runAI(command);
		Boolean gotStart = false, gotEnd = false;
		for (int x = 0; x < w; x++){
			for (int y = 0; y < h; y++){
				Color color = new Color(map.getRGB(x, y), true);
				//JOptionPane.showMessageDialog(Main.parentFrame, color.toString());
				if (color.getRed() == 255 && color.getBlue() == 0 && !gotStart){
					startCoords[0] = x;
					startCoords[1] = y;
					gotStart = true;
				}
				else if (color.getBlue() == 255 && color.getRed() == 0 && !gotEnd){
					endCoords[0] = x;
					endCoords[1] = y;
					gotEnd = true;
				}
			}
		}
		if (startCoords[0] == endCoords[0] && startCoords[1] == endCoords[1])
			JOptionPane.showMessageDialog(Main.parentFrame, "Invalid Image");
		else
			Run();
	}
	
	public AI(){
		
	}
	
	private void Run(){
		
		if(mode == "walk"){					
			JOptionPane.showMessageDialog(Main.parentFrame, "AI Initialized Successfully, Will Now Commence 'Stupid Human' Type Mapping");		
			inst.execute();
		}
		else if(mode == "recur"){
			JOptionPane.showMessageDialog(Main.parentFrame, "AI Initialized Successfully, Will Now Commence 'A*' Type Mapping");
			inst.execute();
		}
		else
			JOptionPane.showMessageDialog(Main.parentFrame, "Something went wrong (AI mode not recognized)");
	}
	
	public void actionPerformed(ActionEvent e) {
		String comm = e.getActionCommand();
		new AI(comm);
			
	}

	class runAI extends SwingWorker<Void, Void>{
	Boolean running;
	String algorithm;
	
	public runAI(String type){
		algorithm = type;
	}
	
		@Override
		protected Void doInBackground() throws Exception {
			if(algorithm == "walk"){
			int speedX = 1, speedY = 0, posX = startCoords[0], posY = startCoords[1];
			int computeTrail[][] = new int[w][h], graphicTrail[][] = new int[w * h][2], trailCount = 0;;
			for(int i = 0; i < w; i++)
				for(int j = 0; j < h; j++)
					if(map.getRGB(i, j) == Color.black.getRGB())
						computeTrail[i][j] = 100;
					else
						computeTrail[i][j] = 0;
			for (int i = 0; i < w * h; i++){
				graphicTrail[i][0] = 0;
				graphicTrail[i][1] = 0;
			}
			while(map.getRGB(posX, posY) != Color.blue.getRGB()){
				
				//Thread.sleep(100);
				map.setRGB(posX, posY, Color.green.getRGB());
				computeTrail[posX][posY] += 1;
				if(map.getRGB(posX - speedX, posY - speedY) != Color.black.getRGB()){
					map.setRGB(posX - speedX,  posY - speedY,  Color.white.getRGB());
					Boolean dupe = false;
					for(int k = 0; k < trailCount; k++)
						if(graphicTrail[k][0] == posX - speedX && graphicTrail[k][1] == posY - speedY){
							dupe = true;
							//graphicTrail[k][0] = 0;
							//graphicTrail[k][1] = 0;
						}
					if(computeTrail[posX - speedX][posY - speedY] > 0 && !dupe){
						graphicTrail[trailCount][0] = posX - speedX;
						graphicTrail[trailCount][1] = posY - speedY;
						trailCount++;
					}
				}
				if(Main.trail.isSelected()){
					for(int i = 0; i < trailCount; i++){
						if(graphicTrail[i][0] != posX && graphicTrail[i][1] != posY){
							map.setRGB(graphicTrail[i][0], graphicTrail[i][1], Color.yellow.getRGB());
						}
					}
				}
				else{
					for(int j = 0; j < trailCount; j++){
						if(graphicTrail[j][0] != posX && graphicTrail[j][1] != posY){
							map.setRGB(graphicTrail[j][0], graphicTrail[j][1], Color.white.getRGB());
						}
					}
				}
				
				if(map.getRGB(posX + speedX, posY + speedY) == Color.black.getRGB()){
					if(speedX != 0){
						if(map.getRGB(posX, posY + 1) != Color.black.getRGB()){
							speedX = 0;
							speedY = 1;
						}
						else if(map.getRGB(posX, posY - 1) != Color.black.getRGB()){
							speedX = 0;
							speedY = -1;
						}
						else
							speedX = -speedX;
					}
					else{
						if(map.getRGB(posX + 1, posY) != Color.black.getRGB()){
							speedX = 1;
							speedY = 0;
						}
						else if(map.getRGB(posX - 1, posY) != Color.black.getRGB()){
							speedX = -1;
							speedY = 0;
						}
						else
							speedY = -speedY;
					}
				}
				
				else if(computeTrail[posX + speedX][posY + speedY] > 0){
					if(computeTrail[posX + speedY][posY + speedX] < computeTrail[posX + speedX][posY + speedY]){
						int buff = speedX;
						speedX = speedY;
						speedY = buff;
					}
					else if (computeTrail[posX - speedY][posY - speedX] < computeTrail[posX + speedX][posY + speedY]){
						int buff = speedX;
						speedX = -speedY;
						speedY = -buff;
					}
				}
				
				posX += speedX;
				posY += speedY;
				Image scaled = map.getScaledInstance(map.getWidth() * (Main.Canvas.getIcon().getIconWidth() / Main.w), map.getHeight() * (Main.Canvas.getIcon().getIconHeight() / Main.h), Image.SCALE_DEFAULT);
				Main.Canvas.setIcon(new ImageIcon(scaled));
			}
			JOptionPane.showMessageDialog(Main.parentFrame, "Success! Please reload the map if you would like to try again");
			}
			//####################################################################################
			//####################################################################################
			else{
				node nodes[][] = new node[w][h], startNode = null, endNode = null, closeNodes[] = new node[4], nextNode = null;
				for (int x = 0; x < w; x++)
					for (int y = 0; y < h; y++){
						if (startCoords[0] == x && startCoords[1] == y)
							startNode = nodes[x][y] = new node(x, y);
						else if (endCoords[0] == x && endCoords[1] == y)
							endNode = nodes[x][y] = new node(x, y);
						else if (map.getRGB(x, y) == Color.black.getRGB())
							nodes[x][y] = new node(x, y, true);
						else if (map.getRGB(x, y) == Color.white.getRGB())
							nodes[x][y] = new node(x, y);
					}
				activeNode = startNode;
				activeNode.G = 1;
				while(activeNode != endNode){
					map.setRGB(activeNode.x, activeNode.y, Color.yellow.getRGB());
					if(!nodes[activeNode.x - 1][activeNode.y].closed && !nodes[activeNode.x - 1][activeNode.y].isIllegal)
						closeNodes[0] = nodes[activeNode.x - 1][activeNode.y];
					else
						closeNodes[0] = null;
					if(!nodes[activeNode.x][activeNode.y - 1].closed && !nodes[activeNode.x][activeNode.y - 1].isIllegal)
						closeNodes[1] = nodes[activeNode.x][activeNode.y - 1];
					else
						closeNodes[1] = null;	
					if(!nodes[activeNode.x + 1][activeNode.y].closed && !nodes[activeNode.x + 1][activeNode.y].isIllegal)
						closeNodes[2] = nodes[activeNode.x + 1][activeNode.y];
					else
						closeNodes[2] = null;
					if(!nodes[activeNode.x][activeNode.y + 1].closed && !nodes[activeNode.x][activeNode.y + 1].isIllegal)
						closeNodes[3] = nodes[activeNode.x][activeNode.y + 1];
					else
						closeNodes[3] = null;
					nextNode = null;
					for (int j = 0; j < 4; j++)
						if(closeNodes[j] != null){
							nextNode = closeNodes[j];
							//JOptionPane.showMessageDialog(Main.parentFrame, "NextNode pos: " + nextNode.x + " " + nextNode.y);
						}
					if(nextNode == null){
						nextNode = new node(-1,-1);
						nextNode.F = 30000;
						//JOptionPane.showMessageDialog(Main.parentFrame, "NextNode pos: " + nextNode.x + " " + nextNode.y);
					}
					for (int i = 0; i < 4; i++)
						if(closeNodes[i] != null){
							if(activeNode == startNode){
								closeNodes[i].G = activeNode.G + 1;
								closeNodes[i].parent = activeNode;
							}
							else if(closeNodes[i].G > activeNode.G + 1 || !closeNodes[i].open){
								closeNodes[i].G = activeNode.G + 1;
								closeNodes[i].parent = activeNode;
							}
							closeNodes[i].open = true;
							closeNodes[i].F = closeNodes[i].G + closeNodes[i].H;
							if(closeNodes[i] == endNode)
								nextNode = endNode;
						}
					activeNode.closed = true;
					activeNode.open = false;
					if(nextNode != endNode)
						for (int x = 0; x < w; x++)
							for (int y = 0; y < h; y++)
								if(nodes[x][y].open){
									if(nodes[x][y].F < nextNode.F)
										nextNode = nodes[x][y];
									map.setRGB(x, y, Color.green.getRGB());
								}
					
					//JOptionPane.showMessageDialog(Main.parentFrame, "nextX: " + nextNode.x + " nextY: " + nextNode.y);
					if(Main.trail.isSelected()){
						Image scaled = map.getScaledInstance(map.getWidth() * (Main.Canvas.getIcon().getIconWidth() / Main.w), map.getHeight() * (Main.Canvas.getIcon().getIconHeight() / Main.h), Image.SCALE_DEFAULT);
						Main.Canvas.setIcon(new ImageIcon(scaled));
					}
					activeNode = nextNode;
				}
				while(activeNode != startNode){
					activeNode = activeNode.parent;
					map.setRGB(activeNode.x, activeNode.y, Color.red.getRGB());
					if(Main.trail.isSelected()){
						Image scaled = map.getScaledInstance(map.getWidth() * (Main.Canvas.getIcon().getIconWidth() / Main.w), map.getHeight() * (Main.Canvas.getIcon().getIconHeight() / Main.h), Image.SCALE_DEFAULT);
						Main.Canvas.setIcon(new ImageIcon(scaled));
					}
				}
				Image scaled = map.getScaledInstance(map.getWidth() * (Main.Canvas.getIcon().getIconWidth() / Main.w), map.getHeight() * (Main.Canvas.getIcon().getIconHeight() / Main.h), Image.SCALE_DEFAULT);
				Main.Canvas.setIcon(new ImageIcon(scaled));
			}
			return null;
			
		}
		
	}
	
	private class node{
		public int x, y, G, H, F;
		public node parent;
		public Boolean closed, open, isIllegal;
		
		public node(int X, int Y){
			x = X;
			y = Y;
			G = -1;
			H = getH(x, y);
			F = H + G;
			parent = null;
			closed = false;
			open = false;
			isIllegal = false;
		}
		public node(int X, int Y, Boolean illegal){
			x = X;
			y = Y;
			G = 0;
			H = getH(x, y);
			F = H + G;
			parent = null;
			closed = false;
			open = false;
			isIllegal = illegal;
		}
		
		int getH(int xPos, int yPos){
			int manhattan = Math.abs(xPos - endCoords[0]) + Math.abs(yPos - endCoords[1]);
			return manhattan;
		}
		
	}
}
