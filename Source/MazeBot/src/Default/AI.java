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
		inst = new runAI();
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
			JOptionPane.showMessageDialog(Main.parentFrame, "AI Initialized Successfully, Will Now Commence 'Walk' Type Mapping");		
			inst.execute();
		}
		else if(mode == "recur"){
			JOptionPane.showMessageDialog(Main.parentFrame, "WIP, not ready!");
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
	
		@Override
		protected Void doInBackground() throws Exception {
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
				Image scaled = map.getScaledInstance(map.getWidth() * (Main.Canvas.getIcon().getIconWidth() / Main.w), map.getHeight() * (Main.Canvas.getIcon().getIconHeight() / Main.h), Image.SCALE_DEFAULT);;
				map.setRGB(posX, posY, Color.green.getRGB());
				computeTrail[posX][posY] += 1;
				if(map.getRGB(posX - speedX, posY - speedY) != Color.black.getRGB()){
					map.setRGB(posX - speedX,  posY - speedY,  Color.white.getRGB());
					Boolean dupe = false;
					for(int k = 0; k < trailCount; k++)
						if(graphicTrail[k][0] == posX - speedX && graphicTrail[k][1] == posY - speedY)
							dupe = true;
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
				
				Main.Canvas.setIcon(new ImageIcon(scaled));
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
			}
			JOptionPane.showMessageDialog(Main.parentFrame, "Success! Please reload the map if you would like to try again");
			return null;
			
		}
		
	}
}
