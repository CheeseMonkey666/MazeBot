package Default;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class LoadImage implements ActionListener {
	
	
	public LoadImage(){
		
	}
	
	public void actionPerformed(ActionEvent e){
		FileDialog fd;
		String fileName;
		BufferedImage img;
		fd = new FileDialog(Main.parentFrame, "Open");
		fd.setMultipleMode(false);
		fd.setFile("*.bmp");
		fd.setDirectory("C:\\");
		fd.setVisible(true);
		fileName = fd.getDirectory() + fd.getFile();
		try {
			img = ImageIO.read(new File(fileName));
			if (img.getHeight() <= 900 && img.getWidth() <= 600){
				Main.map = img;
				Image image = (Image)img;
				int h = img.getHeight(), w = img.getWidth(), scale = 600 / h;
				image = img.getScaledInstance(w * scale, h * scale, Image.SCALE_DEFAULT);
				Main.Canvas.setIcon(new ImageIcon(image));
				Main.Canvas.setBorder(BorderFactory.createLineBorder(Color.black));
			}
			else{
				JOptionPane.showMessageDialog(Main.parentFrame, "Image too large (450x300 max)");
				img = null;
			}
			} catch (IOException e1) {
			JOptionPane.showMessageDialog(Main.parentFrame, "Image not loaded");
			img = null;
		}
		Main.map = img;
		Main.checkMapBuffered();
	}
}
