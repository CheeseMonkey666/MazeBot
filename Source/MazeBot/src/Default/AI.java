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
	BufferedImage map;
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
			while(map.getRGB(posX, posY) != Color.blue.getRGB()){
				
				//Thread.sleep(500);
				int front = map.getRGB(posX, posY);
				map.setRGB(posX, posY, Color.green.getRGB());
				if(map.getRGB(posX - speedX, posY - speedY) != Color.black.getRGB())
					map.setRGB(posX - speedX, posY - speedY, front - new Color(5,5,5).getRGB());
				Image scaled = map.getScaledInstance(map.getWidth() * Main.mapScale, map.getHeight() * Main.mapScale, Image.SCALE_DEFAULT);
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
				
				if(map.getRGB(posX + speedX, posY + speedY) < Color.white.getRGB()){
					if(map.getRGB(posX + speedY, posY + speedX) > map.getRGB(posX + speedX, posY + speedY)){
						int buff = speedX;
						speedX = speedY;
						speedY = buff;
					}
					else if (map.getRGB(posX - speedY, posY - speedX) > map.getRGB(posX + speedX, posY + speedY)){
						int buff = speedX;
						speedX = -speedY;
						speedY = -buff;
					}
				}
				
				posX += speedX;
				posY += speedY;
			}
			JOptionPane.showMessageDialog(Main.parentFrame, "Success! Please reload the map if ou would like to try again");
			return null;
			
		}
		
	}
}
